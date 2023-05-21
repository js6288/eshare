package com.shu.eshare.esrepository;

import com.shu.eshare.model.es.ResourceES;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceESRepository extends ElasticsearchRepository<ResourceES,String> {


    /**
     * 构造分页请求
     * 按照createTime降序排序
     * @param pageNum 从0开始
     * @param pageSize 每页大小
     * @return PageRequest
     */
    default PageRequest getPageRequest(int pageNum, int pageSize) {
        return PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
    }

    void deleteByResourceId(Long resourceId);

    @Query("{\"bool\":{\"must\":[],\"should\":[{\"match\":{\"resourceName\":\"?0\"}},{\"term\":{\"majorName\":\"?1\"}},{\"term\":{\"fileType\":\"?2\"}},{\"term\":{\"type\":\"?3\"}},{\"term\":{\"tagList\":\"?4\"}}]}}")
    Page<ResourceES> search(String searchText, String majorName, String fileType, Integer type,String tag,PageRequest pageRequest);

    Page<ResourceES> searchByResourceName(String keyword, PageRequest pageRequest);
}
