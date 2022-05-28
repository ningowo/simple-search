package team.snof.simplesearch.search.infra.redis;

import redis.clients.jedis.Jedis;

public interface SafeCallWithRedis {

    void call(Jedis redis);

}
