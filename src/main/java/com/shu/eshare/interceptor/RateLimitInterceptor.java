package com.shu.eshare.interceptor;

import com.google.gson.Gson;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.Constant;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.common.annotation.RateLimit;
import com.shu.eshare.utils.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Resource
    private RedisTemplate<String,Serializable> redisTemplate;

    @Resource
    private DefaultRedisScript<Long> redisLuaScript;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        assert handler instanceof HandlerMethod;
        HandlerMethod method = (HandlerMethod) handler;
        RateLimit rateLimit = method.getMethodAnnotation(RateLimit.class);
        //当前方法上有我们自定义的注解
        if (rateLimit != null) {
            //获得单位时间内限制的访问次数
            int count = rateLimit.count();
            String key = rateLimit.key();
            //获得限流单位时间（单位为s）
            int time = rateLimit.time();
            boolean ipLimit = rateLimit.ipLimit();
            //拼接 redis中的key
            StringBuilder sb = new StringBuilder();
            sb.append(Constant.RATE_LIMIT_KEY).append(key).append(":");
            //如果需要限制ip的话
            if(ipLimit){
                sb.append(getIpAddress(request)).append(":");
            }
            List<String> keys = Collections.singletonList(sb.toString());
            //执行lua脚本
            Long execute = redisTemplate.execute(redisLuaScript, keys, time, count);
            assert execute != null;
            if (-1 == execute.intValue()) {
                BaseResponse resultModel = ResultUtils.error(ErrorCode.RATE_LIMIT_ERROR);
                response.setStatus(901);
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json");
                response.getWriter().write(new Gson().toJson(resultModel));
                response.getWriter().flush();
                response.getWriter().close();
                LOG.info("当前接口调用超过时间段内限流,key:{}", sb.toString());
                return false;
            } else {
                LOG.info("当前访问时间段内剩余{}次访问次数", execute.toString());
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***".length()
            if (ipAddress != null && ipAddress.length() > 15) {
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

}
