package write.factory.product.defaults;

import java.io.File;

import com.google.gson.JsonObject;

import common.redis.template.ValueRedisTemplate;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.FileUtil;
import utils.RedisUtil;

public class Pro_default_REG extends IReadWrite{
	@Override
	public void write(JsonObject json) {
		if(json==null || json.isJsonNull())return;
		try{
			String imei=json.get("data").getAsJsonObject().get("imei").getAsString();
			int uid=json.get("data").getAsJsonObject().get("uid").getAsInt();
			
			File store1 = getWriteStoreFile(json,KPI.IMEIREG.raw());
			FileUtil.write(store1, imei+",", true);
			
			File store2 = getWriteStoreFile(json,KPI.UIDREG.raw());
			FileUtil.write(store2, uid+",", true);
			
			int gameId = json.get("game").getAsJsonObject().get("id").getAsInt();
			String ch = json.get("game").getAsJsonObject().get("ch").getAsString();
			String date = json.get("game").getAsJsonObject().get("date").getAsString();
			
			//新用户写入redis，便于新旧用户判断,次日失效
			ValueRedisTemplate redis = ValueRedisTemplate.getInstance(MQInstance.BASE);
			String key = RedisUtil.apply(date, ch, gameId, String.valueOf(uid));
			redis.setAtExpire(key, "1", RedisUtil.unixTimeSince(date, 1));
			
		}catch(Exception e){
			Logger.error(e, "ShunwanReg.write exception %s",e.getMessage());
		}
	}
}
