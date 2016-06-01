package write.factory.product.defaults;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;

import common.redis.template.ValueRedisTemplate;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.FileUtil;
import utils.RedisUtil;

public class Pro_default_LOGIN_ACTION extends IReadWrite{
	@Override
	public void write(JsonObject json) {
		if(json==null || json.isJsonNull())return;
		try{
			String imei=json.get("data").getAsJsonObject().get("imei").getAsString();
			int uid=json.get("data").getAsJsonObject().get("uid").getAsInt();
			
			String caller =  json.get("caller").getAsString();
			String action =  json.get("action").getAsString();
			int gameId = json.get("game").getAsJsonObject().get("id").getAsInt();
			String ch = json.get("game").getAsJsonObject().get("ch").getAsString();
			String date = json.get("game").getAsJsonObject().get("date").getAsString();
			
			//避免一天的数据重复写入
			ValueRedisTemplate redis = ValueRedisTemplate.getInstance(MQInstance.BASE);
			String imeikey = RedisUtil.apply(caller,date, ch, gameId,imei,KPI.IMEILOGIN_KPI.raw());
			String imeivalue = redis.get(imeikey);
			boolean isImeiExist = (!StringUtils.isEmpty(imeivalue) && imeivalue.equals("1"))?true:false;
			
			if(!isImeiExist){
				File store1 = getWriteStoreFile( caller, date, gameId, ch, action,KPI.IMEILOGIN_KPI.raw());
				FileUtil.write(store1, imei+NEW_LINE, true);
				redis.setAtExpire(imeikey, "1", RedisUtil.unixTimeSince(date, 1));//一天后失效
			}
			
			String uidkey = RedisUtil.apply(caller,date, ch, gameId,String.valueOf(uid),KPI.UIDLOGIN_KPI.raw());
			String uidvalue = redis.get(uidkey);
			boolean isUidExist = (!StringUtils.isEmpty(uidvalue) && uidvalue.equals("1"))?true:false;
			
			if(!isUidExist){
				File store2 = getWriteStoreFile( caller, date, gameId, ch, action,KPI.UIDLOGIN_KPI.raw());
				FileUtil.write(store2, uid+NEW_LINE, true);
				redis.setAtExpire(uidkey, "1", RedisUtil.unixTimeSince(date, 1));//一天后失效
			}
			
			String newUidLoginKey = RedisUtil.apply(caller,date, ch, gameId, String.valueOf(uid),KPI.UIDREG_KPI.raw());
			String newUidLoginValue = redis.get(newUidLoginKey);
			boolean newUidLogin = (!StringUtils.isEmpty(newUidLoginValue) && newUidLoginValue.equals("1"))?true:false;
			if(newUidLogin){
				File store = getWriteStoreFile( caller, date, gameId, ch, action,KPI.NEWUIDLOGIN_KPI.raw());
				FileUtil.write(store, uid+NEW_LINE, true);
 			}else{
 				File store = getWriteStoreFile( caller, date, gameId, ch, action,KPI.OLDUIDLOGIN_KPI.raw());
				FileUtil.write(store, uid+NEW_LINE, true);
			}
			 
			String newImeiLoginKey = RedisUtil.apply(caller,date, ch, gameId, String.valueOf(imei),KPI.IMEIREG_KPI.raw());
			String newImeiLoginValue = redis.get(newImeiLoginKey);
			boolean newImeiLogin = (!StringUtils.isEmpty(newImeiLoginValue) && newImeiLoginValue.equals("1"))?true:false;
			if(newImeiLogin){
				File store = getWriteStoreFile( caller, date, gameId, ch, action,KPI.NEWIMEILOGIN_KPI.raw());
				FileUtil.write(store, imei+NEW_LINE, true);
			}else{
				File store = getWriteStoreFile( caller, date, gameId, ch, action,KPI.OLDIMEILOGIN_KPI.raw());
				FileUtil.write(store, imei+NEW_LINE, true);
			}
		}catch(Exception e){
			Logger.error(e, "ShunwanLogin.write exception %s",e.getMessage());
		}
	}

	 
}
