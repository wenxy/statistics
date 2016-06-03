package read.factory.product.defaults;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import constants.KPI;
import interfaces.IReadWrite;
import jws.Logger;
import utils.NumberUtil;
import utils.RedisUtil;

public class Pro_default_NEWPAYTOTAL_KPI extends IReadWrite{
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			String skey = RedisUtil.apply(caller,date, ch, gameId, KPI.NEWPAYTOTAL_KPI.raw());
 
			String redisResult = readFromRedis(skey);
			if(!StringUtils.isEmpty(redisResult) && !isToday(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				return redisResult;
			}
			
			File file = getReadStoreFile(caller,date,gameId,ch,Action.PAY_ACTION.raw(),KPI.NEWPAYTOTAL_KPI.raw());
			String line = readLine(file); 
 			writeToRedis(skey,line.trim(),KPI_CACHE_SEC);
			
			return StringUtils.isEmpty(line)?"0":NumberUtil.format(Double.parseDouble(line.trim()));
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "";
	}
}
