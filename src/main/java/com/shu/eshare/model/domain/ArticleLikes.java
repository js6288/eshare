package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文章点赞表
 * @TableName article_likes
 */
@TableName(value ="article_likes")
@Data
public class ArticleLikes implements Serializable {
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
     * 用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
