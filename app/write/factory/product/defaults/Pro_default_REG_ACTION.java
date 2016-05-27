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

public class Pro_default_REG_ACTION extends IReadWrite{
	@Override
	public void write(JsonObject json) {
		if(json==null || json.isJsonNull())return;
		try{
			String caller =  json.get("caller").getAsString();
			String action =  json.get("action").getAsString();
			int gameId = json.get("game").getAsJsonObject().get("id").getAsInt();
			String ch = json.get("game").getAsJsonObject().get("ch").getAsString();
			String date = json.get("game").getAsJsonObject().get("date").getAsString();
			
			String imei=json.get("data").getAsJsonObject().get("imei").getAsString();
			int uid=json.get("data").getAsJsonObject().get("uid").getAsInt();
			
			File store1 = getWriteStoreFile( caller, date, gameId, ch, action,KPI.IMEIREG_KPI.raw());
			FileUtil.write(store1, imei+NEW_LINE, true);
			
			File store2 = getWriteStoreFile( caller, date, gameId, ch, action,KPI.UIDREG_KPI.raw());
			FileUtil.write(store2, uid+NEW_LINE, true);
			
			//新用户写入redis，方便pay统计算是否新用户支付
			ValueRedisTemplate redis = ValueRedisTemplate.getInstance(MQInstance.BASE);
			String uidkey = RedisUtil.apply(date, ch, gameId, String.valueOf(uid),KPI.UIDREG_KPI.raw());
			redis.setAtExpire(uidkey, "1", RedisUtil.unixTimeSince(date, 100));//可以用日志恢复100天前的数据
			
			//暂没用到判断imei新用户
			String imeikey = RedisUtil.apply(date, ch, gameId, String.valueOf(uid),KPI.IMEIREG_KPI.raw());
			redis.setAtExpire(imeikey, "1", RedisUtil.unixTimeSince(date, 100));
			
		}catch(Exception e){
			Logger.error(e, "ShunwanReg.write exception %s",e.getMessage());
		}
	}
}
