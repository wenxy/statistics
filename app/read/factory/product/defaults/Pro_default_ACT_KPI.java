package read.factory.product.defaults;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import common.redis.template.SetRedisTemplate;
import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.DateUtil;
import utils.RedisUtil;
/**
 * 顺玩激活KPI
 * @author fish
 *
 */
public class Pro_default_ACT_KPI extends IReadWrite{

	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			String skey = RedisUtil.apply(date, ch, gameId, KPI.ACT.raw());
			String redisResult = readFromRedis(skey);
			if(!StringUtils.isEmpty(redisResult) && DateUtil.getDay().equalsIgnoreCase(date)){//查询当天的话，不走缓存，因为数据在实时变化ing
				return redisResult;
			}
			String line = readFromFile(getReadStoreFile(caller,date,gameId,ch,Action.ACT.raw(),KPI.ACT.raw()));
			if(StringUtils.isEmpty(line)){
				return "0";
			}
			List<String> values = new ArrayList<String>();
			for(String one:line.split(",")){
				if(StringUtils.isEmpty(one))continue;
				values.add(one);
			}
			String ckey = RedisUtil.apply(date, ch, gameId, Action.ACT.raw());
			SetRedisTemplate redis = SetRedisTemplate.getInstance(MQInstance.BASE);
			redis.sadd(ckey, values);
			redis.sexpireAt(ckey, RedisUtil.unixTimeSince(1*60));
			long count = redis.sunionstore(ckey, ckey);
			
			writeToRedis(skey,String.valueOf(count),1*24*60*60);//缓存1天，1天之内查询可以走内存
			return String.valueOf(count);
		}catch(Exception e){
			Logger.error(e, "");
		}
		return "0";
	}

}
