package com.shu.eshare.model.request;

import lombok.Data;

/**
 * todo 文章编辑提交实体,目前只支持修改文章标题和文章内容，其他功能如修改标签、封面图片后续在完善
 */
@Data
public class ArticleEditBody {


    private Long articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章内容
     */
    private String content;
}
