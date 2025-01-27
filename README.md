


# **校内资源共享与交流平台的设计与实现**

-------------------------------------------------------------------------------


# **摘 要**

近年来，随着互联网行业、技术的迅速发展，以及移动互联网的普及，高校学生在网络上的社交不断加深，从互联网中学习和获取知识信息也变得越来越普遍。由于互联网上的学习资源质量参差不起以及同学之间、师生之间存在信息交流闭塞的问题，导致学生很难获得合适的学习资源和交流环境。针对上述情况，设计开发一款集成校内社区交流功能与知识资源分享平台，为学生良好的社区交流环境，鼓励同学之间互帮互助，分享学习资源，降低资源搜集的成本。

该平台在技术上采用前后端分离架构，后端采用Java语言、基于Spring Boot框架开发、MyBatis作为持久层、Spring Security作为安全框架和MySQL数据库开发，并结合Redis缓存、RabbitMQ消息中间件、ElasticSearch搜索引擎等分布式中间件提高系统可靠性，前端基于Vue 3.0和Ant Design Vue组件库开发，实现社区、资源库、个人中心等功能。

**\[关键词\]：**高校校园论坛；资源共享；Java；Spring Boot；Vue；

# Abstract

In recent years, with the rapid development of Internet industry and technology, and the popularity of mobile Internet, college students have deepened their social interaction on the network, and learning and obtaining knowledge and information from the Internet has become more and more common. Due to the uneven quality of learning resources on the Internet and the blocked information exchange between students and teachers and students, it is difficult for students to obtain suitable learning resources and communication environment. In view of the above situation, an integrated community communication function and knowledge resource sharing platform is designed and developed to provide students with a good community communication environment, encourage students to help each other, share learning resources, and reduce the cost of resource collection. 

The back-end uses Java language, based on Spring Boot framework development, MyBatis as the persistence layer, Spring Security as the security framework and MySQL database development. Redis cache, RabbitMQ message middleware, ElasticSearch search engine and other distributed middleware are combined to improve the reliability of the system. The front-end is developed based on Vue 3.0 and Ant Design Vue component library to realize the functions of community, resource library, personal center and so on. 

**\[Key words\]:** College Campus Forum; Resource sharing; Java; Spring Boot; Vue

# **目 录**

[TOC]

# 1 绪论 

## 1.1 研究背景

随着互联网行业的迅速发展，以及移动互联网的普及，人们可以通过网络获得丰富的学习资源，使得人们的学习方式和学习效果得到了极大的提升。传统的课堂教学已经不能满足人们的学习需求，人们对于学习资源的需求越来越大。大学生可以通过各种社交网站交流日常生活或者学习心得，从各种技术论坛、社交视频网站、博客、公众号和各种资源网站获取知识和资源。但是由于互联网的知识资源质量往往良莠不齐，导致大学生们在搜集针对性学习资源时，尤其是校内资源，非常困难。

例如，大学生需要获取考研、考公、英语四六级等真题和学习资源，想解公共选修课的相关内容、老师，往届师兄师姐的经验分享，则需要关注大量的公众号获取相关资源和在浩瀚的互联网中搜集。想要获取校内专业课程的PPT、实验报告、期末试卷等校内资源更是难上加难，只能通过与熟悉的同学、老师沟通获取相关资源，这对于部分信息较为闭塞、社交较少的同学来说对未来的学习是不利的。

## 1.2 研究意义与目的 

针对上述问题，开发一个集成论坛社交功能与学习资源分享的平台，鼓励同学之间互助、交流，上传各种资料、分享学习生活心得，在平台参与讨论交流，拓宽学生信息渠道，促进信息资源流通。搜集各种资源不需要依托学校内的人际关系，或者在各大微信公众号和网站大海捞针，能在一定程度上解决学生之间学习信息交流封闭的问题，降低搜集资源的成本。这样的平台可以为大学生提供更好的学习资源和学习体验，促进知识的传播和交流，同时也可以帮助大学生们更好地完成学业，提高自身素质。

## 1.3 国内外相关社交平台和论坛发展现状 

### 1.3.1 国内高校论坛与技术问答社区CSDN概况 

根据笔者在互联网的搜集与调查，国内外各大高校都有自己的BBS论坛，例如华南理工BBS、暨南大学BBS等等，这些论坛集成了求职就业、生活信息、社团组织的信息，作为师生表达观点、互帮互助、输出校园文化的重要平台。这些网站随着互联网各大社交平台的迅速发展，比如微博、虎扑、哔哩哔哩等。目前大多数学生很少浏览校内论坛，目前网站流量并不是很高，社区活跃度日渐下降，例如华南理工大学BBS论坛会员共74208人，日流量约9218，流量最高峰的时候在2017年，最高纪录是5209428，论坛功能并不能很好并且及时的满足学生获取相关资源的需要，由于BBS论坛起源于互联网刚刚兴起的时候，使用的是较老的技术，高校BBS论坛大多数使用PHP作为开发语言[1]，可以看到网站的首页图片展示还保留了已经淘汰的 Flash Player技术，2021年微软和adobe停止了对Adobe Flash Player的支持，目前大部分主流的浏览器已经不再支持flash 播放器。因此BBS论坛在无论在技术上还是在使用上都无法符合时代和学生的需要。

CSDN作为中文IT技术社区，包含app、博客、技术论坛和资源下载中心。其中CSDN下载中心我们可以搜索下载包含前端、后端、大数据、人工智能等各种计算机资源，但是CSDN下载需要充值C币，并且大部分资源价值不高但是价格昂贵，多为搬运GitHub的资源，并且受众有限，只适合IT从业者和计算机专业的同学[2]。

### 1.3.2 国外的技术论坛Stack Overflow

2016年Stack Overflow的开发工程师Nick Craver在他的博客上发表过关于Stack Overflow的技术架构，Stack Overflow仅用35台服务器和.Net技术撑起了世界上最受欢迎的网站之一。Stack Overflow仅用35台服务器和.Net技术撑起了世界上最受欢迎的网站之一，2016年02月09日一天的HTTP请求数达209,420,973此，SQL查询数504,816,843，Redis命中数5,831,683,114，Elastic 查询次数17,158,874，接收数据总量569 GB，发送数据总量达3.08TB[13]。

在技术架构上，Stack Overflow在负载均衡使用的 HAProxy 1.5.15 和 CentOS 7，Web层架构使用IIS 8.5，ASP.Net MVC 5.2.3，和.Net 4.6.1，在缓存层使用了 Redis，Redis 服务器 256GB 内存，采用 master/slave 结构部署，Stack Overflow的搜索采用 Elasticsearch 1.4，SQL Server 是 Stack Overflow 唯一的源数据库，所有 Elastic 和 Redis 的数据都来自 SQL Server[13]。Stack Overflow架构如图1.1所示。![](http://img-md-js.linjsblog.top/img/202406150933372.png){width="4.914443350831146in"
height="3.542351268591426in"}

图1.1 Stack Overflow 架构

> （资料来源：Stack Overflow：架构 - 2016 年版）

# 2 需求分析 

## 2.1 业务需求分析 

校内资源共享平台与交流平台支持PC网页端，基本功能包括用户中心、资源库、社区、后台管理四大模块，第三方平台登录、短信服务、分布式限流等功能。

用户中心：用户中心包含了用户的注册、登录、个人信息管理、个人等级与积分账户等功能。用户登录包含账号密码登录、手机验证码登录和第三方平台登录三种方式。由于国内审核的限制，开发QQ登录授权功能需要开发中认证和网站备案，所以我打算优先完成GitHub第三方平台的授权登录。

用户等级与积分账户：等级分为1级到6级，用户注册时等级为初始为1级，每升一级需要不同的经验，用户通过上传资源、发表帖子获得一定的经验、提升等级；用户注册成功后系统赠送200积分，用户上传资源或其他用户下载该用户上传的资源获得积分，用户可以通过获得的积分下载资源。

资源库：用户可以上传与下载资源，上传资源并通过审核赠送积分，提升经验等级，下载积分资源需要消耗积分。

搜索功能：分为主页搜索和资源库搜索，主页搜索社区模块的文章和帖子，资源库搜索资源库的相关资源。

社区：用户可以发表文章或帖子，文章包含点赞和评论区，评论区包含点赞和回复功能。在设计与开发时，需要考虑用户可以发表帖子或文章，浏览用户可以对其点赞、评论，同时对评论内容可以进行点赞和回复，对回复也可以进行回复和点赞。

搜索功能：分为主页搜索和资源库搜索，主页搜索社区模块的文章和帖子，资源库搜索资源库的相关资源。

系统后台管理：后台管理系统独立于平台，主要由系统管理员登录和维护，包含用户管理、社区内容审核、资源库审核等功能。

## 2.2 功能需求分析 

用例图是系统分析和设计中一种常用的建模工具，用例图可以帮助团队明确系统的功能需求和用户需求，以便在开发过程中能够更加清晰地明确和理解系统的目标。我们将用用例图来分析平台不同角色在各个模块的权限。平台面向的主要用户是在校大学生，平台的角色主要分为普通用户和系统管理员。普通用户主要使用的模块有个人中心、资源库和社区，系统管理员主要使用后台管理系统。

