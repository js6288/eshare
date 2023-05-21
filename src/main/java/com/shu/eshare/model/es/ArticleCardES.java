package com.shu.eshare.model.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * 文章保存在ElasticSearch的实体，主要用于展示在社区首页，能被搜索时到
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "article_card_list")
public class ArticleCardES {

    /**
     * 唯一id
     */
    @Id
    private String cardId;

    /**
     * 文章id
     */
    @Field(type = FieldType.Long)
    private Long articleId;

    /**
     * 文章标题
     */
    @Field(type = FieldType.Text)
    private String articleTitle;


    /**
     * 文章内容(简化)
     */
    @Field(type = FieldType.Text)
    private String content;

    /**
     * 文章类型 0-普通用户文章 1-官方文章
     */
    @Field(type = FieldType.Integer)
    private Integer type;

    /**
     * 文章封面图片：最多三张
     */
    @Field(type = FieldType.Auto)
    private List<String> orderImages;

    /**
     * 评论数
     */
    @Field(type = FieldType.Long)
    private Long commentsNum;

    /**
     * 点赞数
     */
    @Field(type = FieldType.Long)
    private Long likesNum;

    /**
     * 浏览数
     */
    @Field(type = FieldType.Long)
    private Long viewsNum;

    /**
     * 收藏数
     */
    @Field(type = FieldType.Long)
    private Long collectionsNum;

    /**
     * 发表时间
     */
    @Field(type=FieldType.Date)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(type=FieldType.Date)
    private Date updateTime;

    /**
     * 用户id
     */
    @Field(type = FieldType.Long)
    private Long userId;

    /**
     * 用户昵称
     */
    @Field(type = FieldType.Text)
    private String nickname;

    /**
     * 用户头像
     */
    @Field(type = FieldType.Text)
    private String avatar;

    /**
     * 等级
     */
    @Field(type = FieldType.Integer)
    private Integer level;

    /**
     * 标签列表
     */
    @Field(type = FieldType.Auto)
    private List<String> tagList;

}
