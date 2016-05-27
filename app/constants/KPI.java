package constants;

import org.apache.commons.lang3.StringUtils;

/**
 * 基本统计指标
 * @author fish
 *
 */
public enum KPI {
	
	ACT_KPI,//激活数
	UIDREG_KPI,//uid排重注册数
	IMEIREG_KPI,//imei排重注册数
	UIDLOGIN_KPI,//uid排重登录数
	IMEILOGIN_KPI,//imei排重登录数
	NEWUIDLOGIN_KPI,//uid排重新用户登录数
	NEWIMEILOGIN_KPI,//imei排重新用户登录数
	OLDUIDLOGIN_KPI,//uid排重老用户登录数
	OLDIMEILOGIN_KPI,//imei排重老用户登录数
	PAYUSER_KPI,//uid排重付费用户数
	NEWPAYUSER_KPI,//uid排重新付费用户数
	OLDPAYUSER_KPI,//uid排重老付费用户数
	PAYTOTAL_KPI,//总收入
	NEWPAYTOTAL_KPI,//新用户总收入
	OLDPAYTOTAL_KPI,//老用户总收入
	ARPU_KPI,//平均每用户收入
 	ARPPU_KPI,//平均每付费用户收入
	PAYRATE_KPI,//付费率
	NEWPAYRATE_KPI,//新用户付费率
	OLDPAYRATE_KPI,//老用户付费率
	UIDKEEPRATE_KPI,//uid用户留存率
	IMEIKEEPRATE_KPI,//imen用户留存率
	LTV_KPI;//用户价值long time value
	
	public String raw(){
		return this.name().toLowerCase();
	}
	
	public static KPI find(String kpistr){
		if(StringUtils.isEmpty(kpistr))return null;
		for(KPI kpi : KPI.values()){
			if(kpi.raw().equalsIgnoreCase(kpistr)){
				return kpi;
			}
		}
		return null;
	}
	
	public  String forClassName(){
		return this.raw().toUpperCase();
	}
	
}
