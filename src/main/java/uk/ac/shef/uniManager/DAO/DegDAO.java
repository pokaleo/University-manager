package uk.ac.shef.uniManager.DAO;



import uk.ac.shef.uniManager.model.Degree;
import uk.ac.shef.uniManager.model.User;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DegDAO extends BaseDAO{
    public boolean addDeg(Degree degree){
        String sql = "insert into Degrees values(?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, degree.getDegId());
            preparedStatement.setString(2, degree.getDegName());
            preparedStatement.setString(3, degree.getLeadDep());
            if(preparedStatement.executeUpdate() > 0)return true;
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public List<User> getDepList(User user) {
        // TODO Auto-generated method stub
        List<User> retList = new ArrayList<User>();
        StringBuffer sqlString = new StringBuffer("select * from users");
        if(!StringUtil.isEmpty(user.getUsername())){
            sqlString.append(" where name like '%"+user.getUsername()+"%'");
        }
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString.toString());
            ResultSet executeQuery = preparedStatement.executeQuery();
            while(executeQuery.next()){
                User t = new User();
                t.setUsername(executeQuery.getString("username"));
                t.setSalt(executeQuery.getString("salt"));
                t.setType(executeQuery.getString("userType"));
                t.setPassword(executeQuery.getString("password"));
                /**
                 * here can add the username and password
                 */
                retList.add(t);
                conn.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retList;

    }
    public boolean delete(String degId){
        String sql = "delete from degrees where degId=?";
        String sql2 = "delete from DepDeg where degId=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            PreparedStatement ps = conn.prepareStatement(sql2);
            preparedStatement.setString(1, degId);
            ps.setString(1, degId);
            ps.executeUpdate();
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean update(String leadDep, String degId){
        String sql = "update degrees set leadDep=? where degId=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, leadDep);
            preparedStatement.setString(2, degId);
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean linkDepDeg(String depId, String degId){
        String sql = "update degrees set leadDep=? where degId=?;";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, depId);
            preparedStatement.setString(2, degId);
            if(preparedStatement.executeUpdate() > 0)return true;
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public DefaultTableModel query(){
        DefaultTableModel model = new DefaultTableModel(new String[]{"Degree Code", "Degree Name","Lead Department"},
                0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("Select degId, degName, leadDep from degrees");
            while(rs.next()){
                String userName = (rs.getString("degId"));
                String userType = (rs.getString("degName"));
                String leadDep = (rs.getString("leadDep"));
                model.addRow(new Object[]{userName, userType, leadDep});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }
}
