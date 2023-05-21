package com.shu.eshare.model.response;

import com.shu.eshare.model.es.ResourceES;
import lombok.Data;

import java.util.List;

@Data
public class ResourcePage {
    //元素总数
    private long total;

    //总页数
    private int pageNum;

    //当前页
    private int curPage;

    private List<ResourceES> resourceESList;
}
