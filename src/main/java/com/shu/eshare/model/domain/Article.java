package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文章
 * @TableName article
 */
@TableName(value ="article")
@Data
public class Article implements Serializable {
    /**
     * 文章id
     */
    @TableId(type = IdType.AUTO)
    private Long articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章类型 0-普通用户文章 1-官方文章
     */
    private Integer type;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章封面图片：最多三张
     */
    private String orderImages;

    /**
     * 评论数
     */
    private Long commentsNum;

    /**
     * 点赞数
     */
    private Long likesNum;

    /**
     * 浏览数
     */
    private Long viewsNum;

    /**
     * 收藏数
     */
    private Long collectionsNum;

    /**
     * 发表时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 状态  0-正常  1-下架
     */
    private Integer status;

    /**
     * 所属用户id
     */
    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
