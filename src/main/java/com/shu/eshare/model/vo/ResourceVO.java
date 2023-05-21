package com.shu.eshare.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceVO {

    /**
     * 资源id
     */
    private Long resourceId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源描述
     */
    private String resourceDescription;

    /**
     * 专业名称
     */
    private String majorName;

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
     * 创建时间
     */
    private Date createTime;


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

    /**
     * 资源类型
     */
    private List<String> tagList;
}
