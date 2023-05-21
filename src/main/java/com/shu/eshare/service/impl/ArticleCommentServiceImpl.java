package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.common.Constant;
import com.shu.eshare.esrepository.ArticleCardESRepository;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.Article;
import com.shu.eshare.model.domain.ArticleComment;
import com.shu.eshare.model.security.LoginUser;
import com.shu.eshare.model.vo.ArticleCommentVO;
import com.shu.eshare.service.ArticleCommentService;
import com.shu.eshare.mapper.ArticleCommentMapper;
import com.shu.eshare.service.ArticleService;
import com.shu.eshare.service.LevelService;
import com.shu.eshare.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author ljs
* @description 针对表【article_comment(文章评论)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:25
*/
@Service
@Slf4j
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleComment>
    implements ArticleCommentService{

    @Autowired
    LevelService levelService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    @Transactional
    public boolean addComment(ArticleComment articleComment) {

        Long articleId = articleComment.getArticleId();
        //在MySQL中保存评论
        boolean save = save(articleComment);

        //文章评论数增加
        articleService.update(new UpdateWrapper<Article>()
                .setSql("comments_num=comments_num+1").eq("article_id", articleId));

        //ES评论数增加
        UpdateByQueryRequest request = new UpdateByQueryRequest("article_card_list");
        request.setQuery(QueryBuilders.termQuery("articleId", articleId));
        request.setScript(new Script(ScriptType.INLINE, "painless", "ctx._source.commentsNum += 1", Collections.emptyMap()));

        try {
            restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es增加评论数失败",e);
        }
        //增加经验
        levelService.incrExperienceLimitDaily(5, Constant.ARTICLE_COMMENT_ADD);

        return save;
    }

    @Override
    public List<List<ArticleCommentVO>> getArticleCommentList(Long articleId,Long curUserId) {
        if (curUserId == null){
            curUserId = 0L;
        }
        Long loginUserId = curUserId;
        List<List<ArticleCommentVO>> result = new ArrayList<>();

        //查询根评论的所有评论id
        List<Long> commentIds = this.list(new QueryWrapper<ArticleComment>()
                .select("id").eq("article_id", articleId).eq("root_id",0))
                .stream().map(ArticleComment::getId).collect(Collectors.toList());

        if (commentIds.isEmpty()){
            return result;
        }

        //根据评论id查询根评论的所有信息
        List<ArticleCommentVO> rootList = this.baseMapper.selectCommentVOByIds(commentIds,loginUserId);

        //获取排序后的rootIds
//        List<Long> rootIds = rootList.stream().map(ArticleCommentVO::getId).collect(Collectors.toList());


        //将每个根评论添加到二维列表中的每个列表的第一个位置
//        for (ArticleCommentVO articleCommentVO : rootList) {
//            ArrayList<ArticleCommentVO> list = new ArrayList<>();
//            list.add(articleCommentVO);
//            result.add(list);
//        }

        //查询楼中楼id列表 TODO 并发查询
        for (ArticleCommentVO articleCommentVO : rootList) {
            // 根据跟评论id列表查询楼中楼评论id
            //SELECT id FROM article_comment WHERE article_id =? AND root_id=? ORDER BY like_num
            List<Long> lzlIds = this.list(new QueryWrapper<ArticleComment>()
                            .select("id")
                            .eq("article_id", articleId).eq("root_id", articleCommentVO.getId())
                            .orderByDesc("like_num"))
                    .stream().map(ArticleComment::getId).collect(Collectors.toList());

            List<ArticleCommentVO> articleCommentVOS = new ArrayList<>();
            //添加根评论
            articleCommentVOS.add(articleCommentVO);
            if (!lzlIds.isEmpty()){
                // 根据楼中楼id查询楼中楼评论信息追加到末尾
                articleCommentVOS.addAll(this.baseMapper.selectCommentVOByIds(lzlIds, loginUserId));
            }

            //添加到评论区中
            result.add(articleCommentVOS);
        }

        // TODO 批量查询楼中楼评论的信息

        return result;
    }
}




