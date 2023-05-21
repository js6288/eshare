package com.shu.eshare;


import com.google.gson.Gson;
import com.shu.eshare.common.Constant;
import com.shu.eshare.config.GitHubConstant;
import com.shu.eshare.esrepository.ArticleCardESRepository;
import com.shu.eshare.model.domain.Article;
import com.shu.eshare.model.es.ArticleCardES;
import com.shu.eshare.model.response.GitHubUser;
import com.shu.eshare.model.response.GithubAccessToken;
import com.shu.eshare.service.ArticleService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@SpringBootTest
public class EshareApplicationTests2 {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void ESClientTest(){
        System.out.println(restHighLevelClient);
    }


    @Resource
    private ArticleService articleService;

    @Resource
    public ArticleCardESRepository articleCardESRepository;

    /**
     * 测试存储数据到es
     *
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("save");
        indexRequest.id("1");
//        indexRequest.source("username","zhangsan",
//                "age",18);
        Article article = new Article();
        article.setArticleTitle("asdadas");
        article.setContent("asdasdsad");
        Gson gson = new Gson();
        String toJson = gson.toJson(article);
        //json字符串
        indexRequest.source(toJson, XContentType.JSON);

        //执行操作
        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

        //提取响应数据
        System.out.println(index);
    }

    @Test
    public void searchData() throws IOException{
        //检索请求
        SearchRequest searchRequest = new SearchRequest();

        //指定索引
        searchRequest.indices("bank");


        //指定DSL
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 构造检索条件
//        sourceBuilder.query();
//        sourceBuilder.from();
//        sourceBuilder.size();
//        sourceBuilder.aggregations();
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        searchRequest.source(sourceBuilder);

        //执行结果
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        search.status();
    }

    @Test
    public void encodePassword(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode("123456789");
        System.out.println(encode);
    }

    @Test
    public void testESSearch(){
//        List<ArticleCardES> allArticleOnline = articleService.getAllArticleOnline();
//        allArticleOnline.forEach(System.out::println);
    }

    @Test
    public void findByPageable(){
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        int currentPage=0;//当前页，第一页从 0 开始，1 表示第二页
        int pageSize = 5;//每页显示多少条
        //设置查询分页
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize,sort);
        Page<ArticleCardES> all = articleCardESRepository.findAll(pageRequest);
        for (ArticleCardES articleCardES : all.getContent()) {
            System.out.println(articleCardES);
        }
    }

    @Test
    public void findArticleByIdINES(){
        int pageSize = 6;//每页显示多少条
        PageRequest pageRequest = articleCardESRepository.getPageRequest(0, pageSize);
        Page<ArticleCardES> articleCardESPage = articleCardESRepository.findByUserIdOrderByCreateTimeDesc(5L, pageRequest);
        System.out.println(articleCardESPage.getNumber());
        System.out.println(articleCardESPage.getNumberOfElements());
    }

    @Test
    public void findArticleByArticleId(){
        ArticleCardES byArticleId = articleCardESRepository.findByArticleId(27L);
        System.out.println(byArticleId);
    }

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private GitHubConstant gitHubConstant;


    @Test
    public void restTemplateTest(){
        //获取access_token
        //https://github.com/login/oauth/access_token
        String accessTokenURL = gitHubConstant.getAccessToken();
        //构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        //构造请求体
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("client_id", gitHubConstant.getClientId());
        params.add("client_secret", gitHubConstant.getClientSecret());
        params.add("code", "59bb7013e77534c685ab");
        params.add("redirect_uri", gitHubConstant.getRedirectURI());

        HttpEntity<GithubAccessToken> httpEntity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromHttpUrl(accessTokenURL)
                .queryParams(params)
                .build()
                .toUriString();

        System.out.println(url);
        ResponseEntity<GithubAccessToken> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GithubAccessToken.class);
//        System.out.println();
//        Gson gson = new Gson();

        GithubAccessToken githubAccessToken = exchange.getBody();
        assert githubAccessToken != null;
        System.out.println(githubAccessToken.getAccess_token());

        String userInfoURL = gitHubConstant.getUserInfo();

        HttpHeaders headers2 = new HttpHeaders();
        //请求头设置access_token
        headers2.set("Authorization", Constant.TOKEN_PREFIX+githubAccessToken.getAccess_token());

        HttpEntity<GitHubUser> httpEntity2 = new HttpEntity<>(headers2);

        ResponseEntity<GitHubUser> exchange2 = restTemplate.exchange(userInfoURL, HttpMethod.GET, httpEntity2, GitHubUser.class);

        GitHubUser body = exchange2.getBody();

        System.out.println(body);
    }

}
