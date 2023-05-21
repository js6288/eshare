package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.common.Constant;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.common.LevelExperience;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.Level;
import com.shu.eshare.model.vo.DailyRewardProgress;
import com.shu.eshare.service.LevelService;
import com.shu.eshare.mapper.LevelMapper;
import com.shu.eshare.utils.RedisCache;
import com.shu.eshare.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
* @author ljs
* @description 针对表【level(等级表)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:25
* 用户等级分为Lv1至Lv6,
 * 用户初始等级为Lv1、0经验，
 * 每个等级升级所需的经验都不同，各个等级所需经验如下所示：
 * lv1 200经验、lv2 600经验、lv3 1000经验、lv4 2800、经验、lv5 4800经验，升级到lv6后经验不在增加。
*/
@Service
@Slf4j
public class LevelServiceImpl extends ServiceImpl<LevelMapper, Level>
    implements LevelService{

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 增加经验和提升等级
     *
     * @param incrNum 增加的经验数量
     */
    @Override
    @Transactional
    public void incrExperience(Integer incrNum) {
        if (incrNum<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"增加的经验必须大于0");
        }
        // 计算出需要提升的经验和等级: 如果提升的经验超过当前等级所需经验，则需要升级，升级前保留剩余经验
        // 1.查询当前用户等级
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        Level level = getOne(new QueryWrapper<Level>().eq("user_id", userId));

        if (level == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"无法获取当前用户等级");
        }

        int curLevel = level.getLevel();
        int curExperience = level.getExperience();

        /*用户升级算法*/
        // 如果用户为六级则不增加经验
        if (LevelExperience.LV6.equals(curLevel)){
            return;
        }
        do {
            // 获取当前等级所需最大经验
            int maxExperience = LevelExperience.getMaxExperienceByLevel(curLevel);
            // (curExperience+incrNum)/maxExperience == 0 等级不增加
            // (curExperience+incrNum)/maxExperience > 0 等级增加  余留的经验rest=(curExperience+incrNum)-maxExperience
            if ((curExperience + incrNum) / maxExperience == 0) {//如果经验不溢出
                // 等级不提升，增加经验
                curExperience += incrNum;
            } else if ((curExperience + incrNum) / maxExperience > 0) {//如果经验溢出
                //如果增加的经验够升级到6级，则经验值设置为5级的最大值
                if (curLevel >= 5){
                    curLevel = LevelExperience.LV6;
                    curExperience = LevelExperience.LV5MAX;
                }else {
                    curLevel = curLevel + 1;
                    curExperience = (curExperience + incrNum) - maxExperience;
                }
            }
            //如果剩余经验值仍大于当前等级所需最大经验值，则循环继续
        }while (curExperience > LevelExperience.getMaxExperienceByLevel(curLevel));

        //操作数据库更新用户等级
        update(new UpdateWrapper<Level>()
                .eq("id",level.getId())
                .set("level",curLevel)
                .set("experience",curExperience));
    }

    /**
     * 点赞文章 0/5 +2 EXP
     * 收藏文章 0/5 +2 EXP
     * 发表文章 0/10  +10 EXP
     * 上传资源 0/10  +10 EXP
     * 文章评论区发表评论 0/5 +10 EXP
     * @param userId 用户id
     * @return
     */
    @Override
    public DailyRewardProgress getOrSetDailyExperienceRewards(Long userId) {
        //查询redis中的key是否存在
        //拼接key
        String key = Constant.APPLICATION_NAME+Constant.EXPERIENCE+userId;
        DailyRewardProgress cacheObject = redisCache.getCacheObject(key);
        if (cacheObject == null){//如果缓存不存在，则设置一个
            cacheObject = new DailyRewardProgress(0,0,0,0,0);
            //设置过期时间为第二天0点
            redisCache.setCacheObject(key,cacheObject,getRemainSecondsOneDay(new Date()), TimeUnit.SECONDS);
        }

        //如果存在则返回
        return cacheObject;
    }

    public static Integer getRemainSecondsOneDay(Date currentDate) {
        //使用plusDays加传入的时间加1天，将时分秒设置成0
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                        ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        //使用ChronoUnit.SECONDS.between方法，传入两个LocalDateTime对象即可得到相差的秒数
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
        return (int) seconds;
    }


    /**
     * 点赞文章 0/5 +2 EXP
     * 收藏文章 0/5 +2 EXP
     * 发表文章 0/10  +10 EXP
     * 上传资源 0/10  +10 EXP
     * 文章评论区发表评论 0/5 +10 EXP
     * 每日奖励增加经验，限制次数,通过异步的方式
     */
    @Override
//    @Async("customAsyncThreadPool")
    @Transactional
    public void incrExperienceLimitDaily(int incrNum, String field){

        //获取key
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        DailyRewardProgress cacheObject = getOrSetDailyExperienceRewards(userId);
        String key = Constant.APPLICATION_NAME+Constant.EXPERIENCE+userId;

        //分布式锁:加锁的粒度为userId
        RLock lock = redissonClient.getLock("incrExp:"+userId);
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    log.info("get lock:"+Thread.currentThread().getId());
                    //确认更新的是哪个字段
                    if (Constant.ARTICLE_LIKE.equals(field)) {//点赞
                        Integer articleLikeCount = cacheObject.getArticleLikeCount();
                        if (articleLikeCount < 5) {
                            cacheObject.setArticleLikeCount(articleLikeCount + 1);
                        }
                    } else if (Constant.ARTICLE_COLLECTION.equals(field)) {//收藏文章
                        Integer collectionCount = cacheObject.getCollectionCount();
                        if (collectionCount < 5) {
                            cacheObject.setCollectionCount(collectionCount + 1);
                        }
                    } else if (Constant.ARTICLE_PUBLISH.equals(field)) {//发表文章
                        Integer articlePolishCount = cacheObject.getArticlePolishCount();
                        if (articlePolishCount < 10) {
                            cacheObject.setArticlePolishCount(articlePolishCount + 1);
                        }
                    } else if (Constant.UPLOAD.equals(field)) {//上传文件
                        Integer uploadCount = cacheObject.getUploadCount();
                        if (uploadCount < 10) {
                            cacheObject.setUploadCount(uploadCount + 1);
                        }
                    } else if (Constant.ARTICLE_COMMENT_ADD.equals(field)){//文章评论发表
                        Integer articleCommentAddCount = cacheObject.getArticleCommentAddCount();
                        if (articleCommentAddCount < 5){
                            cacheObject.setArticleCommentAddCount(articleCommentAddCount+1);
                        }
                    } else {
                        log.error("没有该规则");
                        return;
                    }
                    //更新redis
                    redisCache.setCacheObject(key, cacheObject, getRemainSecondsOneDay(new Date()), TimeUnit.SECONDS);
                    //添加经验
                    incrExperience(incrNum);
                    return;
                }
            }
        }catch (InterruptedException e){
            log.error("incr Experience Error",e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                log.info("unlock:"+Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}




