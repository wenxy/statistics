package read.factory.impl;

import org.apache.commons.lang3.StringUtils;

import constants.KPI;
import jws.Jws;
import jws.Logger;
import read.IRead;
import read.factory.IReadFactory;

public class Factory_DEFAULT_KPI implements IReadFactory {
	@Override
	public  IRead getReadInstance(String caller,KPI kpi){
		if(StringUtils.isEmpty(caller)){
			Logger.error("FactoryDEFAULTAction.getReadInstance caller not define.");
			return null;
		}
		
		if(kpi == null){
			Logger.error("FactoryDEFAULTAction.getReadInstance kpi not define,caller = %s", caller);
			return null;
		}
		
		String name = "Pro_"+caller+"_"+kpi.raw().toUpperCase()+"_KPI";
		String packages = "read.factory.product";
		String implClass = packages+"."+caller+"."+name;
		IRead read = null;
		try {
			read = (IRead)Jws.classloader.loadClass(implClass).newInstance();
		} catch (InstantiationException e) {
			 
		} catch (IllegalAccessException e) {
			 
		} catch (ClassNotFoundException e) {
			 
		}
		if(read != null){
			return read;
		}
		
		//没有实现则使用默认实现处理		
		implClass = packages+".defaults"+".Pro_default_"+kpi.raw().toUpperCase()+"_KPI";
		try {
			read = (IRead)Jws.classloader.loadClass(implClass).newInstance();
		} catch (InstantiationException e) {
			Logger.error(e, "InstantiationException");
		} catch (IllegalAccessException e) {
			Logger.error(e, "IllegalAccessException");
		} catch (ClassNotFoundException e) {
			Logger.error(e, "ClassNotFoundException");
		}
		return read; 
	}

	 

	 
	
	
}
