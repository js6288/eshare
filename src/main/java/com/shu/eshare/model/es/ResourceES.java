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
 * 存储在ElasticSearch中的Resource信息,用户展示在资源库首页，能被用户搜索到
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "resource_card_list")
public class ResourceES {
    /**
     * 唯一id
     */
    @Id
    private String cardId;

    @Field(type = FieldType.Long)
    private Long resourceId;

    /**
     * 所属用户
     */
    @Field(type = FieldType.Long)
    private Long userId;

    /**
     * 资源名称
     */
    @Field(type = FieldType.Text)
    private String resourceName;

//    /**
//     * 资源描述
//     */
//    @Field(type = FieldType.Text)
//    private String resourceDescription;

    /**
     * 专业名称
     */
    @Field(type = FieldType.Text)
    private String majorName;

    /**
     * 0-免费资源 1-付费资源
     */
    @Field(type = FieldType.Integer)
    private Integer type;

    /**
     * 所需点数
     */
    @Field(type = FieldType.Integer)
    private Integer requirePoint;

    /**
     * 默认为0 0-审核中 1-审核通过 2-审核不通过
     */
    @Field(type = FieldType.Integer)
    private Integer status;


    /**
     * 创建时间
     */
    @Field(type=FieldType.Date)
    private Date createTime;


    /**
     * 更新时间
     */
    @Field(type=FieldType.Date)
    private Date updateTime;

    /**
     * 收藏数量
     */
    @Field(type = FieldType.Long)
    private Long collectionNum;

    /**
     * 下载次数
     */
    @Field(type = FieldType.Long)
    private Long downloadNum;

    /**
     * 文件大小
     */
    @Field(type = FieldType.Double)
    private Double fileSize;

    /**
     * 文件类型（后缀）
     */
    @Field(type = FieldType.Text)
    private String fileType;

    /**
     * 资源类型
     */
    private List<String> tagList;
}
