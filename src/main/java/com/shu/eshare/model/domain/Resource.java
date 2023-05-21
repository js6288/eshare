package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源
 * @TableName resource
 */
@TableName(value ="resource")
@Data
public class Resource implements Serializable {
    /**
     * 资源id
     */
    @TableId(type = IdType.AUTO)
    private Long resourceId;

    /**
     * 所属用户
     */
    private Long userId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源描述
     */
    private String resourceDescription;

    /**
     * 专业id
     */
    private Integer majorId;

    /**
     * 0-免费资源 1-付费资源
     */
    private Integer type;

    /**
     * 所需点数
     */
    private Integer requirePoint;

    /**
     * 默认为0 0-审核中 1-审核通过 2-审核不通过
     */
    private Integer status;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 收藏数量
     */
    private Long collectionNum;

    /**
     * 下载次数
     */
    private Long downloadNum;

    /**
     * 文件大小
     */
    private Double fileSize;

    /**
     * 文件类型（后缀）
     */
    private String fileType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
