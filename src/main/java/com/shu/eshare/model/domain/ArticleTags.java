package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文章标签
 * @TableName article_tags
 */
@TableName(value ="article_tags")
@Data
public class ArticleTags implements Serializable {
    /**
     * 标签id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 标签类型 0-系统标签 1-用户自定义标签
     */
    private Integer tagType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 所属用户id
     */
    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
