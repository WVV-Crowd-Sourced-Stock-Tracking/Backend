package webService;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

public class RestBasis extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private final static String DBRESOURCE = "jdbc/wvv";
	private static Context initCtx = null;
	private static Context envCtx = null;
	private static DataSource dataSource = null;
	protected Connection con = null;

	protected Connection initWS() throws NamingException, SQLException {
		Connection con = null;
		boolean ret = true;
		if (dataSource == null) {
			if (envCtx == null) {
				if (initCtx == null) {
					initCtx = new InitialContext();
				}
				envCtx = (Context) initCtx.lookup("java:comp/env");
			}
			dataSource = (DataSource) envCtx.lookup(DBRESOURCE);
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
