package webService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tools.GenericResponse;
import tools.ProductItem;
import tools.json_items.SupermarketItem;

@Path ("/rest")
public class Rest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static String DBRESOURCE = "jdbc/wvv";
	private static javax.naming.Context initCtx = null;
	private static javax.naming.Context envCtx  = null;
	private static DataSource dataSource = null;
	protected Connection con = null;

	
	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/product
	 *  JSON input 
	 	{ 
			"id_market": 1,
			"id_product": 1,
			"quantity": 100
		}
	 *  JSON output
	   	{
   			"result": "success",
		}
	 */
	@POST
	@Path ("/market/transmit")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response marketTransmit(@Context HttpServletRequest request, MarketTransmitRequest req) {
		Response response = null;
		Connection con = null;
		GenericResponse res = new GenericResponse();
		try {
			con = initWS();
//TODO put data into DB
			
			res.setResult("success");
		}
		catch ( Exception ex) {
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).build();
		}
		return response;		
	}

	
	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/product
	 *  JSON input 
	 	{
			"zip":"61231",
			"radius": 12000,
			"product_id":    [
      			1,
      			2
   			]
		}
	 *  JSON output
	   	{
		   "result": "success",
		   "supermarket": [   {
		      "id": 0,
		      "name": "REWA Center Bad Nauheim",
		      "city": "Bad Nauheim",
		      "street": "Georg-Scheller-StraÃŸe 2-8",
		      "gps_length": "50Â°21'43.0\"N",
		      "gps_width": "8Â°45'15.2\"E"
		   }]
		}
	 */
	@POST
	@Path ("/market/scrape")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response marketScrape(@Context HttpServletRequest request, MarketScrapeRequest req) {
		Response response = null;
		Connection con = null;
		MarketScrapeResponse res = new MarketScrapeResponse();
		try {
			con = initWS();
//TODO /get data from DB
			res.getSupermarket().add( new SupermarketItem( 0, "REWA Center Bad Nauheim", "Bad Nauheim", "Georg-Scheller-Straße 2-8","50°21'43.0\"N","8°45'15.2\"E"));
			
			res.setResult("success");
		}
		catch ( Exception ex) {
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).build();
		}
		return response;		
	}

	
	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/product
	 *  JSON input 
	 * 
	 *  JSON output
	   	{
   			"result": "success",
   			"product": [   {
      			"id": 1,
      			"product_name": "Milch",
      			"quantity": 50
   			}]
		}
	 */
	@POST
	@Path ("/product")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response product(@Context HttpServletRequest request, ProductRequest req) {
		Response response = null;
		Connection con = null;
		ProductResponse res = new ProductResponse();
		try {
			con = initWS();
//TODO get data from DB, now just one test record
			res.getProduct().add( new ProductItem( 1, "Milch", 50) );
			res.getProduct().add( new ProductItem( 1, "Brot", 100) );
			
			String sql = "select data from hello";
			PreparedStatement pstmt = con.prepareStatement( sql );
			ResultSet rs = pstmt.executeQuery();
			while( rs.next() ) {
				String data = rs.getString(1);
				System.out.println( data );
			}
			rs.close();
			pstmt.close();		
			res.setResult("success");
		}
		catch ( Exception ex) {
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).build();
		}
		return response;		
	}
	
	
	/**
 	 *	URL http://127.0.0.1:8080/Backend/ws/rest/hello
 	 *	JSON input	
			{"zahl":1}
	 *	JSON output
			{"text": "HelloWorld 1"}
	 */
	@POST
	@Path ("/hello")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response hello(@Context HttpServletRequest request, HelloRequest req) {
		Response response = null;
		Connection con = null;
		HelloResponse res = new HelloResponse();
		try {
			con = initWS();
			String sql = "select data from hello";
			PreparedStatement pstmt = con.prepareStatement( sql );
			ResultSet rs = pstmt.executeQuery();
			while( rs.next() ) {
				String data = rs.getString(1);
				System.out.println( data );
			}
			rs.close();
			pstmt.close();		
			
			res.setText( "HelloWorld " + req.getZahl());
		}
		catch ( Exception ex) {
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).build();
		}
		return response;		
	}
	
	private Connection initWS() throws NamingException, SQLException {
		Connection con = null;
		boolean ret = true;
		if ( dataSource == null ) {
			if ( envCtx == null ) {
				if ( initCtx == null ) {
					initCtx = new InitialContext();
				}
				envCtx = (javax.naming.Context) initCtx.lookup( "java:comp/env" );
			}
			dataSource = (DataSource) envCtx.lookup(DBRESOURCE );
			if ( dataSource == null ) {
				ret = false;
			}
		}
		
		if ( ret ) {
			con = getConnection();						//Get always a DB connection
		}
		return con;
	}
	
	private Connection getConnection() throws SQLException {
		Connection con = dataSource.getConnection();
		if ( con != null ) {
			con.setAutoCommit(false);
		}
		return con; 
	}
	
	private void finallyWs( Connection con ) {
		try {
			if ( con != null ) {
				con.close();
				con = null;
			}
		} 
		catch (SQLException e) {
		}
	}
	
}
