package write.factory.product.defaults;

import java.io.File;

import com.google.gson.JsonObject;

import constants.KPI;
import interfaces.IReadWrite;
import jws.Logger;
import utils.FileUtil;

public class Pro_default_LOGIN extends IReadWrite{
	@Override
	public void write(JsonObject json) {
		if(json==null || json.isJsonNull())return;
		try{
			String imei=json.get("data").getAsJsonObject().get("imei").getAsString();
			int uid=json.get("data").getAsJsonObject().get("uid").getAsInt();
			
			File store1 = getWriteStoreFile(json,KPI.IMEILOGIN.raw());
			FileUtil.write(store1, imei+",", true);
			
			File store2 = getWriteStoreFile(json,KPI.UIDLOGIN.raw());
			FileUtil.write(store2, uid+",", true);
		
		}catch(Exception e){
			Logger.error(e, "ShunwanLogin.write exception %s",e.getMessage());
		}
	}

	 
}
