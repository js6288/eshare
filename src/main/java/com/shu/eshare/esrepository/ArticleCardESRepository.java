package com.shu.eshare.esrepository;

import com.shu.eshare.model.es.ArticleCardES;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Repository
public interface ArticleCardESRepository extends ElasticsearchRepository<ArticleCardES,String> {

    /**
     * 按照userId 分页查询
     * @param userId 文章所属的用户id
     * @param pageRequest 分页请求
     * @return Page<ArticleCardES> 可以转换为list
     */
    @Query("{\"bool\" : {\"must\" : [{\"term\" : {\"userId\" : \"?0\"}}], \"must_not\" : [], \"should\" : []}}")
    Page<ArticleCardES> findByUserIdOrderByCreateTimeDesc(Long userId, PageRequest pageRequest);

    @Query("{\"terms\":{\"articleId\":?0}}")
    Page<ArticleCardES> findByArticleIdsOrderByCreateTimeDesc(List<Long> articleIds,PageRequest pageRequest);

    @Query("{\"terms\":{\"articleId\":?0}}")
    List<ArticleCardES> findByArticleIds(List<Long> articleIds);

    @Query("{\"bool\": {\"should\": [{\"match\": {\"articleTitle\": \"?0\"}}, {\"match\": {\"content\": \"?0\"}}, {\"match\": {\"nickname\": \"?0\"}}, {\"match\": {\"tagList\": \"?0\"}}]}}")
    Page<ArticleCardES> searchArticleCardESByArticleTitleOrContentOrTagList(String searchText,PageRequest pageRequest);

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

    @Query("{\"bool\" : {\"must\" : [{\"term\" : {\"articleId\" : \"?0\"}}], \"must_not\" : [], \"should\" : []}}")
    ArticleCardES findByArticleId(Long articleId);

    void deleteByArticleId(Long articleId);


    @Query("{\"bool\": {\"must\": [{\"range\": {\"createTime\": {\"gte\": \"?0\",\"lte\": \"?1\"}}}]}}")
    List<ArticleCardES> findByCreateTimeBetween(String start,String end);

    default List<ArticleCardES> findByCreateTimeThisWeek(){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        LocalDate localDate = LocalDate.now();
//        LocalDate startOfWeek = localDate.with(DayOfWeek.MONDAY);
//        LocalDate endOfWeek = localDate.with(DayOfWeek.SUNDAY);
//
//        Date start = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        Date end = Date.from(endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());


//        return findByCreateTimeBetween(start, end);

//        // 获取当前日期时间
//        Calendar cal = Calendar.getInstance();
//        Date now = cal.getTime();
//
//        // 计算本周的开始时间
//        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//        Date startOfWeek = cal.getTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String weekStart = ZonedDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).format(formatter);
        System.out.println("weekStart:"+weekStart);
        String now = ZonedDateTime.now().format(formatter);
        System.out.println("now:"+now);

        return findByCreateTimeBetween(weekStart,now);

    }
}
