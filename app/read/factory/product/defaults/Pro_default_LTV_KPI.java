package read.factory.product.defaults;

import java.io.File;

import common.redis.template.ValueRedisTemplate;
import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.DateUtil;
import utils.RedisUtil;
/**
 * 用户价值
 * @author fish
 * 假设统计的日期为15号，
 * 2日ltv
 * 以15,16号为例子，16号去算2日ltv时 = 15新用户在15,16产生的充值金额/15注册用户数
 * 计算1-7日，15 20 30日
 */
public class Pro_default_LTV_KPI extends IReadWrite{
	
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			String[] result = new String[]{"0","0","0","0","0","0","0","0","0","0"};
			
			long regNum = regNumForDateByUID(caller,date,gameId,ch);
			if(regNum == 0){
				return "0,0,0,0,0,0,0,0,0,0";
			}
			
			result[0] = String.valueOf(getAmountByAfterDay(caller,date,gameId,ch,1)/regNum);
			result[1] = String.valueOf(getAmountByAfterDay(caller,date,gameId,ch,2)/regNum);
			result[2] = String.valueOf(getAmountByAfterDay(caller,date,gameId,ch,3)/regNum);
			result[3] = String.valueOf(getAmountByAfterDay(caller,date,gameId,ch,4)/regNum);
			result[4] = String.valueOf(getAmountByAfterDay(caller,date,gameId,ch,5)/regNum);
			result[5] = String.valueOf(getAmountByAfterDay(caller,date,gameId,ch,6)/regNum);
			result[6] = String.valueOf(getAmountByAfterDay(caller,date,gameId,ch,7)/regNum);
			result[7] = String.valueOf(getAmountByAfterDay(caller,date,gameId,ch,15)/regNum);
			result[8] = String.valueOf(getAmountByAfterDay(caller,date,gameId,ch,20)/regNum);
			result[9] = String.valueOf(getAmountByAfterDay(caller,date,gameId,ch,30)/regNum);
			
			
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<result.length;i++){
				if(i == result.length-1){
					sb.append(result[i]);
				}else{
					sb.append(result[i]).append(",");
				}
			}
			
			return sb.toString();
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "0,0,0,0,0,0,0,0,0,0";
	}
	
	private double getAmountByAfterDay(String caller,String date, int gameId, String ch,int afterDays) throws Exception{
		try{
			double result = 0;
			ValueRedisTemplate redis = ValueRedisTemplate.getInstance(MQInstance.BASE);
			File ltvPayFile = getWriteStoreFile( caller, date, gameId, ch, Action.PAY_ACTION.raw(),KPI.LTV_KPI.raw(),date);
			long time = DateUtil.getDateTime(date+" 00:00:00");
			for(int i=1;i<=afterDays;i++){
				String  payDate = DateUtil.formatDate2(DateUtil.addDay(time, i-1));
				String key = RedisUtil.apply(caller,payDate, ch, gameId, KPI.LTV_KPI.raw(),date);
				double inRedis = 0;
				String redisResult = redis.get(key);
				try{
					inRedis = Double.parseDouble(redisResult);
				}catch(NumberFormatException e){
					inRedis = 0;
				}
				if(inRedis == 0){
		 			try{
		 				String fileResult = readLine(ltvPayFile); 
						inRedis = Double.parseDouble(fileResult);
			 			writeToRedis(key,fileResult.trim(),KPI_CACHE_SEC);
					}catch(NumberFormatException e){
						inRedis = 0;
					}
				}
				result+=inRedis;
			}
			return result;
		}catch(Exception e){
			Logger.error(e, "");
			throw e;
		} 
	}
	/*public static void main(String[] args){
		String  payDate = DateUtil.formatDate2(DateUtil.addDay(new Date().getTime(), -1));
		System.out.println(payDate);
	}*/
	
}