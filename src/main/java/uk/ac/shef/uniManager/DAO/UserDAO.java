package uk.ac.shef.uniManager.DAO;


import uk.ac.shef.uniManager.model.User;
import uk.ac.shef.uniManager.utils.DbConn;
import uk.ac.shef.uniManager.utils.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends BaseDAO{
    public boolean addUser(User user){
        DbConn dbConn = new DbConn();
        Connection con = dbConn.getCon();
        String sql = "insert into users values(null,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getType());
            preparedStatement.setString(4, user.getSalt());

            if(preparedStatement.executeUpdate() > 0)return true;
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getUserList(User user) {
        DbConn dbConn = new DbConn();
        Connection con = dbConn.getCon();
        List<User> retList = new ArrayList<User>();
        StringBuffer sqlString = new StringBuffer("select * from users");
        if(!StringUtil.isEmpty(user.getUsername())){
            sqlString.append(" where username like '%"+user.getUsername()+"%'");
        }
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sqlString.toString());
            ResultSet executeQuery = preparedStatement.executeQuery();
            while(executeQuery.next()){
                User t = new User();
                t.setUserID(executeQuery.getInt("userID"));
                t.setUsername(executeQuery.getString("username"));                
                t.setType(executeQuery.getString("userType"));
                /**
                 * here can add the username and password
                 */
                retList.add(t);
            }
            con.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retList;

    }
    public boolean delete(int UserID){
        DbConn dbConn = new DbConn();
        Connection con = dbConn.getCon();
        String sql = "delete from users where userID=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, UserID);
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean update(User user){
        DbConn dbConn = new DbConn();
        Connection con = dbConn.getCon();
        String sql = "update users set userType=? ,username=? where userID=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, user.getType());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setInt(3, user.getUserID());
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
