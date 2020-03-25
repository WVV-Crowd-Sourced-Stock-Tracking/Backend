package webService;

import java.util.ArrayList;
import java.util.List;

public class MarketStockRequest {
	private int market_id = 0;
	private String maps_id = "";
	private List<Integer> product_id = new ArrayList<Integer>();
	public int getMarket_id() {
		return market_id;
	}
	public void setMarket_id(int market_id) {
		this.market_id = market_id;
	}
	public List<Integer> getProduct_id() {
		return product_id;
	}
	public void setProduct_id(List<Integer> product_id) {
		this.product_id = product_id;
	}
	public String getMaps_id() {
		return maps_id;
	}
	public void setMaps_id(String maps_id) {
		this.maps_id = maps_id;
	}
}
