package tools;

public class MarketStockItem {
	private int product_id = 0;
	private String product_name = "";
	private int availability = 0;	
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
	public int getAvailability() {
		return availability;
	}
	public void setAvailability(int availability) {
		this.availability = availability;
	}
	public String getEmoji() {
		return emoji;
	}
	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}
	public MarketStockItem(int product_id, String product_name, int availability, String emoji) {
		super();
		this.product_id = product_id;
		this.product_name = product_name;
		this.availability = availability;
		this.emoji = emoji;
	}
	
}
