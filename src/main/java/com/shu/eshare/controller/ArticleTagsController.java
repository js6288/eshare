package com.shu.eshare.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.model.domain.ArticleTags;
import com.shu.eshare.service.ArticleTagsService;
import com.shu.eshare.utils.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/article/tags")
public class ArticleTagsController {

    @Resource
    private ArticleTagsService articleTagsService;

    /**
     * 获取所有系统标签
     */
    @GetMapping("/default")
    public BaseResponse<List<ArticleTags>> defaultArticleTag(){

        QueryWrapper<ArticleTags> queryWrapper = new QueryWrapper<ArticleTags>()
                .select("id", "tag_name").eq("tag_type", 0);
        List<ArticleTags> list = articleTagsService.list(queryWrapper);

        return ResultUtils.success(list);
    }
}
