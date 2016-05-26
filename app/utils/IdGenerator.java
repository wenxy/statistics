package utils;

import java.util.Random;
import java.util.UUID;

import common.redis.template.ValueRedisTemplate;
import jws.Logger;
import jws.dal.Cache;


public final class IdGenerator {
	
	private static final int EXPIRE_TIME = 72000;
	
	private static final String KEY = "dc";
	
	private static final String CACHE_PREFIX = "id_";

	private static ValueRedisTemplate valueRedis = ValueRedisTemplate.getInstance("sid");

	/**
	 * 获取全局唯一ID，但不保证有序
	 * @return
	 */
	public static Long getId() {
		return Long.valueOf(System.currentTimeMillis() * 10000L + getRandNum());
	} 
	/** 
     * 获得一个UUID 
     * @return String UUID 
     */ 
    public static String getUUID(){ 
        String s = UUID.randomUUID().toString(); 
        //去掉“-”符号 
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
    } 

	private static long getRandNum() {
	    Long num = null;
	    try{
	        num = Cache.incr(KEY, 1, 1L, Integer.valueOf(EXPIRE_TIME), CACHE_PREFIX);
	    }catch(Exception e){
	        // continue
	        Logger.error(e, "IdGeneration need a stable mc");
	    }
		if (num == null) {
			return Math.round(Math.random() * 10000D);
		}
		if (num.longValue() >= 9999L) {
			Cache.delete(KEY, CACHE_PREFIX);
		}
		return num.longValue();
	}
	
	/**
	 * 生成指定长度的随机数
	 * @param length 随机数位数
	 * @return
	 */
	public static int getRandNumByLength(int length) {
		return (int)((Math.random() * 9 + 1) * Math.pow(10, length-1));
	}
	
	/**
	 * 返回1到max之间的随机整数
	 * @param max
	 * @return
	 */
	public static int getRandNumByScope(int max) {
		Random random = new Random();
		int result = random.nextInt(max) + 1;
		return result;
	}
	
}