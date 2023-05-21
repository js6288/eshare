package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.ArticleLikes;
import com.shu.eshare.service.ArticleLikesService;
import com.shu.eshare.mapper.ArticleLikesMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【article_likes(文章点赞表)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:25
*/
@Service
public class ArticleLikesServiceImpl extends ServiceImpl<ArticleLikesMapper, ArticleLikes>
    implements ArticleLikesService{

}




