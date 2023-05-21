package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.Notice;
import com.shu.eshare.service.NoticeService;
import com.shu.eshare.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【notice(系统消息)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
    implements NoticeService{

    @Override
    public Page<Notice> getNoticePageByUserId(Long userId,Integer curPage) {
        Page<Notice> pageRequest = new Page<>(curPage,15);
        QueryWrapper<Notice> noticeQueryWrapper = new QueryWrapper<Notice>()
                .eq("user_id", userId).orderByDesc("create_time");
        return this.baseMapper.selectPage(pageRequest, noticeQueryWrapper);
    }
}




