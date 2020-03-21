package webService;

import tools.GenericResponse;

public class ProductEanScrapeResponse extends GenericResponse {
	private int product_id = 0;
	private String name = "";
	public int getProduct_id() {
		return product_id;
	}
	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
