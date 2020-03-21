package webService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path ("/rest")
public class Rest {

	@POST
	@Path ("/product")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public ProductResponse product(@Context HttpServletRequest request, ProductRequest req) {
		ProductResponse response = null;
		
		return response;		
	}
	
	
/**
 	URL http://127.0.0.1:8080/Backend/ws/rest/hello
 	JSON input	
		{"zahl":1}
	JSON output
		{"text": "HelloWorld 1"}
 */
	
	@POST
	@Path ("/hello")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response hello(@Context HttpServletRequest request, HelloRequest req) {
		Response response = null;
		HelloResponse res = new HelloResponse();
		res.setText( "HelloWorld " + req.getZahl());
		response = Response.status(200).entity(res).build();
		return response;		
	}
	
	
}
