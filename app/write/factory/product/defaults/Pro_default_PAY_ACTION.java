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

public class Pro_default_PAY_ACTION extends IReadWrite{
	@Override
	public void write(JsonObject json) {
		if(json==null || json.isJsonNull())return;
		try{
			
			String caller =  json.get("caller").getAsString();
			String action =  json.get("action").getAsString();
			int gameId = json.get("game").getAsJsonObject().get("id").getAsInt();
			String ch = json.get("game").getAsJsonObject().get("ch").getAsString();
			String date = json.get("game").getAsJsonObject().get("date").getAsString();
			
			long uid=json.get("data").getAsJsonObject().get("uid").getAsLong();
			double amount = json.get("data").getAsJsonObject().get("amount").getAsDouble();
			
 			
			//is new user?
			String key = RedisUtil.apply(date, ch, gameId, String.valueOf(uid),KPI.UIDREG_KPI.raw());
			ValueRedisTemplate redis = ValueRedisTemplate.getInstance(MQInstance.BASE);
			String value = redis.get(key);
			boolean isNewUser = (!StringUtils.isEmpty(value) && value.equals("1"))?true:false;
			
			
			String payKey = RedisUtil.apply(date, ch, gameId, KPI.PAYTOTAL_KPI.raw());
			String newPayKey = RedisUtil.apply(date, ch, gameId, KPI.NEWPAYTOTAL_KPI.raw());
			String oldPayKey = RedisUtil.apply(date, ch, gameId, KPI.OLDPAYTOTAL_KPI.raw());
			
			String payAmountStr = redis.get(payKey);
			String newPayAmountStr = redis.get(newPayKey);
			String oldPayAmountStr = redis.get(oldPayKey);
			
			double payAmount=0;
			double newPayAmount=0;
			double oldPayAmount=0;
			
			try{
				payAmount = Double.parseDouble(payAmountStr);
			}catch(Exception e){
				//Logger.error(e, "");
				payAmount = 0;
			}
			
			try{
				newPayAmount = Double.parseDouble(newPayAmountStr);
			}catch(Exception e){
				//Logger.error(e, "");
				newPayAmount=0;
			}
			
			try{
				oldPayAmount = Double.parseDouble(oldPayAmountStr);
			}catch(Exception e){
				//Logger.error(e, "");
				oldPayAmount=0;
			}
			
			//store pay user
			File storePayUser = getWriteStoreFile( caller, date, gameId, ch, action,KPI.PAYUSER_KPI.raw());
			FileUtil.write(storePayUser, uid+NEW_LINE, true);
			
			//store pay total
			payAmount += amount;
			File storePayTotal = getWriteStoreFile( caller, date, gameId, ch, action,KPI.PAYTOTAL_KPI.raw());
			FileUtil.write(storePayTotal, String.valueOf(payAmount), false);
			
			redis.set(payKey, String.valueOf(payAmount));
			
			if(isNewUser){
				//store new pay user
				File storeNewPayUser = getWriteStoreFile( caller, date, gameId, ch, action,KPI.NEWPAYUSER_KPI.raw());
				FileUtil.write(storeNewPayUser, uid+NEW_LINE, true);
				//store new pay total
				newPayAmount+=amount;
				File storeNewPayTotal =getWriteStoreFile( caller, date, gameId, ch, action,KPI.NEWPAYTOTAL_KPI.raw());
				FileUtil.write(storeNewPayTotal, String.valueOf(newPayAmount), false);
				
				redis.set(newPayKey, String.valueOf(newPayAmount));
			}else{
				//store old pay user
				File storeOldPayUser = getWriteStoreFile( caller, date, gameId, ch, action,KPI.OLDPAYUSER_KPI.raw());
				FileUtil.write(storeOldPayUser, uid+NEW_LINE, true);
				
				//store old pay total
				oldPayAmount+=amount;
				File storeOldPayTotal = getWriteStoreFile( caller, date, gameId, ch, action,KPI.OLDPAYTOTAL_KPI.raw());
				FileUtil.write(storeOldPayTotal, String.valueOf(oldPayAmount), false);
				
				redis.set(oldPayKey, String.valueOf(oldPayAmount));
			}
		}catch(Exception e){
			Logger.error(e, "ShunwanPay.write exception %s",e.getMessage());
		}
	}
}
