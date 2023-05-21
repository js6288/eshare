package com.shu.eshare.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.Level;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.request.ResetPasswordBody;
import com.shu.eshare.model.request.UpdateUserBody;
import com.shu.eshare.model.vo.UserVO;
import com.shu.eshare.service.LevelService;
import com.shu.eshare.service.UserService;
import com.shu.eshare.utils.ResultUtils;
import com.shu.eshare.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private LevelService levelService;

    /**
     * 获取当前用户详细信息
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(){
        User user = SecurityUtils.getLoginUser().getUser();
        //弱脱敏
        SecurityUtils.securityUser(user);
        return ResultUtils.success(user);
    }

    /**
     * 根据用户id获取用户基本信息,经过严格脱敏，可以免授权访问
     */
    @GetMapping("/safety/{userId}")
    public BaseResponse<UserVO> getUserById(@PathVariable("userId") Integer userId){
        User user = userService.getById(userId);
        if (user == null){
            return ResultUtils.error(ErrorCode.USER_NOT_EXIST);
        }
        //强脱敏
        UserVO userVO = SecurityUtils.securityUserStrict(user);
        //查询用户等级信息
        Level level = levelService.getOne(new QueryWrapper<Level>().select("level").eq("user_id", userId));
        userVO.setLevel(level.getLevel());
        return ResultUtils.success(userVO);
    }

    /**
     * 上传用户头像
     * @param avatarUrl 头像地址
     * @return
     */
    @PutMapping("/avatar")
    public BaseResponse updateUserAvatar(String avatarUrl){
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        UpdateWrapper<User> wrapper = userUpdateWrapper.set("avatar_url", avatarUrl).eq("user_id", userId);
        boolean update = userService.update(wrapper);
        if (update){
            return ResultUtils.success("更新成功");
        }else {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"更新失败");
        }
    }

    /**
     * 重置密码
     * @param resetPasswordBody 新的密码，手机号，验证码，验证码uuid
     * @return
     */
    @PutMapping("/password")
    public BaseResponse<Boolean> ResetPassword(@RequestBody ResetPasswordBody resetPasswordBody){
        if (resetPasswordBody == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //校验字段
        if (StringUtils.isAnyBlank(
                resetPasswordBody.getCode(),
                resetPasswordBody.getNewPassword(),
                resetPasswordBody.getPhone(),
                resetPasswordBody.getUuid()
        )){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //修改密码逻辑
        boolean success = userService.resetPassword(resetPasswordBody);

        return ResultUtils.success(success);

    }

    /**
     * 绑定手机号
     * @param phone 手机号
     * @param code 验证码
     * @param uuid uuid
     * @return true 更新手机号成功，false 更新失败
     */
    @PutMapping("/phone")
    public BaseResponse<Boolean> bindingPhone(String phone,String code,String uuid){
        if (StringUtils.isAnyBlank(phone,code,uuid)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //修改用户手机号逻辑
        boolean success = userService.bindingPhone(phone,code,uuid);

        return ResultUtils.success(success);
    }

    /**
     * 更新用户资料接口
     * @param updateUserBody 更新用户昵称、个性签名，性别，学校、生日
     * @return
     */
    @PutMapping("/update/self")
    public BaseResponse updateUserDetail(@RequestBody UpdateUserBody updateUserBody){
        if (updateUserBody == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isAnyBlank(
                updateUserBody.getNickname(),
                updateUserBody.getPersonalSignature()
        )){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserVO userVO = userService.updateUser(updateUserBody);
        if (userVO!=null){
            //userVO返回给前端，前端本地更新用户信息
            return ResultUtils.success(userVO);
        }else {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"更新用户信息失败,请联系系统管理员或稍后重试");
        }
    }

    /**
     * 获取当前用户积分
     * @return
     */
    @GetMapping("/accumulatePoints")
    public BaseResponse getCurrentUserPoint(){
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        Long accumulatePoints = userService.getOne(new QueryWrapper<User>()
                .select("accumulate_points").eq("user_id", userId)).getAccumulatePoints();

        return ResultUtils.success(accumulatePoints);
    }
}
