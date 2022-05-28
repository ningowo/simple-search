package team.snof.simplesearch.search.infra;

import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class JedisPoolSingleton {

    public static final JedisPool jedisPool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(8);
        config.setMaxTotal(18);
        jedisPool = new JedisPool(config, "127.0.0.1", 6379, 2000, "password");
    }

    public static JedisPool getJedisPool() {
        return jedisPool;
    }

}
