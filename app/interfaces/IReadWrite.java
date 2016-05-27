package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;

import common.redis.template.SetRedisTemplate;
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
	//为什么使用换行符号，因为在读取计算时，可用分批处理
	protected static final String NEW_LINE = System.getProperty("line.separator");
	protected static final int BATCH_READ = 500;
	protected static final int KPI_CACHE_SEC=3*24*60*60;
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
	protected File getWriteStoreFile(String caller,String date,int gameId,String ch,String action,String suffix) throws Exception{
		String home = Jws.configuration.getProperty("stat.store", null);
		if(StringUtils.isEmpty(home)){
			throw new Exception("IReadWrite.getStoreFile exception,you must config the stat.store path.");
		}
		
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
	 * 计算单个集合的大小
	 * @param file
	 * @param ckey
	 * @param redisInstance
	 * @return
	 * @throws Exception
	 */
	protected long caculateSingleSize(File file,String ckey,String redisInstance) throws Exception{
		
		if(file == null || !file.exists())return 0;
		SetRedisTemplate redis = SetRedisTemplate.getInstance(redisInstance);
		Set<String> lineSet = new HashSet<String>();
		
		FileReader fr = null;
		BufferedReader br = null;
		try{
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = null;
			while((line = br.readLine())!=null){
				if(StringUtils.isEmpty(line))continue;
				lineSet.add(line);
				if(lineSet.size() >= BATCH_READ){
					if(redis.sadd(ckey, lineSet.toArray(new String[0]))){
						lineSet.clear();
					}
				}
			}
			if(lineSet.size()>0){
				if(redis.sadd(ckey, lineSet.toArray(new String[0]))){
					lineSet.clear();
				}
			} 
			br.close();
			fr.close(); 
			long count =  redis.sunionstore(ckey,ckey);
			//计算完成之后失效key
			redis.sexpireAt(ckey, RedisUtil.unixTimeSince(1));
			return count;
			
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
		return 0;
		
	}
	/**
	 * 计算多个文件的交集数量 files ckeys 一一对应
	 * @param redisInstance
	 * @param files
	 * @param ckeys
	 * @return
	 * @throws Exception
	 */
	protected long caculateMultiSize(File[] files,String[] ckeys,String redisInstance) throws Exception{
		if(files == null || ckeys==null){
			return 0;
		}
		if(files.length != ckeys.length){
			return 0;
		}
		if(files.length == 0){
			return 0;
		}
		
		SetRedisTemplate redis = SetRedisTemplate.getInstance(redisInstance);
		
		for(int i=0;i<files.length;i++){
			Set<String> lineSet = new HashSet<String>();
			FileReader fr = null;
			BufferedReader br = null;
			if(files[i] == null || !files[i].exists())continue;
			try{
				fr = new FileReader(files[i]);
				br = new BufferedReader(fr);
				String line = null;
				while((line = br.readLine())!=null){
					if(StringUtils.isEmpty(line))continue;
					lineSet.add(line);
					if(lineSet.size() >= BATCH_READ){
						if(redis.sadd(ckeys[i], lineSet.toArray(new String[0]))){
							lineSet.clear();
						}
					}
				}
				if(lineSet.size()>0){
					if(redis.sadd(ckeys[i], lineSet.toArray(new String[0]))){
						lineSet.clear();
					}
				} 
				br.close();
				fr.close(); 
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
		}
		String tmpKey = System.currentTimeMillis()+"_tmpckey";
		long count =  redis.sinterstore(tmpKey, ckeys); 
		//计算完成之后失效key
		redis.sexpireAt(tmpKey, RedisUtil.unixTimeSince(1));
		for(String ckey:ckeys){
			redis.sexpireAt(ckey, RedisUtil.unixTimeSince(1));
		} 
		return count; 
	}
	/**
	 * 讀取一個數字
	 * @param file
	 * @return
	 */
	protected String readLine(File file){
		FileReader fr = null;
		BufferedReader br = null;
		if(file == null || !file.exists())return "0";
		try{
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = br.readLine();
			br.close();
			fr.close(); 
			return StringUtils.isEmpty(line)?"0":line;
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
		return "0";
	}
	/**
	 * 是否今天日期
	 * @param date
	 * @return
	 */
	protected boolean isToday(String date){
		try{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date dt = df.parse(date);
			return DateUtil.isTodayTime(dt.getTime());
		}catch(Exception e){
			Logger.error(e,"");
		}
		return false;
	}
	
}
