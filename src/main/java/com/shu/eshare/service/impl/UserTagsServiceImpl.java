package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.service.UserTagsService;
import com.shu.eshare.model.domain.UserTags;
import com.shu.eshare.mapper.UserTagsMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【user_tags(用户标签)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class UserTagsServiceImpl extends ServiceImpl<UserTagsMapper, UserTags>
    implements UserTagsService {

}




