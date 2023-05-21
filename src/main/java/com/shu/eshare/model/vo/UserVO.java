package com.shu.eshare.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息包含等级，脱敏的
 */
@Data
public class UserVO implements Serializable {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 个性签名
     */
    private String personalSignature;


    /**
     * ip地址：String 类型，如广东
     */
    private String ipAddress;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 学校
     */
    private String school;

    /**
     * 0-男 1女 2保密 默认为2
     */
    private Integer gender;

    /**
     * 用户角色 0-普通用户 1-管理员
     */
    private Integer userRole;

    /**
     * 阅读数
     */
    private Long readNum;

    /**
     * 获赞数
     */
    private Long likesNum;

    /**
     * 账号状态是否封禁 默认为0-正常 1-封禁
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 等级
     */
    private Integer level;
}
