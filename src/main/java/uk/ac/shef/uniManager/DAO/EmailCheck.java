package uk.ac.shef.uniManager.DAO;



import uk.ac.shef.uniManager.utils.DbConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmailCheck extends BaseDAO {
    public static boolean checkExist(String emailAddr) {
        boolean exitst = false;
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        try {
            String sql = "select CASE WHEN count(1) > 0 THEN TRUE ELSE FALSE END from " +
                    "students where email = '" + emailAddr + "'";
            PreparedStatement st = (PreparedStatement) conn
                    .prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                exitst = rs.getBoolean(1);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exitst;
    }
}
