package com.shu.eshare.mapper;

import com.shu.eshare.model.domain.ArticleComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shu.eshare.model.vo.ArticleCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ljs
* @description 针对表【article_comment(文章评论)】的数据库操作Mapper
* @createDate 2023-02-06 21:05:25
* @Entity com.shu.eshare.model.domain.ArticleComment
*/
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {


    List<ArticleCommentVO> selectCommentVOByIds(@Param("commentIds") List<Long> commentIds,@Param("loginUserId") Long loginUserId);
}




