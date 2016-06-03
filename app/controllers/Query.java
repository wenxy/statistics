package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import constants.KPI;
import controllers.dto.ChartDateSetDto;
import controllers.dto.KIPIQueryDto;
import controllers.dto.KPIResponse;
import jws.Jws;
import jws.Logger;
import jws.mvc.Controller;
import read.IRead;
import read.factory.IReadFactory;
import read.factory.impl.Factory_DEFAULT_KPI;
import utils.DateUtil;
import utils.UcmqSignUtil;

public class Query  extends Controller{

	public static void index(){
		String date = DateUtil.getCurrentDate().substring(0, 10);
		render("query.html",date);
	}
	
	public static void query(int gameId,String ch,String date,String caller,String appKey){
		KIPIQueryDto qdto = new KIPIQueryDto();
		qdto.setCaller(caller);
		KIPIQueryDto.Data data = new KIPIQueryDto().new Data();
		
		data.setCh(ch);
		data.setDate(date);
		data.setGameId(gameId);
		String kpis = "";
		for(int i=0;i<KPI.values().length;i++){
			if(i==KPI.values().length-1){
				kpis = kpis+KPI.values()[i].raw().toUpperCase();
			}else{
				kpis = kpis+KPI.values()[i].raw().toUpperCase()+",";
			}
		}
		data.setKpis(kpis);
		qdto.setData(data);
		
		JsonObject requestBody = (new JsonParser()).parse(new Gson().toJson(qdto)).getAsJsonObject();
	
		if (!requestBody.has("id") || requestBody.get("id").isJsonNull()) {
			KPIResponse response = new KPIResponse();
			response.getState().setCode(-1);
			response.getState().setMsg("无效的参数-id.");
			 
			render("query.html",response,gameId,ch,date,caller,appKey);
		}

		if (!requestBody.has("caller") || requestBody.get("caller").isJsonNull() || StringUtils.isEmpty(requestBody.get("caller").getAsString())) {
			KPIResponse response = new KPIResponse();
			response.getState().setCode(-1);
			response.getState().setMsg("无效的参数-caller.");
			render("query.html",response,gameId,ch,date,caller,appKey);
		}
		
		
		if (!requestBody.has("data") || requestBody.get("data").isJsonNull()) {
			KPIResponse response = new KPIResponse();
			response.getState().setCode(-1);
			response.getState().setMsg("无效的参数-data.");
			render("query.html",response,gameId,ch,date,caller,appKey);
		}

		JsonObject dataJson = requestBody.get("data").getAsJsonObject();

		if (!dataJson.has("gameId") || dataJson.get("gameId").isJsonNull() || StringUtils.isEmpty(dataJson.get("gameId").getAsString())) {
			KPIResponse response = new KPIResponse();
			response.getState().setCode(-1);
			response.getState().setMsg("无效的参数-gameId.");
			render("query.html",response,gameId,ch,date,caller,appKey);
		}
		
		if (!dataJson.has("ch") || dataJson.get("ch").isJsonNull()  || StringUtils.isEmpty(dataJson.get("ch").getAsString())) {
			KPIResponse response = new KPIResponse();
			response.getState().setCode(-1);
			response.getState().setMsg("无效的参数-ch.");
			render("query.html",response,gameId,ch,date,caller,appKey);
		}
		
		if (!dataJson.has("date") || dataJson.get("date").isJsonNull()  || StringUtils.isEmpty(dataJson.get("date").getAsString())) {
			KPIResponse response = new KPIResponse();
			response.getState().setCode(-1);
			response.getState().setMsg("无效的参数-date.");
			render("query.html",response,gameId,ch,date,caller,appKey);
		}
		
		String inSign = UcmqSignUtil.buildSign(caller, appKey,requestBody.get("data"));
		String selfSign = UcmqSignUtil.buildSign(caller, requestBody.get("data"));
		
		if (!inSign.equalsIgnoreCase(selfSign)) {
			Logger.error("sign should be %s,you sign is %s", selfSign, inSign);
			KPIResponse response = new KPIResponse();
			response.getState().setCode(-1);
			response.getState().setMsg("无效的参数-caller or appKey-签名错误.");
			render("query.html",response,gameId,ch,date,caller,appKey);
		}
		
		Map<String, String> kpiMap = new HashMap<String, String>();
		for (String kpi : kpis.split(",")) {
			if (StringUtils.isEmpty(kpi)) {
				continue;
			}
			String kpiImplCalss = Jws.configuration.getProperty("kpi.read.impl", "read.factory.impl")+"."+caller + ".Factory_"
					+ kpi.toUpperCase() + "_KPI";
			IReadFactory rf = null;
			try {
				rf = (IReadFactory) Jws.classloader.loadClass(kpiImplCalss).newInstance();
			} catch (InstantiationException e) {
				 
			} catch (IllegalAccessException e) {
				 
			} catch (ClassNotFoundException e) {
				 
			}

			if (rf == null) {
				Logger.info("KPI.query -> using default factory kpi impl->%s", "read.factory.impl.Factory_DEFAULT_KPI");
				rf = new Factory_DEFAULT_KPI();
			}
			IRead read = rf.getReadInstance(caller, KPI.find(kpi));
			if (read != null) {
				String value = read.read(caller, date, gameId, ch);
				kpiMap.put(kpi, StringUtils.isEmpty(value) ? "" : value);
			} else {
				kpiMap.put(kpi, "0");
			}

		} 
		Logger.info("-result=%s", kpiMap);
		render("query.html",gameId,ch,date,caller,appKey,kpiMap);
	}
	
