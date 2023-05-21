package com.shu.eshare.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ResourceCommentVO implements Serializable {

    private String avatarUrl;

    private String nickname;

    private Date createTime;

    private String content;
}
