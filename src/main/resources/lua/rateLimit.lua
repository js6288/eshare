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
