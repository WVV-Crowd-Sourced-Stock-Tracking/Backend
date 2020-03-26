package tools;

public class ProductItem {
	private int product_id = 0;
	private String product_name = "";
	private String emoji = "";
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
	
	public String getEmoji() {
		return emoji;
	}
	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}
	public ProductItem(int product_id, String product_name, String emoji) {
		super();
		this.product_id = product_id;
		this.product_name = product_name;
		this.emoji = emoji;
	}
	
}
