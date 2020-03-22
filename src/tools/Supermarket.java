package tools;

public class Supermarket {
	private int market_id;
	private String name;
	private Location location;
	private String google_id;
	private String distance = "";
	
	public int getMarket_id() {
		return market_id;
	}
	public void setMarket_id(int market_id) {
		this.market_id = market_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getGoogle_id() {
		return google_id;
	}
	public void setGoogle_id(String google_id) {
		this.google_id = google_id;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
}
