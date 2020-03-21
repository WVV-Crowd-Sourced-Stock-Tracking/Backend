package webService;

import java.util.ArrayList;
import java.util.List;

import tools.GenericResponse;
import tools.MarketStockItem;
import tools.ProductItem;

public class MarketStockResponse extends GenericResponse {
	private List<MarketStockItem> product = new ArrayList<MarketStockItem>();

	public List<MarketStockItem> getProduct() {
		return product;
	}

	public void setProduct(List<MarketStockItem> product) {
		this.product = product;
	}
}
