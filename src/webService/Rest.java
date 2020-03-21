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
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/market/transmit
	 *  JSON input 
	 	{ 
			"id_market": 1,
			"id_product": 1,
			"quantity": 100
		}
	 *  JSON output
	   	{
   			"result": "success"
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
		int ret = 0;
		try {
			con = initWS();
		
			PreparedStatement pstmt = null;
			pstmt = con.prepareStatement("update stock set quantity=? where store_id=? and product_id=?");
			pstmt.setInt(1, req.getQuantity());
			pstmt.setInt(2, req.getMarket_id());
			pstmt.setInt(3, req.getProduct_id());
			ret = pstmt.executeUpdate();
			pstmt.close();

			if ( ret == 0 ) {
				pstmt = con.prepareStatement("insert into stock(store_id,product_id,quantity) values(?,?,?)");
				pstmt.setInt(1, req.getMarket_id());
				pstmt.setInt(2, req.getProduct_id());
				pstmt.setInt(3, req.getQuantity());
				ret = pstmt.executeUpdate();
				pstmt.close();
			}
			con.commit();
			res.setResult("success");
		}
		catch ( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;		
	}

	@HEAD
	@Path("/market/transmit")
	public Response marketTransmitHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origi", "*")
	  				.build();
	      return response;
	}
	
	
	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/market/scrape
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
		      "street": "Georg-Scheller-Straße 2-8",
		      "gps_length": "8.754167",
		      "gps_width": "50.361944"
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
//			con = initWS();
//TODO /get data from DB
			res.getSupermarket().add( new SupermarketItem( 0, "REWA Center Bad Nauheim", "Bad Nauheim", "Georg-Scheller-Strasse 2-8","8.754167","50.361944"));
			
			res.setResult("success");
		}
		catch ( Exception ex) {
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
//			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;		
	}
	
	@HEAD
	@Path("/market/scrape")
	public Response marketScrapeHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origi", "*")
	  				.build();
	      return response;
	}


	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/market/stock
	 *  JSON input 
	 	{
			"market_id": 1,
			"product_id":    [
      			1,
      			2
   			]
		}
	 *  JSON output
		{
		   "result": "success",
		   "product": [   {
		      "id": 1,
		      "product_name": "test",
		      "quantity": 50
		   }]
		}
	 */
	@POST
	@Path ("/market/stock")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response marketStock(@Context HttpServletRequest request, MarketStockRequest req) {
		Response response = null;
		Connection con = null;
		MarketStockResponse res = new MarketStockResponse();
		try {
			con = initWS();
//TODO /get data from DB
			res.getProduct().add( new ProductItem( 1, "test", 50) );
			
			res.setResult("success");
		}
		catch ( Exception ex) {
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;		
	}

	@HEAD
	@Path("/market/stock")
	public Response marketStockHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origi", "*")
	  				.build();
	      return response;
	}

	
	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/market/manage
	 *  JSON input 
		{
			"operation":"create",
			"market_id": 1,
			"name":"REWE",
			"city":"Bad Nauheim",
			"zip":"61231",
			"street":"Georg-Scheller-Strasse 2-8",
			"gps_length":"8.754167",
			"gps_width":"50.361944"
		}	 
	 *  JSON output
		{
		   "result": "success"
		}
	 */
	@POST
	@Path ("/market/manage")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response marketManage(@Context HttpServletRequest request, MarketManageRequest req) {
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
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;		
	}

	@HEAD
	@Path("/market/manage")
	public Response marketManageHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origi", "*")
	  				.build();
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
	@Path ("/product/scrape")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response productScrape(@Context HttpServletRequest request, ProductRequest req) {
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
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;		
	}

	@HEAD
	@Path("/product/scrape")
	public Response productScrapeHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origi", "*")
	  				.build();
	      return response;
	}
	
	
	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/product/manage
	 *  JSON input 
		{
			"operation":"create",
			"product_id": 1,
			"name":"Milch",
			"language":"DE"
		}	 
	 *  JSON output
		{
		   "result": "success"
		}
	 */
	@POST
	@Path ("/product/manage")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response productManage(@Context HttpServletRequest request, ProductManageRequest req) {
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
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;		
	}

	@HEAD
	@Path("/product/manage")
	public Response productManageHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origi", "*")
	  				.build();
	      return response;
	}
	
	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/product_ean/manage
	 *  JSON input 
		{
			"operation":"create",
			"ean": "0401234567890",
			"product_id": 1
		}	 
	 *  JSON output
		{
		   "result": "success"
		}
	 */
	@POST
	@Path ("/product_ean/manage")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response productEanManage(@Context HttpServletRequest request, ProductEanManageRequest req) {
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
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;		
	}

	@HEAD
	@Path("/product_ean/manage")
	public Response productEanManageHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origi", "*")
	  				.build();
	      return response;
	}
	
	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/product_ean/scrape
	 *  JSON input 
		{
			"ean":"0401234567890",
			"language":"DE"
		}
	 *  JSON output
		{
		   "result": "success",
		   "product_id": 1,
		   "name": "Milch"
		}
	 */
	@POST
	@Path ("/product_ean/scrape")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response productEanScrape(@Context HttpServletRequest request, ProductEanScrapeRequest req) {
		Response response = null;
		Connection con = null;
		ProductEanScrapeResponse res = new ProductEanScrapeResponse();
		try {
			con = initWS();
//TODO put data into DB
			
			String sql = "select e.product_id,p.name from ean_is_kind_of e, product p " +
						 "where e.ean=? and e.product_id=p.product_id";
			PreparedStatement pstmt = con.prepareStatement( sql );
			pstmt.setLong(1, Long.valueOf(req.getEan()));
			ResultSet rs = pstmt.executeQuery();
			while( rs.next() ) {
				String data = rs.getString(1);
				System.out.println( data );
			}
			rs.close();
			pstmt.close();		

			res.setProduct_id( 1 );
			res.setName( "Milch" );
			res.setResult("success");
		}
		catch ( Exception ex) {
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;		
	}

	@HEAD
	@Path("product_ean/scrape")
	public Response productEanScrapeHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origi", "*")
	  				.build();
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
			String sql = "select location_id from location";
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
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;		
	}
	
	
	@HEAD
	@Path("/hello")
	public Response helloHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	                           .header("someHeader", "someHeaderValue")
	                           .build();
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
