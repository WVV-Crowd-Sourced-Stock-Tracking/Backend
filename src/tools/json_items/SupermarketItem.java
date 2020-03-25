package tools.json_items;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tools.Supermarket;

public class SupermarketItem {
	
	@JsonIgnore
	private Supermarket supermarket;
	
	public SupermarketItem(@NotNull Supermarket supermarket) {
		this.supermarket = supermarket;
		
		id = supermarket.getMarket_id();
		name = supermarket.getName();
		city = supermarket.getLocation().getCity();
		street = supermarket.getLocation().getStreet();
		lng = supermarket.getLocation().getGpsLength();
		lat = supermarket.getLocation().getGpsWidth();
		mapsId = supermarket.getGoogle_id();
		distance = supermarket.getDistance();
		open = supermarket.isOpenNow();
	}
	public SupermarketItem() {
	}
	
	private int id;
	private String name;
	private String city;
	private String street;
	private String lng;
	private String lat;
	private String distance = "";			//UoM is meter
	private String mapsId = "";
	private Boolean open = false;
	private List<ProductItem> products = new ArrayList<ProductItem>();

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	public String getMapsId() {
		return mapsId;
	}
	public void setMapsId(String mapsId) {
		this.mapsId = mapsId;
	}
	
	
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
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
	public SupermarketItem(int id, String name, String city, String street, String lng, String lat, String distance, String mapsId, Boolean open) {
		super();
		this.id = id;
		this.name = name;
		this.city = city;
		this.street = street;
		this.lng = lng;
		this.lat = lat;
		this.distance = distance;
		this.mapsId = mapsId;
		this.open = open;
	}
	public List<ProductItem> getProducts() {
		return products;
	}
	public void setProducts(List<ProductItem> products) {
		this.products = products;
	}

}
