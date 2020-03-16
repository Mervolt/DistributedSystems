package services.rest;


public class OpenWeatherData {
	private OpenWeatherMainElement main;

	public OpenWeatherMainElement getOpenWeatherElement() {
		return main;
	}

	public void setOpenWeatherMainElement(OpenWeatherMainElement openWeatherElement) {
		this.main = openWeatherElement;
	}
}
