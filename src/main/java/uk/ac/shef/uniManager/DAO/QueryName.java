package uk.ac.shef.uniManager.DAO;



import uk.ac.shef.uniManager.utils.DbConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class QueryName {
    public static String query(String username) throws SQLException {
        String name = null;
        //mySQL query
        try{
            Connection conn = new DbConn().getCon();
            PreparedStatement st = (PreparedStatement) conn
                    .prepareStatement("Select username, surname, title from students where username=?");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                name = rs.getString("title")+". "+rs.getString("surname");

            }
            conn.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
    public static String queryFullName(String username) throws SQLException {
        String fullName = null;
        //mySQL query
        try{
            Connection conn = new DbConn().getCon();
            PreparedStatement st = (PreparedStatement) conn
                    .prepareStatement("Select username, surname, title, forename from students where username=?");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                fullName = rs.getString("title")+". "+rs.getString("forename")+" "+rs.getString("surname");;

            }
            conn.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return fullName;
    }
}
