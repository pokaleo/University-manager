package uk.ac.shef.uniManager.DAO;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.uniManager.model.Department;
import uk.ac.shef.uniManager.model.Module;
import uk.ac.shef.uniManager.utils.DbConn;
import uk.ac.shef.uniManager.utils.StringUtil;

public class ModuleDAO extends BaseDAO {
    public boolean addMod(Module module){
        String sql = "insert into modules values(?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, module.getModuleId());
            preparedStatement.setString(2, module.getModuleName());
            preparedStatement.setString(3, module.getTaughtSem());
            if(preparedStatement.executeUpdate() > 0)return true;
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public DefaultTableModel query() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Module Code", "Module Name", "Taught Semester"},
                0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("Select moduleId, moduleName, taughtSem from modules");
            while (rs.next()) {
                String moduleId = (rs.getString("moduleId"));
                String moduleName = (rs.getString("moduleName"));
                String taughtSem = (rs.getString("taughtSem"));
                model.addRow(new Object[]{moduleId, moduleName, taughtSem});
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }
    public boolean delete(String moduleId){
        DbConn dbConn = new DbConn();
        Connection con = dbConn.getCon();
        String sql = "delete from modules where moduleId=?";
        String sql2 = "delete from DegMod where module=?";
        String sql3 = "delete from StudentMod where module=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            PreparedStatement ps = con.prepareStatement(sql2);
            PreparedStatement ps2 = con.prepareStatement(sql3);
            preparedStatement.setString(1, moduleId);
            ps.setString(1, moduleId);
            ps2.setString(1, moduleId);
            ps.executeUpdate();
            ps2.executeUpdate();
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean linkDegMod(String depId, String moduleId, String level, int type){
        String sql = "insert into DegMod values(?,?,?,?)";
        DbConn dbConn = new DbConn();
        Connection con = dbConn.getCon();
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, depId);
            preparedStatement.setString(2, moduleId);
            preparedStatement.setString(3, level);
            preparedStatement.setInt(4, type);
            if(preparedStatement.executeUpdate() > 0)return true;
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean oneYearMaster(String degId){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        String sql = "select degName from degrees where degId =?";
        String degName = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, degId);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                degName = (rs.getString("degName"));
                degName = degName.substring(0,1);
            }
            if(degName.equals("M")){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Module> getModuleList(Module module) {
        List<Module> retList = new ArrayList<Module>();
        StringBuffer sqlString = new StringBuffer("select * from modules");
        if(!StringUtil.isEmpty(module.getModuleId())){
            sqlString.append(" where moduleId like '%"+module.getModuleId()+"%'");
        }
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString.toString());
            ResultSet executeQuery = preparedStatement.executeQuery();
            while(executeQuery.next()){
                Module module1 = new Module();
                module1.setModuleId(executeQuery.getString("moduleId"));
                module1.setModuleName(executeQuery.getString("moduleName"));
                module1.setTaughtSem(executeQuery.getString("taughtSem"));
                /**
                 * here can add the username and password
                 */
                retList.add(module1);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retList;

    }

}