普通用户是平台最主要的使用者，用户在个人中心可以对自己发表的文章和上传的资源，并查询资源审核的进度。并且可以对自己的个人信息进行修改、接受系统通知，查询个人等级和积分余额。用户可以查看自己在社区和资源库收藏的文章和资源，方便重复浏览文章和资源。普通用户在个人中心的用例图如下图2.1所示。

![image-20240615094048081](http://img-md-js.linjsblog.top/img/202406150940118.png)

图2.1 个人中心用例图

用户可以在社区浏览文章、搜索文章，用户登录后可以发表文章、发表评论、收藏文章和点赞文章。社区的用例图如下图2.2所示。

![image-20240615094120846](http://img-md-js.linjsblog.top/img/202406150941887.png)

图2.2 社区用例图

用户可以在资源库浏览资源，用户登录后有权使用下载资源、收藏资源等功能。用户在下载资源后才能对资源发表评论。用户在资源库的用例图如下图2.3所示。

![image-20240615094144190](http://img-md-js.linjsblog.top/img/202406150941240.png)

图2.2 资源库用例图

系统管理员主要通过后台管理系统，后台管理系统独立于校内资源共享与交流平台，实现用户管理、文章管理、资源管理、审核等功能。后台管理系统用例图如图2.3所示。

![image-20240615094214025](http://img-md-js.linjsblog.top/img/202406150942072.png)

图2.2 后台管理用例图

# 3 系统设计

## 3.1 系统总体功能结构图 

校内资源共享与交流平台主要分为四大模块，分别为社区、用户中心、资源库和后台管理系统，总体功能结构图如图3.1所示。

![](http://img-md-js.linjsblog.top/img/202406150942605.png)

图3.1 校内资源共享与交流平台功能结构图

## 3.2 系统关键功能设计

### 3.2.1 基于JWT实现用户认证和授权 

一般情况下，基于JWT登录的流程一般是，用户提交登录表单，服务器返回token作为凭证，用户请求数据时在请求头携带token获取数据。但是由于JWT的无状态性，服务端不能控制用户登录和退出。所以JWT本身就是不安全的，如果token没有过期，论是谁拿到token谁都可以访问，所以我们通常会把token的有效期设置的较短，那么token过期了怎么办呢？如何实现token的续期。参照Oauth2 的实现方案，这个时候我们就要使用两个token实现JWT的续期：access_token和refresh_token。

access_token过期时间比较短，通常设置30分钟，refresh_token设置的比较长可以是一个小时甚至1个星期或更长。这两个token的职责不一样：access_token用于业务系统交互，是最核心的数据。refresh_token只用于向认证中心获取新的access_token与refresh_token。我们假设access_token有效期为30分钟，refresh_token为60分钟。当用户超过30分钟而不到60分钟时，access_token失效，需要通过refresh_token调用刷新接口，生成新的access_token和refresh_token返回给前端替换旧的token。如果refresh_token过期则代表用户状态过期，则前端跳转页面重新登录。

refresh token存在的意义是为了安全性refresh token只有在需要刷新token的时候才会发送给服务端校验，使用频率低才能降低泄露风险，而不是每次都要把refresh token发给服务端。具体时序图如图3.2所示。

![](http://img-md-js.linjsblog.top/img/202406150943274.png){width="5.772222222222222in"
height="4.654166666666667in"}

图3.2 双token解决用户登录token续签问题

### 3.2.2 手机验证码登录

基于JWT的登录流程开发手机号登录和注册。由于注册和手机号登录需要使用手机号接受短信验证码，所以我们采用阿里云短信API，实现短信发送功能。手机号登录流程如下图3.3所示。

![](http://img-md-js.linjsblog.top/img/202406150943885.png){width="5.768055555555556in"
height="6.322222222222222in"}

图3.3 手机号登录流程

### 3.2.3 短信接口的实现

用户填写手机号码后，点击发送短信，请求应用服务器，应用服务器请求阿里短信云服务，由阿里云短信服务向用户手机号码发送短信。

为了实现短信验证码的校验，我们使用Redis实现验证码的校验，设计Redis的存储结构：拼接常量前缀和UUID最后在末尾加上手机号形成Redis的key，UUID由Java自带的UUID工具类生成，code值为验证码，由用户发送的验证码与Redis中的验证码比较是否相同，如果相同，则说明验证成功，如果不相同则验证失败。用户验证码Redis存储结构如下图3.4所示。

![](http://img-md-js.linjsblog.top/img/202406150944653.png){width="4.438888888888889in"
height="2.4430555555555555in"}

图3.4 手机验证码 Redis K-V数据结构

短信验证码校验流程如下图3.5所示。

![](http://img-md-js.linjsblog.top/img/202406150944518.png){width="5.310416666666667in"
height="4.385416666666667in"}

图3.5 短信验证码校验流程

### 3.2.4 接口的分布式限流方案设计

为了防止短信服务被盗刷，或者流量比较大的接口导致系统负载过高宕机我们需要限制对接口的请求，比如根据IP和手机号码进行限制。这里我结合Redis采用令牌桶算法，并通过注解和拦截器的形式对接口的进行限流。

（1）令牌桶算法

令牌桶算法是一种用于控制流量的算法，可以确保系统在任何时候都不会因为流量超出容量而崩溃。在令牌桶算法中，一个固定数量的令牌被放入到一个"桶"中，每当一个请求到达时，系统都会尝试从桶中取出一个令牌。如果桶中没有令牌可用，请求将被拒绝或排队等待^\[15\]^。

具体而言，令牌桶算法定义了一个桶，里面装有固定数量的令牌，以及一个令牌发放速率，即每秒放入桶中的令牌数量。每当一个请求到达时，如果桶中有令牌可用，则取出一个令牌并处理请求；如果桶中没有令牌，则拒绝请求或将其放入等待队列中，直到桶中有令牌可用为止。

令牌桶算法的优点在于可以在一定程度上平滑地控制请求的流量，并且可以灵活地调整令牌的发放速率以适应系统的负载情况。同时，它也有一些缺点，比如可能导致请求的延迟增加或者需要一定的额外开销来实现算法。令牌桶算法如下图3.6。

![](http://img-md-js.linjsblog.top/img/202406150944130.png){width="3.952638888888889in"
height="2.2808814523184604in"}

图3.6 令牌桶算法示意图

（2）基于Redis + Lua脚本 + 令牌桶算法实现限流控制

部分关键代码如下：

-   自定义注解

```java
   @Target({ElementType.TYPE,ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface RateLimit {
       //限流唯一标示
       String key() default "";
   
       //限流单位时间（单位为s）
       int time() default 1;
   
       //单位时间内限制的访问次数
       int count();
   
       //是否限制ip
       boolean ipLimit() default false;
   
   }
```

   编写Lua脚本：


```lua
redis.replicate_commands();
-- 参数中传递的key
local key = KEYS[1]
-- 令牌桶填充 最小时间间隔
local update_len = tonumber(ARGV[1])
-- 记录 当前key上次更新令牌桶的时间的 key
local key_time = 'ratetokenprefix'..key
-- 获取当前时间(这里的curr_time_arr 中第一个是 秒数，第二个是 秒数后毫秒数)，由于我是按秒计算的，这里只要curr_time_arr[1](注意：redis数组下标是从1开始的)
--如果需要获得毫秒数 则为 tonumber(arr[1]*1000 + arr[2])
local curr_time_arr = redis.call('TIME')
-- 当前时间秒数
local nowTime = tonumber(curr_time_arr[1])
-- 从redis中获取当前key 对应的上次更新令牌桶的key 对应的value 上次更新时间秒数
local curr_key_time = tonumber(redis.call('get',key_time) or 0)
-- 获取当前key对应令牌桶中的令牌数
local token_count = tonumber(redis.call('get',KEYS[1]) or -1)
-- 当前令牌桶的容量
local token_size = tonumber(ARGV[2])
-- 令牌桶数量小于0 说明令牌桶没有初始化
if token_count < 0 then
    redis.call('set',key_time,nowTime)
    redis.call('set',key,token_size -1)
    return token_size -1
else
    if token_count > 0 then --当前令牌桶中令牌数够用
        redis.call('set',key,token_count - 1)
        return token_count -1   --返回剩余令牌数
    else    --当前令牌桶中令牌数已清空
        if curr_key_time + update_len < nowTime then    --判断一下，当前时间秒数 与上次更新时间秒数  的间隔，是否大于规定时间间隔数 （update_len）
            redis.call('set',key,token_size -1)
            redis.call('set',key_time,nowTime)
            return token_size - 1
        else
            return -1
        end
    end
end
```



-   Spring中注入拦截器

```java
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

```



-   在controller层添加注解即可使用，具体使用方法如下图3.7所示。

![](http://img-md-js.linjsblog.top/img/202406150956361.png){width="5.768055555555556in"
height="2.123611111111111in"}

图3.7 自定义限流注解的使用

### 3.2.5 第三方平台OAuth登录

（1）QQ登录开发流程如下图3.8所示。

![](http://img-md-js.linjsblog.top/img/202406150957040.png){width="5.772222222222222in" height="0.81875in"}

图3.8 QQ登录开发流程

首先获取Access Token，用户点击登录按钮后，通过OAuth2.0认证获取AccessToken，对于应用而言，需要进行两步^\[9\]^：

①获取Authorization Code；

②通过Authorization Code获取Access Token^[14]^；

![](http://img-md-js.linjsblog.top/img/202406150957477.png){width="5.433333333333334in"
height="4.159722222222222in"}

图3.9 OAuth 2.0 获取access token

> （资料来源：OAuth2.0协议草案V21的4.1节
> http://www.rfcreader.com/#rfc6749）

首先获取Authorization
Code。我们用通过GET方法发送HTTP请求访问<https://graph.qq.com/oauth2.0/authorize>接口，请求参数如下

![](http://img-md-js.linjsblog.top/img/202406150957811.png){width="5.646543088363955in"
height="3.687995406824147in"}

图3.10 获取Authorization Code请求参数

如果用户成功登录并授权，则会跳转到指定的回调地址，并在redirect_uri地址后带上Authorization
Code和原始的state值^[9]^。例如，通过Authorization Code获取Access
Token。

请求地址：<https://graph.qq.com/oauth2.0/token>

请求参数：

![](http://img-md-js.linjsblog.top/img/202406150957514.png){width="5.430003280839895in"
height="2.394012467191601in"}

图3.11 获取Access Token请求参数

返回参数：

![](http://img-md-js.linjsblog.top/img/202406150957416.png){width="5.676288276465442in"
height="1.0941174540682415in"}

图3.12 获取Access Token返回值

获取用户OpenID。通过上一步拿到的AccessToken，得到对应用户身份的OpenID。OpenID是此网站上或应用中唯一对应用户身份的标识，网站或应用可将此ID进行存储，便于用户下次登录时辨识其身份，或将其与用户在网站上或应用中的原有账号进行绑定^[9]^。

调用OpenID访问用户信息。调用get_user_info接口，获取用户QQ昵称、头像、性别，最后将获得的信息写入数据库保存，执行登录流程最终实现用户登录。

（2）GitHub登录

与QQ登录类似，用户跳转到Github进行授权，获取授权码，将拿到的授权码code向GitHub请求令牌，获得access token，拿到令牌后向API请求用户数据即可完成GitHub的第三方登录。

GitHub授权登录流程如图3.13、3.14所示

![](http://img-md-js.linjsblog.top/img/202406150958021.png){width="5.147916666666666in"
height="2.7465277777777777in"}

图3.13 Github登录授权流程

![](http://img-md-js.linjsblog.top/img/202406150958763.png){width="5.656944444444444in"
height="4.271527777777778in"}

图3.14 Github登录授权详细流程时序图

和手机验证码登录类似，第三方授权登录既可以实现注册也可以实现登录，如果用户已经注册账号和绑定了第三方账号，则自动登录，如果用户不存在，则需要保存用户信息到数据库。在从GitHub拿到用户信息跳转回平台，执行的具体逻辑如图3.15所示。

![](http://img-md-js.linjsblog.top/img/202406150958427.png){width="5.772222222222222in"
height="2.3229166666666665in"}

图3.15 Github登录授权流程

### 3.2.6 文章的评论回复功能

为了实现文章评论区的无限盖楼回复，既用户可以对文章进行评论，也可以对评论进行回复，也可以对回复进行回复，也称多级评论。我们对评论区做出如下图3.16所示的树型结构设计。

![](http://img-md-js.linjsblog.top/img/202406150958600.png){width="4.404166666666667in"
height="4.090972222222222in"}

图3.16 评论区表设计

以评论列表的访问为例，我们的查询SQL可能是（已简化）：

（1）查询时间序一级评论id列表：`SELECT id FROM article_cmmment WHERE`
`article_id=? AND root_id=0 AND is_delete=0 ORDER BY create_time=? LIMIT`
`0,20`

（2）批量查询根评论基础信息：`SELECT \* FROM article_cmmment WHERE id in`
`(?,?,\...)`

（3）并发查询楼中楼评论id列表：`SELECT id FROM article_cmmment WHERE`
`article_id =? AND root_id=? ORDER BY like_num LIMIT 0,3`

（4）批量查询楼中楼评论基础信息：`SELECT \* FROM article_cmmment WHERE id`
`in (?,?,\...)`^[11]^。

### 3.2.7 点赞功能的设计

点赞功能在文章和文章评论都有，当已登陆用户访问页面的时候，如果用户已经点赞，需要将点赞按钮高亮表示用户已经点亮，所以在前面数据库设计中，有两张表：文章点赞表article_like和文章评论点赞表comment_like,记录用户对当前文章和评论是否点赞。用户再点击一次按钮就是取消点赞。

因为点赞接口往往并发量较高，所以如果只有一层数据库，无论是查询和操作都需要消耗很大的性能。所以我们使用Redis作为缓存提高点赞功能的并发量和可靠性。

Redis缓存设计：

文章点赞列表如下图所示

> ![](http://img-md-js.linjsblog.top/img/202406151000884.png){width="5.298611111111111in" height="1.875in"}
>
> 图3.17 Redis文章点赞列表

Redis数据存储的键值对结构为Key-value=user:article:likes:{articleId}-user(userId)-score(likeTimestamp)

用articleId作为key，value则是一个zset，user是这篇文章点赞的用户id，score是点赞的时间，当这篇文章某用户有新的点赞操作的时候，就会用zadd的方式把最新的点赞记录加入到该zset中来^[11]^。

评论点赞列表与文章点赞列表类似，只不过是把key值作了调整。

![](http://img-md-js.linjsblog.top/img/202406151000315.png){width="5.683333333333334in"
height="2.098611111111111in"}

> 图3.18 Redis评论点赞列表

### 3.2.8 资源的上传和下载

资源上传后需要经过后台审核通过才能上架资源库，才能被其他用户搜索和下载。资源的上传以及审核流程如下图3.19所示。

![](http://img-md-js.linjsblog.top/img/202406151000134.png){width="5.601388888888889in"
height="7.651388888888889in"}

图3.19 资源上传审核流程图

用户上传的资源文件最终是保存在阿里云OSS对象存储服务上，按照传统方案，用户从前端上传的文件经过后端服务器再上传到OSS,这会占用很大一部分服务器带宽。所以采用服务端签名后由前端直传到OSS方案，比从应用服务器作为桥梁传递资源节省了一倍的带宽，由于服务端签名直传无需将Access Key暴露在前端页面，相比JavaScript客户端签名直传具有更高的安全性^[12]^。

![](http://img-md-js.linjsblog.top/img/202406151000003.png){width="5.7027777777777775in"
height="3.2395833333333335in"}

图3.20 服务端签名直传流程

### 3.2.9 搜索功能

平台的搜索功能分为文章搜索和资源搜索，搜索内容的来源是ElasticSearch, 因为用户上传资源并审核通过后才能上架，所以用户上传资源后，资源信息最先保存再MySQL数据库，当管理员从后台通过审核后，将MySQL数据库中的resource表资源状态status由0改为2（0-审核中 1-审核通过 2-审核通不过），此时将资源信息写入ElasticSearch 搜索引擎。而文章的发表不需要通过审核，所以文章发表后再写入数据库的同时将文章信息写入ElasticSearch。

我们结合文章的发表和资源的上传流程，结合搜索功能绘画出业务流程图如图3.21与3.22所示，注意，用户搜索到的资源信息和文章新信息都来源于ElasticSearch。

![](http://img-md-js.linjsblog.top/img/202406151001479.png){width="5.045138888888889in"
height="2.8756944444444446in"}

图3.21 文章上传与搜索流程

![](http://img-md-js.linjsblog.top/img/202406151001199.png){width="5.506944444444445in"
height="3.602777777777778in"}

图3.22 资源上传与搜索流程

### 3.2.10 文章的个性化推荐 

（1）基于内容的个性化推荐

基于内容的推荐算法是一种常见的推荐算法，往往使用与简单的推荐系统实现，或与其他推荐算法结合使用。它主要利用物品的属性和用户的历史行为来进行推荐。该算法假设用户会对他们以前感兴趣的物品以及与这些物品相关的物品感兴趣。因此，该算法会根据物品的属性和用户的历史行为来推荐与用户先前感兴趣的物品相似的物品^\[4\]^。

基于内容的推荐算法通常具有以下步骤：

特征提取：对于每个物品，从其属性中提取特征，并将这些特征组合成一个向量。例如，对于电影，可以从电影的类型、导演、演员、年份等方面提取特征。

用户建模：对于每个用户，使用他们的历史行为来构建用户模型。例如，对于电影推荐，可以使用用户以前观看过的电影来构建用户模型。

相似度计算：对于每个物品，计算其与用户模型的相似度。例如，可以使用余弦相似度或欧几里得距离等度量方式来计算相似度。

推荐生成：根据相似度计算，选择最相似的物品来生成推荐列表。

![](http://img-md-js.linjsblog.top/img/202406151002225.png){width="5.6in" height="3.6902777777777778in"}

图3.23 基于内容的推荐算法

（2）用户行为数据收集和文章的标签

用户在发表文章时会设定文章的标签；当用户对文章进行点赞、收藏操作时，会根据用户点赞、收藏的文章所属记录历史行为数据并保存到MySQL数据库，在进行个性化推荐时，系统会分析用户的历史行为数据，使用基于内容的推荐算法为用户生成推荐内容列表。

（3）余弦相似度计算

余弦相似度用向量空间中两个向量夹角的余弦值作为衡量两个个体间差异的大小。余弦值越接近1，就表明夹角越接近0度，也就是两个向量越相似，这就叫"余弦相似性"。我们用余弦相似度来计算每个列表标签的相似度。

其计算公式为：

![image-20240615104150683](http://img-md-js.linjsblog.top/img/202406151041711.png)


## 3.2 数据库设计

### 3.2.1 设计原则

1.  数据库版本：MySQL 8.0

2.  字符集：utf8mb4；utf8mb3是阉割版的UTF-8字符集，只用1-3个字节表示字符，utf8mb4使用1-4个字节表示字符，存储emoji等富文本内容需要用好4个字节，因此选用utf8mb4作为MySQL字符集。^[2]^

3.  存储引擎：InnoDB

MySQL支持多种存储引擎，通过"`show engines`"查看MySQL支持的存储引擎。如图3.24所示。

![](http://img-md-js.linjsblog.top/img/202406151028188.png){width="5.768055555555556in"
height="1.261111111111111in"}

图3.24 MySQL 支持的存储引擎

MySQL8.0
默认的存储引擎是InnoDB，InnoDB支持事务、行级锁和外键，外键对性能有一定损耗，因此我们在业务代码中保证数据一致性。

4.  比较规则：utf8mb4_general_ci 向下兼容5.7版本；

5.  数据库表名命名规范：表名采用英文字母+下划线方式进行命名，举例:
    表名BUSINESS_CODE_INFO组成， BUSINESS_CODE_INFO表示业务编码信息；

6.  数据库字段名称命名规范：采用英文字母+下划线方式进行命名，举例：BUSINESS_CODE标识业务编码；

7.  设计表结构时，不使用外键，因为使用外键会增加性能消耗，所以系统数据的一致性由业务代码维护，而不是使用外键保证数据一致性。

### 3.2.2 概念模型

校内资源共享与交流平台的数据库ER图如图3.25所示：

![](http://img-md-js.linjsblog.top/img/202406151014484.png){width="5.665972222222222in"
height="5.956944444444445in"}

图3.25 ER图

### 3.2.3 数据库表结构设计

（1）文章article表结构如图表3.1所示。

表3.1 article表

| 字段名          | 数据类型     | 默认值            | 是否允许为空 | 字段说明                           |
| --------------- | ------------ | ----------------- | ------------ | ---------------------------------- |
| article_id      | bigint       | NULL              | 否           | 文章id                             |
| article_title   | varchar(255) | NULL              | 否           | 文章标题                           |
| type            | int          | 0                 | 是           | 文章类型 0-普通用户文章 1-官方文字 |
| content         | text         | NULL              | 是           | 文章内容                           |
| order_images    | varchar(255) | NULL              | 是           | 文章前三章照片                     |
| comments_num    | bigint       | 0                 | 是           | 评论数                             |
| likes_num       | bigint       | 0                 | 是           | 点赞数                             |
| views_num       | bigint       | 0                 | 是           | 浏览数                             |
| collections_num | bigint       | 0                 | 是           | 收藏数                             |
| create_time     | datetime     | CURRENT_TIMESTAMP | 是           | 发表时间                           |
| update_time     | datetime     | CURRENT_TIMESTAMP | 是           | 更新时间                           |
| is_delete       | int          | 0                 | 是           | 是否删除                           |
| status          | int          | 0                 | 是           | 状态 0-正常 1-下架                 |
| user_id         | bigint       | NULL              | 是           | 所属用户id                         |

（2）文章收藏关系表

表3.2 article_collection表

| 字段名      | 数据类型 | 默认值            | 是否允许为空 | 字段说明 |
| ----------- | -------- | ----------------- | ------------ | -------- |
| id          | bigint   | NULL              | 否           |          |
| article_id  | bigint   | NULL              | 否           | 文章id   |
| user_id     | bigint   | NULL              | 否           | 用户id   |
| is_delete   | int      | NULL              | 是           | 是否删除 |
| create_time | datetime | CURRENT_TIMESTAMP | 是           | 创建时间 |
| update_time | datetime | CURRENT_TIMESTAMP | 是           | 修改时间 |

（3）文章评论关系表

表3.3 article_comment表

| 字段名      | 数据类型     | 默认值            | 是否允许为空 | 字段说明 |
| ----------- | ------------ | ----------------- | ------------ | -------- |
| id          | bigint       | NULL              | 否           | 评论id   |
| content     | varchar(255) | NULL              | 是           | 评论内容 |
| parent_id   | bigint       | NULL              | 否           | 父级评论 |
| root_id     | bigint       | NULL              | 否           | 顶级评论 |
| user_id     | bigint       | NULL              | 否           | 所属用户 |
| article_id  | bigint       | NULL              | 否           | 所属文章 |
| is_delete   | int          | NULL              | 是           | 是否删除 |
| create_time | datetime     | CURRENT_TIMESTAMP | 是           | 评论时间 |
| update_time | datetime     | CURRENT_TIMESTAMP | 是           | 修改时间 |
| like_num    | bigint       | NULL              | 是           | 点赞数量 |

（4）文章点赞表

表3.4 article_likes表

| 字段名      | 数据类型 | 默认值            | 是否允许为空 | 字段说明 |
| ----------- | -------- | ----------------- | ------------ | -------- |
| id          | bigint   | NULL              | 否           |          |
| article_id  | bigint   | NULL              | 否           | 文章id   |
| user_id     | bigint   | NULL              | 否           | 用户id   |
| create_time | datetime | CURRENT_TIMESTAMP | 是           | 创建时间 |
| is_delete   | int      | NULL              | 是           | 是否删除 |


----------------------------------------------------------------------------------

（5）文章标签表article_tags

表3.5 article_tags表

| 字段名      | 数据类型    | 默认值            | 是否允许为空 | 字段说明                             |
| ----------- | ----------- | ----------------- | ------------ | ------------------------------------ |
| id          | bigint      | NULL              | 否           | 标签id                               |
| tag_name    | varchar(15) | NULL              | 否           | 标签名                               |
| tag_type    | int         | NULL              | 否           | 标签类型 0-系统标签 1-用户自定义标签 |
| create_time | datetime    | CURRENT_TIMESTAMP | 是           | 创建时间                             |
| update_time | datetime    | CURRENT_TIMESTAMP | 是           | 修改时间                             |
| is_delete   | int         | 0                 | 是           | 是否删除                             |
| user_id     | bigint      | NULL              | 是           | 所属用户id                           |

（6）等级表level

表3.6 level表

| 字段名     | 数据类型 | 默认值 | 是否允许为空 | 字段说明     |
| ---------- | -------- | ------ | ------------ | ------------ |
| id         | int      | NULL   | 否           |              |
| user_id    | int      | NULL   | 否           | 用户id       |
| level      | int      | 1      | 是           | 用户等级 1-6 |
| experience | int      | 0      | 是           | 经验         |

（7）专业表major

表3.7 major表

----------------------------------------------------------------------------------

| 字段名     | 数据类型     | 默认值 | 是否允许为空 | 字段说明 |
| ---------- | ------------ | ------ | ------------ | -------- |
| id         | int          | NULL   | 否           |          |
| major_name | varchar(100) | NULL   | 否           | 专业名   |
| is_delete  | int          | 0      | 否           | 是否删除 |

（8）消息表notice

| 字段名      | 数据类型     | 默认值 | 是否允许为空 | 字段说明 |
| ----------- | ------------ | ------ | ------------ | -------- |
| id          | bigint       | NULL   | 否           |          |
| user_id     | bigint       | NULL   | 否           | 用户id   |
| title       | varchar(100) | NULL   | 否           | 消息标题 |
| content     | varchar(255) | NULL   | 否           | 消息内容 |
| create_time | datetime     | NULL   | 是           | 创建时间 |
| update_time | datetime     | NULL   | 是           | 修改时间 |
| is_delete   | int          | 0      | 是           | 是否删除 |

（9）资源表resource

| 字段名               | 数据类型     | 默认值            | 是否允许为空 | 字段说明                         |
| -------------------- | ------------ | ----------------- | ------------ | -------------------------------- |
| resource_id          | bigint       | NULL              | 否           | 资源id                           |
| user_id              | bigint       | NULL              | 是           | 所属用户                         |
| resource_name        | varchar(255) | NULL              | 否           | 资源名称                         |
| resource_description | varchar(500) | NULL              | 是           | 资源描述                         |
| major_id             | int          | NULL              | 是           | 专业id                           |
| type                 | int          | 0                 | 是           | 0-免费资源 1-付费资源            |
| require_point        | int          | 0                 | 是           | 所需点数                         |
| status               | int          | 0                 | 是           | 0-审核中 1-审核不通过 2-审核通过 |
| download_url         | varchar(255) | NULL              | 是           | 下载地址                         |
| create_time          | datetime     | CURRENT_TIMESTAMP | 是           | 创建时间                         |
| is_delete            | int          | 0                 | 是           | 是否删除                         |
| update_time          | datetime     | CURRENT_TIMESTAMP | 是           | 更新时间                         |
| collection_num       | bigint       | NULL              | 是           | 收藏数量                         |
| download_num         | bigint       | NULL              | 是           | 下载次数                         |
| file_size            | double       | NULL              | 是           | 文件大小                         |
| file_type            | char(5)      | NULL              | 是           | 文件类型（后缀）                 |


----------------------------------------------------------------------------------------------

（10）资源收藏关系表resource_collection

表3.10 resource_collection表

| 字段名      | 数据类型 | 默认值            | 是否允许为空 | 字段说明 |
| ----------- | -------- | ----------------- | ------------ | -------- |
| id          | bigint   | NULL              | 否           |          |
| resource_id | bigint   | NULL              | 否           | 资源id   |
| user_id     | bigint   | NULL              | 否           | 用户id   |
| is_delete   | int      | 0                 | 否           | 是否删除 |
| create_time | datetime | CURRENT_TIMESTAMP | 是           | 创建时间 |
| update_time | datetime | CURRENT_TIMESTAMP | 是           | 更新时间 |

（11）资源评论表resource_comment

> 表3.11 resource_comment表

| 字段名      | 数据类型     | 默认值            | 是否允许为空 | 字段说明   |
| ----------- | ------------ | ----------------- | ------------ | ---------- |
| id          | bigint       | NULL              | 否           | 评论id     |
| content     | varchar(255) | NULL              | 否           | 评论内容   |
| user_id     | bigint       | NULL              | 是           | 所属用户id |
| create_time | datetime     | CURRENT_TIMESTAMP | 是           | 评论时间   |
| update_time | datetime     | NULL              | 是           | 修改时间   |
| is_delete   | int          | 0                 | 是           | 是否删除   |

（12）资源标签表resource_tags

> 表3.12 resource_tags表

--------------------------------------------------------------------------------------

| 字段名      | 数据类型    | 默认值            | 是否允许为空 | 字段说明                            |
| ----------- | ----------- | ----------------- | ------------ | ----------------------------------- |
| id          | bigint      | NULL              | 否           | id                                  |
| tag_name    | varchar(15) | NULL              | 否           | 标签名                              |
| tag_type    | int         | NULL              | 是           | 0-分类（系统标签） 1-用户自定义标签 |
| create_time | datetime    | CURRENT_TIMESTAMP | 是           | 创建时间                            |
| update_time | datetime    | CURRENT_TIMESTAMP | 是           | 修改时间                            |
| is_delete   | int         | 0                 | 是           | 是否删除                            |
| user_id     | int         | NULL              | 是           | 所属用户id                          |

（13）文章标签关系表tag_article_rel

> 表3.13 tag_article_rel表

---------------------------------------------------------------------------------------

| 字段名         | 数据类型 | 默认值            | 是否允许为空 | 字段说明   |
| -------------- | -------- | ----------------- | ------------ | ---------- |
| id             | bigint   | NULL              | 否           |            |
| user_id        | bigint   | NULL              | 否           | 用户id     |
| article_tag_id | bigint   | NULL              | 否           | 文章标签id |
| is_delete      | int      | 0                 | 否           | 是否删除   |
| create_time    | datetime | CURRENT_TIMESTAMP | 是           |            |
| update_time    | datetime | CURRENT_TIMESTAMP | 是           |            |


---------------------------------------------------------------------------------------

（14）资源标签关系表tag_resource_rel

> 表3.14 tag_resource_rel表

----------------------------------------------------------------------------------------

| 字段名          | 数据类型 | 默认值            | 是否允许为空 | 字段说明   |
| --------------- | -------- | ----------------- | ------------ | ---------- |
| id              | bigint   | NULL              | 否           |            |
| user_id         | bigint   | NULL              | 否           | 用户id     |
| resource_tag_id | bigint   | NULL              | 否           | 资源标签id |
| is_delete       | int      | 0                 | 否           | 是否删除   |
| create_time     | datetime | CURRENT_TIMESTAMP | 是           |            |
| update_time     | datetime | CURRENT_TIMESTAMP | 是           |            |

（15）院校库表university

| 字段名          | 数据类型     | 默认值 | 是否允许为空 | 字段说明 |
| --------------- | ------------ | ------ | ------------ | -------- |
| id              | bigint       | NULL   | 否           |          |
| university_name | varchar(100) | NULL   | 否           | 大学名称 |
| is_delete       | int          | 0      | 是           | 是否删除 |



（16）用户表user

> 表3.16 user表

----------------------------------------------------------------------------------------------

| 字段名             | 数据类型     | 默认值            | 是否允许为空 | 字段说明                             |
| ------------------ | ------------ | ----------------- | ------------ | ------------------------------------ |
| user_id            | bigint       | NULL              | 否           | 用户id                               |
| username           | varchar(255) | NULL              | 否           | 用户名                               |
| nickname           | varchar(255) | NULL              | 否           | 昵称                                 |
| password           | varchar(255) | NULL              | 否           | 密码                                 |
| personal_signature | varchar(255) | NULL              | 是           | 个性签名                             |
| ip_address         | bigint       | NULL              | 是           | ip地址                               |
| avatar_url         | varchar(255) | NULL              | 是           | 头像地址                             |
| school             | varchar(255) | NULL              | 是           | 学校                                 |
| phone              | varchar(20)  | NULL              | 是           | 手机号                               |
| birthday           | datetime     | NULL              | 是           | 生日                                 |
| gender             | int          | 2                 | 是           | '0'-男 ‘1’女 ‘2’保密 默认为2         |
| user_role          | int          | 0                 | 是           | 用户角色 ‘0’-普通用户 ‘1’-管理员     |
| read_num           | bigint       | NULL              | 是           | 阅读数                               |
| likes_num          | bigint       | NULL              | 是           | 获赞数                               |
| accumulate_points  | bigint       | 0                 | 是           | 积分                                 |
| status             | int          | 0                 | 是           | 账号状态是否封禁 默认为0-正常 1-封禁 |
| is_delete          | int unsigned | 0                 | 是           | 逻辑删除 默认为0                     |
| update_time        | datetime     | CURRENT_TIMESTAMP | 是           | 更新时间                             |
| create_time        | datetime     | CURRENT_TIMESTAMP | 是           | 创建时间                             |

（17）用户下载资源关系表user_download_resource

> 表3.17 user_download_resource表

----------------------------------------------------------------------------------

| 字段名      | 数据类型 | 默认值            | 是否允许为空 | 字段说明 |
| ----------- | -------- | ----------------- | ------------ | -------- |
| id          | bigint   | NULL              | 否           |          |
| user_id     | bigint   | NULL              | 否           | 用户id   |
| resource_id | bigint   | NULL              | 否           | 资源id   |
| create_time | datetime | CURRENT_TIMESTAMP | 是           | 下载时间 |
| update_time | datetime | CURRENT_TIMESTAMP | 是           | 修改时间 |
| is_delete   | int      | 0                 | 是           | 是否删除 |

（18）用户标签关系表user_tag_rel

> 表3.18 user_tag_rel表

----------------------------------------------------------------------------------

| 字段名      | 数据类型 | 默认值            | 是否允许为空 | 字段说明   |
| ----------- | -------- | ----------------- | ------------ | ---------- |
| id          | bigint   | NULL              | 否           |            |
| user_id     | bigint   | NULL              | 否           | 用户id     |
| user_tag_id | bigint   | NULL              | 否           | 用户标签id |
| is_delete   | int      | 0                 | 否           | 是否删除   |
| create_time | datetime | CURRENT_TIMESTAMP | 是           |            |
| update_time | datetime | CURRENT_TIMESTAMP | 是           |            |



（19）用户标签表user_tags

> 表3.19 user_tags表

-----------------------------------------------------------------------------------

| 字段名      | 数据类型    | 默认值 | 是否允许为空 | 字段说明 |
| ----------- | ----------- | ------ | ------------ | -------- |
| id          | bigint      | NULL   | 否           | 标签id   |
| tag_name    | varchar(20) | NULL   | 否           | 标签名   |
| create_time | datetime    | NULL   | 是           | 创建时间 |
| update_time | datetime    | NULL   | 是           | 修改时间 |
| is_delete   | int         | NULL   | 是           | 是否删除 |

（20）第三方平台绑定表oauth

> 表3.20 oauth表

------------------------------------------------------------------------------------

| 字段名    | 数据类型     | 默认值 | 是否允许为空 | 字段说明                     |
| --------- | ------------ | ------ | ------------ | ---------------------------- |
| id        | bigint       | NULL   | 否           | 主键id                       |
| user_id   | bigint       | NULL   | 否           | 用户id                       |
| auth_type | varchar(255) | NULL   | 是           | 第三方平台类型- QQ、GitHub等 |
| open_id   | varchar(255) | NULL   | 是           | 第三方平台用户的openId       |

## 3.3 技术选型 

### 3.3.1 前端技术路线

（1）Vue 3.0开发框架

Vue是一款用于构建用户界面的渐进式JavaScript框架，它基于标准 HTML、CSS 和
JavaScript
构建，并提供了一套声明式的、组件化的编程模型，帮助高效地开发用户界面。无论是简单还是复杂的界面，Vue
都可以胜任。^\[5\]^

（2）Ant Design Vue组件库

使用 Vue 实现的遵循 Ant Design 设计规范的高质量 UI
组件库，用于开发和服务于企业级中后台产品，可以简化前端的开发。

（3）Vite

Vite是下一代前端开发和构建工具，使用Vite能够显著提升前端开发体验，随着构建越来越大型的应用时，需要处理的
JavaScript 代码量也呈指数级增长，基于 JavaScript
开发的工具就会开始遇到性能瓶颈，如Web应用重启缓慢。Vite
旨在利用生态系统中的新进展解决上述问题：浏览器开始原生支持 ES
模块，且越来越多 JavaScript 工具使用编译型语言编写。

### 3.3.2 后端技术路线 

（1）主语言：Java

Java具有面向对象、平台无关性、可靠性、支持多线程、安全性、解释与编译并存等特点，并且Java拥有强大的生态和社区支持。

（2）Spring Boot

Spring 是一款开源的轻量级 Java
开发框架，旨在提高开发人员的开发效率以及系统的可维护性。Spring 支持
IoC（Inversion of Control:控制反转） 和 AOP (Aspect-Oriented
Programming:面向切面编程)、可以很方便地对数据库进行访问、可以很方便地集成第三方组件（电子邮件，任务，调度，缓存等等）。

Spring Boot 旨在简化 Spring 开发，Spring Boot
只是简化了配置，如果你需要构建 MVC 架构的 Web 程序，你还是需要使用
Spring MVC 作为 MVC 框架，只是说 Spring Boot 帮你简化了 Spring MVC
的很多配置，真正做到开箱即用

（3）MyBatis

MyBatis框架是一个开源的数据持久层框架。内部封装了通过JDBC访问数据库的操作，支持普通的SQL查询、存储过程和高级映射，几乎消除了所有的JDBC代码和参数的手工设置以及结果集的检索。MyBatis作为持久层框架，其主要思想是将程序中的大量SQL语句剥离出来，配置在配置文件当中，实现SQL的灵活配置。

（3）MyBatis-Plus

MyBatis-Plus简单的来说就是MyBatis的增强工具，在MyBatis的基础上只做增强不做改变，可以简化开发提高效率。MyBatis-Plus无侵入性、损耗小，内置通用Mapper、通用Service，通过少量配置即可实现单表大部分
CRUD
操作，更有强大的条件构造器，满足各类使用需求。支持Lambda形式调用，内置分页插件，因此使用MyBatis-Plus可以大幅提升开发效率。

（4）MySQL数据库

MySQL是关系型数据库管理系统，我们将使用MySQL8.0版本，InnoDB和MyISAM作为MySQL的主要存储引擎，InnoDB支持事务和行级锁、支持在数据库异常崩溃后的安全恢复，而MyISAM并不支持，在很多一直场景中，InnoDB的速度都可以让MyISAM望尘莫及，尤其是用到了聚簇索引，或者需要访问的数据都可以放入内存的应用，因此选择InnoDB作为存储引擎；MySQL将实现校内资源共享与交流平台最主要的数据库，Redis缓存和
Elasticsearch 中的数据都来源于MySQL，并会定时持久化到MySQL中。

（5）Redis

是开源的使用ANSI
C语言编写、支持网络、可基于内存亦可持久化的日志型、Key-Value数据库，并提供多种语言的API。Redis单线程、使用多路IO复用模型，由于Redis基于内存，绝大部分请求是纯粹的内存操作，非常快速；Redis可以用来实现评论区和文章的点赞功能，并作为缓存和中间件提高系统性能。

（6）RabbitMQ

RabbitMQ是一种消息队列中间件，消息队列可以实现异步通信、流量削峰、解耦等功能,，RabbitMQ基于AMQP协议，具有易用性、扩展性、可靠性和高可用性等特点。在项目开发中，RabbitMQ将于MySQL和Redis结合使用，通过读取Binlog异步删除缓存，保证Redis缓存和MySQL的数据一致性。

（7）ElasticSearch搜索引擎

简单地说，ElasticSearch是一个分布式的使用 REST
接口的搜索引擎，是基于Apache
Lucene(TM)的开源搜索引擎，无论在开源还是专有领域，Lucene
可以被认为是迄今为止最先进、性能最好的、功能最全的搜索引擎库。完全基于Java语言开发，支持集群，面向文档，持久化。校内资源共享于交流平台的资源库搜索和社区搜索功能通过ElasticSearch的分词器和倒排索引实现。^\[6\]^

（8）Spring Security安全框架

Spring
Security是一个功能强大、可高度定制的身份验证和访问控制框架。在和Spring
家族的其他成员如 Spring Boot、Spring
Cloud等进行整合时，具有其他框架无可比拟的优势，同时对 OAuth2
有着良好的支持。具有身份认证、授权、防护攻击、加密、会话管理等功能。^\[7\]^

（9）JSON Web Token

是目前最流行的跨域认证解决方案，其本质是一个token，就是用过JSON形式作为Web应用中的令牌，用于在各方之间（例如前后端之间、A系统与B系统之间）安全地传输信息。由于此信息是经过数字签名的，因此可以被验证和信任。由头部（header）、载荷（payload）、签名（signature）组成。主要用于鉴权。

（10）阿里云OSS对象存储服务

是一款海量、安全、低成本、高可靠的云存储服务。支持Restful
API、SDK接口访问OSS，支持多种存储方式。数据以对象（Object）的形式存储在OSS的存储空间（Bucket）中。支持海量的用户并发访问。主要用于实现文件资源的上传下载和管理功能。^\[12\]^

## 3.4 平台架构设计

前端Vue项目打包后，使用Nginx作为静态HTTP服务器，并将后端请求通过负载均衡反向代理到后端服务器。后端通过编写Dockerfile打包成Docker容器镜像。可以在微信云托管、安装有Docker阿里云服务器上发布，至于Redis、Elastic
Search和MySQL等数据库则通过docker安装在阿里云服务器和百度云上。平台的部署架构图如图3.26所示。为了更清新的展示平台不同层次采用的技术，我们根据平台前后端的技术路线，我们将系统的技术架构分为网络通信端、前端服务、后端服务、数据存储服务和部署环境的技术架构,如图3.27所示。

![](http://img-md-js.linjsblog.top/img/202406151024054.png){width="5.665277777777778in"
height="3.7916666666666665in"}

图3.26 系统部署架构图

![](http://img-md-js.linjsblog.top/img/202406151024449.png){width="5.75625in" height="7.790277777777778in"}

图3.27 系统技术架构图

# 4 系统实现 

## 4.1 注册

用户注册填写用户名（用户名用作账号登录），用户名必须唯一，昵称可以重复，密码，确认密码，并校验密码和确认密码相同，获取手机验证码绑定手机号，每次获取验证码间隔60s。点击注册按钮执行注册逻辑，如果手机号已经绑定已有账号，则返回错误信息；注册成功执行登录逻辑跳转至首页（社区）。用户注册成功后，为用户添加200积分。注册页面如图4.1
所示。

![](http://img-md-js.linjsblog.top/img/202406151024098.png){width="4.906944444444444in"
height="5.395138888888889in"}

图4.1 注册页面图

## 4.2 手机号登录 

手机号登录同时具有注册和登录两种功能，需验证真实的手机号，每次获取验证码等待60s，如果该手机号用户没有注册则自动注册并登录跳转主页，系统会自动为用户生成用户名、昵称等默认的个人信息，并为用户赠送200积分。如果用户存在执行登录操作跳转主页（社区）。手机号登录页面如图4.2所示。

![](http://img-md-js.linjsblog.top/img/202406151024201.png){width="5.233333333333333in"
height="3.9166666666666665in"}

图4.2 手机号登录页面

## 4.3 账号密码登录 

与传统的账号密码登录别无二致，账号使用注册时的用户名，如果用户名不存在或密码不正确，则返回错误信息。登录成功跳转至首页（社区）。账号密码登录页面如下图4.3所示。

![](http://img-md-js.linjsblog.top/img/202406151024359.png){width="5.252777777777778in" height="3.9625in"}

图4.3 用户名密码登录

## 4.4 第三方平台登录 

### 4.1.1 QQ登录 

用户点击按钮之后弹出QQ登录的窗口，在登录窗口中将显示网站自己的Logo标识，网站名称以及首页链接地址^\[9\]^。如图4.4所示：

![](http://img-md-js.linjsblog.top/img/202406151025023.png){width="4.836837270341207in"
height="2.7008497375328084in"}

图4.4 QQ登录

（资料来源：<https://wiki.connect.qq.com/网站应用接入流程> ）

如果用户已登录QQ软件，就不用重复输入帐号密码，可以一键实现快速登录并且可以选择授权允许网站访问自己的相关信息，如图4.5所示：

![](http://img-md-js.linjsblog.top/img/202406151025870.png){width="5.17837489063867in"
height="2.9520352143482063in"}

图4.5 QQ登录

（资料来源：<https://wiki.connect.qq.com/网站应用接入流程> ）

### 4.4.2 Github登录 {#github登录 .unnumbered}

一个链接让用户跳转到GitHub，Github会让用户登录，登录后GitHub会询问用户，该应用正在请求数据，你是否统一授权。用户统一授权后，GitHub会跳转回应用^\[8\]^。Github授权页面如图4.6所示。

> ![](http://img-md-js.linjsblog.top/img/202406151025655.png){width="3.832437664041995in"
> height="3.380952537182852in"}

图4.6 Github授权页面

## 4.5 个人中心 

个人中心包含展示当前用户的基本信息，个人中心包含的功能有内容管理-我的投稿、我的收藏，个人信息设置，等级、积分系统，收藏的文章、资源和消息中心，进入个人中心时，默认路由到我的投稿页面。个人中心需要做好相关的权限控制，未登录用户和非当前用户只能展示顶部的信息卡片和历史投稿和上传的资源。只有登录的用户才能看到自己的个人信息卡片下方的功能展示，用户点击左下方的侧导航栏，路由到各个子页面组件如"我的资源"、"我的积分"等页面。个人中心首页如下图4.7所示。

![](http://img-md-js.linjsblog.top/img/202406151025335.png){width="5.772222222222222in"
height="2.4340277777777777in"}

图4.7 个人中心页面

### 4.5.1 投稿管理

投稿管理展示当前用户发表的文章，点击删除按钮弹出对话框是否删除当前文章。点击编辑按钮跳转文章编辑页。点击文章标题跳转文章详情页。点击"点我立即投稿"按钮跳转文章发表页。个人中心-我的投稿如图4.8所示。

![](http://img-md-js.linjsblog.top/img/202406151025851.png){width="5.5579593175853015in"
height="3.2630938320209975in"}

图4.8 个人中心-我的投稿页面

### 4.5.2 资源管理管理 

"我的资源"展示当前用户发表的文件，用户可以对自己上传的资源进行删除，显示用户上传资源的审核状态。点击"上传资源"按钮跳转上传资源页面。"我的资源"页面如图4.9所示。

![](http://img-md-js.linjsblog.top/img/202406151025241.png){width="5.772222222222222in"
height="6.211111111111111in"}

图4.9 个人中心-我的资源页面

### 4.5.3 上传个人头像

上传头像：点击上传照片将照片上传至OSS保存并回显至前端，点击保存更新数据库和前端的头像地址。上传和更新个人头像如下图4.10所示

![](http://img-md-js.linjsblog.top/img/202406151025124.png){width="5.772222222222222in"
height="2.634027777777778in"}

图4.10 个人中心-上传头像

### 4.5.4 编辑资料

用户名不可更改，如果用户没有绑定手机，则显示绑定手机链接，如果用户已经绑定手机号则显示手机号，隐藏中间第4-8位数，点击更换手机弹出对话框，输入手机号获取验证码绑定手机号。院校库信息由爬虫批量导入数据库，前端从后台查询所有院校，编辑资料页面和绑定手机号弹框如下图4.11和4.12所示。

![](http://img-md-js.linjsblog.top/img/202406151025640.png){width="5.573611111111111in"
height="3.477777777777778in"}

图4.11 个人中心-编辑资料页面

![](http://img-md-js.linjsblog.top/img/202406151026112.png){width="5.619047462817148in"
height="3.179304461942257in"}

图4.12 绑定手机号对话框

### 4.5.5 等级系统

用户等级分为Lv1至Lv6,用户初始等级为Lv1，每个等级升级所需的经验都不同。升级到lv6后经验不在增加。可通过如下每日奖励规则获取经验升级：点赞文章增加2经验、收藏文章增加2经验，发表文章增加10经验，上传资源增加10经验,，经验的获取有限制次数，限制次数会在每天0点清零。我的等级页面如下图4.13所示。

![](http://img-md-js.linjsblog.top/img/202406151026930.png){width="5.772222222222222in"
height="3.4590277777777776in"}

图4.13 等级系统页面

### 4.5.6 积分系统 

用户可以完成每日任务来获取积分，例如，新用户注册平台自动赠送200积分、上传一次资源并通过审核赠送10积分等。"我的积分"页面用户展示当前用户的积分余额和获取积分的规则，"我的积分"页面如下图所示。

![](http://img-md-js.linjsblog.top/img/202406151026400.png){width="5.772222222222222in"
height="3.5722222222222224in"}

图4.14 积分系统页面

### 4.5.7 消息中心

系统通知主要有资源审核通知和文章资源被平台下架通知。按照时间顺序排序。消息中心页面如图4.15所示。

![](http://img-md-js.linjsblog.top/img/202406151026149.png){width="5.360416666666667in" height="3.15in"}

图4.15 消息中心

## 4.6 首页文章展示 

社区文章展示模块，分为个性化推荐的文章、热门文章、最新文章三个栏目，可以根据导航栏进行切换。首页的文章列表由一个个文章卡片组成。首页的每个文章卡片展示文章的作者昵称、作者等级、发表时间、发表类型：分为官方和非官方两种类型、文章标题，并截取文章主体内容前20个字作为文章概要，每个文章卡片最多显示3章图片作文文章的封面图片，显示文章所携带的标签（包括系统默认标签和自定义标签，最多展示4个），显示文章数据：浏览量、点赞数、评论数。

### 4.6.1 热门文章 

根据文章的用户点击量、点赞数、评论数、收藏量四个指标综合评价计算分数作为热度进行排序。文章综合分数越高，排名越高。展示越靠前。热门文章显示的是当月发表的热度排行的前10名。榜单数据会每隔10分钟会同步更新1次。热门文章平台界面如下图4.16所示。

![](http://img-md-js.linjsblog.top/img/202406151026045.png){width="5.772222222222222in"
height="2.560416666666667in"}

图4.16 热门文章

### 4.6.2 最新文章列表的分页懒加载

最新文章列表主要展示用户最新发表的文章，按照文章按照时间顺序排序。

系统一般不会一次性显示所有文章列表，否则在数据量非常大的情况下会对数据库和应用系统造成很大的压力。用户通过点击文章列表默认的"点击加载更多内容"向下延伸展示下一分页的文章卡片，如图4.17
所示。

![](http://img-md-js.linjsblog.top/img/202406151026515.png){width="5.7027777777777775in"
height="4.305555555555555in"}

图4.17 文章懒加载

## 4.7 文章发表和文章编辑

用户点击首页"发表文章"进入文章编辑页，文章标题限30字，文章主体字数统一限制文章最大字数为20000个字符。文章内容前端使用markdown编辑器实现，方便文章内容在各大平台的迁移和复制。用户在文章插入图片时将图片上传至云端存储OSS，如果图片来自其他链接则不必上传。用户可以自定义标签和选择系统默认标签，每个文章最多携带5个标签。封面图片最多上传三张。上传的封面图片会在文章首页的文章卡片列表展示。而文章编辑页与文章发表页一致，文章编辑页会在各项表单显示文章的内容，允许用户修改并提交文章内容、标题、标签和封面图片。文章发表页面如图4.18所示。

![](http://img-md-js.linjsblog.top/img/202406151026711.png){width="5.528150699912511in"
height="6.558781714785652in"}

图4.18 文章发表页面

## 4.8 文章详情页

在首页点击文章标题进入文章详情页，如果当前文章属于当前登录用户，则显示编辑按钮，如果该文章不属于当前用户则不显示编辑按钮。点击编辑按钮跳转至文章编辑页（文章发表页），前后端需对文章编辑做权限控制，如果不是文章不属于当前用户，则拒绝跳转，后端需对用户更新文章进行权限校验。用户点赞文章功能：用户点击点赞，则点赞按钮高亮，点赞数+1，如果用户已经点赞，再点一次取消高亮，点赞数-1。用户点击收藏，收藏按钮高亮，再点一次取消收藏，逻辑与点赞相似，并将收藏的文章加入用户收藏列表。用户点赞和收藏的文章之后可以在用户中心管理和查看。文章详情页如图4.19、4.20所示

![](http://img-md-js.linjsblog.top/img/202406151027512.png){width="5.501695100612423in"
height="3.321813210848644in"}

图4.19 文章详情页页头

![](http://img-md-js.linjsblog.top/img/202406151027245.png){width="5.768055555555556in"
height="1.761111111111111in"}

图4.20 文章详情页末尾

## 4.9 文章评论区

评论区在每篇文章的下方。

评论的基础模块^\[10\]^：

①发布评论：支持无限盖楼回复；

②读取评论：按照时间、热度（点赞数量）；

③删除评论：系统管理员删除；

④评论互动：点赞。

评论区如图4.21所示。

![](http://img-md-js.linjsblog.top/img/202406151027077.png){width="5.545833333333333in"
height="4.019444444444445in"}

图4.21 文章评论区

## 4.10 资源库

资源库主要用来为学生提供查找、下载资源的地方资源库主页如下图所示，最顶部展示轮播图，中间筛选栏用于根据资源的形式、分类、专业、格式筛选查找资源。可以与搜索栏合并搜索，下方的资源列表默认展示最新资源。资源只能展示审核通过的资源。资源库主页如图4.22所示。

![](http://img-md-js.linjsblog.top/img/202406151027995.png){width="4.466330927384077in"
height="5.898252405949257in"}

图4.22 资源库主页

## 4.11 资源上传 

资源上传的文件大小限制在2GB，每次最多上传一个文件。如果要上传多个文件需要打包成压缩包。用户可为资源自定义标签，每个资源用户最多定义三个标签。所需分类、发布形式在数据库中都属于系统初始标签，专业从专业数据库搜索。如果用户发布形式选择积分资源，则需填写所需积分，积分必须为整数，下载资源所需积分的最大限度为500积分。资源上传后需要经过后台审核通过才能上架资源库，才能被其他用户搜索到。资源上传页面如下图4.23所示。

![](http://img-md-js.linjsblog.top/img/202406151028297.png){width="5.7097123797025375in"
height="5.838853893263342in"}

图4.23 资源上传

## 4.12 资源详情页

资源详情页第一张卡片展示资源的基本信息：资源标题，资源标签，下载所需积分，下载量，收藏量，资源文件大小，上传时间，资源上传者头像和昵称。

点击立即下载，从对象存储服务下载文件，下载量+1，点击收藏将资源添加到个人的收藏列表（数据库添加关联信息），收藏量+1。第二张卡片，显示资源的简介。资源详情页如图4.24所示。

![](http://img-md-js.linjsblog.top/img/202406151028711.png){width="4.8541393263342085in"
height="7.04696741032371in"}

图4.24 资源详情页

相比起文章评论区，资源评论区的结构要简单的多，并不需要实现无限盖楼的效果，只是一维的评论列表，并实现分页功能。只包含发表评论的用户头像、昵称、评论内容、发表时间。只有下载资源才能发表评论。发表评论获得5积分，每个资源发表评论只能获得一次积分。

# 5 总结 

本文介绍了一款基于前后端分离架构的校内资源共享和交流平台的设计和实现。该平台主要由后端和前端两部分组成，后端采用Java语言、Spring Boot框架、MyBatis作为持久层、Spring Security作为安全框架和MySQL数据库开发，同时结合Redis缓存、RabbitMQ消息中间件、ElasticSearch搜索引擎等分布式中间件提高系统可靠性，前端则采用Vue 3.0和Ant Design Vue实现。

在系统的设计阶段，首先对平台进行了需求分析，确定了功能模块和用例，然后进行了系统的架构设计，包括系统的模块划分、组件之间的关系、接口的设计和实现等。在实现过程中，采用了Spring Boot框架和MyBatis作为持久层，实现了资源上传、下载、浏览和搜索等功能。同时，采用了Redis缓存、RabbitMQ消息中间件、ElasticSearch搜索引擎等分布式中间件提高系统的可靠性和性能。在前端实现方面，采用了Vue 3.0和Ant Design Vue框架，实现了用户注册、登录、资源上传、下载、浏览和搜索等功能。

在测试阶段，通过对系统进行了功能测试、性能测试和安全测试，证明了系统的功能和性能符合预期，并保证了系统的安全性。

在项目完成后，我们对该平台的创新点和不足进行了分析。创新点主要体现在采用前后端分离架构、采用Spring Security安全框架和结合Redis缓存、RabbitMQ消息中间件、ElasticSearch搜索引擎等分布式中间件提高系统可靠性。不足主要是在用户体验和部分功能上仍有改进空间，例如页面UI、资源推荐、社交互动等方面。

总的来说，该平台在校内资源共享和交流方面有着广泛的应用前景和社会价值，同时也对技术能力进行了全面提升。未来，我将进一步完善该平台的功能和性能，提高用户体验，为学生提供更好的服务。

> # 参考文献 
>
> 1.  李鹏飞. 基于Web技术的校园论坛设计与实现\[D\]. 内蒙古科技大学, 2019.
>
> 2.  李胜利,钟滢.中外技术问答社区的实证对比研究与启示------以CSDN和Stack
>     Overflow为例\[J\]. 情报学报, 2020, 39(09): 989-1000.
>
> 3.  小孩子4919. MySQL是怎样运行的\[M\]. 北京:人民邮电出版社, 2020:34-36.
>
> 4.  杨博, 赵鹏飞. 推荐算法综述\[J\]. 山西大学学报(自然科学版), 2011,
>     34(03): 337-350.
>
> 5.  刘亚茹, 张军. Vue.js框架在网站前端开发中的研究\[J\].
>     电脑编程技巧与维护, 2022(01):18-19+39.
>
> 6.  柳帆. 基于ElasticSearch的科技资源检索系统的研究与实现\[J\].
>     现代计算机, 2021, 27(26): 93-100.
>
> 7.  刘姚. 基于Spring和OAuth2.0的第三方授权框架\[J\]. 计算机技术与发展,
>     2017, 27(03): 167-170.
>
> 8.  阮一峰. GitHub OAuth 第三方登录示例教程\[EB/OL\].
>     (2019-04-21)\[2023-03-08\].
>     <https://www.ruanyifeng.com/blog/2019/04/github-oauth.html>
>
> 9.  QQ互联. 网站应用接入\[EB/OL\]. (2016-08-20)\[2023-03-08\].
>     <https://wiki.connect.qq.com/%e7%bd%91%e7%ab%99%e5%ba%94%e7%94%a8%e6%8e%a5%e5%85%a5%e6%b5%81%e7%a8%8b>
>
> 10.  黄振. B站评论架构设计\[EB/OL\]. (2022-12-09)\[2023-03-08\].
>      https://www.bilibili.com/read/cv20346888
>
> 11.  芦文超. B站千亿级点赞系统服务架构设计\[EB/OL\].
>      (2023-02-03)\[2023-03-08\]. https://www.bilibili.com/read/cv21576373
>
> 12.  阿里云. 阿里云OSS产品文档-最佳实践-服务端签名后直传\[EB/OL\].
>      (2022-12-26)\[2023-03-08\].
>      https://help.aliyun.com/document_detail/31926.html
>
> 13.  Nick Craver. Stack Overflow: The Architecture - 2016
>      Edition\[EB/OL\].
>      <https://nickcraver.com/blog/2016/02/17/stack-overflow-the-architecture-2016-edition/>
>
> 14.  D. Hardt, Ed. The OAuth 2.0 Authorization Framework\[DB/OL\].
>      (2012-10-01)\[2023-03-08\].<https://www.rfc-editor.org/rfc/rfc6749#section-4.1>
>
> 15.  Wikipedia. Token bucket\[DB/OL\]. (2023-02-07)\[2023-03-08\].
>      <https://en.wikipedia.org/wiki/Token_bucket>

# 致 谢 

在完成本文之际，首先还是要感谢在大学四年坚持自我学习、不懈补拙的自己。虽然在大学期间我完成通过团队或个人完成了不少类似于这次毕业设计的项目，但这次毕业设计是我四年独立完成、工作量最大、涉及技术面最广、最新、最难的一次项目，是我在大学所学计算机知识和相关技术栈的融会贯通，从2022年末开题至2023年3月底完成论文，中间花费了较多的时间和精力，没有长时间的学习和积累是无法完成的。

其次，要感谢帮助我在大一学习职业目标比较迷茫的时候为我指引方向的学长学姐，校园网络中心是我在大学唯一没有后悔加入校级组织，这里的学长学姐非常友好，遇到什么问题都会互帮互助，平时的活动也很少，是一个学习性组织，也是因为这些机遇，在前辈的指引下让我认识并参加大学计算机竞赛例如蓝桥杯等，让我明确之后的学习和职业方向，在大学期间不断紧随潮流学习新技术、夯实基础，为我在后来的开发以及大四找工作、考研提供很大的帮助。我的大学生涯相对来说单调一些，当然也有疫情的影响，在其他人看来，我比较特立独行，喜欢独处，喜欢独立思考、独立解决问题，更多的时间是在学习技术上，为找工作准备，也参加过研究生考试初试，不过很遗憾没有进入复试，庆幸的是秋招签了一家公司，打算先干着。总之，还是很感谢一路过来帮助我的同学和老师们。

最后要感谢的是我的论文指导老师张顺超老师，非常感谢老师花费时间和精力愿意指导和修改我的论文，不然我的论文可能会显得很不规范和粗糙。值此提交论文之时，向一路走来帮助我的人致以由衷的感谢。

