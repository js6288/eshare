package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.UserTagRel;
import com.shu.eshare.service.UserTagRelService;
import com.shu.eshare.mapper.UserTagRelMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【user_tag_rel(用户标签关系表)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class UserTagRelServiceImpl extends ServiceImpl<UserTagRelMapper, UserTagRel>
    implements UserTagRelService{

}




