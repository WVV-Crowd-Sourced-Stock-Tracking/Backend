package tools;

public class Product {
	private ProductCategory productCategory;
	private int quantity;
	
	public Product(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}
	
	public ProductCategory getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
