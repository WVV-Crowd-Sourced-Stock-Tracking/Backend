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
	private String gps_length;
	private String gps_width;
	private List<ProductItem> product = new ArrayList<ProductItem>();

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
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param city
	 * @param street
	 * @param gps_length
	 * @param gps_width
	 */
	public SupermarketItem(int id, String name, String city, String street, String gps_length, String gps_width) {
		super();
		this.id = id;
		this.name = name;
		this.city = city;
		this.street = street;
		this.gps_length = gps_length;
		this.gps_width = gps_width;
	}
	public List<ProductItem> getProduct() {
		return product;
	}
	public void setProduct(List<ProductItem> product) {
		this.product = product;
	}

}
