package utils;

import java.lang.reflect.Field;

import common.annotation.FromDDL;
import constants.MessageCode;
import exception.BusinessException;
import jws.Logger;

public class CopyDDLUtil <DDL,DTO>{
	
	private DDL ddl;
	private DTO dto;
	
	public CopyDDLUtil(DDL ddl,DTO dto){
		this.ddl = ddl;
		this.dto = dto;
		//(Class<T>)Jws.classloader.loadClass(persistClassName)
	}
	public void copy() throws BusinessException  {
		Field[] fields = dto.getClass().getDeclaredFields();
		for(Field f:fields){
			FromDDL fromDDL = f.getAnnotation(FromDDL.class);
			String name = fromDDL==null?f.getName():fromDDL.name();
			
			try {
				Object o = PropertyUtil.getProperty(ddl, name);
				PropertyUtil.setProperty(dto, f.getName(),o ); 
			} catch (ClassNotFoundException e) { 
				Logger.error(e, "");
				throw new BusinessException(MessageCode.SERVER_ERROR);
			}
			
		}
	}
}
