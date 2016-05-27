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
 * 總登錄數-新用戶數（uid）
 * @author fish
 *
 */
public class Pro_default_OLDUIDLOGIN_KPI extends IReadWrite{
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			//计算旧用户登录数
			String skeyLogin = RedisUtil.apply(date, ch, gameId, KPI.OLDUIDLOGIN_KPI.raw());
			String ckeyLogin = RedisUtil.apply(date, ch, gameId, Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());//计算key caculateKey
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
			
			return String.valueOf(oldLogincount);
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "";
	}
}
