package com.shu.eshare.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shu.eshare.model.domain.ResourceComment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shu.eshare.model.vo.ResourceCommentVO;
import org.springframework.transaction.annotation.Transactional;

/**
* @author ljs
* @description 针对表【resource_comment(资源评论)】的数据库操作Service
* @createDate 2023-02-06 21:05:26
*/
public interface ResourceCommentService extends IService<ResourceComment> {

    @Transactional
    boolean publishComment(Long resourceId, String content);

    Page<ResourceCommentVO> getResourceCommentPage(Long resourceId, int curPage);
}
