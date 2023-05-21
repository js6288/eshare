package com.shu.eshare.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.model.domain.Notice;
import com.shu.eshare.service.NoticeService;
import com.shu.eshare.utils.ResultUtils;
import com.shu.eshare.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 获当前用户的系统通知,分页查询,每页15条
     * @return
     */
    @GetMapping("/page")
    public BaseResponse getSelfNoticePage(Integer curPage){
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        Page<Notice> noticePage = noticeService.getNoticePageByUserId(userId,curPage);

        return ResultUtils.success(noticePage);
    }
}
