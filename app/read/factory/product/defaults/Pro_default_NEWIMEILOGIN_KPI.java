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
			String skey = RedisUtil.apply(date, ch, gameId, KPI.NEWIMEILOGIN_KPI.raw());
			
			String ckeyLogin = RedisUtil.apply(date, ch, gameId, Action.LOGIN_ACTION.raw(),KPI.IMEILOGIN_KPI.raw());//计算key caculateKey
			String ckeyReg = RedisUtil.apply(date, ch, gameId, Action.REG_ACTION.raw(),KPI.IMEIREG_KPI.raw());//计算key caculateKey
			String[] ckeys = new String[]{ckeyLogin,ckeyReg};
			
			String redisResult = readFromRedis(skey);
			if(!StringUtils.isEmpty(redisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				return redisResult;
			}
			
			File fileLogin = getReadStoreFile(caller,date,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.IMEILOGIN_KPI.raw());
			File fileReg = getReadStoreFile(caller,date,gameId,ch,Action.REG_ACTION.raw(),KPI.IMEIREG_KPI.raw());
			File[] files = new File[]{fileLogin,fileReg};
			
			//计算数 
			long count = caculateMultiSize(files,ckeys,MQInstance.BASE);
			writeToRedis(skey,String.valueOf(count),KPI_CACHE_SEC);
			
			return String.valueOf(count);
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "";
	}
}