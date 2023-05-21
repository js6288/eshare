package com.shu.eshare.model.request;

import lombok.Data;

@Data
public class ResourceSearchBody {

    private String searchText;

    private String majorName;

    private Integer type;

    private String fileType;

    private String tag;

    private Integer curPage;
}
