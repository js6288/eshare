package com.shu.eshare.common;

public class Constant {

    /**
     * 应用名称key
     */
    public static final String APPLICATION_NAME="eshare:";

    /**
     * 手机验证码 redis key
     */
    public static final String PHONE_CODE_KEY = "phone_codes:";

    /**
     * redis 限流
     */
    public static final String RATE_LIMIT_KEY = "rateLimit:";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 用户默认头像
     */
    public static final String AVATAR_URL = "http://img-md-js.linjsblog.top/img/202303031522110.png";

    /**
     * 用户名前缀
     */
    public static final String USERNAME_PREFIX = "user_";

    /**
     * 用户昵称前缀
     */
    public static final String NICKNAME_PREFIX = "普通用户_";

    /**
     * 用户默认签名
     */
    public static final String PERSONAL_SIGNATURE = "系统原装签名，送给每一个小可爱~";

    public static final Integer RESOURCE_DOWNLOAD_MAX_POINT = 200;

    /**
     * 第三方平台
     */
    public static final String THIRD_PARTY_GITHUB = "GITHUB";
    public static final String THIRD_PARTY_QQ = "QQ";

    /**
     * 经验key
     */
    public static final String EXPERIENCE = "exp:";

    /**
     * 点赞文章
     */
    public static final String ARTICLE_LIKE = "article_like";
    public static final String ARTICLE_COLLECTION = "article_collection";
    public static final String ARTICLE_PUBLISH = "article_publish";
    public static final String UPLOAD = "upload";
    public static final String ARTICLE_COMMENT_ADD = "article_comment_add";






}
