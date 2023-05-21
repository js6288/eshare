package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName oauth 第三方授权关系表
 */
@TableName(value ="oauth")
@Data
public class Oauth implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 第三方平台- QQ、GitHub等
     */
    private String authType;

    /**
     * 第三方平台用户的openId
     */
    private String openId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
