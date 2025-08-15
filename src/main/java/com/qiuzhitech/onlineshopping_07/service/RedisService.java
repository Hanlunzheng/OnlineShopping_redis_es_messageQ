package com.qiuzhitech.onlineshopping_07.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;

@Service
@Slf4j

public class RedisService {
    @Resource
    private JedisPool jedisPool;
    public String setValue(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String res = jedis.set(key, value);
        jedis.close();
        return res;
    };

    public String getValue(String key) {
        Jedis jedis = jedisPool.getResource();
        String res = jedis.get(key);
        jedis.close();
        return res;
    };

    public long deductStockWithCommodityId(String key) {
        // query , then deduct, merge into one operation, lua
        Jedis jedis = jedisPool.getResource();
        String script =
                "if redis.call('exists', KEYS[1]) == 1 then\n" +
                        "    local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                        "    if (stock<=0) then\n" +
                        "        return -1\n" +
                        "    end\n" +
                        "\n" +
                        "    redis.call('decr', KEYS[1]);\n" +
                        "    return stock - 1;\n" +
                        "end\n" +
                        "\n" +
                        "return -1;";
        Long stock = -1L;
        try {
            stock = (Long)jedis.eval(script, Collections.singletonList(key), Collections.emptyList());
        } catch (Exception e) {
            log.error("Redis failed on stockDeduct", e);
        } finally {
            jedis.close();
        }
        return stock;
    }

    public boolean tryToGetDistributedLock(String lockKey, String requestId, int expireTime) {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.set(lockKey, requestId, "NX", "PX", expireTime);
        jedis.close();
        return "OK".equals(result);
    }

    public boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis jedis = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]" +
                " then return redis.call('del', KEYS[1])" +
                " else return 0 end";
        Long res = (Long)jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        jedis.close();
        return res == 1L;
    }

    public Long revertStockWithCommodityId(String key) {
        Jedis jedis = jedisPool.getResource();
        Long incr = jedis.incr(key);
        jedis.close();
        return  incr;
    }
    public void addToDenyList(String userId, String commodityId) {
        // key: online_shopping:denyListUserId:3
        // value: set of commodityId {1539,123,134,133}
        String key = "online_shopping:denyListUserId:" + userId;
        Jedis jedis = jedisPool.getResource();
        jedis.sadd(key, commodityId);
        jedis.close();
        log.info("Add userId: {} into denyList for commodityId: {}", userId, commodityId);
    }
    public boolean isInDenyList(String userId, String commodityId) {
        String key = "online_shopping:denyListUserId:" + userId;
        Jedis jedis = jedisPool.getResource();
        Boolean isInDenyList = jedis.sismember(key, commodityId);
        jedis.close();
        return isInDenyList;
    }

    public void removeFromDenyList(String userId, String commodityId) {
        String key = "online_shopping:denyListUserId:" + userId;
        Jedis jedis = jedisPool.getResource();
        jedis.srem(key, commodityId);
        jedis.close();
        log.info("Remove userId: {} into denyList for commodityId: {}", userId, commodityId);
    }
}