	//init line dateSet
	public static void ajaxQuery(int gameId,String ch,String date,String caller,String appKey,int type,int add){
		
		if(add == 1){
			long time = DateUtil.getDateTime(date+" 00:00:00");
			date = DateUtil.formatDate2(DateUtil.addDay(time, 1));
		}
		
		KIPIQueryDto qdto = new KIPIQueryDto();
		qdto.setCaller(caller);
		KIPIQueryDto.Data data = new KIPIQueryDto().new Data();
		
		data.setCh(ch);
		data.setDate(date);
		data.setGameId(gameId);
		String kpis = "";
		qdto.setData(data);
		if(type == 0){
			kpis = KPI.ACT_KPI.raw().toUpperCase();
		}
		if(type == 1){
			kpis = KPI.UIDREG_KPI.raw().toUpperCase()+","+ KPI.UIDLOGIN_KPI.raw().toUpperCase()+","+
				   KPI.NEWUIDLOGIN_KPI.raw().toUpperCase()+","+ KPI.OLDUIDLOGIN_KPI.raw().toUpperCase();
		}
		if(type == 2){
			kpis = KPI.IMEIREG_KPI.raw().toUpperCase()+","+ KPI.IMEILOGIN_KPI.raw().toUpperCase()+","+
				   KPI.NEWIMEILOGIN_KPI.raw().toUpperCase()+","+ KPI.OLDIMEILOGIN_KPI.raw().toUpperCase();
		}
		if(type == 3){
			kpis = KPI.PAYTOTAL_KPI.raw().toUpperCase()+","+ KPI.NEWPAYTOTAL_KPI.raw().toUpperCase()+","+
					   KPI.OLDPAYTOTAL_KPI.raw().toUpperCase()+","+ KPI.PAYUSER_KPI.raw().toUpperCase()+","+
					   KPI.NEWPAYUSER_KPI.raw().toUpperCase()+","+ KPI.OLDPAYUSER_KPI.raw().toUpperCase();
		}
		data.setKpis(kpis);
		
		JsonObject requestBody = (new JsonParser()).parse(new Gson().toJson(qdto)).getAsJsonObject();

		String inSign = UcmqSignUtil.buildSign(caller, appKey,requestBody.get("data"));
		String selfSign = UcmqSignUtil.buildSign(caller, requestBody.get("data"));
		
		if (!inSign.equalsIgnoreCase(selfSign)) {
			Logger.error("sign should be %s,you sign is %s", selfSign, inSign);
			KPIResponse response = new KPIResponse();
			response.getState().setCode(-1);
			response.getState().setMsg("无效的参数-caller or appKey-签名错误.");
			render("query.html",response,gameId,ch,date,caller,appKey);
		}
		ChartDateSetDto list = new ChartDateSetDto();
		list.setDate(date);
		Random ra =new Random();
 		for (String kpi : kpis.split(",")) {
		
			if (StringUtils.isEmpty(kpi)) {
				continue;
			}
			ChartDateSetDto.Data rspData = new ChartDateSetDto().new Data();
			rspData.setLabel(kpi);
			 
			
			String kpiImplCalss = Jws.configuration.getProperty("kpi.read.impl", "read.factory.impl")+"."+caller + ".Factory_"
					+ kpi.toUpperCase() + "_KPI";
			IReadFactory rf = null;
			try {
				rf = (IReadFactory) Jws.classloader.loadClass(kpiImplCalss).newInstance();
			} catch (InstantiationException e) {
				 
			} catch (IllegalAccessException e) {
				 
			} catch (ClassNotFoundException e) {
				 
			}
			if (rf == null) {
				Logger.info("KPI.query -> using default factory kpi impl->%s", "read.factory.impl.Factory_DEFAULT_KPI");
				rf = new Factory_DEFAULT_KPI();
			}
			IRead read = rf.getReadInstance(caller, KPI.find(kpi));
			if (read != null) {
				String value = read.read(caller, date, gameId, ch);
				rspData.setValue(Double.parseDouble(value));
				//kpiMap.put(kpi, StringUtils.isEmpty(value) ? "" : value);
			} else {
				rspData.setValue(0);
			} 
			//rspData.setValue(ra.nextDouble());
			list.getData().add(rspData);
		} 
		renderJSON(list);
	}
}
