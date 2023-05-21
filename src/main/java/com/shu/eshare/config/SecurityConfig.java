package com.shu.eshare.config;

import com.shu.eshare.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * 自定义用户认证逻辑
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 认证失败处理类
     */
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;



    /**
     * 强散列哈希加密实现
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //测试接口
                .antMatchers("/api/test").anonymous()
                .antMatchers("/api/test2").anonymous()
                //短信接口
                .antMatchers("/api/sms/sendSms").anonymous()
                //刷新token接口
                .antMatchers("/api/login/refresh/token").anonymous()
                //账号密码登录接口
                .antMatchers("/api/login/password").anonymous()
                //手机登录注册接口
                .antMatchers("/api/login/phone").anonymous()
                //传统注册接口
                .antMatchers("/api/register/common").anonymous()
                //获取基本用户信息
                .antMatchers("/api/user/safety/**").anonymous()
                //获取最新文章
                .antMatchers("/api/article/getOnlineArticle/latest").anonymous()
                //根据userId 查询用户投稿
                .antMatchers("/api/article/mine/page/id").anonymous()
                //根据articleId查询用户详情
                .antMatchers("/api/article/get/one").anonymous()
                //根据userId查询用户上传的资源page
                .antMatchers("/api/resource/user/page").anonymous()
                //获取GitHub登录页
                .antMatchers("/api/oauth/getGithubUrl").anonymous()
                //github登录
                .antMatchers("/api/oauth/login/github").anonymous()
                //获取资源库首页的资源列表
                .antMatchers("/api/resource/library/page").anonymous()
                //获取资源详情
                .antMatchers("/api/resource/detail").anonymous()
                //查询资源评论分页
                .antMatchers("/api/resource/comment/page").anonymous()
                //浏览文章数+1
                .antMatchers("/api/article/view").anonymous()
                //搜索文章
                .antMatchers("/api/article/search").anonymous()
                //热门文章
                .antMatchers("/api/article/hot").anonymous()
                //资源搜索
                .antMatchers("/api/resource/search/keyword").anonymous()
                //查询文章评论
                .antMatchers("/api/article/comment/list").anonymous()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //异常过滤器
        http.exceptionHandling()
                //配置认证失败过滤器
                .authenticationEntryPoint(authenticationEntryPoint);
        //配置跨域
        http
                .cors()
                .configurationSource(configurationSource());
    }

    CorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用 UserDetailsService 进行用户名密码认证
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

        // 使用自定义的手机号认证方式进行认证
//        auth.authenticationProvider(mobileAuthenticationProvider());
    }
}
