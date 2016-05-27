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
			//查新用户付费数
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
			
			//查询新活跃用户数
			String skeyLogin = RedisUtil.apply(date, ch, gameId, KPI.NEWUIDLOGIN_KPI.raw());
			String ckeyLogin = RedisUtil.apply(date, ch, gameId, Action.LOGIN_ACTION.raw(),KPI.NEWUIDLOGIN_KPI.raw());//计算key caculateKey
  			long newLogincount = 0;
			String loginRedisResult = readFromRedis(skeyLogin);
			if(!StringUtils.isEmpty(loginRedisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				newLogincount = Long.parseLong(loginRedisResult);
			}
			
			if(newLogincount == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.NEWUIDLOGIN_KPI.raw());
				//计算数 
				newLogincount = caculateSingleSize(file,ckeyLogin,MQInstance.BASE);
				writeToRedis(skeyLogin,String.valueOf(newLogincount),KPI_CACHE_SEC);
			}
			
			if(newLogincount == 0){
				return "0";
			}
			 
			return String.valueOf(newpaycount/newLogincount);
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "0";
	}
}