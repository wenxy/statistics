package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jws.Jws;
import jws.Logger;
import jws.libs.Codec;
import jws.module.ucgc.api.Client;

public class UcmqSignUtil {

	/**
	 * 统计框架签名
	 * @param caller
	 * @param data
	 * @return
	 */
	public static String buildSign(String caller, JsonElement data) {
		if (StringUtils.isEmpty(caller)) {
			return "";
		}

		JsonObject jsonObject = data.getAsJsonObject();
		ArrayList<String> keys = new ArrayList<String>();
		StringBuffer sortedDataBuffer = new StringBuffer();
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (entry.getValue().isJsonPrimitive()) {
				keys.add(entry.getKey());
			} else if (entry.getValue().isJsonNull()) {
				keys.add(entry.getKey());
			}
		}
		if (keys.size() > 0) {
			Collections.sort(keys);
			for (String key : keys) {
				JsonElement valueElement = jsonObject.get(key);
				String value = "null";
				if (!valueElement.isJsonNull()) {
					value = valueElement.getAsString();
				}
				sortedDataBuffer.append(key).append("=").append(value);
			}

		}
		String sortedData = sortedDataBuffer.toString();
		String appkey = Jws.configuration.getProperty(caller+".appkey", "");
		String sign = Codec.hexMD5(caller + sortedData + appkey).toLowerCase();

		if (Logger.isDebugEnabled()) {
			Logger.debug("sorted data = %s, sign = %s", sortedData, sign);
		}

		return sign;
	}
}
