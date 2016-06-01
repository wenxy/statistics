package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

/**
 * Redis 业务使用工具类
 * @author fish
 *
 */
public class RedisUtil {

	/**
	 * key 申请
	 * @param date
	 * @param ch
	 * @param gameId
	 * @param kpiName
	 * @return
	 * @throws Exception
	 */
	public static String apply(String caller,String date,String ch,int gameId,String... exts) throws Exception{
	
		if(StringUtils.isEmpty(date) ||
				StringUtils.isEmpty(ch) || StringUtils.isEmpty(caller) ||
				gameId <=0){
			throw new Exception("RedisKeyUtil.apply Invalid param.");
		}
		
		if(exts != null && exts.length>0){
			StringBuffer keySb =  new StringBuffer(caller).append("_").append(date).append("_").append(gameId).append("_").append(ch).append("_");
			for(int i=0;i<exts.length;i++){
				if(i==exts.length-1){
					keySb.append(exts[i]);
				}else{
					keySb.append(exts[i]).append("_");
				}
			}
			return keySb.toString();
		}
		
		return new StringBuffer(date).append("_").append(gameId).append("_").append(ch)
				.toString();
	}
	
	/**
	 * 计算失效的位置 精确到天
	 * @param sinceDate 格式 2016-05-24
	 * @param days
	 * @return
	 * @throws Exception
	 */
	public static long unixTimeSince(String sinceDate,int days) throws Exception{
		if(StringUtils.isEmpty(sinceDate)){
			throw new Exception("RedisUtil.unixTimeSince Invalid param.");
		}
		try{
			String fromatDate = sinceDate+" 00:00:00";
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar calendar = new GregorianCalendar();
		    calendar.setTime(df.parse(fromatDate));
		    calendar.add(Calendar.DATE, days);
		    return calendar.getTimeInMillis()/1000;
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * 计算失效的位置 精确到秒
	 * @param sinceDate 格式 2016-05-24
	 * @param days
	 * @return
	 * @throws Exception
	 */
	public static long unixTimeSince(int seconds) throws Exception{
		try{
			Calendar calendar = new GregorianCalendar();
		    calendar.setTime(new Date());
		    calendar.add(Calendar.SECOND, seconds);
		    return calendar.getTimeInMillis()/1000;
		}catch(Exception e){
			throw e;
		}
	}
}
