package read.factory.product.defaults;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import constants.KPI;
import constants.MQInstance;
import interfaces.IReadWrite;
import jws.Logger;
import utils.DateUtil;
import utils.RedisUtil;

public class Pro_default_IMEIREG_KPI extends IReadWrite{
	@Override
	public String read(String caller,String date, int gameId, String ch) {
		try{
			return String.valueOf(regNumForDateByIMEI(caller,date,gameId,ch));
		}catch(Exception e){
			Logger.error(e, "0");
		}
		return "";
	}
}