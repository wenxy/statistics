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
			//先查登錄用戶數
			long uidLoginNum = 0;
			String uidLogin_skey = RedisUtil.apply(date, ch, gameId, KPI.UIDLOGIN_KPI.raw());
			String uidLogin_ckey = RedisUtil.apply(date, ch, gameId, Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());//计算key caculateKey

			String uidLogin_redisResult = readFromRedis(uidLogin_skey);
			if(!StringUtils.isEmpty(uidLogin_redisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				uidLoginNum = Long.parseLong(uidLogin_redisResult);
			}
			
			if(uidLoginNum == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
				uidLoginNum = caculateSingleSize(file,uidLogin_ckey,MQInstance.BASE);
				writeToRedis(uidLogin_skey,String.valueOf(uidLoginNum),KPI_CACHE_SEC);
			}
			
			if(uidLoginNum==0){
				return "0";
			}

			////////計算新用戶登錄數
			String skey = RedisUtil.apply(date, ch, gameId, KPI.NEWUIDLOGIN_KPI.raw());
			String ckeyLogin = RedisUtil.apply(date, ch, gameId, Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());//计算key caculateKey
			String ckeyReg = RedisUtil.apply(date, ch, gameId, Action.REG_ACTION.raw(),KPI.UIDREG_KPI.raw());//计算key caculateKey
			String[] ckeys = new String[]{ckeyLogin,ckeyReg};
			long count = 0;
			String redisResult = readFromRedis(skey);
			if(!StringUtils.isEmpty(redisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				count = Long.parseLong(redisResult);
			}
			
			if(count == 0){
				File fileLogin = getReadStoreFile(caller,date,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
				File fileReg = getReadStoreFile(caller,date,gameId,ch,Action.REG_ACTION.raw(),KPI.UIDREG_KPI.raw());
				File[] files = new File[]{fileLogin,fileReg};
				
				//计算数 
				count = caculateMultiSize(files,ckeys,MQInstance.BASE);
				writeToRedis(skey,String.valueOf(count),KPI_CACHE_SEC);
			}
			
			if(count > uidLoginNum){
				Logger.error("Pro_default_OLDUIDLOGIN_KPI.read  Impossible,newuidlogin[%s]>uidloginnum[%]", count,uidLoginNum);
				return "0";
			}
			
			return String.valueOf(uidLoginNum-count);
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "";
	}
}
