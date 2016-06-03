package controllers.dto;

import java.util.ArrayList;
import java.util.List;

public class ChartDateSetDto {
	private List<Data> data = new ArrayList<Data>();
	private String date;
	
	public List<Data> getData() {
		return data;
	}

	public void setData(List<Data> data) {
		this.data = data;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public class Data{
		private String label;
		private double value;
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		
	}
	 
	
	
}
