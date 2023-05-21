package com.shu.eshare.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.request.ResourceCommentAdd;
import com.shu.eshare.model.vo.ResourceCommentVO;
import com.shu.eshare.service.ResourceCommentService;
import com.shu.eshare.utils.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resource/comment")
public class ResourceCommentController {

    @Autowired
    private ResourceCommentService resourceCommentService;

    /***
     * 在资源详情页发表评论
     * @param resourceCommentAdd 包含资源id和
     * @return
     */
    @PostMapping("/publish")
    public BaseResponse addResourceComment(@RequestBody ResourceCommentAdd resourceCommentAdd){
        if (resourceCommentAdd == null || resourceCommentAdd.getResourceId() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(resourceCommentAdd.getContent())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"评论内容不能为空");
        }
        if (resourceCommentAdd.getContent().length()>150){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"评论内容不能超过150个字符");
        }

        boolean commit = resourceCommentService.publishComment(resourceCommentAdd.getResourceId(), resourceCommentAdd.getContent());

        return ResultUtils.success(commit);
    }

    /**
     * 查询资源评论分页
     * @param resourceId 根据资源编号查询
     * @return
     */
    @GetMapping("/page")
    public BaseResponse getResourceCommentList(Long resourceId,int curPage){
        if (resourceId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<ResourceCommentVO> resourceCommentVOPage = resourceCommentService.getResourceCommentPage(resourceId,curPage);

        return ResultUtils.success(resourceCommentVOPage);
    }
}
