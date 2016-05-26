package common.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import common.task.TaskScheduler;
import jws.Jws;
import jws.Logger;
import jws.config.ReloadRegister;
import jws.mq.MQGroup;
import jws.mq.MQServer;
import utils.UCMQUtils;

/**
 * 初始化类，服务器启动时调用
 * @author chenxx
 *
 */
public class Init implements jws.Init {
	
	@Override
	public void init() {
		this.registerScheduler();
		//this.readConfig();
		try {
			this.initMQ();
		} catch (Exception e) {
			Logger.error(e, "");
			Jws.stop();
		}
	}
	
	/**
	 * 注册任务调度器
	 */
	private void registerScheduler() {
	    if (Jws.id.equals("")){
    		Logger.info("register task scheduler");
    		ReloadRegister.getInstance().register(TaskScheduler.getInstance());
	    }
	}

	/**
	 * 读取配置文件
	private static void readConfig() {
		Configuration.initConfig();
	} 
	 * @throws Exception */
	
	private void initMQ() throws Exception{
		boolean enableMq = Boolean.valueOf(Jws.configuration.getProperty("mq.enabled", "false"));
		if(!enableMq){
			return ;
		}
		Logger.info("set ucmq attribute.");
		String[] mqList = jws.Jws.configuration.getProperty("ucmq.list", "").split(",");
        for(String mqName:mqList){
        	if(StringUtils.isEmpty(mqName)){
        		continue;
        	}
        	UCMQUtils.configMQ(mqName);
        	UCMQUtils.printStatus(mqName);
        }
        Logger.info("set ucmq attribute,done!");
	}
}
