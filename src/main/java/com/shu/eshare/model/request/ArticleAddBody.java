package com.shu.eshare.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.shu.eshare.model.domain.ArticleTags;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ArticleAddBody implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 文章标题
     */
    private String articleTitle;


    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章封面图片：最多三张,json数组存储
     */
    private List<String> orderImages;

    /**
     * 用户自定义标签
     */
    private List<String> userTagList;

    /**
     * 默认标签id列表
     */
    private List<Long> systemTagIdList;

}
