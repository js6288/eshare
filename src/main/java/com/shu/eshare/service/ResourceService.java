package com.shu.eshare.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shu.eshare.model.domain.Resource;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shu.eshare.model.request.ResourceCommitBody;
import com.shu.eshare.model.request.ResourceSearchBody;
import com.shu.eshare.model.response.ResourcePage;
import com.shu.eshare.model.vo.ResourceVO;

/**
* @author ljs
* @description 针对表【resource(资源)】的数据库操作Service
* @createDate 2023-02-06 21:05:26
*/
public interface ResourceService extends IService<Resource> {

    boolean addResource(ResourceCommitBody resourceCommitBody);

    Page<Resource> getUserResourceByUserId(Long userId, int curPage);

    ResourcePage getAllResourceByPage(int curPage);

    ResourceVO getResourceDetails(Long resourceId);

    String getDownloadResource(Long resourceId);

    boolean isResourceDownload(Long resourceId, Long userId);

    boolean collect(Long resourceId,int collect);

    boolean isCollect(Long resourceId);

    Page<Resource> getResourceCollectPage(Integer curPage);

    boolean removeSelfResource(Long resourceId);

    ResourcePage searchForPage(ResourceSearchBody resourceSearchBody);

    ResourcePage searchByKeyword(String keyword, Integer curPage);
}
