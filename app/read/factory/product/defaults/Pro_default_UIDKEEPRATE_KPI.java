package read.factory.product.defaults;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.DateUtil;
import utils.RedisUtil;
/**
 * 计算1-7 15 20 30 日留存(UID)
 * @author fish
 *
 */
public class Pro_default_UIDKEEPRATE_KPI extends IReadWrite{

	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			String[] result = new String[]{"0","0","0","0","0","0","0","0","0","0"};
			
 			long time = DateUtil.getDateTime(date+" 00:00:00");
			String date_1 = DateUtil.formatDate2(DateUtil.addDay(time, 1));
			String date_2 = DateUtil.formatDate2(DateUtil.addDay(time, 2));
			String date_3 = DateUtil.formatDate2(DateUtil.addDay(time, 3));
			String date_4 = DateUtil.formatDate2(DateUtil.addDay(time, 4));
			String date_5 = DateUtil.formatDate2(DateUtil.addDay(time, 5));
			String date_6 = DateUtil.formatDate2(DateUtil.addDay(time, 6));
			String date_7 = DateUtil.formatDate2(DateUtil.addDay(time, 7));
			String date_15 = DateUtil.formatDate2(DateUtil.addDay(time, 15));
			String date_20 = DateUtil.formatDate2(DateUtil.addDay(time, 20));
			String date_30 = DateUtil.formatDate2(DateUtil.addDay(time, 30));
			 
			//注册数
			long regCount = 0;
			String cKeyReg = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_REG");
			File regFile = getReadStoreFile(caller,date,gameId,ch,Action.REG_ACTION.raw(),KPI.UIDREG_KPI.raw());
			regCount = regNumForDateByUID(caller,date, gameId, ch);
			if(regCount == 0){
				return "0,0,0,0,0,0,0,0,0,0";
			}
			
			if(regFile == null || !regFile.exists()){
				return "0,0,0,0,0,0,0,0,0,0";
			}
			
