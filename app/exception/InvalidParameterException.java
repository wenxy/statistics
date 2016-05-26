package exception;

import constants.MessageCode;

public class InvalidParameterException extends RuntimeException{

	private String message;
    
    private MessageCode msgCode;
    
    public InvalidParameterException() {  
    	this.msgCode = MessageCode.INVALID_PARAM;
    	this.message = String.format(msgCode.msg());
    }
    
    public String getMessage() {
    	return this.message;
    }
    
    public MessageCode getMessageCode() {
    	return this.msgCode;
    }
}
