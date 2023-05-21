package com.shu.eshare.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shu.eshare.model.domain.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shu.eshare.model.es.ArticleCardES;
import com.shu.eshare.model.request.ArticleAddBody;
import com.shu.eshare.model.request.ArticleEditBody;
import com.shu.eshare.model.response.ArticlesPage;

import java.io.IOException;
import java.util.List;

/**
* @author ljs
* @description 针对表【article(文章)】的数据库操作Service
* @createDate 2023-02-06 21:05:25
*/
public interface ArticleService extends IService<Article> {

    boolean articleAdd(ArticleAddBody articleAddBody);

    List<ArticleCardES> getAllArticleOnline(int curPage);

    ArticlesPage getSelfArticlesByPage(int curPage, Long userId);

    ArticleCardES getArticleEs(Long articleId);

    boolean editArticle(ArticleEditBody articleEditBody);

    void articleViewNumAdd(Long articleId);

    boolean like(Long articleId, int like);

    boolean isLike(Long articleId);

    boolean collect(Long articleId, int collect);

    boolean isCollect(Long articleId);

    boolean removeSelfArticle(Long articleId);

    ArticlesPage getSelfCollectArticlePage(Integer curPage);

    ArticlesPage searchArticle(String searchText,Integer curPage);

    List<ArticleCardES> getHotArticleList();
}
