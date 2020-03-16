package services.rest;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

@Path("/weather")
public class RESTHelloWorld {
	
	private static String getWeatherBitAPIKey() {
		return "9beb0120fc584ecea976022ac1d51901";
	}
	
	private static String getOpenWeatherAPIKey() {
		return "e4db018b995487570d852046de73cd7e";
	}
	
	private static String getDarkSkyAPIKey() {
		return "10e901539e79979cb85a914b324af261";
	}
	
	private String toHTML(DarkSkyData dsd, TemperatureDataObject currentTdo, List<TemperatureDataObject> tdos, OpenWeatherData owd) {
		StringBuilder sr = new StringBuilder();
		OpenWeatherMainElement owme = owd.getOpenWeatherElement();
		sr.append("Current weather: Temperature ").append(currentTdo.getData().get(0).getTemp()).append("\r\n");
		
		if(dsd != null && dsd.getCurrently() != null) {
			 sr.append("<br/>Current situation: ").append(dsd.getCurrently().getIcon()).
			 append(" ").append(dsd.getCurrently().getSummary());
			 sr.append("<br/>About to happen: ").append(dsd.getHourly().getSummary());
		}
		sr.append("<br/>Current conditions: Humidity (%) ").append(owme.getHumidity()).append(" Pressure (hPa) ").
		append(owme.getPressure()).append("\r\n");
	     
		sr.append(" <table>\r\n" + 
				"  <tr>\r\n" + 
				"    <th></th>\r\n" + 
				"    <th>At this time</th>\r\n" + 
				"    <th> Maximum </th>\r\n" + 
				"    <th> Minimum </th>\r\n" + 
				"  </tr>\r\n");
		
		for (TemperatureDataObject tdo : tdos) {
			List<TemperatureData> tds = tdo.getData();
			TemperatureData td = tds.get(0);
			
			sr.append("<tr>\r\n");
			sr.append("<td>" + td.getDatetime() + "</td>\r\n");
			sr.append("<td>" + td.getTemp() + "</td>\r\n");
			sr.append("<td>" + td.getMax_temp() + "</td>\r\n");
			sr.append("<td>" + td.getMin_temp() + "</td>\r\n");
			sr.append("</tr>\r\n");
		
		}
		sr.append("</table>");
		return sr.toString();
	}
	
	

	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String handleClientRequest(@QueryParam("City") String cityName, @QueryParam("Days") String days) {
		
		LocalDateTime currentTime = LocalDateTime.now();

		Integer convertedDays;
		try {
			convertedDays = Integer.parseInt(days);
			if(convertedDays < 1)
				return "Please write a positive number!";
		}
		catch(Exception ex) {
			return "Not a number!";
		}
		
		LocalDateTime pastTime = currentTime.minusDays(convertedDays);
		pastTime.plusDays(1);
		Month pastMonth = pastTime.getMonth();
		Integer pastDay = pastTime.getDayOfMonth();
		Integer pastYear = pastTime.getYear();
		LocalDateTime dayNextToPastTime = pastTime.plusDays(1);
		Month nextMonth = dayNextToPastTime.getMonth();
		Integer nextDay = dayNextToPastTime.getDayOfMonth();
		Integer nextYear = dayNextToPastTime.getYear();
		
		List<TemperatureDataObject> result = new ArrayList<>();
		
		for(int i = 0; i < convertedDays; i++) {
				
			StringBuilder weatherBitRequest = new StringBuilder("https://api.weatherbit.io/v2.0/history/daily?&city=");
			weatherBitRequest.append(cityName.trim()).append("&start_date=");
			weatherBitRequest.append(pastYear).append("-").append(pastMonth.getValue()).append("-").append(pastDay);
			weatherBitRequest.append("&end_date=");
			weatherBitRequest.append(nextYear).append("-").append(nextMonth.getValue()).append("-").append(nextDay);
			weatherBitRequest.append("&key=").append(getWeatherBitAPIKey());
			
			String weatherBitRequestCompleted = weatherBitRequest.toString();
			
			
			Client client = ClientBuilder.newClient(); 
			WebTarget weatherBitTarget = client.target(URI.create(weatherBitRequestCompleted));
			Gson gson = new Gson();
			TemperatureDataObject tdo = gson.fromJson(weatherBitTarget.request
					(MediaType.APPLICATION_JSON).get(String.class), TemperatureDataObject.class);
			result.add(tdo);
			 
			
			pastTime = pastTime.plusDays(1);
			pastMonth = pastTime.getMonth();
			pastDay = pastTime.getDayOfMonth();
			pastYear = pastTime.getYear();
			
			dayNextToPastTime = pastTime.plusDays(1);
			nextMonth = dayNextToPastTime.getMonth();
			nextDay = dayNextToPastTime.getDayOfMonth();
			nextYear = dayNextToPastTime.getYear();
			
		}
		
		StringBuilder currentWeatherRequest = new StringBuilder("https://api.weatherbit.io/v2.0/current?city=");
		currentWeatherRequest.append(cityName).append("&key=").append(getWeatherBitAPIKey());
		String currentWeatherRequestCompleted = currentWeatherRequest.toString();
		
		Client weatherBitCurrentClient = ClientBuilder.newClient();
		WebTarget weatherBitCurrentTarget = weatherBitCurrentClient.target(URI.create(currentWeatherRequestCompleted));
		
		Gson currentWeatherGson = new Gson();
		TemperatureDataObject currentTdo = currentWeatherGson.fromJson
				(weatherBitCurrentTarget.request(MediaType.APPLICATION_JSON).get(String.class), TemperatureDataObject.class);
		
		

		StringBuilder darkSkyForecastRequest = new StringBuilder("https://api.darksky.net/forecast/");
		darkSkyForecastRequest.append(getDarkSkyAPIKey()).append("/");
		darkSkyForecastRequest.append(currentTdo.getData().get(0).getLat()).append(",").append(currentTdo.getData().get(0).getLon());
		String darkSkyForecastRequestCompleted = darkSkyForecastRequest.toString();
		
		Client darkSkyClient = ClientBuilder.newClient();
		WebTarget darkSkyTarget = darkSkyClient.target(URI.create(darkSkyForecastRequestCompleted));
		
		
		 Gson darkSkyGson = new Gson(); 
		 DarkSkyData dsd = darkSkyGson.fromJson(darkSkyTarget.request(MediaType.APPLICATION_JSON).get(String.class),
		 DarkSkyData.class);
		 
		
		
		StringBuilder openWeatherRequest = new StringBuilder("http://api.openweathermap.org/data/2.5/weather?q=");
		openWeatherRequest.append(cityName).append("&units=metric&appid=").append(getOpenWeatherAPIKey()); 
		String openWeatherRequestCompleted = openWeatherRequest.toString();

		Client openWeatherClient = ClientBuilder.newClient();
		WebTarget openWeatherTarget = openWeatherClient.target(URI.create(openWeatherRequestCompleted));

		
		Gson openWeatherGson = new Gson();
		OpenWeatherData owd = openWeatherGson.fromJson
				(openWeatherTarget.request(MediaType.APPLICATION_JSON).get(String.class), OpenWeatherData.class);
		
		
		return toHTML(dsd, currentTdo, result, owd);
		}
}