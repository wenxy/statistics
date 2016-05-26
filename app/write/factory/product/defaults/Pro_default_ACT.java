package write.factory.product.defaults;

import java.io.File;

import com.google.gson.JsonObject;

import constants.KPI;
import interfaces.IReadWrite;
import jws.Logger;
import utils.FileUtil;

public class Pro_default_ACT extends IReadWrite{

	@Override
	public void write(JsonObject json) {
		if(json==null || json.isJsonNull())return;
		try{
			String content=json.get("data").getAsJsonObject().get("imei").getAsString();
			File store = getWriteStoreFile(json,KPI.ACT.raw());
			FileUtil.write(store, content+",", true);
			
		}catch(Exception e){
			Logger.error(e, "ShunwanAct.write exception %s",e.getMessage());
		}
	}

}
