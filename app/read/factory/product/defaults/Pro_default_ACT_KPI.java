package read.factory.product.defaults;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import common.redis.template.SetRedisTemplate;
import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.DateUtil;
import utils.RedisUtil;
/**
 * 顺玩激活KPI
 * @author fish
 *
 */
public class Pro_default_ACT_KPI extends IReadWrite{

	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			
			String skey = RedisUtil.apply(caller,date, ch, gameId, KPI.ACT_KPI.raw());//存储key storeKey
			String ckey = RedisUtil.apply(caller,date, ch, gameId, Action.ACT_ACTION.raw(),KPI.ACT_KPI.raw());//计算key caculateKey

			String redisResult = readFromRedis(skey);
			
			long count = 0;
			try{
				count = Long.parseLong(redisResult);
			}catch(NumberFormatException e){
				
			}
			if(count>0 && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				return redisResult;
			}
			
			File file = getReadStoreFile(caller,date,gameId,ch,Action.ACT_ACTION.raw(),KPI.ACT_KPI.raw());
			 
			//计算数 
			count = caculateSingleSize(file,ckey,MQInstance.BASE);
			writeToRedis(skey,String.valueOf(count),KPI_CACHE_SEC); 
			
			return String.valueOf(count);
		}catch(Exception e){
			Logger.error(e, "");
		}
		return "0";
	}

}
