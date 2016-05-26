package utils;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import jws.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

public class ChecksumHelper {

	/**
	 * 获取业务校验码
	 * 
	 * @param params URL参数对，需要使用TreeMap来保持参数字典序
	 * @param secretKey 业务密钥
	 * 
	 * @return 业务校验码
	 */
	public static String getChecksum(Map<String, String> params, String secretKey) {
		StringBuilder sb = new StringBuilder();

		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value == null) {
				value = "";
			}
			sb.append(value);
		}

		if (!StringUtils.isEmpty(secretKey)) {
			sb.append(secretKey);
		}

		String vcode = DigestUtils.md5Hex(sb.toString());
		Logger.info("产生vcode的源串: %s, vcode=%s", sb, vcode);
		return vcode;
	}
	

}
