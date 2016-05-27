package read.factory.product.defaults;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.RedisUtil;

public class Pro_default_OLDPAYUSER_KPI extends IReadWrite{
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			String skey = RedisUtil.apply(date, ch, gameId, KPI.OLDPAYUSER_KPI.raw());
			String ckey = RedisUtil.apply(date, ch, gameId, Action.PAY_ACTION.raw(),KPI.OLDPAYUSER_KPI.raw());//计算key caculateKey

			String redisResult = readFromRedis(skey);
			if(!StringUtils.isEmpty(redisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				return redisResult;
			}
			
			File file = getReadStoreFile(caller,date,gameId,ch,Action.PAY_ACTION.raw(),KPI.OLDPAYUSER_KPI.raw());
			//计算数 
			long count = caculateSingleSize(file,ckey,MQInstance.BASE);
			writeToRedis(skey,String.valueOf(count),KPI_CACHE_SEC);
			
			return String.valueOf(count);
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "0";
	}
}