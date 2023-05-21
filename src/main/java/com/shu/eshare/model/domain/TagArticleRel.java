package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 标签文章关系表
 * @TableName tag_article_rel
 */
@TableName(value ="tag_article_rel")
@Data
public class TagArticleRel implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 文章标签id
     */
    private Long articleTagId;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
