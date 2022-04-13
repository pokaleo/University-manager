package uk.ac.shef.uniManager.DAO;



import uk.ac.shef.uniManager.model.Department;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DepDAO extends BaseDAO{
    public boolean addDep(Department department){
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
        // TODO Auto-generated method stub
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
                /**
                 * here can add the username and password
                 */
                retList.add(t);
            }
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retList;

    }
    public boolean delete(String depId){
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
    public DefaultTableModel query(){
        DefaultTableModel model = new DefaultTableModel(new String[]{"Department Code", "Department Name"},
                0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("Select depId, depName from departments");
            while(rs.next()){
                String userName = (rs.getString("depId"));
                String userType = (rs.getString("depName"));
                model.addRow(new Object[]{userName, userType});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }
}
