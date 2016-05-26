package common.redis.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import common.redis.RedisConnectionFactory;

import redis.clients.jedis.ShardedJedis;

public class HashRedisTemplate extends BasicRedisTemplate {

	private static Map<String, HashRedisTemplate> _instances = new HashMap<String, HashRedisTemplate>();

	public synchronized static HashRedisTemplate getInstance(String cluster){

		if (!_instances.containsKey(cluster)){
			HashRedisTemplate template = new HashRedisTemplate();
			template.setConnectionFactory(RedisConnectionFactory.getInstance(cluster));
			template.initPool();
			
			_instances.put(cluster, template);
		}
		
		return _instances.get(cluster);
	}
    /**
     * Set the Object value of a hash field
     *
     * @param key
     * @param field
     * @param value
     * @return
     * 
     */
    public Boolean hset(final String key, final String field, final Object value) {
        return hset(key, field, value, 0);
    }

    /**
     * Set the Object value of a hash field
     *
     * @param key
     * @param field
     * @param value
     * @return
     * 
     */
    public Boolean hset(final String key, final String field, final Object value, final int ttl) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Long reply = jedis.hset(key.getBytes(), field.getBytes(), toJsonByteArray(value));
            if (ttl > 0) {
                jedis.expire(key, ttl);
            }
            return reply == 1;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get the value of a hash field
     *
     * @param key
     * @param field
     * @return
     * 
     */
    public String hget(final String key, final String field) {
        return hget(key, field, 0);
    }

    /**
     * Get the value of a hash field
     *
     * @param key
     * @param field
     * @return
     * 
     */
    public String hget(final String key, final String field, int ttl) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            String res = jedis.hget(key, field);
            if (ttl > 0) {
                jedis.expire(key, ttl);
            }
            return res;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get the value of a hash field
     *
     * @param key
     * @param field
     * @return
     * 
     */
    public <T> T hget(final String key, final String field, final Class<T> clazz) {
        return hget(key, field, clazz);
    }

