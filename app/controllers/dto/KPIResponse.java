package controllers.dto;

import java.util.HashMap;
import java.util.Map;

public class KPIResponse {

	private long id = System.currentTimeMillis();
	private State state = new State();
	private Map<String,String> data = new HashMap<String,String>();
	
	
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public State getState() {
		return state;
	}


	public void setState(State state) {
		this.state = state;
	}


	public Map<String, String> getData() {
		return data;
	}


	public void setData(Map<String, String> data) {
		this.data = data;
	}


	public class State{
		private int code;
		private String msg;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
	
	
}
