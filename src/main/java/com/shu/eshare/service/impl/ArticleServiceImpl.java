package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.shu.eshare.common.Constant;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.esrepository.ArticleCardESRepository;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.mapper.ArticleMapper;
import com.shu.eshare.model.domain.*;
import com.shu.eshare.model.es.ArticleCardES;
import com.shu.eshare.model.request.ArticleAddBody;
import com.shu.eshare.model.request.ArticleEditBody;
import com.shu.eshare.model.response.ArticlesPage;
import com.shu.eshare.model.security.LoginUser;
import com.shu.eshare.service.*;
import com.shu.eshare.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wiremock.com.google.common.reflect.TypeToken;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author ljs
 * @description 针对表【article(文章)】的数据库操作Service实现
 * @createDate 2023-02-06 21:05:25
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {

    @Resource
    private ArticleTagsService articleTagsService;


    @Resource
    private TagArticleRelService tagArticleRelService;

    @Resource
    private UserService userService;

    @Resource
    private LevelService levelService;

    @Autowired
    private ArticleCardESRepository articleCardESRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ArticleLikesService articleLikesService;

    @Autowired
    private ArticleCollectionService articleCollectionService;

    /**
     * 将文章保存在 MySQL和 ElasticSearch 数据库中
     *
     * @param articleAddBody
     * @return
     */
    @Override
    @Transactional
    public boolean articleAdd(ArticleAddBody articleAddBody) {

        Article article = new Article();
        article.setArticleTitle(articleAddBody.getArticleTitle());
        article.setContent(articleAddBody.getContent());

        //orderImages 转成json数组,保存在数据库中
        Gson gson = new Gson();
        String toJson = gson.toJson(articleAddBody.getOrderImages());
        article.setOrderImages(toJson);

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUser().getUserId();
        article.setUserId(userId);

        //保存文章信息到MySQL数据库
        boolean save = this.save(article);
        if (!save) {
            return false;
        }

        //保存用户自定义标签
        List<String> userTagList = articleAddBody.getUserTagList();
        List<ArticleTags> articleTagsList = new ArrayList<>();

        int userTagListSize = userTagList.size();
        if (userTagListSize > 0) {
            userTagList.forEach(item -> {
                ArticleTags articleTags = new ArticleTags();
                articleTags.setTagType(1);
                articleTags.setTagName(item);
                articleTags.setUserId(userId);
                articleTagsList.add(articleTags);
            });
            //保存用户自定义标签
            boolean saveUserDefineTags = articleTagsService.saveBatch(articleTagsList);
            if (!saveUserDefineTags) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "保存自定义标签失败");
            }
        }

        //文章标签关系实体列表
        List<TagArticleRel> tagArticleRelList = new ArrayList<>();

        if (userTagListSize > 0) {
            //新增与文章和标签的联系到tagArticleRelList,你问我为什么不在保存用户自定义标签的时候添加关系
            //因为需要在数据库保存保存用户自定义标签得到回写的articleTagId,才能保存关系，改代码的时候一定要慎重
            for (ArticleTags articleTag : articleTagsList) {
                TagArticleRel tagArticleRel = new TagArticleRel();
                tagArticleRel.setArticleTagId(articleTag.getId());
                tagArticleRel.setArticleId(article.getArticleId());
                tagArticleRelList.add(tagArticleRel);
            }
        }

        List<Long> systemTagIdList = articleAddBody.getSystemTagIdList();
        if (systemTagIdList.size() > 0) {
            //默认标签与文章的联系到tagArticleRelList
            systemTagIdList.forEach(item -> {
                TagArticleRel tagArticleRel = new TagArticleRel();
                tagArticleRel.setArticleTagId(item);
                tagArticleRel.setArticleId(article.getArticleId());
                tagArticleRelList.add(tagArticleRel);
            });
        }

        //保存所有标签和文章的联系到关系表
        if (tagArticleRelList.size() > 0) {
            boolean saveRel = tagArticleRelService.saveBatch(tagArticleRelList);
            if (!saveRel) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }


        Article byId = this.getById(article.getArticleId());
        //TODO 保存文章信息到ElasticSearch
        saveArticleToES(byId, userTagList, systemTagIdList);

        //发表文章赠送10经验，每天限10次
        levelService.incrExperienceLimitDaily(10, Constant.ARTICLE_PUBLISH);
        return true;
    }


    /**
     * 分页查询，按时间顺序排序
     *
     * @return
     */
    @Override
    public List<ArticleCardES> getAllArticleOnline(int curPage) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        int currentPage = curPage;//当前页，第一页从 0 开始，1 表示第二页
        int pageSize = 5;//每页显示多少条
        //设置查询分页
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);
        Page<ArticleCardES> all = articleCardESRepository.findAll(pageRequest);
        return all.getContent();
    }

    /**
     * 从elasticsearch中查询
     *
     * @param curPage
     * @param userId
     * @return
     */
    @Override
    public ArticlesPage getSelfArticlesByPage(int curPage, Long userId) {
        int pageSize = 5;//每页显示多少条
        PageRequest pageRequest = articleCardESRepository.getPageRequest(curPage, pageSize);
        Page<ArticleCardES> articleCardESPage = articleCardESRepository.findByUserIdOrderByCreateTimeDesc(userId, pageRequest);
        ArticlesPage articlesPage = new ArticlesPage();
        long totalElements = articleCardESPage.getTotalElements();//总数，
        articlesPage.setTotal(totalElements);
        int cur = articleCardESPage.getNumber();//当前页码
        articlesPage.setCurPage(cur);
        int totalPages = articleCardESPage.getTotalPages();//总页数
        articlesPage.setPageNum(totalPages);
        articlesPage.setArticleCardESList(articleCardESPage.getContent());
        return articlesPage;
    }

    @Override
    public ArticleCardES getArticleEs(Long articleId) {
        return articleCardESRepository.findByArticleId(articleId);
    }

    @Override
    @Transactional
    public boolean editArticle(ArticleEditBody articleEditBody) {
        //判断文章是否属于当前用户
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        Article article = this.getOne(new QueryWrapper<Article>()
                .select("article_id", "user_id").eq("article_id", articleEditBody.getArticleId()));
        if (article == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
        }
        if (!article.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "这篇文章不属于你");
        }

        //更新文章mysql
        this.update(new UpdateWrapper<Article>()
                .set("article_title", articleEditBody.getArticleTitle())
                .set("content", articleEditBody.getContent())
                .eq("article_id", articleEditBody.getArticleId()));

        //更新文章elasticSearch
        UpdateByQueryRequest request = new UpdateByQueryRequest("article_card_list");
        request.setQuery(QueryBuilders.matchQuery("articleId",articleEditBody.getArticleId()));
        String script = "ctx._source.articleTitle=params.articleTitle;ctx._source.content = params.content";
        Map<String, Object> params = new HashMap<>();
        params.put("articleTitle",articleEditBody.getArticleTitle());
        params.put("content",articleEditBody.getContent());
        request.setScript(new Script(ScriptType.INLINE,"painless",script,params));

        try {
            restHighLevelClient.updateByQuery(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es更新文章失败",e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public void articleViewNumAdd(Long articleId) {

        //对文章加分布式锁
        RLock lock = redissonClient.getLock("articleViewNumAdd:" + articleId);
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    //更新数据库
                    this.update(new UpdateWrapper<Article>().setSql("views_num=views_num+1").eq("article_id", articleId));

                    // ElasticSearch 增加浏览次数
                    UpdateByQueryRequest request = new UpdateByQueryRequest("article_card_list");
                    request.setQuery(QueryBuilders.matchQuery("articleId",articleId));
                    request.setScript(new Script(ScriptType.INLINE,"painless","ctx._source.viewsNum += 1", Collections.emptyMap()));
                    try {
                        restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
                    } catch (IOException e) {
                        log.error("elasticsearch 增加浏览次数失败",e);
                    }
                    break;
                }
            }
        } catch (InterruptedException e) {
            log.error("articleViewNumAdd Error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("unlock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }

    }

    //TODO
    @Override
    public boolean like(Long articleId, int like) {
        //获取当前文章点赞数
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        String key = "user:article:likes:"+articleId;
        RScoredSortedSet<Object> set = redissonClient.getScoredSortedSet(key);

        Double score = set.getScore(userId);
        if (like == 1){
            //判断用户是否点过赞
            if (score !=null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"已经点赞过这篇文章");
            }
            set.add(new Date().getTime(),userId);
        }else if (like == 2){
            if (score == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有点赞过这篇文章，不能取消点赞");
            }
            set.remove(userId);
        }else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //更新MySQL点赞数,添加点赞和用户的关系/ 减少
        addLikeCountMySQL(articleId,userId,like);


        //elasticsearch 点赞数添加/减少
        addLikeCountES(articleId,like);

        //点赞文章赠送2经验
        levelService.incrExperienceLimitDaily(2,Constant.ARTICLE_LIKE);

        return true;
    }

    @Override
    public boolean isLike(Long articleId) {
        String key = "user:article:likes:"+articleId;
        RScoredSortedSet<Object> set = redissonClient.getScoredSortedSet(key);
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        Double score = set.getScore(userId);

        return score != null;
    }

    @Override
    public boolean collect(Long articleId, int collect) {
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        String key = "user:article:collect:"+articleId;
        RScoredSortedSet<Object> set = redissonClient.getScoredSortedSet(key);

        Double score = set.getScore(userId);
        if (collect == 1){
            if (score!=null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"已经收藏过这篇文章");
            }
            set.add(new Date().getTime(),userId);
        }else if (collect == 2){
            if (score == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有收藏过这篇文章，不能取消收藏");
            }
            set.remove(userId);
        }else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //更新MySQL收藏数，维护收藏于用户的关系
        updateArticleCollectionMySQL(articleId,userId,collect);

        //更新ElasticSearch收藏数
        updateArticleCollectionNumsES(articleId,collect);

        //收藏文章，赠送经验
        levelService.incrExperienceLimitDaily(2,Constant.ARTICLE_COLLECTION);

        return true;
    }

    @Override
    public boolean isCollect(Long articleId) {
        String key = "user:article:collect:"+articleId;
        RScoredSortedSet<Object> set = redissonClient.getScoredSortedSet(key);
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        Double score = set.getScore(userId);
        return score != null;
    }

    @Override
    public boolean removeSelfArticle(Long articleId) {

        //判断当前文章是否属于当前用户
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        Article article = this.getOne(new QueryWrapper<Article>()
                .eq("article_id", articleId).select("article_id", "user_id"));
        if (article == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前文章已经不存在或被删除");
        }

        if (!userId.equals(article.getUserId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前文章不属于你");
        }

        //删除MySQL上的文章
        boolean b = removeById(articleId);

        //删除ElasticSearch的文章
        articleCardESRepository.deleteByArticleId(articleId);

        return b;
    }

    /**
     * 查询当前用户收藏的文章
     * @param curPage 当前页码从0开始
     * @return ArticlesPage分页
     */
    @Override
    public ArticlesPage getSelfCollectArticlePage(Integer curPage)  {
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        //获取当前用户的收藏列表
        List<ArticleCollection> list = articleCollectionService
                .list(new QueryWrapper<ArticleCollection>().eq("user_id", userId).select("article_id"));
        List<Long> articleIdList = list.stream().map(ArticleCollection::getArticleId).collect(Collectors.toList());

        PageRequest pageRequest = articleCardESRepository.getPageRequest(curPage, 5);

        Page<ArticleCardES> articleCardESPage = articleCardESRepository.findByArticleIdsOrderByCreateTimeDesc(articleIdList, pageRequest);

        ArticlesPage articlesPage = new ArticlesPage();
        long totalElements = articleCardESPage.getTotalElements();//总数，
        articlesPage.setTotal(totalElements);
        int cur = articleCardESPage.getNumber();//当前页码
        articlesPage.setCurPage(cur);
        int totalPages = articleCardESPage.getTotalPages();//总页数
        articlesPage.setPageNum(totalPages);
        articlesPage.setArticleCardESList(articleCardESPage.getContent());
        return articlesPage;
    }

    /**
     * 从ElasticSearch中搜索文章
     * @param searchText 搜索关键词
     * @param curPage 页码从0开始
     * @return
     */
    @Override
    public ArticlesPage searchArticle(String searchText, Integer curPage) {
        PageRequest pageRequest = articleCardESRepository.getPageRequest(curPage,5);

        Page<ArticleCardES> page = articleCardESRepository
                .searchArticleCardESByArticleTitleOrContentOrTagList(searchText,pageRequest);

        ArticlesPage articlesPage = new ArticlesPage();
        articlesPage.setTotal(page.getTotalElements());
        articlesPage.setPageNum(page.getTotalPages());
        articlesPage.setCurPage(page.getNumber());
        articlesPage.setArticleCardESList(page.getContent());
        return articlesPage;
    }

    @Override
    public List<ArticleCardES> getHotArticleList() {

        //获取top10的 articleId列表
        RScoredSortedSet<Long> scoredSortedSet = redissonClient.getScoredSortedSet("eshare:hot:article");

        List<Long> articleIdList = (List<Long>) scoredSortedSet.valueRangeReversed(0, 9);

        for (Long aLong : articleIdList) {
            System.out.println(aLong+":"+scoredSortedSet.getScore(aLong));
        }

        List<ArticleCardES> articleCardESList = articleCardESRepository.findByArticleIds(articleIdList);

        List<ArticleCardES> sortedList = articleIdList.stream()
                .flatMap(articleId -> articleCardESList.stream()
                        .filter(articleCardES -> articleCardES.getArticleId().equals(articleId)))
                    .collect(Collectors.toList());

        return sortedList;
    }

    @Async("customAsyncThreadPool")
    public void updateArticleCollectionNumsES(Long articleId, int collect) {
        RLock lock = redissonClient.getLock("articleCollectNumAddES:" + articleId);
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    // ElasticSearch 增加浏览次数
                    UpdateByQueryRequest request = new UpdateByQueryRequest("article_card_list");
                    request.setQuery(QueryBuilders.matchQuery("articleId", articleId));
                    if (collect == 1) {
                        request.setScript(new Script(ScriptType.INLINE, "painless", "ctx._source.collectionsNum += 1", Collections.emptyMap()));
                    }else {
                        request.setScript(new Script(ScriptType.INLINE, "painless", "ctx._source.collectionsNum -= 1", Collections.emptyMap()));
                    }
                    try {
                        restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
                    } catch (IOException e) {
                        log.error("elasticsearch 修改收藏次数失败", e);
                    }
                    break;
                }
            }
        }catch (InterruptedException e) {
            log.error("updateArticleCollectionNumsES Error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("unlockES:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    @Async("customAsyncThreadPool")
    public void updateArticleCollectionMySQL(Long articleId,Long userId,int collect){
        RLock lock = redissonClient.getLock("articleCollectionNumAddMYSQL:" + articleId);
        try{
            while (true){
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)){
                    if (collect == 1){
                        this.update(new UpdateWrapper<Article>()
                                .setSql("collections_num=collections_num+1").eq("article_id", articleId));
                        ArticleCollection articleCollection = new ArticleCollection();
                        articleCollection.setArticleId(articleId);
                        articleCollection.setUserId(userId);
                        articleCollectionService.save(articleCollection);
                    }else {
                        this.update(new UpdateWrapper<Article>()
                                .setSql("collections_num=collections_num-1").eq("article_id", articleId));
                        articleCollectionService.remove(new QueryWrapper<ArticleCollection>()
                                .eq("article_id",articleId).eq("user_id",userId));
                    }
                    break;
                }
            }
        }catch (InterruptedException e) {
            log.error("updateArticleCollectionMySQL Error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("unlock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    @Async("customAsyncThreadPool")
    public void addLikeCountMySQL(Long articleId,Long userId,int like){
        RLock lock = redissonClient.getLock("articleLikesNumAddMYSQL:" + articleId);
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    //点赞数增加
                    if (like == 1) {
                        this.update(new UpdateWrapper<Article>().setSql("likes_num=likes_num+1").eq("article_id", articleId));

                        //点赞表
                        ArticleLikes articleLikes = new ArticleLikes();
                        articleLikes.setArticleId(articleId);
                        articleLikes.setUserId(userId);
                        articleLikesService.save(articleLikes);
                    }else {
                        this.update(new UpdateWrapper<Article>().setSql("likes_num=likes_num-1").eq("article_id", articleId));

                        articleLikesService.remove(new QueryWrapper<ArticleLikes>()
                                .eq("article_id",articleId).eq("user_id",userId));
                    }
                    break;
                }
            }
        }catch (InterruptedException e) {
            log.error("addLikeCountMySQL Error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("unlock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    @Async("customAsyncThreadPool")
    public void addLikeCountES(Long articleId,int like){

        RLock lock = redissonClient.getLock("articleLikesNumAddES:" + articleId);
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    // ElasticSearch 增加浏览次数
                    UpdateByQueryRequest request = new UpdateByQueryRequest("article_card_list");
                    request.setQuery(QueryBuilders.matchQuery("articleId", articleId));
                    if (like == 1) {
                        request.setScript(new Script(ScriptType.INLINE, "painless", "ctx._source.likesNum += 1", Collections.emptyMap()));
                    }else {
                        request.setScript(new Script(ScriptType.INLINE, "painless", "ctx._source.likesNum -= 1", Collections.emptyMap()));
                    }
                    try {
                        restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
                    } catch (IOException e) {
                        log.error("elasticsearch 修改点赞次数失败", e);
                    }
                    break;
                }
            }
        }catch (InterruptedException e) {
            log.error("addLikeCountES Error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("unlockES:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }



    //TDOO从MySQL中分页查询
//    public List<ArticleCardES> getSelfArticlesByPageOnMySQL(int curPage,Long userId){
//        this.baseMapper.selectPage()
//    }

    //保存文章信息到ElasticSearch
    public void saveArticleToES(Article article, List<String> userTagList, List<Long> systemTagIdList) {
        ArticleCardES articleCardES = new ArticleCardES();
        articleCardES.setArticleId(article.getArticleId());
        articleCardES.setArticleTitle(article.getArticleTitle());
        articleCardES.setContent(article.getContent());
        articleCardES.setType(article.getType());
        articleCardES.setOrderImages(new Gson().fromJson(article.getOrderImages(), new TypeToken<List<String>>() {
        }.getType()));
        articleCardES.setCommentsNum(article.getCommentsNum());
        articleCardES.setCollectionsNum(article.getCollectionsNum());
        articleCardES.setLikesNum(article.getLikesNum());
        articleCardES.setViewsNum(article.getViewsNum());
        articleCardES.setCreateTime(article.getCreateTime());
        articleCardES.setUpdateTime(article.getUpdateTime());
        articleCardES.setUserId(article.getUserId());
        //添加用户自定义标签
        articleCardES.setTagList(userTagList);

        //查询用户昵称和头像
        User user = userService.getById(article.getUserId());
        if (user == null) {
            //如果用户不存在，用户可能注销了
            articleCardES.setNickname("用户已注销");
            articleCardES.setAvatar(Constant.AVATAR_URL);
            articleCardES.setLevel(0);
        } else {
            articleCardES.setNickname(user.getNickname());
            articleCardES.setAvatar(user.getAvatarUrl());

            //查询用户等级
            Level level = levelService.getOne(new QueryWrapper<Level>().select("level").eq("user_id", article.getUserId()));
            articleCardES.setLevel(level.getLevel());

        }
        //批量查询系统自带标签
        if (systemTagIdList.size() > 0) {
            List<ArticleTags> articleTagsList = articleTagsService.listByIds(systemTagIdList);
            for (ArticleTags articleTags : articleTagsList) {
                //添加系统标签
                articleCardES.getTagList().add(articleTags.getTagName());
            }
        }

//        articleCardES.getTagList().addAll()
//        for(Long tagId:systemTagIdList){
//            ArticleTags systemTag = articleTagsService.getById(tagId);
//            //添加系统标签
//            articleCardES.getTagList().add(systemTag.getTagName());
//        }


        //保存文章信息到ElasticSearch
        articleCardESRepository.save(articleCardES);
    }



}




