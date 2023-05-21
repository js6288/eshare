package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.ArticleTags;
import com.shu.eshare.service.ArticleTagsService;
import com.shu.eshare.mapper.ArticleTagsMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【article_tags(文章标签)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:25
*/
@Service
public class ArticleTagsServiceImpl extends ServiceImpl<ArticleTagsMapper, ArticleTags>
    implements ArticleTagsService{

}




