package webService;

import java.util.ArrayList;
import java.util.List;

import tools.GenericResponse;
import tools.json_items.ProductItem;

public class ProductResponse extends GenericResponse {
	List<ProductItem> product = new ArrayList<ProductItem>();
}
