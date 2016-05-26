package constants;

import org.apache.commons.lang3.StringUtils;

/**
 * 用户行为定义
 * @author fish
 *
 */
public enum Action {
	ACT,
	REG,
	LOGIN,
	PAY,
	LVUP,
	ROLECREATE;
	
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
	
}
