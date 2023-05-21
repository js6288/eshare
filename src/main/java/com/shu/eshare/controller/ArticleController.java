package com.shu.eshare.controller;

import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.common.annotation.RateLimit;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.es.ArticleCardES;
import com.shu.eshare.model.request.ArticleAddBody;
import com.shu.eshare.model.request.ArticleEditBody;
import com.shu.eshare.model.response.ArticlesPage;
import com.shu.eshare.model.security.LoginUser;
import com.shu.eshare.service.ArticleService;
import com.shu.eshare.utils.ResultUtils;
import com.shu.eshare.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Resource
    public ArticleService articleService;




    /**
     * 发表文章
     * @param articleAddBody 发表文章请求体
     * @return
     */
    @PostMapping("/publish")
    public BaseResponse articlePublish(@RequestBody ArticleAddBody articleAddBody){
        if (articleAddBody == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        //标题不为空且长度不超过 30 字
        String title = articleAddBody.getArticleTitle();
        if (StringUtils.isBlank(title) || title.length() > 30){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"标题不为空且长度不超过 30 字");
        }
        //文章不为空且长度不超过 20000 字
        String content = articleAddBody.getContent();
        if (StringUtils.isBlank(content) || content.length() > 20000){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"文章不为空且长度不超过 20000 字");
        }
        //封面图片最多三张
        if (articleAddBody.getOrderImages().size() > 3){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"封面图片最多三张");
        }
        //用户自定义标签+默认标签不超过5个
        if (articleAddBody.getUserTagList().size()+articleAddBody.getSystemTagIdList().size() > 5){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"用户自定义标签+默认标签不超过5个");
        }
        //用户每个自定义标签长度不能超过 20 字
        for(String item : articleAddBody.getUserTagList()){
            if (item.length() > 20){
                return ResultUtils.error(ErrorCode.PARAMS_ERROR,"自定义标签过长");
            }
        }
        // 添加文章逻辑
        boolean articleAdd = articleService.articleAdd(articleAddBody);
        if (!articleAdd){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"文章提交失败");
        }
        //返回成功
        return ResultUtils.success("添加成功");

    }

    /**
     * 查询最新投稿
     * @param curPage 从第0页开始
     * @return
     */
    @GetMapping("/getOnlineArticle/latest")
    public BaseResponse<List<ArticleCardES>> getLatestAllOnlineArticle(int curPage){
        if (curPage<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(articleService.getAllArticleOnline(curPage));
    }

    /**
     * 查询我的投稿
     * @param curPage 从第0页开始
     * @return
     */
    @GetMapping("/mine/page/list")
    public BaseResponse<ArticlesPage> selfArticle(int curPage){
        if (curPage<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();

        //TODO 从ES库中查 || 从MySQL中查
        Long userId = loginUser.getUser().getUserId();
        ArticlesPage selfArticlesByPageList = articleService.getSelfArticlesByPage(curPage, userId);

        return ResultUtils.success(selfArticlesByPageList);
    }

    /**
     * 查询我的投稿，根据userId
     * @param curPage 从第0页开始
     * @return
     */
    @GetMapping("/mine/page/id")
    public BaseResponse<ArticlesPage> selfArticle(int curPage,Long userId){
        if (curPage<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        LoginUser loginUser = SecurityUtils.getLoginUser();

        //TODO 从ES库中查 || 从MySQL中查
//        Long userId = loginUser.getUser().getUserId();
        ArticlesPage selfArticlesByPageList = articleService.getSelfArticlesByPage(curPage, userId);

        return ResultUtils.success(selfArticlesByPageList);
    }

    /**
     * 根据articleId查询Article
     */
    @GetMapping("/get/one")
    public BaseResponse<ArticleCardES> getArticleCardEsById(Long articleId){
        if (articleId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ArticleCardES articleCardES = articleService.getArticleEs(articleId);

        return ResultUtils.success(articleCardES);
    }

    /**
     * 文章编辑
     * todo 目前只支持修改文章标题和文章内容，其他功能如修改标签、封面图片后续在完善
     */
    @PostMapping("/edit")
    public BaseResponse editArticle(@RequestBody ArticleEditBody articleEditBody){
        if (articleEditBody == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        if (articleEditBody.getArticleId() == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        //标题不为空且长度不超过 30 字
        String title = articleEditBody.getArticleTitle();
        if (StringUtils.isBlank(title) || title.length() > 30){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"标题不为空且长度不超过 30 字");
        }
        //文章不为空且长度不超过 20000 字
        String content = articleEditBody.getContent();
        if (StringUtils.isBlank(content) || content.length() > 20000){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"文章不为空且长度不超过 20000 字");
        }

        boolean edit = articleService.editArticle(articleEditBody);

        return ResultUtils.success(edit);
    }

    /**
     * 文章浏览数加一,根据ip限流
     */
    @PostMapping("/view")
    @RateLimit(key = "article:view",count = 10,ipLimit = true)
    public void viewArticle(Long articleId){
        if (articleId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        articleService.articleViewNumAdd(articleId);
    }


    /**
     * 文章点赞
     * @param articleId 文章id
     * @param like 1-点赞 2-取消点赞
     * @return
     */
    @PostMapping("/like")
    @RateLimit(key = "article:likes",count = 60,ipLimit = true)
    public BaseResponse like(Long articleId,int like){
        if (articleId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean success = articleService.like(articleId,like);

        return ResultUtils.success(success);
    }


    /**
     * 判断用户是否对该文章点赞过
     */
    @GetMapping("/isLike")
    public BaseResponse isLike(Long articleId){
        if (articleId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isLike = articleService.isLike(articleId);

        return ResultUtils.success(isLike);
    }

    /**
     * 收藏文章和取消收藏
     * @param articleId 文章id
     * @param collect 1-收藏 2-取消收藏
     */
    @PostMapping("/collect")
    public BaseResponse collectArticle(Long articleId,int collect){
        if (articleId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean success = articleService.collect(articleId,collect);
        return ResultUtils.success(success);
    }

    /**
     * 判断是否收藏
     */
    @GetMapping("/isCollect")
    public BaseResponse isCollect(Long articleId){
        if (articleId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isCollect = articleService.isCollect(articleId);
        return ResultUtils.success(isCollect);
    }

    /**
     * 删除稿件,只能删除当前用户的投稿
     * @param articleId 文章编号
     * @return
     */
    @DeleteMapping("/remove/self")
    public BaseResponse removeSelfArticle(Long articleId){
        if (articleId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean delete = articleService.removeSelfArticle(articleId);

        return ResultUtils.success(delete);
    }

    /**
     * 查询我的收藏文章,分页查询
     * @param curPage 当前页码从0开始
     * @return
     */
    @GetMapping("/mine/collect")
    public BaseResponse myCollectArticle(Integer curPage){
        if (curPage == null){
            curPage = 0;
        }
        ArticlesPage articleCardESPage = articleService.getSelfCollectArticlePage(curPage);

        return ResultUtils.success(articleCardESPage);
    }

    /**
     * 文章搜索，搜索标题、内容、列表、用户昵称
     */
    @GetMapping("/search")
    public BaseResponse search(String searchText,Integer curPage){
        if (StringUtils.isBlank(searchText)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"搜索内容不能为空");
        }
        if (curPage == null){
            curPage = 0;
        }

        ArticlesPage articlesPage = articleService.searchArticle(searchText,curPage);

        return ResultUtils.success(articlesPage);
    }

    @GetMapping("/hot")
    public BaseResponse getHotArticle(){

        //
        List<ArticleCardES> articleCardESList = articleService.getHotArticleList();

        return ResultUtils.success(articleCardESList);
    }

}
