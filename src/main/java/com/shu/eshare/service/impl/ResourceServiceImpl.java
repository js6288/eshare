package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.common.Constant;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.esrepository.ResourceESRepository;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.mapper.ResourceMapper;
import com.shu.eshare.model.domain.*;
import com.shu.eshare.model.es.ResourceES;
import com.shu.eshare.model.request.ResourceCommitBody;
import com.shu.eshare.model.request.ResourceSearchBody;
import com.shu.eshare.model.response.ResourcePage;
import com.shu.eshare.model.vo.ResourceVO;
import com.shu.eshare.service.*;
import com.shu.eshare.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author ljs
* @description 针对表【resource(资源)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
@Slf4j
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource>
    implements ResourceService{

    @Autowired
    private ResourceTagsService resourceTagsService;

    @Autowired
    private TagResourceRelService tagResourceRelService;

    @Autowired
    private ResourceESRepository resourceESRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private UserDownloadResourceService userDownloadResourceService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private LevelService levelService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ResourceCollectionService resourceCollectionService;

    @Override
    @Transactional
    public boolean addResource(ResourceCommitBody resourceCommitBody) {
        //拷贝一些属性
        Resource resource = new Resource();
        resource.setResourceName(resourceCommitBody.getResourceName());
        resource.setResourceDescription(resourceCommitBody.getResourceDescription());
        resource.setMajorId(resourceCommitBody.getMajorId());
        resource.setType(resourceCommitBody.getType());
        resource.setDownloadUrl(resourceCommitBody.getDownloadUrl());
        resource.setFileSize(resourceCommitBody.getFileSize());
        resource.setRequirePoint(resourceCommitBody.getRequirePoint());
        resource.setFileType(resourceCommitBody.getFileType());


        //获取当前用户userId,设置resource 所属的用户id
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        resource.setUserId(userId);

        //提交到resource表
        boolean save = save(resource);
        if (!save){
            return false;
        }
        List<String> userDefineTagList = resourceCommitBody.getUserDefineTagList();
        List<ResourceTags> resourceTagsList = new ArrayList<>();
        if (userDefineTagList.size() >0){
            userDefineTagList.forEach(item->{
                ResourceTags resourceTags = new ResourceTags();
                resourceTags.setTagType(1);
                resourceTags.setTagName(item);
                resourceTags.setUserId(userId);
                resourceTagsList.add(resourceTags);
            });

            //保存用户自定义标签
            boolean saveUserDefineTags = resourceTagsService.saveBatch(resourceTagsList);
            if (!saveUserDefineTags){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"保存自定义标签失败");
            }
        }

        List<TagResourceRel> tagResourceRelList = new ArrayList<>();

        //添加用户自定义的资源标签，保存关联关系 tag_resource_rel
        if (userDefineTagList.size()>0){
            for (ResourceTags resourceTags : resourceTagsList) {
                TagResourceRel tagResourceRel = new TagResourceRel();
                tagResourceRel.setResourceTagId(resourceTags.getId());
                tagResourceRel.setResourceId(resource.getResourceId());
                tagResourceRelList.add(tagResourceRel);
            }
        }
        //添加系统资源标签与资源的关系 tag_resource_rel
        Long defaultResourceTagId = resourceCommitBody.getDefaultResourceTagId();
        if (defaultResourceTagId != null){
            TagResourceRel tagResourceRel = new TagResourceRel();
            tagResourceRel.setResourceTagId(defaultResourceTagId);
            tagResourceRel.setResourceId(resource.getResourceId());
            tagResourceRelList.add(tagResourceRel);
        }
        //保存关系到MySQL
        if (tagResourceRelList.size()>0){
            boolean saveBatch = tagResourceRelService.saveBatch(tagResourceRelList);
            if (!saveBatch){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }


        return true;
    }

    /**
     * 根据用户id分页查询resource简要信息
     * @param userId userId
     * @param curPage 从第1页开始
     * @return
     */
    @Override
    public Page<Resource> getUserResourceByUserId(Long userId, int curPage) {
        Page<Resource> page = new Page<>(curPage,9);
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("resource_id", "user_id", "resource_name","collection_num", "download_num", "file_size","status");
        queryWrapper.eq("user_id",userId);
        queryWrapper.orderByDesc("create_time");
        Page<Resource> resourcePage = this.baseMapper.selectPage(page, queryWrapper);
        return resourcePage;
    }

    /**
     * 从elasticSearch中
     * @param curPage
     * @return
     */
    @Override
    public ResourcePage getAllResourceByPage(int curPage) {
        int pageSize = 15;//每页15条
        PageRequest pageRequest = resourceESRepository.getPageRequest(curPage, pageSize);
        org.springframework.data.domain.Page<ResourceES> resourceESPage = resourceESRepository.findAll(pageRequest);
        ResourcePage resourcePage = new ResourcePage();
        long totalElements = resourceESPage.getTotalElements();//总数
        int number = resourceESPage.getNumber();//当前页码
        int totalPages = resourceESPage.getTotalPages();//总页数
        resourcePage.setCurPage(number);
        resourcePage.setTotal(totalElements);
        resourcePage.setPageNum(totalPages);
        resourcePage.setResourceESList(resourceESPage.getContent());
        return resourcePage;
    }

    /**
     * 获取资源详细信息用户前端页面展示
     * @param resourceId 资源编号
     * @return ResourceVO 视图
     */
    @Override
    public ResourceVO getResourceDetails(Long resourceId) {
        ResourceVO resourceVO = new ResourceVO();
        // 1.获取资源本体,且资源必须上架。
        Resource resource = getOne(new QueryWrapper<Resource>()
                .eq("resource_id",resourceId).eq("status",1));
        if (resource == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"找不到该资源");
        }
        resourceVO.setResourceId(resource.getResourceId());
        resourceVO.setResourceName(resource.getResourceName());
        resourceVO.setResourceDescription(resource.getResourceDescription());
        resourceVO.setFileSize(resource.getFileSize());
        resourceVO.setRequirePoint(resource.getRequirePoint());
        resourceVO.setFileType(resource.getFileType());
        resourceVO.setCreateTime(resource.getCreateTime());
        resourceVO.setStatus(resource.getStatus());
        resourceVO.setUpdateTime(resource.getUpdateTime());
        resourceVO.setCollectionNum(resource.getCollectionNum());
        resourceVO.setType(resource.getType());
        resourceVO.setDownloadNum(resource.getDownloadNum());
        // 获取用户昵称和头像
        User user = userService.getOne(new QueryWrapper<User>()
                .select("nickname", "avatar_url").eq("user_id", resource.getUserId()));
        if (user == null){
            user = new User();
            user.setNickname("用户已注销");
            user.setAvatarUrl(Constant.AVATAR_URL);
        }
        resourceVO.setNickname(user.getNickname());
        resourceVO.setAvatar(user.getAvatarUrl());

        // 2.获取标签列表
        // 获取标签关系列表
        List<TagResourceRel> tagResourceRels = tagResourceRelService
                .list(new QueryWrapper<TagResourceRel>()
                        .eq("resource_id", resource.getResourceId()));
        if (tagResourceRels!= null && !tagResourceRels.isEmpty()){
            // map成资源标签的id列表
            List<Long> resourceTagIdList = tagResourceRels.stream()
                    .map(TagResourceRel::getResourceTagId).collect(Collectors.toList());
            // 查询标签列表
            List<ResourceTags> resourceTagsList = resourceTagsService.listByIds(resourceTagIdList);
            if (resourceTagsList != null && !resourceTagsList.isEmpty()) {
                List<String> tagList = resourceTagsList.stream().map(ResourceTags::getTagName).collect(Collectors.toList());
                resourceVO.setTagList(tagList);
            }
        }


        // 查询资源所属专业名
        Major major = majorService.getOne(new QueryWrapper<Major>()
                .select("major_name").eq("id", resource.getMajorId()));
        if (major!=null){
            resourceVO.setMajorName(major.getMajorName());
        }

        return resourceVO;
    }

    /**
     *
     * @param resourceId 资源编号
     * @return
     */
    @Override
    @Transactional
    public String getDownloadResource(Long resourceId) {
        //查询资源下载url
        Resource resource = this.getOne(new QueryWrapper<Resource>()
                .eq("resource_id", resourceId).eq("status", 1)
                .select("resource_id","user_id","download_url","require_point","type"));
        if (resource == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"资源不存在");
        }
        String downloadUrl = resource.getDownloadUrl();

        //获取当前用户
        User user = SecurityUtils.getLoginUser().getUser();
        if (resource.getType() == 1){//如果是付费资源，则需要扣减积分
            Integer requirePoint = resource.getRequirePoint();
            //积分不足
            if (requirePoint >user.getAccumulatePoints()){
                throw new BusinessException(ErrorCode.FORBIDDEN,"积分余额不足");
            }

            //扣减积分
            boolean decr = userService.update(new UpdateWrapper<User>()
                    .eq("user_id", user.getUserId())
                    .setSql("accumulate_points = accumulate_points - " + requirePoint));
            if (!decr){//扣减积分成功
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"扣减积分失败，请稍后重试");
            }
        }

        // 资源下载一次为上传者增加 1 积分
        userService.update(new UpdateWrapper<User>()
                .eq("user_id",resource.getUserId())
                .setSql("accumulate_points = accumulate_points +"+ 1 )
        );


        //mysql增加下载次数
        this.update(new UpdateWrapper<Resource>()
                .eq("resource_id",resourceId).setSql("download_num=download_num+1"));

        //es增加下载次数
        UpdateByQueryRequest request = new UpdateByQueryRequest("resource_card_list");
        request.setQuery(QueryBuilders.matchQuery("resourceId", resourceId));
        request.setScript(new Script(ScriptType.INLINE, "painless", "ctx._source.downloadNum += 1", Collections.emptyMap()));
        try {
            restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        //维护用户与资源下载关系表，给前端判断资源当前用户是否下载资源
        UserDownloadResource userDownloadResource = new UserDownloadResource();
        userDownloadResource.setResourceId(resourceId);
        userDownloadResource.setUserId(user.getUserId());
        userDownloadResourceService.save(userDownloadResource);
        return downloadUrl;
    }

    @Override
    public boolean isResourceDownload(Long resourceId, Long userId) {
        long count = userDownloadResourceService.count(new QueryWrapper<UserDownloadResource>()
                .eq("resource_id", resourceId).eq("user_id", userId));
        return count > 0;
    }

    @Override
    public boolean collect(Long resourceId,int collect) {
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        String key = "user:resource:collect:"+resourceId;
        RScoredSortedSet<Object> set = redissonClient.getScoredSortedSet(key);

        Double score = set.getScore(userId);
        if (collect == 1){
            if (score!=null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"已经收藏过这个资源");
            }
            set.add(new Date().getTime(),userId);
        }else if (collect == 2){
            if (score == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有收藏过该资源，不能取消收藏");
            }
            set.remove(userId);
        }else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //更新MySQL收藏数，维护收藏于用户的关系
        updateResourceCollectionMySQL(resourceId,userId,collect);
        //更新ElasticSearch收藏数
        updateResourceCollectionNumsES(resourceId,collect);

        return true;
    }

    @Override
    public boolean isCollect(Long resourceId) {
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        String key = "user:resource:collect:"+resourceId;
        RScoredSortedSet<Object> set = redissonClient.getScoredSortedSet(key);

        Double score = set.getScore(userId);
        return score != null;
    }

    /**
     * 查询当前用户收藏的资源列表
     * @param curPage 当前页码
     * @return
     */
    @Override
    public Page<Resource> getResourceCollectPage(Integer curPage) {
        Page<Resource> page = new Page<>(curPage,9);
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();

        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        //获取当前用户收藏的id列表
        List<ResourceCollection> list = resourceCollectionService.list(new QueryWrapper<ResourceCollection>()
                .select("resource_id").eq("user_id", userId));

        List<Long> resourceIdList = list.stream().map(ResourceCollection::getResourceId).collect(Collectors.toList());

        Page<Resource> resourcePage = this.page(page, new QueryWrapper<Resource>().in("resource_id", resourceIdList));
        return resourcePage;
    }

    @Override
    public boolean removeSelfResource(Long resourceId) {
        //判断当前文章是否属于当前用户
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        Resource resource = this.getOne(new QueryWrapper<Resource>()
                .eq("resource_id", resourceId).select("resource_id", "user_id"));

        if (resource == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前资源不存在或已经被删除");
        }

        if (!userId.equals(resource.getUserId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前资源不属于你");
        }

        //删除MySQL上的资源
        boolean b = removeById(resourceId);

        //删除ElasticSearch上的资源
        resourceESRepository.deleteByResourceId(resourceId);

        return b;
    }

    /**
     * 搜索
     * @param resourceSearchBody
     * @return
     */
    @Override
    public ResourcePage searchForPage(ResourceSearchBody resourceSearchBody) {
        int pageSize = 15;//每页15条
        if (resourceSearchBody.getCurPage() == null){
            resourceSearchBody.setCurPage(0);
        }
        PageRequest pageRequest = resourceESRepository.getPageRequest(resourceSearchBody.getCurPage(), pageSize);

        org.springframework.data.domain.Page<ResourceES> searchResult = resourceESRepository.search(
                resourceSearchBody.getSearchText(),
                resourceSearchBody.getMajorName(),
                resourceSearchBody.getFileType(),
                resourceSearchBody.getType(),
                resourceSearchBody.getTag(),
                pageRequest
        );

        ResourcePage resourcePage = new ResourcePage();
        resourcePage.setTotal(searchResult.getTotalElements());
        resourcePage.setCurPage(searchResult.getNumber());
        resourcePage.setPageNum(searchResult.getTotalPages());
        resourcePage.setResourceESList(searchResult.getContent());
        return resourcePage;
    }

    @Override
    public ResourcePage searchByKeyword(String keyword, Integer curPage) {

        int pageSize = 15;//每页15条
        PageRequest pageRequest = resourceESRepository.getPageRequest(curPage, pageSize);
        org.springframework.data.domain.Page<ResourceES> searchResult = resourceESRepository
                .searchByResourceName(keyword,pageRequest);

        ResourcePage resourcePage = new ResourcePage();
        resourcePage.setTotal(searchResult.getTotalElements());
        resourcePage.setCurPage(searchResult.getNumber());
        resourcePage.setPageNum(searchResult.getTotalPages());
        resourcePage.setResourceESList(searchResult.getContent());

        return resourcePage;
    }

    @Async("customAsyncThreadPool")
    public void updateResourceCollectionNumsES(Long resourceId, int collect) {
        RLock lock = redissonClient.getLock("resourceCollectNumAddES:" + resourceId);
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    // ElasticSearch 增加浏览次数
                    UpdateByQueryRequest request = new UpdateByQueryRequest("resource_card_list");
                    request.setQuery(QueryBuilders.matchQuery("resourceId", resourceId));
                    if (collect == 1) {
                        request.setScript(new Script(ScriptType.INLINE, "painless", "ctx._source.collectionNum += 1", Collections.emptyMap()));
                    }else {
                        request.setScript(new Script(ScriptType.INLINE, "painless", "ctx._source.collectionNum -= 1", Collections.emptyMap()));
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
            log.error("updateResourceCollectionNumsES Error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("unlockES:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    @Async("customAsyncThreadPool")
    public void updateResourceCollectionMySQL(Long resourceId, Long userId, int collect) {
        RLock lock = redissonClient.getLock("updateResourceCollectionMySQL:"+resourceId);
        try{
            while (true){
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)){
                    if (collect == 1){
                        this.update(new UpdateWrapper<Resource>()
                                .setSql("collection_num=collection_num+1").eq("resource_id", resourceId));
                        ResourceCollection resourceCollection = new ResourceCollection();
                        resourceCollection.setResourceId(resourceId);
                        resourceCollection.setUserId(userId);
                        resourceCollectionService.save(resourceCollection);
                    }else {
                        this.update(new UpdateWrapper<Resource>()
                                .setSql("collection_num=collection_num-1").eq("resource_id", resourceId));
                        resourceCollectionService.remove(new QueryWrapper<ResourceCollection>()
                                .eq("resource_id",resourceId).eq("user_id",userId));
                    }
                    break;
                }
            }
        }catch (InterruptedException e) {
            log.error("updateResourceCollectionMySQL Error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("unlock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }


    public void incrAccumulatePoints(Long userId,Integer point){
        if (point <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"point required more than zero");
        }
        userService.update(new UpdateWrapper<User>()
                .eq("user_id",userId)
                .setSql("accumulate_points = accumulate_points +"+ point )
        );
    }

}




