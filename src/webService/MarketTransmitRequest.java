package webService;

import java.util.ArrayList;
import java.util.List;

import tools.MarketTransmitItem;

public class MarketTransmitRequest {
	private int market_id = 0;
	private int product_id = 0;
	private int quantity = 0;
	private List<MarketTransmitItem> bulk = new ArrayList<MarketTransmitItem>();
	public int getMarket_id() {
		return market_id;
	}
	public void setMarket_id(int market_id) {
		this.market_id = market_id;
	}
	public int getProduct_id() {
		return product_id;
	}
	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public List<MarketTransmitItem> getBulk() {
		return bulk;
	}
	public void setBulk(List<MarketTransmitItem> bulk) {
		this.bulk = bulk;
	}
}
