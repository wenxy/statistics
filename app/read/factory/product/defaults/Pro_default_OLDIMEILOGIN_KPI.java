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
 * 總登錄數-新用戶數（imei）
 * @author fish
 *
 */
public class Pro_default_OLDIMEILOGIN_KPI extends IReadWrite{
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			//先查登錄用戶數
			long imeiLoginNum = 0;
			String imeiLogin_skey = RedisUtil.apply(date, ch, gameId, KPI.IMEILOGIN_KPI.raw());
			String imeiLogin_ckey = RedisUtil.apply(date, ch, gameId, Action.LOGIN_ACTION.raw(),KPI.IMEILOGIN_KPI.raw());//计算key caculateKey

			String imeiLogin_redisResult = readFromRedis(imeiLogin_skey);
			if(!StringUtils.isEmpty(imeiLogin_redisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				imeiLoginNum = Long.parseLong(imeiLogin_redisResult);
			}
			
			if(imeiLoginNum == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.IMEILOGIN_KPI.raw());
				imeiLoginNum = caculateSingleSize(file,imeiLogin_ckey,MQInstance.BASE);
				writeToRedis(imeiLogin_skey,String.valueOf(imeiLoginNum),KPI_CACHE_SEC);
			}
			
			if(imeiLoginNum==0){
				return "0";
			}

			////////計算新用戶登錄數
			String skey = RedisUtil.apply(date, ch, gameId, KPI.NEWIMEILOGIN_KPI.raw());
			String ckeyLogin = RedisUtil.apply(date, ch, gameId, Action.LOGIN_ACTION.raw(),KPI.IMEILOGIN_KPI.raw());//计算key caculateKey
			String ckeyReg = RedisUtil.apply(date, ch, gameId, Action.REG_ACTION.raw(),KPI.IMEIREG_KPI.raw());//计算key caculateKey
			String[] ckeys = new String[]{ckeyLogin,ckeyReg};
			long count = 0;
			String redisResult = readFromRedis(skey);
			if(!StringUtils.isEmpty(redisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				count = Long.parseLong(redisResult); 
			}
			
			if(count == 0){			
				File fileLogin = getReadStoreFile(caller,date,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.IMEILOGIN_KPI.raw());
				File fileReg = getReadStoreFile(caller,date,gameId,ch,Action.REG_ACTION.raw(),KPI.IMEIREG_KPI.raw());
				File[] files = new File[]{fileLogin,fileReg};
				
				//计算数 
				count = caculateMultiSize(files,ckeys,MQInstance.BASE);
				writeToRedis(skey,String.valueOf(count),KPI_CACHE_SEC);
			}
			
			if(count > imeiLoginNum){
				Logger.error("Pro_default_OLDIMEILOGIN_KPI.read  Impossible,newimeilogin[%s]>imeiloginnum[%]", count,imeiLoginNum);
				return "0";
			}
			
			return String.valueOf(imeiLoginNum-count);
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "";
	}
}
