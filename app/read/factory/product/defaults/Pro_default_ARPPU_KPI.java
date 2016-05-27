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
public class Pro_default_ARPPU_KPI extends IReadWrite{

	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			
			double payTotal = 0.0;
			String payTotal_skey = RedisUtil.apply(date, ch, gameId, KPI.PAYTOTAL_KPI.raw());
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
			
			//付費用戶數
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
			return String.valueOf(payTotal/payCount);
			
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