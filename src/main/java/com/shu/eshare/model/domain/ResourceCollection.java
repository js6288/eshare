package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源收藏表
 * @TableName resource_collection
 */
@TableName(value ="resource_collection")
@Data
public class ResourceCollection implements Serializable {
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
     * 用户id
     */
    private Long userId;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
