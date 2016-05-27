package read.factory.product.defaults;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.RedisUtil;
/**
 * 新用户登录KPI-IMEI
 * count(今天登录用户   ∩   今天注册用户)
 * @author fish
 *
 */
public class Pro_default_NEWIMEILOGIN_KPI extends IReadWrite{
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			String skeyLogin = RedisUtil.apply(date, ch, gameId, KPI.OLDUIDLOGIN_KPI.raw());
			String ckeyLogin = RedisUtil.apply(date, ch, gameId, Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());//计算key caculateKey
  			long newLogincount = 0;
			String loginRedisResult = readFromRedis(skeyLogin);
			if(!StringUtils.isEmpty(loginRedisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				newLogincount = Long.parseLong(loginRedisResult);
			}
			
			if(newLogincount == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.OLDUIDLOGIN_KPI.raw());
				//计算数 
				newLogincount = caculateSingleSize(file,ckeyLogin,MQInstance.BASE);
				writeToRedis(skeyLogin,String.valueOf(newLogincount),KPI_CACHE_SEC);
			}
			
			if(newLogincount == 0){
				return "0";
			}
			
			return String.valueOf(newLogincount);
			
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "";
	}
}