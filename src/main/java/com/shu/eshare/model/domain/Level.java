package com.shu.eshare.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 等级表
 * @TableName level
 */
@TableName(value ="level")
@Data
public class Level implements Serializable {
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
     * 用户等级 1-6
     */
    private Integer level;

    /**
     * 经验
     */
    private Integer experience;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
