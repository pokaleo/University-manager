package uk.ac.shef.uniManager.DAO;



import uk.ac.shef.uniManager.model.Degree;
import uk.ac.shef.uniManager.model.User;
import uk.ac.shef.uniManager.utils.DbConn;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DegDAO extends BaseDAO{
    public boolean addDeg(Degree degree){
        DbConn dbConn = new DbConn();
        Connection con = dbConn.getCon();
        String sql = "insert into Degrees values(?,?,?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, degree.getDegId());
            preparedStatement.setString(2, degree.getDegName());
            preparedStatement.setString(3, degree.getLeadDep());
            if(preparedStatement.executeUpdate() > 0)return true;
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public List<Degree> getDegList(Degree degree) {
        // TODO Auto-generated method stub
        List<Degree> retList = new ArrayList<Degree>();
        StringBuffer sqlString = new StringBuffer("select * from degrees");
        if(!StringUtil.isEmpty(degree.getDegId())){
            sqlString.append(" where degId like '%"+degree.getDegId()+"%'");
        }
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString.toString());
            ResultSet executeQuery = preparedStatement.executeQuery();
            while(executeQuery.next()){
                Degree degree1 = new Degree();
                degree1.setDegId(executeQuery.getString("degId"));
                degree1.setDegName(executeQuery.getString("degName"));
                degree1.setLeadDep(executeQuery.getString("leadDep"));
                /**
                 * here can add the username and password
                 */
                retList.add(degree1);
            }
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retList;

    }
    public boolean delete(String degId){
        DbConn dbConn = new DbConn();
        Connection con = dbConn.getCon();
        String sql = "delete from degrees where degId=?";
        String sql2 = "delete from DepDeg where degId=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            PreparedStatement ps = con.prepareStatement(sql2);
            preparedStatement.setString(1, degId);
            ps.setString(1, degId);
            ps.executeUpdate();
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(String leadDep, String degId){
        DbConn dbConn = new DbConn();
        Connection con = dbConn.getCon();
        String sql = "update degrees set leadDep=? where degId=?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, leadDep);
            preparedStatement.setString(2, degId);
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean linkDepDeg(String depId, String degId){
        DbConn dbConn = new DbConn();
        Connection con = dbConn.getCon();
        String sql = "insert into DepDeg values(?,?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, degId);
            preparedStatement.setString(2, depId);
            if(preparedStatement.executeUpdate() > 0)return true;
            con.close();
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
