package write.ucmq;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import constants.Action;
import jws.Jws;
import jws.Logger;
import jws.mq.WorkerInvocation;
import utils.UcmqSignUtil;
import write.IWrite;
import write.factory.IWriteFactory;
import write.factory.impl.Factory_DEFAULT_ACTION;
/*
 {
 	"caller":"sdksz",
 	"action":"login",
 	"sign":"fajk8fda*08)8dasfda"
 	"data":{
 		"uid":4123456,
 		"imei":"EDKJ88fM"
 	}
 }
 */
public class UcmqWorker extends WorkerInvocation{

	@Override
	public void onTask(String s) {
		JsonObject message;
		try {
			message = parse( URLDecoder.decode(s, "utf-8") );
		} catch (UnsupportedEncodingException e1) {
			Logger.error(e1, "");
			return;
		}
		 
		if(!check(message)){
			Logger.error("UcmqWorker.onTask check message invalid %s", message);
			return ;
		}
		//先根据业务获取是否定义特殊实现，找不到则使用默认工厂
		String caller = message.get("caller").getAsString();
		String action = message.get("action").getAsString();
		String implClass = Jws.configuration.getProperty("action.write.impl","write.factory.impl")+".Factory_"+action.toUpperCase()+"_ACTION";
		
		IWriteFactory wf = null;
		try {
			 wf = (IWriteFactory)Jws.classloader.loadClass(implClass).newInstance();
		} catch (InstantiationException e) {
			  
		} catch (IllegalAccessException e) {
			 
		} catch (ClassNotFoundException e) {
			  
		}
		
		if(wf == null){//找不到则使用默认工厂
			Logger.info("UcmqWorker.onTask  using default factory=%s", "write.factory.impl.FactoryDEFAULTAction");
			wf = new Factory_DEFAULT_ACTION();
		}
		
		IWrite writer = wf.getWriteInstance(caller, Action.find(action));
		if(writer==null){
			Logger.error("UcmqWorker.onTask get writer failed.caller=%s,action=%s",caller,action);
			return ;
		}
		writer.write(message); 
	}
	
	/**
	 * 解析mq message
	 * @return
	 */
	private JsonObject parse(String s){
		JsonObject requestBody = null;
		try{
			requestBody = (new JsonParser()).parse(s).getAsJsonObject();
		}catch(Exception e){
			Logger.error(e,"invalid mq message");
		} 
		return requestBody;
	}
	/**
	 * 
	 * @param message
	 * @return
	 */
	private boolean check(JsonObject message){
		if(message == null)return false;
		if(!message.has("game") || message.get("game").isJsonNull()){
			Logger.error("mq message invalid %s", "game is empty.");
		}
		
		JsonObject game =  message.get("game").getAsJsonObject();
		if(!game.has("id") || game.get("id").isJsonNull() ){
			Logger.error("mq message invalid %s", "game.id is empty.");
		}
		if(!game.has("ch") || game.get("ch").isJsonNull() ){
			Logger.error("mq message invalid %s", "game.ch is empty.");
		}
		if(!game.has("date") || game.get("date").isJsonNull() ){
			Logger.error("mq message invalid %s", "game.date is empty.");
		}
		
		if(!message.has("caller") || message.get("caller").isJsonNull()){
			Logger.error("mq message invalid %s", "caller is empty.");
		}
		if(!message.has("id") || message.get("id").isJsonNull()){
			Logger.error("mq message invalid %s", "id is empty.");
		}
		if(!message.has("action") || message.get("action").isJsonNull()){
			Logger.error("mq message invalid %s", "action is empty.");
		}
		if(!message.has("sign") || message.get("sign").isJsonNull()){
			Logger.error("mq message invalid %s", "sign is empty.");
		}
		if(!message.has("data") || message.get("data").isJsonNull()){
			Logger.error("mq message invalid %s", "data is empty.");
		}
		JsonElement data =  message.get("data");
		String caller = message.get("caller").getAsString();
		String inSign = message.get("sign").getAsString();
		String selfSign = UcmqSignUtil.buildSign(caller, data);
		if(inSign.equalsIgnoreCase(selfSign)){
			return true;
		}
		Logger.error("UcmqWorker.check sign failed.sign is %s,but shoud be %s", inSign,selfSign);
		return false;
	}

}
