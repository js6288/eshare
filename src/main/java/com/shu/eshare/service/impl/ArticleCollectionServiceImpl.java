package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.ArticleCollection;
import com.shu.eshare.service.ArticleCollectionService;
import com.shu.eshare.mapper.ArticleCollectionMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【article_collection(文章收藏表)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:25
*/
@Service
public class ArticleCollectionServiceImpl extends ServiceImpl<ArticleCollectionMapper, ArticleCollection>
    implements ArticleCollectionService{

}




