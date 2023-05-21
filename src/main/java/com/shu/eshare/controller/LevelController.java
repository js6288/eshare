package com.shu.eshare.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.common.LevelExperience;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.Level;
import com.shu.eshare.model.vo.DailyRewardProgress;
import com.shu.eshare.service.LevelService;
import com.shu.eshare.utils.ResultUtils;
import com.shu.eshare.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/level")
public class LevelController {

    @Resource
    private LevelService levelService;

    /**
     * 获取当前用户等级
     */
    @GetMapping("/current")
    public BaseResponse<Level> getCurrentUserLevel(){
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        Level level = levelService.getOne(new QueryWrapper<Level>().eq("user_id", userId));
        return ResultUtils.success(level);
    }

    /**
     * 根据等级获取当前等级升级所需的最大经验
     */
    @GetMapping("/maxExperience")
    public BaseResponse<Integer> getMaxExperience(Integer level){
        if (level == null || level < LevelExperience.LV1 || level > LevelExperience.LV6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (level.equals(LevelExperience.LV6)){
            return ResultUtils.success(LevelExperience.LV5MAX);
        }
        return ResultUtils.success(LevelExperience.getMaxExperienceByLevel(level));
    }

    /**
     * 获取当前用户每日奖励的进度
     * @return
     */
    @GetMapping("/daily")
    public BaseResponse getDailyRewardMessage(){
        //获取当前用户信息
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        //获取和设置用户经验奖励进度
        DailyRewardProgress dailyRewardProgress = levelService.getOrSetDailyExperienceRewards(userId);

        return ResultUtils.success(dailyRewardProgress);
    }
}
