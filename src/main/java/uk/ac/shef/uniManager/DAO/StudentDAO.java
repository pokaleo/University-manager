package uk.ac.shef.uniManager.DAO;


import uk.ac.shef.uniManager.model.Student;
import uk.ac.shef.uniManager.utils.DbConn;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO extends BaseDAO{


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
    public DefaultTableModel tableModel(){
        DefaultTableModel model = new DefaultTableModel(new String[]{"Username", "name", "Degree","Registration No.",
                "Period of Study"},
                0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("Select username, registeredDegree, registrationNumber, periodOfStudy, " +
                    " forename, surname from students");
            while(rs.next()){
                String userName = (rs.getString("username"));
                String degree = (rs.getString("registeredDegree"));
                String name = (rs.getString("forename"))+ " " + (rs.getString("surname"));
                String registrationNumber = (String)(rs.getString("registrationNumber"));
                String periodOfStudy = (rs.getString("periodOfStudy"));
                model.addRow(new Object[]{userName, name, degree, registrationNumber,periodOfStudy});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel fulltableModel(String username){
        DefaultTableModel model = new DefaultTableModel(new String[]{"Username", "Title", "Surname","Forename",
                "RegisteredDegree","RegistrationNumber","Email","Tutor","PeriodOfStudy"},
                0);
        try {
            String sql ="Select * from students where username=?";
            PreparedStatement ps2 = conn.prepareStatement(sql);
            ps2.setString(1, username);
            ResultSet rs = ps2.executeQuery();
            if(rs.next()){
                String userName = (rs.getString("username"));
                String title = (rs.getString("title"));
                String surname = (rs.getString("surname"));
                String forename = (rs.getString("forename"));
                String registeredDegree = (rs.getString("registeredDegree"));
                String registrationNumber = (String)(rs.getString("registrationNumber"));
                String email = (rs.getString("email"));
                String tutor = (rs.getString("tutor"));
                String periodOfStudy = (rs.getString("periodOfStudy"));
                model.addRow(new Object[]{userName,title,surname,forename,registeredDegree,registrationNumber,
                email,tutor,periodOfStudy});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel regTableModel(String username){
        DefaultTableModel model = new DefaultTableModel(new String[]{"Username", "name", "Degree","Registration No.",
                "Period of Study"},
                0);
        try {
            String sql ="Select username, registeredDegree, registrationNumber, periodOfStudy, " +
                    " forename, surname from students where username=?";
            PreparedStatement ps2 = conn.prepareStatement(sql);
            ps2.setString(1, username);
            ResultSet rs = ps2.executeQuery();
            if(rs.next()){
                String userName = (rs.getString("username"));
                String degree = (rs.getString("registeredDegree"));
                String name = (rs.getString("forename"))+ " " + (rs.getString("surname"));
                String registrationNumber = (String)(rs.getString("registrationNumber"));
                String periodOfStudy = (rs.getString("periodOfStudy"));
                model.addRow(new Object[]{userName, name, degree, registrationNumber,periodOfStudy});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public boolean checkReg (String username){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        try {
            String sql ="Select * from students where username=?";
            PreparedStatement ps2 = conn.prepareStatement(sql);
            ps2.setString(1, username);
            ResultSet rs = ps2.executeQuery();
            if(rs.next()){
                if (rs.getString("username") == null){
                    return false;
                }
                if (rs.getString("title") == null){
                    return false;
                }
                if (rs.getString("surname") == null){
                    return false;
                }
                if (rs.getString("forename") == null){
                    return false;
                }
                if (rs.getString("registeredDegree") == null){
                    return false;
                }
                if (rs.getString("registrationNumber") == null){
                    return false;
                }
                if (rs.getString("email") == null){
                    return false;
                }
                if (rs.getString("tutor") == null){
                    return false;
                }
                if (rs.getString("periodOfStudy") == null){
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean addStudent(Student student){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        String sql = "insert into students values(?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, student.getUsername());
            preparedStatement.setString(2, student.getTitle());
            preparedStatement.setString(3, student.getSurname());
            preparedStatement.setString(4, student.getForename());
            preparedStatement.setString(5, student.getRegDeg());
            preparedStatement.setInt(6, student.getRegNumber());
            preparedStatement.setString(7, student.getEmail());
            preparedStatement.setString(8, student.getTutor());
            preparedStatement.setString(9, student.getPeriodOfStudy());



            if(preparedStatement.executeUpdate() > 0)return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String username){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        String sql = "delete from students where username=?";
        String sql2 = "delete from users where username=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            PreparedStatement ps = conn.prepareStatement(sql2);
            preparedStatement.setString(1, username);
            ps.setString(1, username);
            ps.executeUpdate();
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }


    public boolean addCoreModule(int level, String username, String degId){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        List<String> mods = new ArrayList<>();
        boolean b = false;
        try {
            String sql = "select module from DegMod where Type =? and levelOfStudy =? and degId=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, level);
            preparedStatement.setString(3, degId);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                mods.add(rs.getString("module"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String modId;
        for(int i=0; i<mods.size(); i++ ){
            String sql= "insert into studentMod values(?,?)";
            modId = mods.get(i);
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, modId);
                if(preparedStatement.executeUpdate() > 0){
                    b=true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    public List<Student> getStudentList(Student student){
        List<Student> retList = new ArrayList<Student>();
        StringBuffer sqlString = new StringBuffer("select * from students");
        if(!StringUtil.isEmpty(student.getName())){
            sqlString.append(" and name like '%"+student.getName()+"%'");
        }

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString.toString().replaceFirst("and", "where"));
            ResultSet executeQuery = preparedStatement.executeQuery();
            while(executeQuery.next()){
                Student s = new Student();

                //s.setName(executeQuery.getString("name"));
                s.setSurName(executeQuery.getString("surname"));
                s.setForeName(executeQuery.getString("forename"));
                s.setUsername(executeQuery.getString("username"));
                s.setTitle(executeQuery.getString("title"));
                s.setTutor(executeQuery.getString("tutor"));
                s.setRegDeg(executeQuery.getString("registeredDegree"));
                s.setRegNumber(executeQuery.getInt("registrationNumber"));
                s.setPeriodOfStudy(executeQuery.getString("periodOfStudy"));
                retList.add(s);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retList;
    }

    public String period(String username){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        String  period = null;
        try {
            PreparedStatement st = conn.prepareStatement(
                    "select periodOfStudy from students where  username = ?"
            );
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                period = (rs.getString("periodOfStudy"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String p = period.substring(8,9);
        return p;
    }

    public boolean isGradu(String username){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        String  period = null;
        try {
            PreparedStatement st = conn.prepareStatement(
                    "select periodOfStudy from students where  username = ?"
            );
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                period = (rs.getString("periodOfStudy"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (period.substring(0,1).equals("C")){
            return true;
        }else{
            return false;
        }
    }

}
