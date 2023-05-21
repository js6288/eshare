package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.ResourceComment;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.domain.UserDownloadResource;
import com.shu.eshare.model.vo.ResourceCommentVO;
import com.shu.eshare.service.ResourceCommentService;
import com.shu.eshare.mapper.ResourceCommentMapper;
import com.shu.eshare.service.UserDownloadResourceService;
import com.shu.eshare.service.UserService;
import com.shu.eshare.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author ljs
* @description 针对表【resource_comment(资源评论)】的数据库操作Service实现
* @createDate 2023-02-06 21:05:26
*/
@Service
public class ResourceCommentServiceImpl extends ServiceImpl<ResourceCommentMapper, ResourceComment>
    implements ResourceCommentService{

    @Autowired
    private UserDownloadResourceService userDownloadResourceService;

    @Autowired
    private UserService userService;


    /**
     * 用户发表资源评论，获得5积分
     * @param resourceId 资源id
     * @return
     */
    @Override
    @Transactional
    public boolean publishComment(Long resourceId, String content) {
        //获取当前用户id
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        //判断用户是否下载资源,如果用户没有下载该resourceId对应的资源则不能发表评论
//        long count = userDownloadResourceService.count(new QueryWrapper<UserDownloadResource>()
//                .eq("resource_id", resourceId).eq("user_id", userId));
        //优化SQL：select 1 FROM user_download_resource WHERE user_id = 5 and resource_id = 12 limit 1;
        LambdaQueryWrapper<UserDownloadResource> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(UserDownloadResource::getResourceId,resourceId)
                .eq(UserDownloadResource::getUserId,userId)
                .last("limit 1");
        List<Object> result = userDownloadResourceService.getBaseMapper().selectObjs(lambdaQueryWrapper);
        boolean isExist = !result.isEmpty();
        if (!isExist){
            throw new BusinessException(ErrorCode.FORBIDDEN,"当前账号没有下载过该资源，请下载后再评论");
        }

        //发表评论
        ResourceComment resourceComment = new ResourceComment();
        resourceComment.setContent(content);
        resourceComment.setUserId(userId);
        resourceComment.setResourceId(resourceId);

        //赠送 5 积分,每个资源只能赠送一次
        //查询该资源下用户是否发表过评论
        LambdaQueryWrapper<ResourceComment> resourceCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceCommentLambdaQueryWrapper
                        .eq(ResourceComment::getResourceId,resourceId)
                        .eq(ResourceComment::getUserId,userId)
                        .last("limit 1");
        List<Object> objects = this.baseMapper.selectObjs(resourceCommentLambdaQueryWrapper);
        //是否在该资源下已经发表过评论
        boolean isPublish = !objects.isEmpty();
        if (!isPublish){
            //如果没有发表过评论，则赠送 5 积分
            userService.update(new UpdateWrapper<User>()
                    .setSql("accumulate_points = accumulate_points + 5").eq("user_id",userId));
        }


        return save(resourceComment);
    }

    @Override
    @Transactional
    public Page<ResourceCommentVO> getResourceCommentPage(Long resourceId, int curPage) {
        // 每页10条数据
        Page<ResourceCommentVO> page = new Page<>(curPage,10);
//        QueryWrapper<ResourceComment> queryWrapper = new QueryWrapper<>();
        //按照时间排序
//        queryWrapper.select("content","user_id","create_time").eq("resource_id",resourceId).orderByDesc("create_time");
//        Page<ResourceComment> resourceCommentPage = page(page, queryWrapper);
//        List<ResourceComment> resourceCommentList = resourceCommentPage.getRecords();
//        List<Long> userIdList = resourceCommentList.stream().map(ResourceComment::getUserId).collect(Collectors.toList());
//
//        List<User> users = userService.listByIds(userIdList);
//
//        ArrayList<ResourceCommentVO> commentVOArrayList = new ArrayList<>(10);
//        for (int i = 0; i < 10; i++) {
//            ResourceCommentVO resourceCommentVO = new ResourceCommentVO();
//            ResourceComment resourceComment = resourceCommentList.get(i);
//            User user = users.get(i);
//        }
        // 分页查询
        Page<ResourceCommentVO> resourceCommentVOPage = (Page<ResourceCommentVO>) this.getBaseMapper().findResourceCommentVOByResourceId(page, resourceId);

        return resourceCommentVOPage;
    }
}




