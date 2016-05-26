package utils;

import jws.Logger;
import jws.module.ucgc.api.ApiRequest;
import jws.mvc.Http;

public class LoginUtil {
	
	public  static String  getLoginCh(){
		try{
			String loginCh = ((ApiRequest)Http.Request.current().args.get(ApiRequest.ClassID)).getLoginCh();
			return loginCh;
		}catch(Exception e){
			Logger.error(e, "");
		}
 		return "";
	}
	
	public static int getRoleId(){
		try{
			int roleId = ((ApiRequest)Http.Request.current().args.get(ApiRequest.ClassID)).getRoleId();
			return roleId;
		}catch(Exception e){
			Logger.error(e, "");
		}
		return -1;
	}
}
