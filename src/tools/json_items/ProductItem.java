package tools.json_items;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tools.Product;

public class ProductItem {
	@JsonIgnore
	private Product product;
	
	private int product_id = 0;
	private String product_name = "";
	private int quantity = 0;
	
	public ProductItem(@NotNull Product product) {
		this.product = product;
		this.product_id = product.getProductCategory().getId();
		this.product_name = product.getProductCategory().getName();
		this.quantity = product.getQuantity();
	}
	
	
	public int getProduct_id() {
		return product_id;
	}
	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
