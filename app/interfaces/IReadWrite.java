package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;

import common.redis.template.ValueRedisTemplate;
import constants.MQInstance;
import jws.Jws;
import jws.Logger;
import read.IRead;
import utils.DateUtil;
import utils.FileUtil;
import utils.RedisUtil;
import write.IWrite;

public abstract class IReadWrite implements IWrite,IRead{
	@Override
	public String read(String caller,String date,int gameId,String ch){
		return null;
	}
	@Override
	public void write(JsonObject json){
		return;
	} 
	/**
	 * 獲取store
	 * @param json
	 * @return
	 * @throws Exception 
	 */
	protected File getWriteStoreFile(JsonObject json,String suffix) throws Exception{
		String home = Jws.configuration.getProperty("stat.store", null);
		if(StringUtils.isEmpty(home)){
			throw new Exception("IReadWrite.getStoreFile exception,you must config the stat.store path.");
		}
		String caller =  json.get("caller").getAsString();
		String action =  json.get("action").getAsString();
		int gameId = json.get("game").getAsJsonObject().get("id").getAsInt();
		String ch = json.get("game").getAsJsonObject().get("ch").getAsString();
		String date = json.get("game").getAsJsonObject().get("date").getAsString();
		
		String fname = date+"_"+gameId+"_"+ch+"."+suffix;
		// home/caller/action/year/gameId/ch/
		String filePath = home+File.separator+caller+File.separator+action+File.separator+DateUtil.getYear(date)+File.separator+gameId+File.separator+ch+File.separator;
		
		FileUtil.createDir(filePath);
		
		String fileName = filePath+fname;
		File file = new File(fileName);
		if(file.exists()){
			return file;
		}
		if(file.createNewFile()){
			return file;
		}
		throw new Exception("IReadWrite.getStoreFile create file failed."+filePath);
	}
	
	/**
	 * 獲取store
	 * @param json
	 * @return
	 * @throws Exception 
	 */
	protected File getReadStoreFile(String caller,String date,int gameId,String ch,String action,String suffix) throws Exception{
		String home = Jws.configuration.getProperty("stat.store", null);
		if(StringUtils.isEmpty(home)){
			throw new Exception("IReadWrite.getReadStoreFile exception,you must config the stat.store path.");
		}
		
		String fname = date+"_"+gameId+"_"+ch+"."+suffix;
		// home/caller/action/year/gameId/ch/
		String filePath = home+File.separator+caller+File.separator+action+File.separator+DateUtil.getYear(date)+File.separator+gameId+File.separator+ch+File.separator;
		String fileName = filePath+fname;
		File file = new File(fileName);
		if(file.exists()){
			return file;
		}
		return null;
	}
	
	/**
	 * 根据key从redis读取缓存数据
	 * @param key
	 * @return
	 */
	protected String readFromRedis(String key){
		if(StringUtils.isEmpty(key))return null;
		ValueRedisTemplate redis = ValueRedisTemplate.getInstance(MQInstance.BASE);
		return redis.get(key);
	}
	
	//写入redis，便于新旧用户判断,次日失效
	protected void writeToRedis(String date,String key,String value , int exprieDay) throws Exception{
		ValueRedisTemplate redis = ValueRedisTemplate.getInstance(MQInstance.BASE);
		redis.setAtExpire(key,value, RedisUtil.unixTimeSince(date, exprieDay));
	}
	
	
	//写入redis，便于新旧用户判断,seconds 秒后失效
	protected void writeToRedis(String key,String value , int seconds) throws Exception{
		ValueRedisTemplate redis = ValueRedisTemplate.getInstance(MQInstance.BASE);
		redis.setAtExpire(key,value, RedisUtil.unixTimeSince(seconds));
	}
	 
	/**
	 * 根据key从File读取数据
	 * @param file
	 * @return
	 */
	protected String readFromFile(File file){
		if(file == null)return null;
		FileReader fr = null;
		BufferedReader br = null;
		try{
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = br.readLine();
			br.close();
			fr.close();
			return line;
		}catch(Exception e){
			Logger.error(e, "1");
		}finally{
			
				try {
					if(fr!=null)fr.close();
					if(br!=null)br.close();
				} catch (IOException e) {
					Logger.error(e, "2");
				}
			
		}
		return null;
	}
	
}
