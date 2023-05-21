package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.request.ResetPasswordBody;
import com.shu.eshare.model.request.UpdateUserBody;
import com.shu.eshare.model.vo.UserVO;
import com.shu.eshare.service.SmsService;
import com.shu.eshare.service.UserService;
import com.shu.eshare.mapper.UserMapper;
import com.shu.eshare.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author ljs
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    @Resource
    private SmsService smsService;

    @Override
    public boolean resetPassword(ResetPasswordBody resetPasswordBody) {

        //密码长度为8-16位
        int passwordLength = resetPasswordBody.getNewPassword().length();
        if (passwordLength < 8 || passwordLength > 16){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //查询用户手机号与用户身份是否匹配
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        User user = userMapper.selectOne(new QueryWrapper<User>().select("phone").eq("user_id", userId));
        //如果用户提交的手机号和数据库中查询的手机号不相同，则说明用户没有绑定手机号或者手机号与用户绑定的手机号不相同
        if (!resetPasswordBody.getPhone().equals(user.getPhone())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户没有绑定手机号或者手机号与用户绑定的手机号不相同");
        }
        //校验手机号
        boolean isVerify = smsService.verifyMessage(resetPasswordBody.getCode(), resetPasswordBody.getUuid(), resetPasswordBody.getPhone());
        if (!isVerify){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码错误或已过期");
        }

        //加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(resetPasswordBody.getNewPassword());

        //修改数据库
        boolean update = this.update(new UpdateWrapper<User>().set("password", encodePassword).eq("user_id", userId));
        if (!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败，请稍后重试");
        }
        return true;
    }

    @Override
    public boolean bindingPhone(String phone, String code, String uuid) {

        //校验手机号是否已经绑定或被其他用户绑定
        User userByPhone = this.getOne(new QueryWrapper<User>().eq("phone", phone).select("user_id"));
        if (userByPhone != null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该手机号已被注册");
        }

        //校验验证码
        boolean verifyMessage = smsService.verifyMessage(code, uuid, phone);
        if (!verifyMessage){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码错误或已过期");
        }

        //获取当前用户id
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        //更新数据库
        return this.update(new UpdateWrapper<User>().set("phone", phone).eq("user_id", userId));
    }

    @Override
    public UserVO updateUser(UpdateUserBody updateUserBody) {
        //昵称长度
        int nickNameLength = updateUserBody.getNickname().length();
        if (nickNameLength<1 || nickNameLength>30){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //个性签名长度限制
        int personalSignatureLength = updateUserBody.getPersonalSignature().length();
        if (personalSignatureLength>60){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断性别字段是否合法只能为0，1，2
        if (updateUserBody.getGender() != 0 && updateUserBody.getGender() != 1 && updateUserBody.getGender() != 2){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //更新数据库
        User user = new User();
        //获取当前用户，
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        BeanUtils.copyProperties(updateUserBody,user);
        user.setUserId(userId);
        boolean b = this.updateById(user);
        if (!b){
            //更新失败
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"更新用户信息失败");
        }

        //获取最新的用户信息生成UserVO返回给前端
        User byId = this.getById(userId);

        //脱敏
        return SecurityUtils.securityUserStrict(byId);
    }
}




