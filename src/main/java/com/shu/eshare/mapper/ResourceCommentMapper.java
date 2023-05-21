package com.shu.eshare.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shu.eshare.model.domain.ResourceComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shu.eshare.model.vo.ResourceCommentVO;
import org.apache.ibatis.annotations.Param;

/**
* @author ljs
* @description 针对表【resource_comment(资源评论)】的数据库操作Mapper
* @createDate 2023-02-06 21:05:26
* @Entity com.shu.eshare.model.domain.ResourceComment
*/
public interface ResourceCommentMapper extends BaseMapper<ResourceComment> {

    IPage<ResourceCommentVO> findResourceCommentVOByResourceId(Page<ResourceCommentVO> page,@Param("resourceId") Long resourceId);
}




