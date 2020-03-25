package tools;

import java.util.ArrayList;
import java.util.List;

public class SupermarketItem {
	
	public SupermarketItem() {
	}
	
	private int market_id;
	private String market_name;
	private String city;
	private int zip;
	private String street;
	private String longitude;
	private String latitude;
	private String distance = "";			//UoM is meter
	private String maps_id = "";
	private Boolean open = false;
	private List<ProductAvailabilityItem> products = new ArrayList<ProductAvailabilityItem>();

	public int getMarket_id() {
		return market_id;
	}
	public void setMarket_id(int market_id) {
		this.market_id = market_id;
	}
	public String getMarket_name() {
		return market_name;
	}
	public void setMarket_name(String market_name) {
		this.market_name = market_name;
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
	
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	public String getMaps_id() {
		return maps_id;
	}
	public void setMaps_id(String maps_id) {
		this.maps_id = maps_id;
	}
	
	
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
	}
	
	public int getZip() {
		return zip;
	}
	public void setZip(int zip) {
		this.zip = zip;
	}
	/**
	 * 
	 * @param id
	 * @param name
	 * @param city
	 * @param street
	 * @param gps_length
	 * @param gps_width
	 */
	public SupermarketItem(int market_id, String market_name, String city, String street, String longitude, String latitude, String distance, String maps_id, Boolean open) {
		super();
		this.market_id = market_id;
		this.market_name = market_name;
		this.city = city;
		this.street = street;
		this.longitude = longitude;
		this.latitude = latitude;
		this.distance = distance;
		this.maps_id = maps_id;
		this.open = open;
	}
	public List<ProductAvailabilityItem> getProducts() {
		return products;
	}
	public void setProducts(List<ProductAvailabilityItem> products) {
		this.products = products;
	}
	
	public void setLocation( Location location) {
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		city = location.getCity();
		zip = location.getZip();
		street = location.getStreet();
	}

}
