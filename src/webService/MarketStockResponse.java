package webService;

import java.util.ArrayList;
import java.util.List;

import tools.GenericResponse;
import tools.ProductItem;

public class MarketStockResponse extends GenericResponse {
	private List<ProductItem> product = new ArrayList<ProductItem>();

	public List<ProductItem> getProduct() {
		return product;
	}

	public void setProduct(List<ProductItem> product) {
		this.product = product;
	}
}
