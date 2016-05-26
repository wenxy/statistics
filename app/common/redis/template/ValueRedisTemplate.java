package common.redis.template;

import java.util.HashMap;
import java.util.Map;

import common.redis.RedisConnectionFactory;

import redis.clients.jedis.ShardedJedis;

public class ValueRedisTemplate extends BasicRedisTemplate {

	private static Map<String, ValueRedisTemplate> _instances = new HashMap<String, ValueRedisTemplate>();

	public synchronized static ValueRedisTemplate getInstance(String cluster){

		if (!_instances.containsKey(cluster)){
			ValueRedisTemplate template = new ValueRedisTemplate();
			template.setConnectionFactory(RedisConnectionFactory.getInstance(cluster));
			template.initPool();
			
			_instances.put(cluster, template);
		}
		
		return _instances.get(cluster);
	}
    /**
     * set key-value
     *
     * @param key
     * @param value String 
     */
    public void set(String key, String value) {
        set(key, value, 0);
    }

    /**
     * set key-value
     *
     * @param key
     * @param value String 
     * 
     * 
     */
    public void set(String key, String value, int ttl) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
            if (ttl > 0) {
                jedis.expire(key.getBytes(), ttl);
            }
        } finally {
            this.closeRedis(jedis);
        }
    }
    /**
     * 指定时间失效
     * @param key
     * @param value
     * @param unixTime
     */
    public void setAtExpire(String key, String value, long unixTime) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
            if (unixTime > 0) {
                jedis.expireAt(key.getBytes(), unixTime);
            }
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * set key-value For Object(NOT String)
     *
     * @param key
     * @param value Object
     * 
     * 
     */
    public void set(String key, Object value) {
        set(key, value, 0);
    }

    /**
     * set key-value For Object(NOT String)
     *
     * @param key
     * @param value Object
     * @param ttl int
     * 
     * 
     */
    public void set(String key, Object value, int ttl) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key.getBytes(), toJsonByteArray(value));
            if (ttl > 0) {
                jedis.expire(key.getBytes(), ttl);
            }
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * set key-value with expired time(s)
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     * 
     * 
     */
    public String setex(String key, int seconds, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setex(key, seconds, value);

        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * set key-value For Object(NOT String) with expired time(s)
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     * 
     * 
     */
    public String setex(String key, int seconds, Object value) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setex(key.getBytes(), seconds, toJsonByteArray(value));

        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 如果key不存在则进行设置，返回true，否则返回false.
     *
     * @param key
     * @param value
     * @return 
     * 
     * 
     */
    public boolean setnx(String key, String value, int ttl) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Long reply = jedis.setnx(key, value);
            if (ttl > 0) {
                jedis.expire(key, ttl);
            }
            return reply == 1;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 如果key不存在则进行设置，返回true，否则返回false.
     *
     * @param key
     * @param value
     * @return 
     * 
     * 
     */
    public boolean setnx(String key, String value) {
        return setnx(key, value, 0);
    }

    /**
     * 如果key不存在则进行设置 For Object，返回true，否则返回false.
     *
     * @param key
     * @param value
     * @return 
     * 
     * 
     */
    public boolean setnx(String key, Object value) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setnx(key.getBytes(), toJsonByteArray(value)) == 1 ? true : false;

        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 如果key不存在则进行设置 For Object，返回true，否则返回false.
     *
     * @param key
     * @param value
     * @return 
     * 
     * 
     */
    public boolean setnx(String key, Object value, int ttl) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Long reply = jedis.setnx(key.getBytes(), toJsonByteArray(value));
            if (ttl > 0) {
                jedis.expire(key.getBytes(), ttl);
            }
            return reply == 1;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 如果key不存在, 返回null.
     *
     * @param key
     * @return
     * 
     */
    public String get(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            String result = jedis.get(key);
            if(result == null || result.equalsIgnoreCase("null")){
            	return null;
            }
            return result;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, 如果key不存在, 返回null.
     *
     * @param key
     * @return
     * 
     */
    public <T> T get(String key, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return (T) fromJsonByteArray(jedis.get(key.getBytes()), clazz);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 自增 +1
     *
     * @param key
     * @return 返回自增后结果
     * 
     */
    public Long incr(final String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.incr(key);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 自增 +1
     *
     * @param key
     * @param ttl 单位为s
     * @return 返回自增后结果
     * 
     */
    public Long incr(final String key, int ttl) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            long count = jedis.incr(key);
            if (ttl > 0) {
                jedis.expire(key, ttl);
            }
            return count;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 自减 -1
     *
     * @param key
     * @return 返回自减后结果
     * 
     */
    public Long decr(final String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.decr(key);
        } finally {
            this.closeRedis(jedis);
        }
    }

}
