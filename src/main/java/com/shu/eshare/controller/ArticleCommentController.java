package com.shu.eshare.controller;

import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.ArticleComment;
import com.shu.eshare.model.vo.ArticleCommentVO;
import com.shu.eshare.service.ArticleCommentService;
import com.shu.eshare.utils.ResultUtils;
import com.shu.eshare.utils.SecurityUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/article/comment")
@RestController
public class ArticleCommentController {

    @Autowired
    private ArticleCommentService articleCommentService;

    /**
     * 发表评论
     * @param articleComment 评论实体
     * @return
     */
    @PostMapping("/add")
    public BaseResponse addArticleComment(@RequestBody ArticleComment articleComment){
        if (articleComment == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(articleComment.getContent())||articleComment.getContent().length()>500){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"评论不能为空且内容不宜过长");
        }
        if (ObjectUtils.anyNull(
                articleComment.getArticleId(),
                articleComment.getParentId(),
                articleComment.getRootId()
        )){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        //获取当前用户
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        articleComment.setUserId(userId);
        //保存评论
        boolean save = articleCommentService.addComment(articleComment);

        return ResultUtils.success(save);
    }

    //TODO 评论列表
    @GetMapping("/list")
    public BaseResponse articleCommentList(Long articleId,Long userId){
        if (articleId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<List<ArticleCommentVO>> articleCommentList = articleCommentService.getArticleCommentList(articleId,userId);
        return ResultUtils.success(articleCommentList);
    }
}
