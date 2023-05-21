package com.shu.eshare.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ArticleCommentVO implements Serializable {

    /**
     * 评论id
     */
    private Long id;


    /**
     * 评论用户的昵称
     */
    private String nickname;

    /**
     * 评论用户头像
     */
    private String avatarUrl;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父级评论id
     */
    private Long parentId;

    /**
     * 顶级评论id
     */
    private Long rootId;

    /**
     * 所属用户
     */
    private Long userId;

    /**
     * 所属文章id
     */
    private Long articleId;


    /**
     * 评论时间
     */
    private Date createTime;


    /**
     * 点赞数量
     */
    private Long likeNum;

    /**
     * 点赞状态，是否点赞，0-未点赞，1-已点赞
     */
    private Integer likeStatus;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
