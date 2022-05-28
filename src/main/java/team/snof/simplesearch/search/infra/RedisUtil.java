package team.snof.simplesearch.search.infra;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

/**
 * 内部封装了关闭连接的逻辑，直接调用就行
 */

@Component
public class RedisUtil {

    private final JedisPool jedisPool;

    public RedisUtil() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(8);
        config.setMaxTotal(8);
        jedisPool = new JedisPool(config, "127.0.0.1", 6379, 2000, "password");
    }

    private List<String> lrange(String key, long start, long stop) {
        try (Jedis jedis = jedisPool.getResource()) {
           return jedis.lrange(key, start, stop);
        }
    }

    private Long llen(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(key);
        }
    }


}
