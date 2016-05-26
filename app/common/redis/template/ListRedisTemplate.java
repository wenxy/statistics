package common.redis.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.ShardedJedis;

import com.google.common.collect.Lists;
import common.redis.RedisConnectionFactory;

public class ListRedisTemplate extends BasicRedisTemplate {

	private static Map<String, ListRedisTemplate> _instances = new HashMap<String, ListRedisTemplate>();

	public synchronized static ListRedisTemplate getInstance(String cluster){

		if (!_instances.containsKey(cluster)){
			ListRedisTemplate template = new ListRedisTemplate();
			template.setConnectionFactory(RedisConnectionFactory.getInstance(cluster));
			template.initPool();
			
			_instances.put(cluster, template);
		}
		
		return _instances.get(cluster);
	}
    /**
     * Prepend one or multiple values to a list
     *
     * @param key
     * @param values
     * 
     */
    public void lpush(String key, String... values) {
        ShardedJedis jedis = null;
        if (values == null)
            return;
        try {
            jedis = pool.getResource();
            jedis.lpush(key, values);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object,Prepend one or multiple values to a list
     *
     * @param key
     * @param values
     * 
     */
    public void lpush(String key, Object... values) {
        ShardedJedis jedis = null;
        if (values == null)
            return;
        try {
            jedis = pool.getResource();
            byte[][] strings = new byte[values.length][];
            for (int j = 0; j < values.length; j++) {
                strings[j] = toJsonByteArray(values[j]);
            }
            jedis.lpush(key.getBytes(), strings);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For single Object,Prepend one or multiple values to a list
     *
     * @param key
     * @param values
     * 
     */
    public void lpushForObject(String key, Object value) {
        ShardedJedis jedis = null;
        if (value == null)
            return;
        try {
            jedis = pool.getResource();
            jedis.lpush(key.getBytes(), toJsonByteArray(value));
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For single Object,Prepend one or multiple values to a list
     *
     * @param key
     * @param values
     * 
     */
    public void rpushForObject(String key, Object value) {
        ShardedJedis jedis = null;
        if (value == null)
            return;
        try {
            jedis = pool.getResource();
            jedis.rpush(key.getBytes(), toJsonByteArray(value));
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Append one or multiple values to a list
     *
     * @param key
     * @param values
     * 
     */
    public void rpush(String key, String... values) {
        ShardedJedis jedis = null;
        if (values == null)
            return;
        try {
            jedis = pool.getResource();
            jedis.rpush(key, values);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Append one or multiple values to a list
     *
     * @param key
     * @param values
     * 
     */
    public void rpush(String key, Object... values) {
        ShardedJedis jedis = null;
        if (values == null)
            return;
        try {
            jedis = pool.getResource();
            byte[][] strings = new byte[values.length][];
            for (int j = 0; j < values.length; j++) {
                strings[j] = toJsonByteArray(values[j]);
            }
            jedis.rpush(key.getBytes(), strings);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Remove and get the last element in a list
     *
     * @param key
     * @return
     * 
     */
    public String rpop(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.rpop(key);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Remove and get the last element in a list
     *
     * @param key
     * @param clazz
     * @return
     * 
     */
    public <T> T rpop(String key, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return (T) fromJsonByteArray(jedis.rpop(key.getBytes()), clazz);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Remove and get the first element in a list
     *
     * @param key
     * @return
     * 
     */
    public String lpop(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpop(key);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Remove and get the first element in a list
     *
     * @param key
     * @param clazz
     * @return
     * 
     */
    public <T> T lpop(String key, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return (T) fromJsonByteArray(jedis.lpop(key.getBytes()), clazz);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get the length of a list
     *
     * @param key
     * @return
     * 
     */
    public Long llen(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.llen(key);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 删除List中的等于value的元素
     * 
     * count = 1 :删除第一个；
     * count = 0 :删除所有；
     * count = -1:删除最后一个；
     *
     * @param key
     * @param count
     * @param value
     * @return
     * 
     */
    public Long lrem(String key, long count, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrem(key, count, value);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * FOR Object, 删除List中的等于value的元素
     * 
     * count = 1 :删除第一个；
     * count = 0 :删除所有；
     * count = -1:删除最后一个；
     *
     * @param key
     * @param count
     * @param value
     * @return
     * 
     */
    public Long lrem(String key, long count, Object value) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrem(key.getBytes(), count, toJsonByteArray(value));
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get a range of elements from a list.
     * <P>
     * For example LRANGE foobar 0 2 will return the first three elements of the
     * list.
     * </p>
     * <P>
     * For example LRANGE foobar -1 -2 will return the last two elements of the
     * list.
     * </p>
     * @param key
     * @param start
     * @param end
     * @return
     * 
     */
    public List<String> lrange(String key, long start, long end) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Get a range of elements from a list.
     * <P>
     * For example LRANGE foobar 0 2 will return the first three elements of the
     * list.
     * </p>
     * <P>
     * For example LRANGE foobar -1 -2 will return the last two elements of the
     * list.
     * </p>
     * @param key
     * @param start
     * @param end
     * @return
     * 
     */
    public <T> List<T> lrange(String key, long start, long end, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            List<byte[]> list = jedis.lrange(key.getBytes(), start, end);
            if (list != null && list.size() > 0) {
                List<T> results = Lists.newArrayList();
                for (byte[] bytes : list) {
                    results.add((T) fromJsonByteArray(bytes, clazz));
                }
                return results;
            }
            return null;
        } finally {
            this.closeRedis(jedis);
        }
    }
    
    
}
