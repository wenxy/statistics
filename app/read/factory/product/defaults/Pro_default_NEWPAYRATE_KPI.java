package read.factory.product.defaults;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.RedisUtil;

public class Pro_default_NEWPAYRATE_KPI extends IReadWrite{
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			//先查新付費用戶數
			long newpaycount = 0;
			String newPayuser_skey = RedisUtil.apply(date, ch, gameId, KPI.NEWPAYUSER_KPI.raw());
			String newPayUser_ckey = RedisUtil.apply(date, ch, gameId, Action.PAY_ACTION.raw(),KPI.NEWPAYUSER_KPI.raw());//计算key caculateKey

			String newPayUseredisResult = readFromRedis(newPayuser_skey);
			if(!StringUtils.isEmpty(newPayUseredisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				newpaycount = Long.parseLong(newPayUseredisResult);
			}
			
			if(newpaycount == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.PAY_ACTION.raw(),KPI.NEWPAYUSER_KPI.raw());
				//计算数 
				newpaycount = caculateSingleSize(file,newPayUser_ckey,MQInstance.BASE);
				writeToRedis(newPayuser_skey,String.valueOf(newpaycount),KPI_CACHE_SEC);
			}
			
			if(newpaycount == 0){
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
			return String.valueOf(newpaycount/payCount);
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "0";
	}
}