package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源标签
 * @TableName resource_tags
 */
@TableName(value ="resource_tags")
@Data
public class ResourceTags implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 0-分类（系统标签） 1-用户自定义标签
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
