package tools.json_items;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tools.ProductCategory;

public class ProductCategoryItem {
	
	@JsonIgnore
	private ProductCategory productCategory;
	
	private int id;
	private String name;
	
	public ProductCategoryItem(@NotNull ProductCategory productCategory) {
		this.productCategory = productCategory;
		this.id = productCategory.getId();
		this.name = productCategory.getName();
	}

}
