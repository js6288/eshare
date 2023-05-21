package com.shu.eshare.job;

import com.shu.eshare.esrepository.ArticleCardESRepository;
import com.shu.eshare.model.es.ArticleCardES;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PreCacheJob {

    @Autowired
    private ArticleCardESRepository articleCardESRepository;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 查询本月内分数最高的前20位，保存在Redis SortedSet中，每隔10分钟更新一次数据
     * 分数规则=用户点击量*0.3+点赞数*0.4+评论数*0.5+收藏数*0.4
     */
    @Scheduled(cron = "0 0/10 * * * *")//每10分钟执行一次
    public void doCacheHot(){

        //分布式锁解决集群定时任务重复执行的问题
        RLock lock = redissonClient.getLock("doCacheHot");
        try{
            if (lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
                log.info("lock:"+ Thread.currentThread().getId());
                System.out.println("Spring Schedule 定时任务每10分钟执行1次");

                List<ArticleCardES> list = articleCardESRepository.findByCreateTimeThisWeek();


                System.out.println("定时任务更新缓存");
                RScoredSortedSet<Long> scoredSortedSet = redissonClient.getScoredSortedSet("eshare:hot:article");

                for (ArticleCardES articleCardES : list) {
                    scoredSortedSet.add(computeScore(articleCardES),articleCardES.getArticleId());
                }
            }
        }catch (InterruptedException e) {
            log.error("doCacheHot Error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("unlock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }


//        RScoredSortedSet<Long> scoredSortedSet2 = redissonClient.getScoredSortedSet("eshare:hot:article");
//
//        Collection<Long> longs = scoredSortedSet2.valueRangeReversed(0, 9);
//
//        for (Long aLong : longs) {
//            System.out.println(aLong+":"+scoredSortedSet2.getScore(aLong));
//        }
    }

    private Double computeScore(ArticleCardES articleCardES){
        Long viewsNum = articleCardES.getViewsNum();
        Long likesNum = articleCardES.getLikesNum();
        Long commentsNum = articleCardES.getCommentsNum();
        Long collectionsNum = articleCardES.getCollectionsNum();

        double score = viewsNum*0.3+likesNum*0.4+commentsNum*0.5+collectionsNum*0.4;
        // 四舍五入保留一位小数
        score = (double) Math.round(score * 10) / 10;

        return score;
    }
}
