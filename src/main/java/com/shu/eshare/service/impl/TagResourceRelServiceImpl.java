package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.TagResourceRel;
import com.shu.eshare.service.TagResourceRelService;
import com.shu.eshare.mapper.TagResourceRelMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【tag_resource_rel(标签与资源关系表)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class TagResourceRelServiceImpl extends ServiceImpl<TagResourceRelMapper, TagResourceRel>
    implements TagResourceRelService{

}




