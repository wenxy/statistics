package constants;

import org.apache.commons.lang3.StringUtils;

/**
 * 用户行为定义
 * @author fish
 *
 */
public enum Action {
	ACT_ACTION,//激活动作
	REG_ACTION,//注册动作
	LOGIN_ACTION,//登录动作
	PAY_ACTION,//支付动作
	LVUP_ACTION,//升级动作
	ROLECREATE_ACTION;//角色建立动作
	
	public String raw(){
		return this.name().toLowerCase();
	}
	
	public static Action find(String action){
		if(StringUtils.isEmpty(action))return null;
		for(Action act : Action.values()){
			if(act.raw().equalsIgnoreCase(action)){
				return act;
			}
		}
		return null;
	}
	
	public  String forClassName(){
		return this.raw().toUpperCase();
	}
	
}
