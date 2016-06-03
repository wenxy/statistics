package controllers.dto;

public class KIPIQueryDto {
	private long id = System.currentTimeMillis();
	private String caller;
	private String sign;
	
	private Data data = new Data();
	
	
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getCaller() {
		return caller;
	}


	public void setCaller(String caller) {
		this.caller = caller;
	}


	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
	}


	public Data getData() {
		return data;
	}


	public void setData(Data data) {
		this.data = data;
	}


	public class Data{
		private int gameId;
		private String ch;
		private String date;
		private String kpis;
		public int getGameId() {
			return gameId;
		}
		public void setGameId(int gameId) {
			this.gameId = gameId;
		}
		public String getCh() {
			return ch;
		}
		public void setCh(String ch) {
			this.ch = ch;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getKpis() {
			return kpis;
		}
		public void setKpis(String kpis) {
			this.kpis = kpis;
		}
		
	}
	
}
