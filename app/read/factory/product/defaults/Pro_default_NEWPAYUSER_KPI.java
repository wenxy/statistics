package read.factory.product.defaults;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.RedisUtil;

public class Pro_default_NEWPAYUSER_KPI extends IReadWrite{
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			String skey = RedisUtil.apply(caller,date, ch, gameId, KPI.NEWPAYUSER_KPI.raw());
			String ckey = RedisUtil.apply(caller,date, ch, gameId, Action.PAY_ACTION.raw(),KPI.NEWPAYUSER_KPI.raw());//计算key caculateKey

			long count = 0;
			try{
				String redisResult = readFromRedis(skey);
				count = Long.parseLong(redisResult);
			}catch(NumberFormatException e){
				
			}
 			if(count>0 && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				return String.valueOf(count);
			}
			
			File file = getReadStoreFile(caller,date,gameId,ch,Action.PAY_ACTION.raw(),KPI.NEWPAYUSER_KPI.raw());
			//计算数 
			count = caculateSingleSize(file,ckey,MQInstance.BASE);
			writeToRedis(skey,String.valueOf(count),KPI_CACHE_SEC);
			
			return String.valueOf(count);
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "";
	}
}
