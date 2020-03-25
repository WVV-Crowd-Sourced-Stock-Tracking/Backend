package tools;

public class ProductAvailabilityItem {
	private int product_id = 0;
	private String product_name = "";
	private int availability = 0;
	
	/**
	 * 
	 * @param product_id
	 * @param name
	 * @param availability
	 */
	public ProductAvailabilityItem(int product_id, String product_name, int availability) {
		super();
		this.product_id = product_id;
		this.product_name = product_name;
		this.availability = availability;
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
	public int getAvailability() {
		return availability;
	}

	public void setAvailability(int availability) {
		this.availability = availability;
	}
	
	
}
