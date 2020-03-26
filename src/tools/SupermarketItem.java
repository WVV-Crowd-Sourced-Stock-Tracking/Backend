package tools;

import java.util.ArrayList;
import java.util.List;

public class SupermarketItem {
	
	public SupermarketItem() {
	}
	
	private int market_id;
	private String market_name;
	private String city;
	private String zip;
	private String street;
	private String longitude;
	private String latitude;
	private String distance = "";			//UoM is meter
	private String maps_id = "";
	private String icon_url = "";
	private List<PeriodItem> periods = new ArrayList<PeriodItem>();
	private List<MarketStockItem> products = new ArrayList<MarketStockItem>();

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
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getIcon_url() {
		return icon_url;
	}
	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}
	/**
	 * 
	 * @param id
	 * @param name
	 * @param city
	 * @param street
	 * @param longitude
	 * @param latitude
	 */
	public SupermarketItem(int market_id, String market_name, String city, String street, String longitude, String latitude, String distance, String maps_id) {
		super();
		this.market_id = market_id;
		this.market_name = market_name;
		this.city = city;
		this.street = street;
		this.longitude = longitude;
		this.latitude = latitude;
		this.distance = distance;
		this.maps_id = maps_id;
	}
	public List<MarketStockItem> getProducts() {
		return products;
	}
	public void setProducts(List<MarketStockItem> products) {
		this.products = products;
	}
	
	public List<PeriodItem> getPeriods() {
		return periods;
	}
	public void setPeriods(List<PeriodItem> periods) {
		this.periods = periods;
	}
	public void setLocation( Location location) {
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		city = location.getCity();
		zip = location.getZip();
		street = location.getStreet();
	}

}
