package common.redis.template;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import common.redis.RedisConnectionFactory;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.Tuple;

public class ZSetRedisTemplate extends BasicRedisTemplate {
	private static Map<String, ZSetRedisTemplate> _instances = new HashMap<String, ZSetRedisTemplate>();

	public synchronized static ZSetRedisTemplate getInstance(String cluster){

		if (!_instances.containsKey(cluster)){
			ZSetRedisTemplate template = new ZSetRedisTemplate();
			template.setConnectionFactory(RedisConnectionFactory.getInstance(cluster));
			template.initPool();
			
			_instances.put(cluster, template);
		}
		
		return _instances.get(cluster);
	}
    /**
     * 加入Sorted set, 如果member在Set里已存在, 只更新score并返回false, 否则返回true.
     *
     * @param key
     * @param member
     * @param score
     * @return
     * 
     */
    public Boolean zadd(String key, double score, String member) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, member) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, 加入Sorted set, 如果member在Set里已存在, 只更新score并返回false, 否则返回true.
     *
     * @param key
     * @param member
     * @param score
     * @return
     * 
     */
    public Boolean zadd(String key, double score, Object member) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key.getBytes(), score, toJsonByteArray(member)) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Return a range of members in a sorted set, by index. Ordered from the lowest to the highest score.
     *
     * @param key
     * @param start
     * @param end
     * @return Ordered from the lowest to the highest score.
     * 
     */
    public Set<String> zrange(String key, long start, long end) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Return a range of members in a sorted set, by index.Ordered from the lowest to the highest score.
     *
     * @param key
     * @param start
     * @param end
     * @return Ordered from the lowest to the highest score.
     * 
     */
    public <T> Set<T> zrange(String key, long start, long end, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<byte[]> tempSet = jedis.zrange(key.getBytes(), start, end);
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
     * Return a range of members in a sorted set, by index. Ordered from the highest to the lowest score.
     *
     * @param key
     * @param start
     * @param end
     * @return Ordered from the highest to the lowest score.
     * 
     */
    public Set<String> zrevrange(String key, long start, long end) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Return a range of members in a sorted set, by index. Ordered from the highest to the lowest score.
     *
     * @param key
     * @param start
     * @param end
     * @param clazz
     * @return Ordered from the highest to the lowest score.
     * 
     */
    public <T> Set<T> zrevrange(String key, long start, long end, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<byte[]> tempSet = jedis.zrevrange(key.getBytes(), start, end);
            if (tempSet != null && tempSet.size() > 0) {
                Set<T> result = new TreeSet<T>();
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
     * Return the all the elements in the sorted set at key with a score between
     * min and max (including elements with score equal to min or max).
     * 
     * @param key
     * @param min
     * @param max
     * @return Ordered from the lowest to the highest score.
     * 
     */
    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScore(key, min, max);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Return the all the elements in the sorted set at key with a score between
     * min and max (including elements with score equal to min or max).
     * 
     * @param key
     * @param min
     * @param max
     * @return Ordered from the lowest to the highest score.
     * 
     */
    public <T> Set<T> zrangeByScore(final String key, final double min, final double max, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<byte[]> tempSet = jedis.zrangeByScore(key.getBytes(), min, max);
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
     * For Object, Return the all the elements in the sorted set at key with a score between
     * min and max (including elements with score equal to min or max).
     * 
     * @param key
     * @param min
     * @param max
     * @return Ordered from the lowest to the highest score.
     * 
     */
    public <T> Set<T> zrangeHashSetByScore(final String key, final double min, final double max, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<byte[]> tempSet = jedis.zrangeByScore(key.getBytes(), min, max);
            if (tempSet != null && tempSet.size() > 0) {
                HashSet<T> result = new HashSet<T>();
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
     * Return the all the elements in the sorted set at key with a score between
     * min and max (including elements with score equal to min or max).
     * @param key
     * @param min
     * @param max
     * @return Ordered from the highest to the lowest score.
     * 
     */
    public Set<String> zrevrangeByScore(final String key, final double min, final double max) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeByScore(key, max, min);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Return the all the elements in the sorted set at key with a score between
     * min and max (including elements with score equal to min or max).
     * @param key
     * @param min
     * @param max
     * @param clazz
     * @return Ordered from the highest to the lowest score.
     * 
     */
    public <T> Set<T> zrevrangeByScore(final String key, final double min, final double max, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<byte[]> tempSet = jedis.zrevrangeByScore(key.getBytes(), max, min);
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
     * 分页版-For Object, Return the elements in the sorted set at key with a score between
     * min and max (including elements with score equal to min or max)
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @param clazz
     * @return Ordered from the highest to the lowest score.
     * 
     */
    public <T> Set<T> zrevrangeByScore(final String key, final double min, final double max, int offset, int count, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<byte[]> tempSet = jedis.zrevrangeByScore(key.getBytes(), max, min, offset, count);
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
     * Return a range of members with scores in a sorted set, by index. Ordered from the lowest to the highest score.
     *
     * @param key
     * @param start
     * @param end
     * @return
     * 
     */
    public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeWithScores(key, start, end);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Return a range of members with scores in a sorted set, by index. Ordered from the highest  to the lowest score.
     *
     * @param key
     * @param start
     * @param end
     * @return
     * 
     */
    public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeWithScores(key, start, end);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Return the all the elements in the sorted set at key with a score between
     * min and max (including elements with score equal to min or max). Ordered from the lowest to the highest score.
     *
     * @param key
     * @param min
     * @param max
     * @return Ordered from the lowest to the highest score.
     * 
     */
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScoreWithScores(key, min, max);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Return the all the elements in the sorted set at key with a score between
     * min and max (including elements with score equal to min or max). Ordered from the highest to the lowest score.
     *
     * @param key
     * @param min
     * @param max
     * @return Ordered from the highest to the lowest score.
     * 
     */
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double min, final double max) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Remove one or more members from a sorted set
     *
     * @param key
     * @param members
     * @return
     * 
     */
    public Boolean zrem(final String key, final String... members) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrem(key, members) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For Object, Remove one or more members from a sorted set
     *
     * @param key
     * @param members
     * @return
     * 
     */
    public Boolean zrem(final String key, final Object... members) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            byte[][] strings = new byte[members.length][];
            for (int j = 0; j < members.length; j++) {
                strings[j] = toJsonByteArray(members[j]);
            }
            return jedis.zrem(key.getBytes(), strings) == 1 ? true : false;
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Get the score associated with the given member in a sorted set
     *
     * @param key
     * @param member
     * @return
     * 
     */
    public Double zscore(final String key, final String member) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * For ObjecGet the score associated with the given member in a sorted set
     *
     * @param key
     * @param member
     * @return
     * 
     */
    public Double zscore(final String key, final Object member) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key.getBytes(), toJsonByteArray(member));
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * Remove all elements in the sorted set at key with rank between start and
     * end. Start and end are 0-based with rank 0 being the element with the
     * lowest score. Both start and end can be negative numbers, where they
     * indicate offsets starting at the element with the highest rank. For
     * example: -1 is the element with the highest score, -2 the element with
     * the second highest score and so forth.
     * <p>
     * <b>Time complexity:</b> O(log(N))+O(M) with N being the number of
     * elements in the sorted set and M the number of elements removed by the
     * operation
     * @param key
     * @param start
     * @param end
     * @return
     * 
     */
    public Long zremrangeByRank(String key, long start, long end) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zremrangeByRank(key, start, end);
        } finally {
            this.closeRedis(jedis);
        }
    }

    public Long zremrangeByScore(String key, long min, long max) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zremrangeByScore(key, min, max);
        } finally {
            this.closeRedis(jedis);
        }
    }

    /**
     * 
     * Get the length of a sorted set
     * 
     * @param key
     * @param min
     * @param max
     * @return
     * 
     */
    public Long zcount(final String key, final double min, final double max) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcount(key, min, max);
        } finally {
            this.closeRedis(jedis);
        }
    }

    public Long zcard(final String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } finally {
            this.closeRedis(jedis);
        }
    }

}
