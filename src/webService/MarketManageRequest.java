package webService;

public class MarketManageRequest {
	private String operation = "";
	private int market_id = 0;
	private String name = "";
	private String city = "";
	private String street = "";
	private String zip = "";
	private String gps_length = "";
	private String gps_width = "";
	private String google_id = "";
	
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
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
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getGps_length() {
		return gps_length;
	}
	public void setGps_length(String gps_length) {
		this.gps_length = gps_length;
	}
	public String getGps_width() {
		return gps_width;
	}
	public void setGps_width(String gps_width) {
		this.gps_width = gps_width;
	}
	public String getGoogle_id() {
		return google_id;
	}
	public void setGoogle_id(String google_id) {
		this.google_id = google_id;
	}
	
}
