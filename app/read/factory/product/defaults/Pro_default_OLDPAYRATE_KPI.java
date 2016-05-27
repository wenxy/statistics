package read.factory.product.defaults;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.RedisUtil;

public class Pro_default_OLDPAYRATE_KPI extends IReadWrite{
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			//先查舊付費用戶數
			long oldpaycount = 0;
			String oldPayuser_skey = RedisUtil.apply(date, ch, gameId, KPI.OLDPAYUSER_KPI.raw());
			String oldPayUser_ckey = RedisUtil.apply(date, ch, gameId, Action.PAY_ACTION.raw(),KPI.OLDPAYUSER_KPI.raw());//计算key caculateKey

			String oldPayUseredisResult = readFromRedis(oldPayuser_skey);
			if(!StringUtils.isEmpty(oldPayUseredisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				oldpaycount = Long.parseLong(oldPayUseredisResult);
			}
			
			if(oldpaycount == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.PAY_ACTION.raw(),KPI.OLDPAYUSER_KPI.raw());
				//计算数 
				oldpaycount = caculateSingleSize(file,oldPayUser_ckey,MQInstance.BASE);
				writeToRedis(oldPayuser_skey,String.valueOf(oldpaycount),KPI_CACHE_SEC);
			}
			
			if(oldpaycount == 0){
				return "0";
			}
			
			//總付費用戶數 
			String skey = RedisUtil.apply(date, ch, gameId, KPI.PAYUSER_KPI.raw());
			String ckey = RedisUtil.apply(date, ch, gameId, Action.PAY_ACTION.raw(),KPI.PAYUSER_KPI.raw());//计算key caculateKey
			long payCount = 0;
			String redisResult = readFromRedis(skey);
			if(!StringUtils.isEmpty(redisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				payCount = Long.parseLong(redisResult);
			}
			if(payCount == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.PAY_ACTION.raw(),KPI.PAYUSER_KPI.raw());
				//计算数 
				payCount = caculateSingleSize(file,ckey,MQInstance.BASE);
				writeToRedis(skey,String.valueOf(payCount),KPI_CACHE_SEC);
			} 
			return String.valueOf(oldpaycount/payCount);
		}catch(Exception e){
			Logger.error(e, "0");
		}
		
		return "0";
	}
}
