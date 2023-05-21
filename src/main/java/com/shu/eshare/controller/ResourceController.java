package com.shu.eshare.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.Constant;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.Major;
import com.shu.eshare.model.domain.Resource;
import com.shu.eshare.model.request.ResourceCommitBody;
import com.shu.eshare.model.request.ResourceSearchBody;
import com.shu.eshare.model.response.ArticlesPage;
import com.shu.eshare.model.response.ResourcePage;
import com.shu.eshare.model.vo.ResourceVO;
import com.shu.eshare.service.LevelService;
import com.shu.eshare.service.MajorService;
import com.shu.eshare.service.ResourceService;
import com.shu.eshare.utils.ResultUtils;
import com.shu.eshare.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {


    @Autowired
    private ResourceService resourceService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private LevelService levelService;


    @PostMapping("/commit")
    private BaseResponse commitResource(@RequestBody ResourceCommitBody resourceCommitBody){

        //1.校验字段
        if (resourceCommitBody == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //标题长度不能大于30
        String resourceName = resourceCommitBody.getResourceName();
        if (StringUtils.isBlank(resourceName) || resourceName.length()>30){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"标题不为空且标题长度不能大于30");
        }

        //资源描述长度不得大于500
        String resourceDescription = resourceCommitBody.getResourceDescription();
        if (StringUtils.isBlank(resourceDescription) || resourceDescription.length() > 500){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"资源描述不为空，资源描述长度不得大于500");
        }

        //自定义资源标签不能超过5个
        if (resourceCommitBody.getUserDefineTagList().size()>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户自定义标签不能超过5个");
        }

        //用户每个自定义标签长度不能超过 20 字
        for (String item : resourceCommitBody.getUserDefineTagList()) {
            if (item.length() > 20){
                return ResultUtils.error(ErrorCode.PARAMS_ERROR,"自定义标签过长");
            }
        }

        //如果是免费资源type=0，则所需积分requirePoint必须为0，如果是积分资源，则所需积分必须大于0，并且最多200积分
        if (resourceCommitBody.getType() == 0){
            resourceCommitBody.setRequirePoint(0);
        }else if (resourceCommitBody.getType() == 1
                && (resourceCommitBody.getRequirePoint() <= 0
                || resourceCommitBody.getRequirePoint() > Constant.RESOURCE_DOWNLOAD_MAX_POINT)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,
                    "积分资源所需积分必须大于0,且小于"+Constant.RESOURCE_DOWNLOAD_MAX_POINT+"积分");
        }
        //判断majorId是否合法
        if (resourceCommitBody.getMajorId() == null || resourceCommitBody.getMajorId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }else {
            Major byId = majorService.getById(resourceCommitBody.getMajorId());
            if (byId == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }

        //文件大小不能为空
        //下载地址不能为空
        //文件类型不能为空
        if (resourceCommitBody.getFileSize() == null || StringUtils.isAnyBlank(resourceCommitBody.getDownloadUrl(),resourceCommitBody.getFileType())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        boolean resourceAdd = resourceService.addResource(resourceCommitBody);
        if (!resourceAdd){
            ResultUtils.error(ErrorCode.SYSTEM_ERROR,"资源提交失败，请稍后重试");
        }

        //上传资源，赠送经验
        levelService.incrExperienceLimitDaily(10,Constant.UPLOAD);

        return ResultUtils.success("提交成功");
    }

    /**
     * 根据userId 查询一个用户的所有上传的资源
     * @param userId
     * @return
     */
    @GetMapping("/user/page")
    public BaseResponse getUserResourceWithPage(Long userId,int curPage){
        if (userId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Resource> page = resourceService.getUserResourceByUserId(userId, curPage);

        return ResultUtils.success(page);
    }


    /**
     *
     * @param curPage
     * @return
     */
    @GetMapping("/library/page")
    public BaseResponse getResourceLibrary(int curPage){
        ResourcePage resourcePage = resourceService.getAllResourceByPage(curPage);

        return ResultUtils.success(resourcePage);
    }


    /**
     * 查询资源详情（已通过审核的资源）
     */
    @GetMapping("/detail")
    public BaseResponse getResourceDetail(Long resourceId){
        if (resourceId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResourceVO resourceVO = resourceService.getResourceDetails(resourceId);

        return ResultUtils.success(resourceVO);
    }

    /**
     * 下载文件业务，根据resourceId获取资源的下载地址downloadUrl,前端根据downloadUrl下载资源。
     * 如果资源是付费资源需扣减相应的积分
     * @param resourceId
     * @return
     */
    @PostMapping("/get/download/url")
    public BaseResponse<String> downloadResourceFile(Long resourceId){
        if (resourceId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String downloadUrl = resourceService.getDownloadResource(resourceId);

        return ResultUtils.success(downloadUrl);
    }


    /**
     * 判断该资源是否下载
     */
    @GetMapping("/isDownload")
    public BaseResponse isResourceDownload(Long resourceId){
        if (resourceId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        if (userId == null){
            return ResultUtils.success(false);
        }
        boolean isDownload = resourceService.isResourceDownload(resourceId,userId);
        return ResultUtils.success(isDownload);
    }

    /**
     * 收藏资源
     * @param resourceId 资源编号
     * @param collect 1-收藏 2-取消收藏
     */
    @PostMapping("/collect")
    public BaseResponse collectResource(Long resourceId,int collect){
        if (resourceId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean success = resourceService.collect(resourceId,collect);

        return ResultUtils.success(success);
    }

    /**
     * 判断用户是否收藏该资源
     */
    @GetMapping("/isCollect")
    public BaseResponse isCollect(Long resourceId){
        if (resourceId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isCollect = resourceService.isCollect(resourceId);

        return ResultUtils.success(isCollect);
    }

    /**
     * 查询用户收藏的资源列表
     */
    @GetMapping("/mine/collect")
    public BaseResponse userCollectPage(Integer curPage){
        if (curPage == null){
            curPage = 1;
        }
        Page<Resource> resourcePage = resourceService.getResourceCollectPage(curPage);

        return ResultUtils.success(resourcePage);
    }


    /**
     * 从MySQL和ElasticSearch中删除资源
     */
    @DeleteMapping("/remove")
    public BaseResponse remove(Long resourceId){
        if (resourceId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean delete = resourceService.removeSelfResource(resourceId);
        return ResultUtils.success(delete);
    }

    /**
     * TODO 资源筛选+搜索 开发中
     */
    @GetMapping("/search")
    public BaseResponse searchResource(@RequestBody ResourceSearchBody resourceSearchBody){
        if (resourceSearchBody == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ResourcePage resourcePage = resourceService.searchForPage(resourceSearchBody);

        return ResultUtils.success(resourcePage);
    }

    /**
     * 关键词搜索资源
     */
    @GetMapping("/search/keyword")
    public BaseResponse searchResourceByKeyWord(String keyword,Integer curPage){
        if (StringUtils.isBlank(keyword)){
            ResultUtils.error(ErrorCode.PARAMS_ERROR,"搜索关键词不能为空");
        }
        if (curPage == null){
            curPage = 0;
        }
        ResourcePage resourcePage = resourceService.searchByKeyword(keyword,curPage);

        return ResultUtils.success(resourcePage);
    }





}
