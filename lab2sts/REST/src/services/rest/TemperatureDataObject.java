package services.rest;

import java.util.List;

public class TemperatureDataObject {
	private String city_name;
	
	private String lat;
	
	private String lon;
	
	private List<TemperatureData> data;
	
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
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
	
	public List<TemperatureData> getData(){
		return data;
	}
	
	public String toString() {
		return "City " + this.city_name + " Data "  + this.data.toString();
	}
}
