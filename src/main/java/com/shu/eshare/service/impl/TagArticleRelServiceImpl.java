package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.model.domain.TagArticleRel;
import com.shu.eshare.service.TagArticleRelService;
import com.shu.eshare.mapper.TagArticleRelMapper;
import org.springframework.stereotype.Service;

/**
* @author ljs
* @description 针对表【tag_article_rel(标签文章关系表)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class TagArticleRelServiceImpl extends ServiceImpl<TagArticleRelMapper, TagArticleRel>
    implements TagArticleRelService{

}




