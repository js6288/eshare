package com.shu.eshare.model.response;

import com.shu.eshare.model.es.ArticleCardES;
import lombok.Data;

import java.util.List;

@Data
public class ArticlesPage {
    //元素总数
    private long total;

    //总页数
    private int pageNum;

    //当前页
    private int curPage;

    //当前页列表
    private List<ArticleCardES> articleCardESList;
}
