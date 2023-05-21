package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.ResourceTags;
import com.shu.eshare.service.ResourceTagsService;
import com.shu.eshare.mapper.ResourceTagsMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【resource_tags(资源标签)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class ResourceTagsServiceImpl extends ServiceImpl<ResourceTagsMapper, ResourceTags>
    implements ResourceTagsService{

}




