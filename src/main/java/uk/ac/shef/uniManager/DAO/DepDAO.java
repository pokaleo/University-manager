package uk.ac.shef.uniManager.DAO;



import uk.ac.shef.uniManager.model.Department;
import uk.ac.shef.uniManager.utils.DbConn;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepDAO extends BaseDAO{
    public boolean addDep(Department department){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        String sql = "insert into departments values(?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, department.getDepId());
            preparedStatement.setString(2, department.getDepName());

            if(preparedStatement.executeUpdate() > 0)return true;
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public List<Department> getDepList(Department department) {
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        List<Department> retList = new ArrayList<Department>();
        StringBuffer sqlString = new StringBuffer("select * from departments");
        if(!StringUtil.isEmpty(department.getDepId())){
            sqlString.append(" where depId like '%"+department.getDepId()+"%'");
        }
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString.toString());
            ResultSet executeQuery = preparedStatement.executeQuery();
            while(executeQuery.next()){
                Department t = new Department();
                t.setDepId(executeQuery.getString("depId"));
                t.setDepName(executeQuery.getString("depName"));
                retList.add(t);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retList;

    }
    public boolean delete(String depId){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        String sql = "delete from departments where depId=?";
        String sql2 = "delete from DepDeg where depId=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            preparedStatement.setString(1, depId);
            ps2.setString(1,depId);
            ps2.executeUpdate();
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
