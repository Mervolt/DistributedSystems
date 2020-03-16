package services.rest;

public class TemperatureData {
	private String temp;
	private String datetime;
	private String max_temp;
	private String min_temp;
	private String lat;
	private String lon;
	
	public String getTemp() {
		return temp;
	}
	
	public void setTemp(String temp) {
		this.temp = temp;
	}
	
	public String getMax_temp() {
		return max_temp;
	}
	public void setMax_temp(String max_temp) {
		this.max_temp = max_temp;
	}
	
	public String getMin_temp() {
		return min_temp;
	}
	
	public void setMin_temp(String min_temp) {
		this.min_temp = min_temp;
	}
	
	public String toString() {
		return "Temp: " + this.temp + " max temp: " + this.max_temp + " min temp: " + this.min_temp + " ";
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}
}