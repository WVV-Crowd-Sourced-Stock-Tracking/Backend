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

@Path ("/rest")
public class Rest extends HttpServlet {
	private final static String DBRESOURCE = "jdbc/wvv";
	private static javax.naming.Context initCtx = null;
	private static javax.naming.Context envCtx  = null;
	private static DataSource dataSource = null;
	protected Connection con = null;

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
