package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
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
     * 密码
     */
    private String password;

    /**
     * 个性签名
     */
    private String personalSignature;

    /**
     * ip地址
     */
    private Long ipAddress;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 学校
     */
    private String school;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 生日
     */
    private Date birthday;

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
     * 积分
     */
    private Long accumulatePoints;

    /**
     * 账号状态是否封禁 默认为0-正常 1-封禁
     */
    private Integer status;

    /**
     * 逻辑删除 默认为0
     */
    private Object isDelete;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
