package constants;

import org.apache.commons.lang3.StringUtils;

/**
 * 基本统计指标
 * @author fish
 *
 */
public enum KPI {
	
	ACT,
	UIDREG,
	IMEIREG,
	UIDLOGIN,
	IMEILOGIN,
	NEWUIDUSER,
	NEWIMEIUSER,
	OLDUIDUSER,
	OLDIMEIUSER,
	INCOME,
	NEWINCOME,
	OLDINCOME,
	PAYUSER,
	NEWPAYUSER,
	OLDPAYUSER,
	PAYTOTAL,
	NEWPAYTOTAL,
	OLDPAYTOTAL,
	UIDDAU,
	IMEIDAU,
	ARPPU,
	PAYRATE,
	NEWPAYRATE,
	OLDPAYRATE,
	UIDKEEPRATE,
	IMEIKEEPRATE,
	LTV;
	
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
	
}
