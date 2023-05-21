package com.shu.eshare.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 每日奖励进度
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyRewardProgress implements Serializable {
    /**
     * 每日点赞次数
     */
    private Integer articleLikeCount;

    /**
     * 每日收藏文章次数
     */
    private Integer collectionCount;

    /**
     * 发表文章次数
     */
    private Integer articlePolishCount;

    /**
     * 上传资源次数
     */
    private Integer uploadCount;

    /**
     * 文章评论区发表评论
     */
    private Integer articleCommentAddCount;



}
