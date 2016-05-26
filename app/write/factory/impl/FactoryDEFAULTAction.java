package write.factory.impl;

import org.apache.commons.lang3.StringUtils;

import constants.Action;
import jws.Jws;
import jws.Logger;
import write.IWrite;
import write.factory.IWriteFactory;

public class FactoryDEFAULTAction implements IWriteFactory {
	@Override
	public  IWrite getWriteInstance(String caller,Action action){
		if(StringUtils.isEmpty(caller)){
			Logger.error("FactoryDEFAULTAction.getWriteInstance caller not define.");
			return null;
		}
		
		if(action == null){
			Logger.error("FactoryDEFAULTAction.getWriteInstance action not define,caller = %s", caller);
			return null;
		}
		
		String name = "Pro_"+caller+"_"+action.raw().toUpperCase();
		String packages = "write.factory.product."+caller;
		String implClass = packages+"."+name;
		IWrite write = null;
		try {
			write = (IWrite)Jws.classloader.loadClass(implClass).newInstance();
		} catch (InstantiationException e) {
			Logger.error(e, "InstantiationException");
		} catch (IllegalAccessException e) {
			Logger.error(e, "IllegalAccessException");
		} catch (ClassNotFoundException e) {
			Logger.error(e, "ClassNotFoundException");
		}
		if(write != null){
			return write;
		}
		
		//没有实现则使用默认实现处理		
		implClass = packages+".Prod_default_"+action.raw().toUpperCase();;
		try {
			write = (IWrite)Jws.classloader.loadClass(implClass).newInstance();
		} catch (InstantiationException e) {
			Logger.error(e, "InstantiationException");
		} catch (IllegalAccessException e) {
			Logger.error(e, "IllegalAccessException");
		} catch (ClassNotFoundException e) {
			Logger.error(e, "ClassNotFoundException");
		}
		return write; 
	}

	 
	
	
}
