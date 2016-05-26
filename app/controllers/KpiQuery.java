package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import constants.KPI;
import controllers.dto.KPIResponse;
import jws.Jws;
import jws.Logger;
import jws.mvc.Controller;
import jws.mvc.Http;
import read.IRead;
import read.factory.IReadFactory;
import read.factory.impl.Factory_DEFAULT_KPI;
import utils.UcmqSignUtil;

/**
 * KPI查询接口
 * 
 * @author fish
 *
 */
public class KpiQuery extends Controller {

	public static void query(String body) {
		try {
			String bodyStr = Http.Request.current().params.get("body");

			if (StringUtils.isEmpty(bodyStr)) {
				bodyStr = body;
			}
			if (StringUtils.isEmpty(bodyStr)) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数");
				renderJSON(new Gson().toJson(response));
			}

			try {
				bodyStr = URLDecoder.decode(bodyStr, "utf-8");
				Logger.info("KPI.query -> body %s", bodyStr);
			} catch (UnsupportedEncodingException e1) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数" + e1.getMessage());
				renderJSON(new Gson().toJson(response));
			}

			JsonObject requestBody = null;
			try {
				requestBody = (new JsonParser()).parse(bodyStr).getAsJsonObject();
			} catch (Exception e) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数" + e.getMessage());
				renderJSON(new Gson().toJson(response));
			}

			if (requestBody == null) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数.");
				renderJSON(new Gson().toJson(response));
			}

			if (!requestBody.has("id") || requestBody.get("id").isJsonNull()) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-id.");
				renderJSON(new Gson().toJson(response));
			}

			if (!requestBody.has("id") || requestBody.get("id").isJsonNull()) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-id.");
				renderJSON(new Gson().toJson(response));
			}

			if (!requestBody.has("caller") || requestBody.get("caller").isJsonNull()) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-caller.");
				renderJSON(new Gson().toJson(response));
			}
			String caller = requestBody.get("caller").getAsString();

			if (!requestBody.has("sign") || requestBody.get("sign").isJsonNull()) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-sign.");
				renderJSON(new Gson().toJson(response));
			}
			// check sign
			String inSign = requestBody.get("sign").getAsString();
			String selfSign = UcmqSignUtil.buildSign(caller, requestBody.get("data"));
			if (!inSign.equalsIgnoreCase(selfSign)) {
				Logger.error("sign should be %s,you sign is %s", selfSign, inSign);
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-sign-签名错误.");
				renderJSON(new Gson().toJson(response));
			}

			if (!requestBody.has("data") || requestBody.get("data").isJsonNull()) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-data.");
				renderJSON(new Gson().toJson(response));
			}

			JsonObject dataJson = requestBody.get("data").getAsJsonObject();

			if (!dataJson.has("gameId") || dataJson.get("gameId").isJsonNull()) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-gameId.");
				renderJSON(new Gson().toJson(response));
			}

			int gameId = 0;
			try {
				gameId = Integer.parseInt(dataJson.get("gameId").getAsString());
			} catch (Exception e) {
				gameId = 0;
			}
			if (gameId == 0) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-gameId.");
				renderJSON(new Gson().toJson(response));
			}

			if (!dataJson.has("ch") || dataJson.get("ch").isJsonNull()) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-ch.");
				renderJSON(new Gson().toJson(response));
			}
			String ch = dataJson.get("ch").getAsString();

			if (!dataJson.has("date") || dataJson.get("date").isJsonNull()) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-date.");
				renderJSON(new Gson().toJson(response));
			}

			String date = dataJson.get("date").getAsString();

			if (!dataJson.has("kpis") || dataJson.get("kpis").isJsonNull()) {
				KPIResponse response = new KPIResponse();
				response.getState().setCode(-1);
				response.getState().setMsg("无效的参数-kpis.");
				renderJSON(new Gson().toJson(response));
			}

			Map<String, String> kpiMap = new HashMap<String, String>();
			String kpis = dataJson.get("kpis").getAsString();
			for (String kpi : kpis.split(",")) {
				if (StringUtils.isEmpty(kpi)) {
					continue;
				}
				String kpiImplCalss = Jws.configuration.getProperty("kpi.read.impl", "read.factory.impl") + ".Factory_"
						+ kpi.toUpperCase() + "_KPI";
				IReadFactory rf = null;
				try {
					rf = (IReadFactory) Jws.classloader.loadClass(kpiImplCalss).newInstance();
				} catch (InstantiationException e) {
					Logger.error(e, "");
				} catch (IllegalAccessException e) {
					Logger.error(e, "");
				} catch (ClassNotFoundException e) {
					Logger.error(e, "");
				}

				if (rf == null) {
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

			KPIResponse response = new KPIResponse();
			response.getState().setCode(1);
			response.getState().setMsg("KPI查询成功");
			response.setData(kpiMap);

			Logger.info("KPI.query -> result %s", new Gson().toJson(response));
			renderJSON(new Gson().toJson(response));
		} catch (Exception e) {
			Logger.error(e, "");
			KPIResponse response = new KPIResponse();
			response.getState().setCode(-1);
			response.getState().setMsg("获取KPI失败." + e.getMessage());
			renderJSON(new Gson().toJson(response));
		}

	}

}
