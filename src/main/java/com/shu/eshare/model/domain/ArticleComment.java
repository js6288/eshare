package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文章评论
 * @TableName article_comment
 */
@TableName(value ="article_comment")
@Data
public class ArticleComment implements Serializable {
    /**
     * 评论id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父级评论
     */
    private Long parentId;

    /**
     * 顶级评论
     */
    private Long rootId;

    /**
     * 所属用户
     */
    private Long userId;

    /**
     * 所属文章
     */
    private Long articleId;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 评论时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 点赞数量
     */
    private Long likeNum;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
