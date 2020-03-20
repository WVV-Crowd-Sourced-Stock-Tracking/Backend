package webService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path ("/Backend")
public class Backend {

	public ProductResponse product(@Context HttpServletRequest request, ProductRequest req) {
		ProductResponse response = null;
		
		return response;		
	}
}
