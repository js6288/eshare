package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.ResourceCollection;
import com.shu.eshare.service.ResourceCollectionService;
import com.shu.eshare.mapper.ResourceCollectionMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【resource_collection(资源收藏表)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class ResourceCollectionServiceImpl extends ServiceImpl<ResourceCollectionMapper, ResourceCollection>
    implements ResourceCollectionService{

}




