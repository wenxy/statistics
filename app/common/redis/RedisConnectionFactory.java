package common.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

public class RedisConnectionFactory{

    // 格式为： 127.0.0.1:6379,10.1.2.242:6379 多个之间用逗号分隔
    // 对于server的权重目前没有需求，统一用默认值
    protected String servers;

    private int timeout = 3000; // 3seconds

    private boolean usePool = true;

    protected ShardedJedisPool pool;

    private JedisPoolConfig poolConfig;

    private static Map<String, RedisConnectionFactory> _instances = new HashMap<String, RedisConnectionFactory>();
    		
    public static synchronized RedisConnectionFactory getInstance(String cluster) {
    	
    	if (!_instances.containsKey(cluster)){
    		JedisPoolConfig poolConfig = new JedisPoolConfig();

    		poolConfig.setMinIdle(Integer.parseInt(Jws.configuration.getProperty("redis."+cluster+".pool.minIdle", "5")));
    		poolConfig.setMaxIdle(Integer.parseInt(Jws.configuration.getProperty("redis."+cluster+".pool.maxIdle", "20")));
    		poolConfig.setMaxTotal(Integer.parseInt(Jws.configuration.getProperty("redis."+cluster+".pool.maxTotal", "20")));
    		poolConfig.setMaxWaitMillis(Integer.parseInt(Jws.configuration.getProperty("redis."+cluster+".pool.maxWaitMillis", "1000")));
    		poolConfig.setTestOnBorrow(true);
    	
    		RedisConnectionFactory factory = new RedisConnectionFactory();
    		factory.setPoolConfig(poolConfig);
    		factory.setServers(Jws.configuration.getProperty("redis."+cluster+".servers.host", "127.0.0.1:6379"));
    		factory.setTimeout(Integer.parseInt(Jws.configuration.getProperty("redis."+cluster+".servers.timeout", "1000")));
    		factory.setUsePool(true);
    		
    		factory.init();
    		
    		_instances.put(cluster, factory);
    	}
    	
    	return _instances.get(cluster);
    }

    public void init(){
        List<JedisShardInfo> shardInfoList = new ArrayList<JedisShardInfo>();
        for (String server : servers.split("[,]")) {
            String[] sa = server.split("[:]");
            if (sa.length == 2) {
                String host = sa[0];
                int port = Integer.parseInt(sa[1]);
                // new JedisShardInfo(host, port);
                shardInfoList.add(new JedisShardInfo(host, port, timeout));
            }
        }
        pool = new ShardedJedisPool(poolConfig, shardInfoList);
    }

    public ShardedJedisPool getConnectionPool() {
        if (pool == null) {
            try {
                init();
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            }
        }
        return this.pool;
    }

    public void destroy() throws Exception {
        if (usePool && pool != null) {
            try {
                pool.destroy();
            } catch (Exception ex) {
                Logger.warn("Cannot properly close Jedis pool", ex);
            }
            pool = null;
        }
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isUsePool() {
        return usePool;
    }

    public void setUsePool(boolean usePool) {
        this.usePool = usePool;
    }

    public JedisPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public void setPoolConfig(JedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

}
