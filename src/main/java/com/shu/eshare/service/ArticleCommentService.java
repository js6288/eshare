package com.shu.eshare.service;

import com.shu.eshare.model.domain.ArticleComment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shu.eshare.model.vo.ArticleCommentVO;

import java.util.List;

/**
* @author ljs
* @description 针对表【article_comment(文章评论)】的数据库操作Service
* @createDate 2023-02-06 21:05:25
*/
public interface ArticleCommentService extends IService<ArticleComment> {

    boolean addComment(ArticleComment articleComment);

    List<List<ArticleCommentVO>> getArticleCommentList(Long articleId,Long curUserId);
}
