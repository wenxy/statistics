package constants;

import org.apache.commons.lang3.StringUtils;

/**
 * @author shun
 * 业务操作信息类，根据相应的提示key，在messages文件中取得相应的提示语
 * 其中的msgCode会返回给调用端作为结果码
 */
public enum MessageCode {
	//
	//按业务划分code范围
	//接口调用统一 40000-49999
	
	 
	//SDK 业务相关 20000-29999
	//钱包交易相关 30000-39999
	SERVER_ERROR("服务器 异常",40000),
	INVALID_PARAM("无效参数",40001),
	
	//
	PRIVILEGE_REJECT("权限不够",20000),
	CH_NOT_EXISIT("渠道不存在",20001),
	CH_NOT_FIRST("[%s]非一级渠道号",20002),
	CH_NOT_SEC("[%s]非二级渠道号",20003),
	CH_ADD_ERR("渠道新增失败",20004),
	CH_UPD_ERR("渠道更新失败",20005),
	CH_EXISIT_ERR("渠道已经存在",20006),
	GAME_NOT_EXISIT("游戏不存在",20007),
	CH_GAME_EXISIT("该游戏已经配置过了",20007),
	CH_GAME_ADD_ERR("游戏配置失败",20008),
	CH_GAME_NOT_EXISIT("该游戏还未新增配置",20009),
	CH_GAME_DEL_ERR("游戏配置删除失败",20010),
	CH_GAME_CONF_UPDATE_ERR("游戏信息更新失败",20011),
	CH_GAME_REF_UPDATE_ERR("游戏渠道信息更新失败",20012),
	CH_TASK_GAME_ADD_ERR("打包任务新增失败",20013),
	CH_TASK_GAME_NOT_UPLOAD_ERR("游戏产商未上传游戏包",20013),
	CH_TASK_GAME_EXIST_ERR("该打包任务已存在，正等待执行",20014),
	CH_TASK_GAME_UPDATE_ERR("打包任务更新失败",20015),
	CH_ADD_FORMAT_ERR("新增渠道失败，渠道账号格式不正确，只能由字母数字下划线组合",20016),
	COUPON_ADD_REBATE_ERR("保存返利结算失败",20017),
	COUPON_AUDIT_REBATE_ERR("审核返利结算失败",20018),
	REBATE_NOT_EXISIT("返利记录不存在",20019),
	CH_USER_NOT_EXISIT("代金券后台不存在该渠道用户",20020),
	CH_USER_AMOUNT_NOT_EXISIT("该渠道用户的额度记录不存在",20021),
	//
	WALLET_TRADE_ERR("交易失败",30000),
	WALLET_CREATE_ERR("创建钱包失败",30001),
	WALLET_NOT_EXISIT_ERR("钱包不存在",30002),
	WALLET_AMOUMT_ILLEGAL_ERR("交易金额非正整数",30002),
	WALLET_FUNDS_LESS__ERR("账户余额不足",30003),
	WALLET_ILLEGAL_CH_ERR("渠道转账非法渠道用户",30004),
	WALLET_PWD_NOT_SETTING("支付密码未设置",30005),
	WALLET_PWD_WRONG_ERR("支付密码错误",30006),
	WALLET_PWD_CHANGE_ERR("修改支付密码错误",30007),
	
	 
	
	 /*SDK 服务定义  10 ~ 1000*/
	 ERROR_CODE_10("解析请求参数出错",10),
	 ERROR_CODE_99 ("服务器内部错误",99),
	 ERROR_CODE_110("记录客户端信息出错",110),
	 ERROR_CODE_111("登录失败，账号不存在",111),
	 ERROR_CODE_112("登录失败，密码不正确",112),
	 ERROR_CODE_113("登录失败，账号已锁定",113),
	 ERROR_CODE_114("会话刷新失败，用户登录会话不存在",114),
	 ERROR_CODE_115("会话刷新失败，更新最近一次登录时间出错",115),
	 ERROR_CODE_116("支付失败，用户未登录或会话已超时",116),
	 ERROR_CODE_117("用户未登录或会话已超时,%s",117),
	 ERROR_CODE_118("订单状态查询失败，找不到相关订单信息",118),
	 ERROR_CODE_119("资源包下载失败，根据文件名找不到相关资源包",119),
	 ERROR_CODE_120("错误日志上传失败，日志内容为空",120),
	 ERROR_CODE_121("游戏id与cpid不匹配",121),
	 ERROR_CODE_122("游戏cp订单号为空",122),
	 ERROR_CODE_123("新增订单失败",123),
	 ERROR_CODE_124("订单ID为空",124),
	 ERROR_CODE_125("更新订单失败",125),
	 ERROR_CODE_126("获取订单配置失败",126),
	 ERROR_CODE_127("非法用户ID",127),
	 ERROR_CODE_128("非法游戏ID",128),
	 ERROR_CODE_129("代金券ID列表为空",129),
	 ERROR_CODE_130("非法支付金额",130),
	 ERROR_CODE_131("使用代金券的业务标识为空",131),
	 ERROR_CODE_132("不足以支付所需金额",132),
	 ERROR_CODE_133("更新代金券失败",133),
	 ERROR_CODE_134("创建订单失败，用户未登录或会话已超时",134),
	 ERROR_CODE_135("获取订单失败",135),
	 ERROR_CODE_136("生成支付链接失败",136),
	 ERROR_CODE_137("不支持的游戏源",137),
	 ERROR_CODE_138("创建账号失败",138),
	 ERROR_CODE_139("密码修改失败",139),
	 ERROR_CODE_140("手机绑定失败",140),
	 ERROR_CODE_141("用户不存在",141),
	 ERROR_CODE_142("密码输入错误",142),
	 ERROR_CODE_143("非法手机号",143),
	 ERROR_CODE_150("用户起角色名称较前时间已创建",150),
	 ERROR_CODE_156("用户角色未创建",156),
	 ERROR_CODE_999("用户未登录或会话已超时,请重新登录",999),
	 
	 ERROR_CODE_500("%s",500),
	 OP_TYPE_SYSTEM_INIT("系统初始化",0),
	 OP_TYPE_QUICK_REGISTER("一键注册", 10), 
	 OP_TYPE_USER_LOGIN("用户登录",20),
	 OP_TYPE_USER_LOGOUT("用户注销",40),
	 OP_RESULT_SUCCESS("操作成功",1),
	 OP_RESULT_FAILED("操作失败",0);

	
	private String msg;
	
	private int msgCode;
	
	private MessageCode(String msg, int msgCode) {
		this.msg = msg;
		this.msgCode = msgCode;
	}
	
	public String msg() {
		return msg;
	}

	public int msgCode() {
		return msgCode;
	}

	public String toString() {
		return Integer.valueOf(this.msgCode).toString();
	}
	
	public static MessageCode getMessageCode(String msgKey) {
		if(StringUtils.isEmpty(msgKey)) {
			return null;
		}
		for(MessageCode msgCode : MessageCode.values()) {
			if(msgCode.msg().equals(msgKey)) {
				return msgCode;
			}
		}
		return null;
	}
}
