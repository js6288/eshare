package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户标签关系表
 * @TableName user_tag_rel
 */
@TableName(value ="user_tag_rel")
@Data
public class UserTagRel implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户标签id
     */
    private Long userTagId;

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
