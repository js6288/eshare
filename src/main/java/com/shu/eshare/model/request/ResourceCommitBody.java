package com.shu.eshare.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ResourceCommitBody {


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
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 文件大小
     */
    private Double fileSize;

    /**
     * 文件类型（后缀）
     */
    private String fileType;

    /**
     * 用户自定义的资源标签
     */
    private List<String> userDefineTagList;

    /**
     * 系统资源标签id（资源分类）
     */
    private Long defaultResourceTagId;



}
