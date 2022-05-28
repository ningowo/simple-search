package team.snof.simplesearch.search.infra.redis;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisPoolExecutor {

    private final JedisPool jedisPool;

    public RedisPoolExecutor() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(8);
        config.setMaxTotal(18);
        jedisPool = new JedisPool(config, "127.0.0.1", 6379, 2000, "password");
    }

    public void execute(SafeCallWithRedis caller) {
        try (Jedis jedis = jedisPool.getResource()) {
            caller.call(jedis);
        }
    }

}