package webService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

public class RestBasis extends HttpServlet{
	private static final long serialVersionUID = 1L;
//	private final static String DBRESOURCE = "jdbc/wvv";
//	private static Context initCtx = null;
//	private static Context envCtx = null;
//	private static DataSource dataSource = null;
	
	private static BasicDataSource dataSource = null;

//	protected Connection con = null;

	protected Connection initWS() throws Exception {
		Connection con = null;
		boolean ret = true;
		if (dataSource == null) {
			
			String d = "org.postgresql.Driver";
			String u = "jdbc:postgresql://ec2-46-137-84-173.eu-west-1.compute.amazonaws.com:5432/d1ssropt4bpql2";
			Properties dbcpProperties = new Properties();
			dbcpProperties.put("driverClassName", d);
			dbcpProperties.put("url", u);
			dbcpProperties.put("username", "jqjbfxsjohmmhe" );
			dbcpProperties.put("password", "bc7cf56183d22703c7915ffedd717fae431cd6847152ad572035fec60fff33d4" );
			dbcpProperties.put("defaultAutoCommit", "FALSE");
			dbcpProperties.put("maxActive", "1");
			dbcpProperties.put("ssl", "true");

			dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(dbcpProperties);
/*			
			if (envCtx == null) {
				if (initCtx == null) {
					initCtx = new InitialContext();
				}
				envCtx = (Context) initCtx.lookup("java:comp/env");
			}
			dataSource = (DataSource) envCtx.lookup(DBRESOURCE);
*/
			if (dataSource == null) {
				ret = false;
			}
		}

		if (ret) {
			con = getConnection(); // Get always a DB connection
		}
		return con;
	}

	protected Connection getConnection() throws SQLException {
		Connection con = dataSource.getConnection();
		if (con != null) {
			con.setAutoCommit(false);
		}
		return con;
	}
	
}
