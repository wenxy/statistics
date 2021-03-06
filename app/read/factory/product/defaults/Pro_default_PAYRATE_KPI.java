package read.factory.product.defaults;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.NumberUtil;
import utils.RedisUtil;

public class Pro_default_PAYRATE_KPI extends IReadWrite{
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			//活跃用户数
			String uidLoginskey = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDLOGIN_KPI.raw());
			String uidLoginckey = RedisUtil.apply(caller,date, ch, gameId, Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());//计算key caculateKey
			long loginCount = 0;
			String loginRedisResult = readFromRedis(uidLoginskey);
			if(!StringUtils.isEmpty(loginRedisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				loginCount = Long.parseLong(loginRedisResult);
			}
			if(loginCount == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
				//计算数 
				loginCount = caculateSingleSize(file,uidLoginckey,MQInstance.BASE);
				writeToRedis(uidLoginskey,String.valueOf(loginCount),KPI_CACHE_SEC);
			}
			if(loginCount == 0){
				return "0";
			}
			
			//付費用戶數
			long payCount = 0;
			String skey = RedisUtil.apply(caller,date, ch, gameId, KPI.PAYUSER_KPI.raw());
			String ckey = RedisUtil.apply(caller,date, ch, gameId, Action.PAY_ACTION.raw(),KPI.PAYUSER_KPI.raw());//计算key caculateKey
			
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
			
			return String.valueOf(NumberUtil.formatP((double)payCount/loginCount));
		}catch(Exception e){
			Logger.error(e, "");
		}
		return "0";
	} 
}