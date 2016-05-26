package common.core;
 
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;

import constants.MessageCode;
import controllers.ucgc.api.ApiController;
import exception.BusinessException;
import exception.InvalidParameterException;
import jws.Logger;
import jws.module.ucgc.api.ApiRequest;
import jws.mvc.Before;
import jws.mvc.Catch;
import jws.mvc.Finally;
import jws.mvc.Http;
import utils.LoginUtil;

public class UcgcController extends ApiController {

	 
	@Before
	public static void beforeAction() {
		 
	}
	@Finally
	public static void finallyAction(){
		 
		// 此后不能再也任何业务处理
	}
	@Catch(Exception.class)
	protected static void onException(Throwable throwable) {
		
		if(throwable instanceof InvalidParameterException) {
			InvalidParameterException ipe = (InvalidParameterException)throwable;
			Logger.warn(throwable, "@Catch Exception: " + ipe.getMessage());
			getHelper().returnError(ipe.getMessageCode().msgCode(), ipe.getMessage());
		} else if (throwable instanceof ValidationException){
			ValidationException ve = (ValidationException)throwable;
			Logger.warn(throwable, "@Catch Exception: " + ve.getMessage());
			getHelper().returnError(MessageCode.INVALID_PARAM.msgCode(), MessageCode.INVALID_PARAM.msg());
		} else if (throwable instanceof BusinessException){
			BusinessException be = (BusinessException)throwable;
			Logger.warn(throwable, "@Catch Exception: " + be.getMessage());
			getHelper().returnError(be.getMessageCode().msgCode(), be.getMessage());
		} else {
			Logger.error(throwable, "Unknown Exception: " + throwable.getMessage());
			getHelper().returnError(MessageCode.SERVER_ERROR.msgCode(), MessageCode.SERVER_ERROR.msg());
		}
	}
}
