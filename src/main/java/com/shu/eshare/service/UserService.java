package com.shu.eshare.service;

import com.shu.eshare.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shu.eshare.model.request.ResetPasswordBody;
import com.shu.eshare.model.request.UpdateUserBody;
import com.shu.eshare.model.vo.UserVO;

/**
* @author ljs
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2023-02-06 21:05:26
*/
public interface UserService extends IService<User> {

    boolean resetPassword(ResetPasswordBody resetPasswordBody);

    boolean bindingPhone(String phone, String code, String uuid);

    UserVO updateUser(UpdateUserBody updateUserBody);
}
