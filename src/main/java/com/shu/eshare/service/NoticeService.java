package com.shu.eshare.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shu.eshare.model.domain.Notice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author ljs
* @description 针对表【notice(系统消息)】的数据库操作Service
* @createDate 2023-02-06 21:05:26
*/
public interface NoticeService extends IService<Notice> {

    Page<Notice> getNoticePageByUserId(Long userId,Integer curPage);
}
