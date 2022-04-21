package uk.ac.shef.uniManager.DAO;



import uk.ac.shef.uniManager.utils.DbConn;

import java.sql.Connection;
import java.sql.SQLException;

public class BaseDAO {
    public Connection conn = new DbConn().getCon();
    public void closeDao(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
