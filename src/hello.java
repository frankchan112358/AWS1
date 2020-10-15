
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/hello")
public class hello extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();

		out.println("<form action=\"hello\" method=\"post\">");
		out.println("<input type=\"text\" name=\"upname\">");
		out.println("<input type=\"submit\" value=\"上傳\">");
		out.println("</form>");

		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT NAME FROM AWSEX01");
			while (rs.next()) {
				out.printf("<p>%s</p>%n", rs.getString("NAME"));
			}
		} catch (SQLException e) {
			throw new UnavailableException("Line 40. SQLException. Couldn't select all.");
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		try {
			PreparedStatement pstmt = null;
			pstmt = con.prepareStatement("INSERT INTO AWSEX01(NAME) VALUES(?)");

			String upname = req.getParameter("upname");
			pstmt.clearParameters();
			pstmt.setString(1, upname);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new UnavailableException("Line 55. SQLException. Couldn't insert.");
		}
		doGet(req, res);
	}

	public void init() throws ServletException {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@database-1.cuma19povfet.ap-northeast-1.rds.amazonaws.com:1521:orcl", "admin", "12345678");
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Line 65. ClassNotFoundException. Couldn't load JdbcOdbcDriver");
		} catch (SQLException e) {
			throw new UnavailableException("Line 67. SQLException. Couldn't get db connection");
		}
	}

	public void destroy() {
		try {
			if (con != null)
				con.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}
