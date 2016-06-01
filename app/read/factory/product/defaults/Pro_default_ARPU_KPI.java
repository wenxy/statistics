package read.factory.product.defaults;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.RedisUtil;
/**
 * 人均付費 元
 * @author fish
 *
 */
public class Pro_default_ARPU_KPI extends IReadWrite{

	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			
			double payTotal = 0.0;
			String payTotal_skey = RedisUtil.apply(caller,date, ch, gameId, KPI.PAYTOTAL_KPI.raw());
			String payTotalredisResult = readFromRedis(payTotal_skey);
			if(!StringUtils.isEmpty(payTotalredisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				payTotal = Double.parseDouble(payTotalredisResult);
			}
			
			if(payTotal == 0){
				File file = getReadStoreFile(caller,date,gameId,ch,Action.PAY_ACTION.raw(),KPI.PAYTOTAL_KPI.raw());
				String line = readLine(file); 
	 			writeToRedis(payTotal_skey,line.trim(),KPI_CACHE_SEC);
	 			payTotal = Double.parseDouble(line);
			}
			
			if(payTotal == 0){
				return "0";
			}
			
			//登錄用戶數
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
			return String.valueOf(payTotal/loginCount);
			
		}catch(Exception e){
			Logger.error(e, "");
		}
		return "0";
	} 
	/*public static void main(String[] args){
		double x = 3004.43;
		long y = 10;
		NumberFormat nf = new DecimalFormat("##.##");
		System.out.println(nf.format(x/y));
	}*/

}