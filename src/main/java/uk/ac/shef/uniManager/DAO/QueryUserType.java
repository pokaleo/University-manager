package uk.ac.shef.uniManager.DAO;



import uk.ac.shef.uniManager.utils.DbConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class QueryUserType {
    public static String query(String username) throws SQLException {
        String userType = null;
        //mySQL query
        try{
            Connection conn = new DbConn().getCon();
            PreparedStatement st = (PreparedStatement) conn
                    .prepareStatement("Select username, userType from users where username=?");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                userType = rs.getString("userType");

            }
            conn.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return userType;
    }
}
