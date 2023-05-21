package com.shu.eshare.model.response;

import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserResponse implements Serializable {
    private UserVO userVO;
    private Token token;
}
