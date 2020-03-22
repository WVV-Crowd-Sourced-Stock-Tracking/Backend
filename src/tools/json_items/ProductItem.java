package tools.json_items;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tools.Product;

public class ProductItem {
	@JsonIgnore
	private Product product;
	
	private int id = 0;
	private String name = "";
	private int availability = 0;
	
	public ProductItem(@NotNull Product product) {
		this.product = product;
		this.id = product.getProductCategory().getId();
		this.name = product.getProductCategory().getName();
		this.availability = product.getQuantity();
	}

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

	public int getAvailability() {
		return availability;
	}

	public void setAvailability(int availability) {
		this.availability = availability;
	}
	
	
}
