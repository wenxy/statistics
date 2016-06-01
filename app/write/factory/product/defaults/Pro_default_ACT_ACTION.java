package write.factory.product.defaults;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;

import common.redis.template.ValueRedisTemplate;
import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.FileUtil;
import utils.RedisUtil;

public class Pro_default_ACT_ACTION extends IReadWrite{

	@Override
	public void write(JsonObject json) {
		if(json==null || json.isJsonNull())return;
		try{
			String content=json.get("data").getAsJsonObject().get("imei").getAsString();
			
			String caller =  json.get("caller").getAsString();
			String action =  json.get("action").getAsString();
			int gameId = json.get("game").getAsJsonObject().get("id").getAsInt();
			String ch = json.get("game").getAsJsonObject().get("ch").getAsString();
			String date = json.get("game").getAsJsonObject().get("date").getAsString();
			
			//避免一天的数据重复写入
			ValueRedisTemplate redis = ValueRedisTemplate.getInstance(MQInstance.BASE);
			String key = RedisUtil.apply(caller,date, ch, gameId,content,KPI.ACT_KPI.raw());
			String value = redis.get(key);
			boolean isExist = (!StringUtils.isEmpty(value) && value.equals("1"))?true:false;
			
			if(isExist){
				return ;
			}
			
			File store = getWriteStoreFile( caller, date, gameId, ch, action,KPI.ACT_KPI.raw());
			FileUtil.write(store, content+NEW_LINE, true);
			

			redis.setAtExpire(key, "1", RedisUtil.unixTimeSince(date, 1));//一天后失效
			
		}catch(Exception e){
			Logger.error(e, "ShunwanAct.write exception %s",e.getMessage());
		}
	}

}
