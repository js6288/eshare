package com.shu.eshare.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.model.domain.ResourceTags;
import com.shu.eshare.service.ResourceTagsService;
import com.shu.eshare.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resource/tags")
public class ResourceTagsController {

    @Autowired
    private ResourceTagsService resourceTagsService;

    /**
     * 获取所有默认资源标签(分类)（type == 0）
     * @return
     */
    @GetMapping("/default")
    public BaseResponse<List<ResourceTags>> defaultResourceTags(){
        QueryWrapper<ResourceTags> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","tag_name").eq("tag_type",0);

        List<ResourceTags> list = resourceTagsService.list(queryWrapper);
        return ResultUtils.success(list);
    }
}