			File dateFile = null;
			String cKeyDate = null;
			String sKeyDate = null;
			String redisResult = null;
			//1日留存						
			dateFile = getReadStoreFile(caller,date_1,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
			cKeyDate = RedisUtil.apply(caller,date_1, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_LOGIN");

			sKeyDate = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_1");
 			redisResult = readFromRedis(sKeyDate);
			if(!StringUtils.isEmpty(redisResult)){
				result[0] = redisResult;
			}else{
				result[0] = String.valueOf(caculate(regFile,dateFile,cKeyReg,cKeyDate,regCount));
			}	
			
			
			//2日留存
			dateFile = getReadStoreFile(caller,date_2,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
			cKeyDate = RedisUtil.apply(caller,date_2, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_LOGIN");

			sKeyDate = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_2");
 			redisResult = readFromRedis(sKeyDate);
			if(!StringUtils.isEmpty(redisResult)){
				result[1] = redisResult;
			}else{
				result[1] = String.valueOf(caculate(regFile,dateFile,cKeyReg,cKeyDate,regCount));
			}
			//3日留存
			dateFile = getReadStoreFile(caller,date_3,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
			cKeyDate = RedisUtil.apply(caller,date_3, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_LOGIN");
			sKeyDate = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_3");
 			redisResult = readFromRedis(sKeyDate);
			if(!StringUtils.isEmpty(redisResult)){
				result[2] = redisResult;
			}else{
				result[2] = String.valueOf(caculate(regFile,dateFile,cKeyReg,cKeyDate,regCount));
			}			
			//4日留存
			dateFile = getReadStoreFile(caller,date_4,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
			cKeyDate = RedisUtil.apply(caller,date_4, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_LOGIN");
			sKeyDate = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_4");
 			redisResult = readFromRedis(sKeyDate);
			if(!StringUtils.isEmpty(redisResult)){
				result[3] = redisResult;
			}else{
				result[3] = String.valueOf(caculate(regFile,dateFile,cKeyReg,cKeyDate,regCount));
			}				
			
			//5日留存
			dateFile = getReadStoreFile(caller,date_5,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
			cKeyDate = RedisUtil.apply(caller,date_5, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_LOGIN");
			sKeyDate = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_5");
 			redisResult = readFromRedis(sKeyDate);
			if(!StringUtils.isEmpty(redisResult)){
				result[4] = redisResult;
			}else{
				result[4] = String.valueOf(caculate(regFile,dateFile,cKeyReg,cKeyDate,regCount));
			}			
			
			//6日留存
			dateFile = getReadStoreFile(caller,date_6,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
			cKeyDate = RedisUtil.apply(caller,date_6, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_LOGIN");
			sKeyDate = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_6");
 			redisResult = readFromRedis(sKeyDate);
			if(!StringUtils.isEmpty(redisResult)){
				result[5] = redisResult;
			}else{
				result[5] = String.valueOf(caculate(regFile,dateFile,cKeyReg,cKeyDate,regCount));
			}				
			//7日留存
			dateFile = getReadStoreFile(caller,date_7,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
			cKeyDate = RedisUtil.apply(caller,date_7, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_LOGIN");
			sKeyDate = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_7");
 			redisResult = readFromRedis(sKeyDate);
			if(!StringUtils.isEmpty(redisResult)){
				result[6] = redisResult;
			}else{
				result[6] = String.valueOf(caculate(regFile,dateFile,cKeyReg,cKeyDate,regCount));
			}				
			
			//15日留存
			dateFile = getReadStoreFile(caller,date_15,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
			cKeyDate = RedisUtil.apply(caller,date_15, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_LOGIN");
			sKeyDate = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_15");
 			redisResult = readFromRedis(sKeyDate);
			if(!StringUtils.isEmpty(redisResult)){
				result[7] = redisResult;
			}else{
				result[7] = String.valueOf(caculate(regFile,dateFile,cKeyReg,cKeyDate,regCount));
			}				
			
			//20日留存
			dateFile = getReadStoreFile(caller,date_20,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
			cKeyDate = RedisUtil.apply(caller,date_20, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_LOGIN");
			sKeyDate = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_20");
 			redisResult = readFromRedis(sKeyDate);
			if(!StringUtils.isEmpty(redisResult)){
				result[8] = redisResult;
			}else{
				result[8] = String.valueOf(caculate(regFile,dateFile,cKeyReg,cKeyDate,regCount));
			}				
			
			//30日留存
			dateFile = getReadStoreFile(caller,date_30,gameId,ch,Action.LOGIN_ACTION.raw(),KPI.UIDLOGIN_KPI.raw());
			cKeyDate = RedisUtil.apply(caller,date_30, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_LOGIN");
			sKeyDate = RedisUtil.apply(caller,date, ch, gameId, KPI.UIDKEEPRATE_KPI.raw(),"_30");
 			redisResult = readFromRedis(sKeyDate);
			if(!StringUtils.isEmpty(redisResult)){
				result[9] = redisResult;
			}else{
				result[9] = String.valueOf(caculate(regFile,dateFile,cKeyReg,cKeyDate,regCount));
			}
			
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
	
	/**
	 * 计算留存率
	 * @param regFile
	 * @param dateFile
	 * @param cKeyReg
	 * @param cKey
	 * @param regCount
	 * @return
	 * @throws Exception
	 */
	private double caculate(File regFile,File dateFile,String cKeyReg ,String cKey,long regCount) throws Exception{
		try{
 			if(dateFile == null || !dateFile.exists()){
				return 0;
			}else{
				long dateFileNum = caculateMultiSize(new File[]{regFile,dateFile},new String[]{cKeyReg,cKey},MQInstance.BASE);
				return ((double)dateFileNum/regCount);
			}  
		}catch(Exception e){
			Logger.error(e, "0");
			throw e;
		} 
	}
}
