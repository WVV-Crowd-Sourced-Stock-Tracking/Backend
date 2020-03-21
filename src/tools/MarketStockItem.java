package tools;

public class MarketStockItem {
	private int product_id = 0;
	private String product_name = "";
	private int quantity = 0;	
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
	public MarketStockItem(int product_id, String product_name, int quantity) {
		super();
		this.product_id = product_id;
		this.product_name = product_name;
		this.quantity = quantity;
	}
	
}
