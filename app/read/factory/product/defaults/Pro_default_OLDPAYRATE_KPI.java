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
			
			//查旧用户付费数
			long oldpaycount = 0;
			String oldpayuser_skey = RedisUtil.apply(caller,date, ch, gameId, KPI.OLDPAYUSER_KPI.raw());
			String oldpayUser_ckey = RedisUtil.apply(caller,date, ch, gameId, Action.PAY_ACTION.raw(),KPI.OLDPAYUSER_KPI.raw());//计算key caculateKey

			String oldpayUseredisResult = readFromRedis(oldpayuser_skey);
			if(!StringUtils.isEmpty(oldpayUseredisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				oldpaycount = Long.parseLong(oldpayUseredisResult);
			}
			
			if(oldpaycount == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.PAY_ACTION.raw(),KPI.OLDPAYUSER_KPI.raw());
				//计算数 
				oldpaycount = caculateSingleSize(file,oldpayUser_ckey,MQInstance.BASE);
				writeToRedis(oldpayuser_skey,String.valueOf(oldpaycount),KPI_CACHE_SEC);
			}
			
			if(oldpaycount == 0){
				return "0";
			}
			
			
			//计算旧活跃用户数
			String skeyLogin = RedisUtil.apply(caller,date, ch, gameId, KPI.OLDUIDLOGIN_KPI.raw());
			String ckeyLogin = RedisUtil.apply(caller,date, ch, gameId, Action.LOGIN_ACTION.raw(),KPI.OLDUIDLOGIN_KPI.raw());//计算key caculateKey
  			long oldLogincount = 0;
			String loginRedisResult = readFromRedis(skeyLogin);
			if(!StringUtils.isEmpty(loginRedisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				oldLogincount = Long.parseLong(loginRedisResult);
			}
			
			if(oldLogincount == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.OLDUIDLOGIN_KPI.raw());
				//计算数 
				oldLogincount = caculateSingleSize(file,ckeyLogin,MQInstance.BASE);
				writeToRedis(skeyLogin,String.valueOf(oldLogincount),KPI_CACHE_SEC);
			}
			
			if(oldLogincount == 0){
				return "0";
			}
			
			 
			return String.valueOf((double)oldpaycount/oldLogincount);
		}catch(Exception e){
			Logger.error(e, "0");
		}
		
		return "0";
	}
}
