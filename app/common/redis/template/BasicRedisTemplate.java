package common.redis.template;

import jws.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

import common.redis.RedisConnectionFactory;
import common.redis.RedisException;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public abstract class BasicRedisTemplate {

    protected RedisConnectionFactory connectionFactory;

    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    protected ShardedJedisPool pool;
    protected void initPool() {
        this.pool = connectionFactory.getConnectionPool();
    }
    
    protected void closeRedis(ShardedJedis jedis) {
        if (jedis != null) {
            try {
                pool.returnResource(jedis);
            } catch (Exception e) {
                Logger.error("Error happen when return jedis to pool, try to close it directly.");
                if ((jedis != null)) {
                    try {
                        jedis.disconnect();
                    } catch (Exception e1) {
                    }
                }
            }
        }
    }

    /**
     * 删除key, 如果key存在返回true, 否则返回false。
     *
     * @param key
     * @return
     * 
     */
    public boolean del(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.del(key) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * true if the key exists, otherwise false
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.exists(key);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
    * set key expired time
    *
    * @param key
    * @param seconds
    * @return 
    */
    public boolean expire(String key, int seconds) {
        ShardedJedis jedis = null;
        if (seconds == 0)
            return true;
        try {
            jedis = pool.getResource();
            return jedis.expire(key, seconds) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 
     * 把object转换为json byte array
     *
     * @param o
     * @return
     */
    protected byte[] toJsonByteArray(Object o) {
        return new JsonRedisSerializer().serialize(o);
    }

    /**
     * 
     * 把json byte array转换为T类型object
     *
     * @param b
     * @param clazz
     * @return
     */
    protected <T> T fromJsonByteArray(byte[] b, Class<T> clazz) {
        return new JsonRedisSerializer().deserialize(b, clazz);
    }

    static class JsonRedisSerializer {

        private ObjectMapper objectMapper = new ObjectMapper();

        static boolean isEmpty(byte[] data) {
            return (data == null || data.length == 0);
        }

        static JavaType getJavaType(Class<?> clazz) {
            return TypeFactory.defaultInstance().constructType(clazz);
        }

        public <T> T deserialize(byte[] bytes, Class<T> clazz) {
            if (isEmpty(bytes)) {
                return null;
            }
            try {
                return (T) this.objectMapper.readValue(bytes, 0, bytes.length, getJavaType(clazz));
            } catch (Exception e) {
                throw new RedisException("反序列化json失败,e: " + e.getMessage(), e);
            }
        }

        public byte[] serialize(Object t) {
            if (t == null) {
                return new byte[0];
            }
            try {
                return this.objectMapper.writeValueAsBytes(t);
            } catch (Exception e) {
                throw new RedisException("序列化json失败,e: " + e.getMessage(), e);
            }
        }
    }

}
