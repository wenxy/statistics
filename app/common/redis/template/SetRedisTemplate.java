package common.redis.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import common.redis.RedisConnectionFactory;

import redis.clients.jedis.ShardedJedis;

public class SetRedisTemplate extends BasicRedisTemplate {

	private static Map<String, SetRedisTemplate> _instances = new HashMap<String, SetRedisTemplate>();

	public synchronized static SetRedisTemplate getInstance(String cluster){

		if (!_instances.containsKey(cluster)){
			SetRedisTemplate template = new SetRedisTemplate();
			template.setConnectionFactory(RedisConnectionFactory.getInstance(cluster));
			template.initPool();
			
			_instances.put(cluster, template);
		}
		
		return _instances.get(cluster);
	}
    /**
     * Add one or more members to a set
     *
     * @param key
     * @param members
     * @return
     * 
     */
    public Boolean sadd(final String key, final String... members) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, members) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 设置过期时间
     * @param key
     * @param unixTime
     * @return
     */
    public Boolean sexpireAt(final String key,long unixTime){
    	ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.expireAt(key, unixTime) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }    /**
     * For Object, Add one or more members to a set
     *
     * @param key
     * @param members
     * @return
     * 
     */
    public Boolean sadd(final String key, final Object... members) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            byte[][] strings = new byte[members.length][];
            for (int j = 0; j < members.length; j++) {
                strings[j] = toJsonByteArray(members[j]);
            }
            return jedis.sadd(key.getBytes(), strings) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Remove one or more members from a set
     *
     * @param key
     * @param members
     * @return
     * 
     */
    public Boolean srem(final String key, final String... members) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, members) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Remove one or more members from a set
     *
     * @param key
     * @param members
     * @return
     * 
     */
    public Boolean srem(final String key, final Object... members) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            byte[][] strings = new byte[members.length][];
            for (int j = 0; j < members.length; j++) {
                strings[j] = toJsonByteArray(members[j]);
            }
            return jedis.srem(key.getBytes(), strings) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get all the members in a set.
     *
     * @param key
     * @return
     * 
     */
    public Set<String> smembers(final String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.smembers(key);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Get all the members in a set.
     *
     * @param key
     * @param clazz
     * @return
     * 
     */
    public <T> Set<T> smembers(final String key, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<byte[]> tempSet = jedis.smembers(key.getBytes());
            if (tempSet != null && tempSet.size() > 0) {
                TreeSet<T> result = new TreeSet<T>();
                for (byte[] value : tempSet) {
                    result.add((T) fromJsonByteArray(value, clazz));
                }
                return result;
            }
            return null;
        } finally {
            this.closeRedis(jedis);
        }
    }
    
    /**
     * 返回集合中的元素数量
     * @param key
     * @return
     */
    public long scard(final String key){
    	 ShardedJedis jedis = null;
         try {
             jedis = pool.getResource();
             return jedis.scard(key);
         } finally {
             this.closeRedis(jedis);
         }
    }
    
    /**
     * 差集
     * key-keys的集合
     * @param key
     * @param clazz
     * @param keys
     * @return
     */
    public <T> Set<T> sdiff(Class<T> clazz,final String... keys) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<String> tempSet = jedis.sdiff(keys);
            if (tempSet != null && tempSet.size() > 0) {
                TreeSet<T> result = new TreeSet<T>();
                for (String value : tempSet) {
                    result.add((T) fromJsonByteArray(value.getBytes(), clazz));
                }
                return result;
            }
            return null;
        } finally {
            this.closeRedis(jedis);
        }
    }
    
    /**
     * 差集
     * key-keys的集合
     * @param key
     * @param clazz
     * @param keys
     * @return
     */
    public Set<String> sdiff(final String... keys) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return  jedis.sdiff(keys);
        } finally {
            this.closeRedis(jedis);
        }
    }
    
    /**
     * 差集合
     * 结果集存储到新的key
     * @param dstkey
     * @param keys
     * @return
     */
    public long sdiffstore(String dstkey,final String... keys) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Long result = jedis.sdiffstore(dstkey, keys);
            return  result==null?0:result;
        } finally {
            this.closeRedis(jedis);
        }
    }
    
    
    
    /**
     * 交集
     * key-keys的集合
     * @param key
     * @param clazz
     * @param keys
     * @return
     */
    public <T> Set<T> sinter(Class<T> clazz,final String... keys) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<String> tempSet = jedis.sinter(keys);
            if (tempSet != null && tempSet.size() > 0) {
                TreeSet<T> result = new TreeSet<T>();
                for (String value : tempSet) {
                    result.add((T) fromJsonByteArray(value.getBytes(), clazz));
                }
                return result;
            }
            return null;
        } finally {
            this.closeRedis(jedis);
        }
    }
    
    /**
     * 交集
     * key-keys的集合
     * @param key
     * @param clazz
     * @param keys
     * @return
     */
    public Set<String> sinter(final String... keys) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return  jedis.sinter(keys);
        } finally {
            this.closeRedis(jedis);
        }
    }
    
    /**
     * 交集
     * 结果集存储到新的key
     * @param dstkey
     * @param keys
     * @return
     */
    public long sinterstore(String dstkey,final String... keys) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Long result = jedis.sinterstore(dstkey, keys);
            return  result==null?0:result;
        } finally {
            this.closeRedis(jedis);
        }
    }

    
    /**
     * 并集
     * key-keys的集合
     * @param key
     * @param clazz
     * @param keys
     * @return
     */
    public <T> Set<T> sunion(Class<T> clazz,final String... keys) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<String> tempSet = jedis.sunion(keys);
            if (tempSet != null && tempSet.size() > 0) {
                TreeSet<T> result = new TreeSet<T>();
                for (String value : tempSet) {
                    result.add((T) fromJsonByteArray(value.getBytes(), clazz));
                }
                return result;
            }
            return null;
        } finally {
            this.closeRedis(jedis);
        }
    }
    
    /**
     * 并集
     * key-keys的集合
     * @param key
     * @param clazz
     * @param keys
     * @return
     */
    public Set<String> sunion(final String... keys) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return  jedis.sunion(keys);
        } finally {
            this.closeRedis(jedis);
        }
    }
    
    /**
     * 并集
     * 结果集存储到新的key
     * @param dstkey
     * @param keys
     * @return
     */
    public long sunionstore(String dstkey,final String... keys) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Long result = jedis.sunionstore(dstkey, keys);
            return  result==null?0:result;
        } finally {
            this.closeRedis(jedis);
        }
    }
}
