package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 标签与资源关系表
 * @TableName tag_resource_rel
 */
@TableName(value ="tag_resource_rel")
@Data
public class TagResourceRel implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源id
     */
    private Long resourceId;

    /**
     * 资源标签id
     */
    private Long resourceTagId;

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
