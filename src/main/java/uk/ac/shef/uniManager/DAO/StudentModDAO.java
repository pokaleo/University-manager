package uk.ac.shef.uniManager.DAO;


import uk.ac.shef.uniManager.model.Student;

import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentModDAO extends BaseDAO{

    public Student query(String username){
        Student studentRst = null;
        try {
            PreparedStatement st = conn.prepareStatement("Select * from students where username=?");
            st.setString(1, username);
            ResultSet executeQuery = st.executeQuery();
            if(executeQuery.next()){
                studentRst = new Student();
                studentRst.setName(QueryName.queryFullName(username));
                studentRst.setRegNumber(executeQuery.getInt("registrationNumber"));
                studentRst.setEmail(executeQuery.getString("email"));
                studentRst.setRegDeg(executeQuery.getString("registeredDegree"));
                studentRst.setTutor(executeQuery.getString("tutor"));
                studentRst.setPeriodOfStudy(executeQuery.getString("periodOfStudy"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentRst;
    }

    public DefaultTableModel tableModel(String username){
        DefaultTableModel model = new DefaultTableModel(new String[]{"Username", "Name", "Module Selected", "Credits"},
                0);
        int level = 0;
        String name = null;
        String leveltemp;
        int credits = 0;
        try {
            String sql2 = "Select periodOfStudy, forename, surname from Students where username=? ";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setString(1, username);
            ResultSet rs2 = ps2.executeQuery();
            if(rs2.next()){
                leveltemp = (rs2.getString("periodOfStudy"));
                leveltemp = leveltemp.substring(leveltemp.length() - 1);
                name = (rs2.getString("forename")+" "+(rs2.getString("surname")));
                level = Integer.parseInt(leveltemp);
            }
            if (level<4){
                credits=20;
            }else if(level == 4) {
                credits=15;
            }
            String sql = "Select module from StudentMod WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String module = (rs.getString("module"));
                if (module.equals("DISERT")){
                    if(level == 3){
                        credits=30;
                    }
                    if(level == 4){
                        credits=60;
                    }
                }
                model.addRow(new Object[]{username, name, module, credits});
                if (level<4){
                    credits=20;
                }else if(level == 4) {
                    credits=15;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public String queryPeriod(String username){
        String leveltemp;
        String periodOfStudy = null;
        try {
            String sql2 = "Select periodOfStudy, forename, surname from Students where username=? ";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setString(1, username);
            ResultSet rs2 = ps2.executeQuery();
            if(rs2.next()){
                leveltemp = (rs2.getString("periodOfStudy"));
                leveltemp = leveltemp.substring(leveltemp.length() - 1);
                periodOfStudy= (rs2.getString("periodOfStudy"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return periodOfStudy;
    }

    public boolean addModule(String username, String moduleId){
        String sql = "insert into studentMod values(?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, moduleId);

            if(preparedStatement.executeUpdate() > 0)return true;
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteModule(String username, String moduleId){
        String sql = "delete from studentMod where username=? and module=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, moduleId);
            if(ps.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