    /**
     * Get the value of a hash field
     *
     * @param key
     * @param field
     * @return
     * 
     */
    public <T> T hget(final String key, final String field, final Class<T> clazz, final int ttl) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            T res = (T) fromJsonByteArray(jedis.hget(key.getBytes(), field.getBytes()), clazz);
            if (ttl > 0) {
                this.expire(key, ttl);
            }
            return res;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Delete one or more hash fields
     *
     * @param key
     * @param fields
     * @return
     * 
     */
    public Boolean hdel(final String key, final String... fields) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hdel(key, fields) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Check if a hash field exists
     *
     * @param key
     * @param field
     * @return
     * 
     */
    public Boolean hexists(final String key, final String field) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hexists(key, field);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get all the fields and values in a hash
     * 当Hash较大时候，慎用！
     * @param key
     * @return
     * 
     */
    public Map<String, String> hgetAll(final String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hgetAll(key);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get all the fields and values in a hash
     * 当Hash较大时候，慎用！
     * @param key
     * @return
     * 
     */
    public <T> Map<String, T> hgetAllObject(final String key, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Map<byte[], byte[]> byteMap = jedis.hgetAll(key.getBytes());
            if (byteMap != null && byteMap.size() > 0) {
                Map<String, T> map = new HashMap<String, T>();
                for (Entry<byte[], byte[]> e : byteMap.entrySet()) {
                    map.put(new String(e.getKey()), (T) fromJsonByteArray(e.getValue(), clazz));
                }
                return map;
            }
            return null;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get the values of all the given hash fields.
     *
     * @param key
     * @param fields
     * @return
     * 
     */
    public List<String> hmget(final String key, final String... fields) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hmget(key, fields);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 
     * Get the value of a mulit fields
     *
     * @param key
     * @param ttl
     * @param fields
     * @return
     * 
     */
    public Map<String, Object> hmgetObject(final String key, final int ttl, final Map<String, Class<?>> map) {
        ShardedJedis jedis = null;
        try {
            if (null == map) {
                return null;
            }
            jedis = pool.getResource();
            List<byte[]> byteList = new ArrayList<byte[]>();
            for (String field : map.keySet()) {
                byteList.add(field.getBytes());
            }
            List<byte[]> resBytes = jedis.hmget(key.getBytes(), byteList.toArray(new byte[byteList.size()][]));
            Map<String, Object> resMap = null;
            if (null != resBytes) {
                resMap = new HashMap<String, Object>();
                for (int i = 0; i < resBytes.size(); i++) {
                    String keys = new String(byteList.get(i));
                    resMap.put(keys, fromJsonByteArray(resBytes.get(i), map.get(keys)));
                }
            }
            if (ttl > 0) {
                this.expire(key, ttl);
            }
            return resMap;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 
     * Get the value of a mulit fields
     *
     * @param key
     * @param fields
     * @return
     * 
     */
    public Map<String, Object> hmgetObject(final String key, final Map<String, Class<?>> map) {
        return hmgetObject(key, 0, map);
    }

    /**
     * 
     * Get mulit  value of a mulit fields
     *
     * @param keys 获取的key列表
     * @param map  字段与类型的映射关系
     * @return
     */
    public List<Map<String, Object>> mget(final List<String> keys, final Map<String, Class<?>> map) {
        ShardedJedis jedis = null;
        try {
            if (null == keys || keys.size() == 0 || null == map) {
                return null;
            }
            jedis = pool.getResource();
            List<byte[]> byteList = new ArrayList<byte[]>();
            for (String field : map.keySet()) {
                byteList.add(field.getBytes());
            }
            List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
            for (String key : keys) {
                if (!this.exists(key)) {
                    continue;
                }
                List<byte[]> resBytes = jedis.hmget(key.getBytes(), byteList.toArray(new byte[byteList.size()][]));
                Map<String, Object> resMap = null;
                if (null != resBytes) {
                    resMap = new HashMap<String, Object>();
                    for (int i = 0; i < resBytes.size(); i++) {
                        String filed = new String(byteList.get(i));
                        resMap.put(filed, fromJsonByteArray(resBytes.get(i), map.get(filed)));
                    }
                }
                resList.add(resMap);
            }
            return resList;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Set multiple hash fields to multiple values.
     *
     * @param key
     * @param hash
     * @return
     * 
     */
    public String hmset(final String key, final Map<String, String> hash) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hmset(key, hash);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Set multiple hash fields to multiple values.
     *
     * @param key
     * @param hash
     * @return
     * 
     */
    public String hmsetObject(final String key, final Map<String, Object> hash) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Map<byte[], byte[]> byteMap = new HashMap<byte[], byte[]>(hash.size());
            for (Entry<String, Object> e : hash.entrySet()) {
                byteMap.put(e.getKey().getBytes(), toJsonByteArray(e.getValue()));
            }
            return jedis.hmset(key.getBytes(), byteMap);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Set multiple hash fields to multiple values.
     *
     * @param key
     * @param hash
     * @param unixTime
     * @return
     * 
     */
    public String hmsetObject(final String key, final Map<String, Object> hash, int ttl) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Map<byte[], byte[]> byteMap = new HashMap<byte[], byte[]>(hash.size());
            for (Entry<String, Object> e : hash.entrySet()) {
                byteMap.put(e.getKey().getBytes(), toJsonByteArray(e.getValue()));
            }
            String result = jedis.hmset(key.getBytes(), byteMap);
            if (ttl > 0) {
                jedis.expire(key, ttl);
            }
            return result;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Increment the integer value of a hash field by the given number.
     *
     * @param key
     * @param field
     * @param value
     * @return
     * 
     */
    public Long hincrBy(final String key, final String field, final long value) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hincrBy(key, field, value);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get all the fields in a hash.
     *
     * @param key
     * @return
     * 
     */
    public Set<String> hkeys(final String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hkeys(key);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get all the fields in a hash.
     *
     * @param key
     * @return
     * 
     */
    public <T> Set<T> hkeys(final String key, final Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<byte[]> set = jedis.hkeys(key.getBytes());
            Set<T> objectSet = new HashSet<T>();
            if (set != null && set.size() != 0) {
                for (byte[] b : set) {
                    objectSet.add((T) fromJsonByteArray(b, clazz));
                }
            }
            return objectSet;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get the number of fields in a hash.
     *
     * @param key
     * @return
     * 
     */
    public Long hlen(final String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hlen(key);
        } finally {
            this.closeRedis(jedis);
        }
    }
}
