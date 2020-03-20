package webService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path ("/Backend")
public class Backend {

	@POST
	@Path ("/product")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public ProductResponse product(@Context HttpServletRequest request, ProductRequest req) {
		ProductResponse response = null;
		
		return response;		
	}
}
