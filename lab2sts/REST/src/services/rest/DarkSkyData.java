package services.rest;

public class DarkSkyData {
	private DarkSkyElement currently;
	private DarkSkyElement hourly;
	
	public DarkSkyElement getCurrently() {
		return currently;
	}

	public void setCurrently(DarkSkyElement currently) {
		this.currently = currently;
	}

	public DarkSkyElement getHourly() {
		return hourly;
	}

	public void setHourly(DarkSkyElement hourly) {
		this.hourly = hourly;
	}
}
