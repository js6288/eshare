package com.shu.eshare.service;

import com.shu.eshare.model.domain.Level;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shu.eshare.model.vo.DailyRewardProgress;

/**
* @author ljs
* @description 针对表【level(等级表)】的数据库操作Service
* @createDate 2023-02-06 21:05:25
*/
public interface LevelService extends IService<Level> {

    /**
     * 增加经验
     */
    void incrExperience(Integer incrNum);

    DailyRewardProgress getOrSetDailyExperienceRewards(Long userId);

    void incrExperienceLimitDaily(int incrNum, String field);
}
