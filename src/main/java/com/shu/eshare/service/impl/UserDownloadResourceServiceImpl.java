package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.UserDownloadResource;
import com.shu.eshare.service.UserDownloadResourceService;
import com.shu.eshare.mapper.UserDownloadResourceMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【user_download_resource(用户下载与资源关系表，用于判断当前用户是否下载该资源)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class UserDownloadResourceServiceImpl extends ServiceImpl<UserDownloadResourceMapper, UserDownloadResource>
    implements UserDownloadResourceService{

}




