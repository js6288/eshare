package com.shu.eshare.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResourceCommentAdd implements Serializable {

    /**
     * 资源编号
     */
    private Long resourceId;
    /**
     * 评论内容
     */
    private String content;
}
