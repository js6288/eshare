package com.shu.eshare.model.request;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateUserBody {
    private String nickname;

    private String personalSignature;

    private String school;

    private Date birthday;

    /**
     * 0-男 1女 2保密 默认为2
     */
    private Integer gender;

}
