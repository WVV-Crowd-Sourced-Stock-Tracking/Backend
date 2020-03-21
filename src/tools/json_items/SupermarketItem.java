package tools.json_items;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tools.Supermarket;

public class SupermarketItem {
	
	@JsonIgnore
	private Supermarket supermarket;
	
	public SupermarketItem(@NotNull Supermarket supermarket) {
		this.supermarket = supermarket;
		
		id = supermarket.getId();
		name = supermarket.getName();
		city = supermarket.getLocation().getCity();
		street = supermarket.getLocation().getStreet();
		gps_length = supermarket.getLocation().getGpsLength();
		gps_width = supermarket.getLocation().getGpsWidth();
		
	}
	
	private int id;
	private String name;
	private String city;
	private String street;
	private long gps_length;
	private long gps_width;

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
	public long getGps_length() {
		return gps_length;
	}
	public void setGps_length(long gps_length) {
		this.gps_length = gps_length;
	}
	public long getGps_width() {
		return gps_width;
	}
	public void setGps_width(long gps_width) {
		this.gps_width = gps_width;
	}

}
