package uk.ac.shef.uniManager.DAO;



import uk.ac.shef.uniManager.model.Grades;
import uk.ac.shef.uniManager.utils.DbConn;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GradesDAO extends BaseDAO{

    public JComboBox queryModule(String username){
        JComboBox comboBox= new JComboBox();
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        try {
            String sql = "SELECT module FROM grades WHERE username=?";
            String module = null;
            PreparedStatement ps2 = conn.prepareStatement(sql);
            ps2.setString(1, username);
            ResultSet rs = ps2.executeQuery();
            while(rs.next()){

                if ((rs.getString("module"))!=module){
                    module = rs.getString("levelOfStudy");
                    comboBox.addItem(String.valueOf(module));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comboBox;
    }

    public boolean addGrade(Grades grades) {
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        String sql = "insert into grades values(?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, grades.getUsername());
            preparedStatement.setString(2, grades.getModId());
            preparedStatement.setString(3, grades.getGrades1Str());
            preparedStatement.setString(4, grades.getGrades2Str());
            preparedStatement.setString(5, grades.getLevelOfStudy());
            if(preparedStatement.executeUpdate() > 0)return true;
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update1(Grades grades, String username, String module ){
        String sql = "update grades set grades1 = ? , grades2= ? where username=? and module=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, grades.getGrades1());
            preparedStatement.setInt(2, grades.getGrades2());
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, module);

            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    public boolean delete1(String username, String module){
        String sql = "delete from grades where username=? and module=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, module);
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public DefaultTableModel query1(String username, String module){
        DefaultTableModel model = new DefaultTableModel(new String[]{"Module", "1st Attempt", "Resit", "levelOfStudy"},
                0);

        try {
            PreparedStatement st = conn.prepareStatement("Select module, grades1, grades2, levelOfStudy from " +
                    "grades where username=? and module=?");
            st.setString(1, username);
            st.setString(2, module);
            ResultSet rs = st.executeQuery();
            while(rs.next()){


                String grades1 = (String.valueOf(rs.getInt("grades1")));
                String grades2 = (String.valueOf(rs.getInt("grades2")));
                String levelOfStudy = (rs.getString("levelOfStudy"));
                model.addRow(new Object[]{ module,grades1, grades2,levelOfStudy});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel query(String username){
        DefaultTableModel model = new DefaultTableModel(new String[]{"Module", "1st Attempt", "Resit", "levelOfStudy"},
                0);

        try {
            PreparedStatement st = conn.prepareStatement("Select module, grades1, grades2, levelOfStudy from " +
                    "grades where username=?");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Grades grade = new Grades();
                String modId = (rs.getString("module"));
                String grades1 = (String.valueOf(rs.getInt("grades1")));
                String grades2 = (String.valueOf(rs.getInt("grades2")));
                String levelOfStudy = (rs.getString("levelOfStudy"));
                model.addRow(new Object[]{modId, grades1, grades2,levelOfStudy});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public JComboBox queryPeriods(String username){
        JComboBox comboBox= new JComboBox();
        try {
            String sql = "SELECT levelOfstudy FROM grades WHERE username=?";
            int levelOfStudy = 0;
            PreparedStatement ps2 = conn.prepareStatement(sql);
            ps2.setString(1, username);
            ResultSet rs = ps2.executeQuery();
            while(rs.next()){
                /*
                System.out.println(levelOfStudy);
                System.out.println(rs.getInt("levelOfStudy"));
                Boolean b = rs.getInt("levelOfStudy")==levelOfStudy;
                System.out.println(b);
                */
                if ((rs.getInt("levelOfStudy"))!=levelOfStudy){
                    levelOfStudy = rs.getInt("levelOfStudy");
                    comboBox.addItem(String.valueOf(levelOfStudy));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comboBox;
    }

    public DefaultTableModel meanGrade(String username,int level){
        DefaultTableModel model = new DefaultTableModel(new String[]{"ModuleId", "Best Attempt"},
                0);
        try {
            int higerGrades = 0;
            PreparedStatement st = conn.prepareStatement(
                    "select grades1,grades2,module from grades where  username = ? and " +
                            "levelOfStudy=?"
            );
            st.setString(1,username);
            st.setInt(2,level);
            ResultSet executeQuery = st.executeQuery();
            while(executeQuery.next()){
                if(executeQuery.getInt("grades1")>executeQuery.getInt("grades2")){
                    higerGrades=executeQuery.getInt("grades1");
                }else{
                    higerGrades=executeQuery.getInt("grades2");
                }
                String moduleId = executeQuery.getString("module");
                model.addRow(new Object[]{moduleId, higerGrades});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public double meanGrades(String username, int level){
        double meanGrades=0;
        try {
            double higerGrades = 0;
            PreparedStatement st = conn.prepareStatement(
                    "select grades1,grades2,module from grades where  username = ? and " +
                            "levelOfStudy=?"
            );
            st.setString(1,username);
            st.setInt(2,level);
            ResultSet executeQuery = st.executeQuery();
            while(executeQuery.next()){
                if(executeQuery.getInt("grades1")>executeQuery.getInt("grades2")){
                    higerGrades=executeQuery.getInt("grades1");
                }else{
                    higerGrades=executeQuery.getInt("grades2");
                }
                String moduleId = executeQuery.getString("module");
                if (level<3){
                    meanGrades=meanGrades+higerGrades*20/120;
                }
                if(level==3){
                    if(executeQuery.getString("module").equals("DISERT")) {
                        meanGrades = meanGrades + higerGrades * 40 / 120;
                    }else{
                        meanGrades=meanGrades+higerGrades * 20/120;
                    }
                }
                if(level==4){
                    if(executeQuery.getString("module").equals("DISERT")) {
                        meanGrades = meanGrades + higerGrades * 60 / 180;
                    }else{
                        meanGrades=meanGrades+higerGrades*15/180;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  meanGrades;
    }

    public boolean delete(String username, String module, String levelOfStudy){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        String sql = "delete from grades where username=? and module =? and levelOfStudy=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, module);
            preparedStatement.setString(3, levelOfStudy);
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Grades grades){
        DbConn dbConn = new DbConn();
        Connection conn = dbConn.getCon();
        String sql = "update grades set grades1 = ? , grades2= ? where username=? and module=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, grades.getGrades1());
            preparedStatement.setInt(2, grades.getGrades2());
            preparedStatement.setString(3, grades.getUsername());
            preparedStatement.setString(4, grades.getModId());
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Grades> getGradesList(Grades grades){
        List<Grades> retList = new ArrayList<Grades>();
        StringBuffer sqlString = new StringBuffer("select * from grades");
        if(!StringUtil.isEmpty(grades.getUsername())){
            sqlString.append(" where username like '%"+ grades.getUsername() +"%'");
        }
        if(!StringUtil.isEmpty(grades.getModId())){
            sqlString.append(" where username like '%"+ grades.getModId() +"%'");
        }

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString.toString().replaceFirst("and", "where"));
            ResultSet executeQuery = preparedStatement.executeQuery();
            while(executeQuery.next()){
                Grades s = new Grades();
                s.setModId(executeQuery.getString("module"));
                s.setUsername(executeQuery.getString("username"));
                s.setGrades1(executeQuery.getInt("grades1"));
                s.setGrades2(executeQuery.getInt("grades2"));
                s.setLevelOfStudy(executeQuery.getString("levelOfStudy"));
                retList.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retList;
    }

    public boolean repeatYear(String username, int level){
        String sql = "update grades set grades1=0, grades2=0 where username=? and levelOfStudy=? and " +
                "grades1<40 and grades2<40";
        String periodTemp = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, level);
            if(preparedStatement.executeUpdate() > 0){
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            String sql3 = "select periodOfStudy from students where username=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql3);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                periodTemp = (rs.getString("periodOfStudy"));
                System.out.println(periodTemp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql2 = "update students set periodOfStudy=? where username=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql2);
            System.out.println(periodTemp);
            String period = "B" +periodTemp.substring(1,9);
            System.out.println(period);
            preparedStatement.setString(1,period);
            preparedStatement.setString(2, username);
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean progress(String username, int level){
        String periodTemp = null;
        try {
            String sql3 = "select periodOfStudy from students where username=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql3);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                periodTemp = (rs.getString("periodOfStudy"));
                System.out.println(periodTemp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql2 = "update students set periodOfStudy=? where username=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql2);
            System.out.println(periodTemp);
            Calendar date = Calendar.getInstance();
            String yearString = String.valueOf(date.get(Calendar.YEAR));
            yearString = yearString.substring(0,2);
            int year = Integer.parseInt(yearString);
            String period = "A-"+year+"-"+String.valueOf((year++))+"-"+
                    (Integer.parseInt(periodTemp.substring(8, 9))+1);
            System.out.println(period);
            preparedStatement.setString(1,period);
            preparedStatement.setString(2, username);
            if(preparedStatement.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public int numberOfFails(String username,int level){
        int numberOfFails = 0;
        try {
            double higerGrades = 0;
            PreparedStatement st = conn.prepareStatement(
                    "select grades1,grades2,module from grades where  username = ? and levelOfStudy=?"
            );
            st.setString(1,username);
            st.setInt(2,level);
            ResultSet executeQuery = st.executeQuery();
            while(executeQuery.next()){
                if(executeQuery.getInt("grades1")>executeQuery.getInt("grades2")){
                    higerGrades=executeQuery.getInt("grades1");
                }else{
                    higerGrades=executeQuery.getInt("grades2");
                }
                if (level<4 && higerGrades<40){
                    numberOfFails++;
                }
                if(level==4 && higerGrades<50){
                    numberOfFails++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberOfFails;
    }

    public boolean concededBoundaries(String username,int level){
        boolean boundaries = true;
        try {
            double higerGrades = 0;
            PreparedStatement st = conn.prepareStatement(
                    "select grades1,grades2,module from grades where  username = ? and levelOfStudy=?"
            );
            st.setString(1,username);
            st.setInt(2,level);
            ResultSet executeQuery = st.executeQuery();
            while(executeQuery.next()){
                if(executeQuery.getInt("grades1")>executeQuery.getInt("grades2")){
                    higerGrades=executeQuery.getInt("grades1");
                }else{
                    higerGrades=executeQuery.getInt("grades2");
                }
                    //System.out.println(higerGrades);
                    //System.out.println(level==4 && higerGrades<40);
                if(level==4 && higerGrades<40){
                    System.out.println(level==4 && higerGrades<40);
                    System.out.println(higerGrades);
                    System.out.println("test!!!!!!!!!!!");
                    return false;
                }
                if (level<4 && higerGrades<30){
                    return false;
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boundaries;
    }

    public boolean failDissertation(String username,int level){
        boolean status = true;
        double meanGrades = 0.00;
        try {
            double higerGrades = 0;
            PreparedStatement st = conn.prepareStatement(
                    "select grades1,grades2 from grades where  username = ? and levelOfStudy=? and " +
                            "module =?"
            );
            st.setString(1,username);
            st.setInt(2,level);
            st.setString(3,"DISERT");
            ResultSet executeQuery = st.executeQuery();
            if(executeQuery.next()){
                if(executeQuery.getInt("grades1")>executeQuery.getInt("grades2")){
                    higerGrades=executeQuery.getInt("grades1");
                }else{
                    higerGrades=executeQuery.getInt("grades2");
                }
                if(level==3){
                    if (higerGrades<40){
                        status=false;

                    }
                }
                if(level==4){
                    if (higerGrades<50){
                        status=false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public String outcome(String username, int level){
        String periodTemp = null;
        String outcome =null;
        /* QUERY FOR CURRENT LEVE！！！！！！！！！！！
        try {
            String sql3 = "select periodOfStudy from students where username=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql3);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                periodTemp = (rs.getString("periodOfStudy"));
                System.out.println(periodTemp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int period = (Integer.parseInt(periodTemp.substring(8, 9)));
         */
        int period = level;
        if(period<3 && meanGrades(username,period)>=40){
            if(numberOfFails(username,period)<1){
                outcome ="pass";
            }
            if(numberOfFails(username,period)==1 && concededBoundaries(username,period)){
                outcome ="conceded pass";
            }
            if(numberOfFails(username,period)>1 || concededBoundaries(username,period)==false){
                outcome="fail";
            }
        }else if (period<3 && meanGrades(username,period)<40){
            outcome="fail";
        }else if(period==3 && meanGrades(username,period)>=40 ){
            if(numberOfFails(username,period)<1&&failDissertation(username,level)){
                outcome ="Bc Graduate with ";
            }
            if(numberOfFails(username,period)==1 && concededBoundaries(username,period)&&
                    failDissertation(username,level)){
                outcome="conceded pass";
            }
            if(numberOfFails(username,period)>1 || concededBoundaries(username,period)==false ||
                    failDissertation(username,level)==false){
                outcome="fail";
            }
        } else if(period==3 && meanGrades(username,period)<40 ){
            outcome="fail";
        }else if(period==4 && meanGrades(username,period)>=50 ){
            if(numberOfFails(username,period)<1&&failDissertation(username,level)){
                outcome ="Msc Graduate with ";
            }
            if(numberOfFails(username,period)==1 && concededBoundaries(username,period)&&
                    failDissertation(username,level)){
                System.out.println(period);
                outcome="conceded pass";
            }
            if(numberOfFails(username,period)>1 || concededBoundaries(username,period)==false ||
                    failDissertation(username,level)==false){
                outcome="fail";
            }
        } else if(period==4 && meanGrades(username,period)<50 ){
            outcome="fail";
        }
        return outcome;
    }

    public String graduate(String username, int level){
        String graduate = null;
        String degree = null;
        double mean = 0;
        String periodtemp= null;
        try {
            String sql = "select registeredDegree from students where username=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                degree = (rs.getString("registeredDegree"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql = "select periodOfStudy from students where username=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                periodtemp = (rs.getString("periodOfStudy"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String period = "C"+periodtemp.substring(1,9);
        System.out.println(period);

        try {
            String sql = "update students set periodOfStudy=? where username=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, period);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String degName = null;
        try {
            String sql = "select degName from degrees where degId=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, degree);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                degName = (rs.getString("degName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(degName.substring(0,3));
        if(degName.substring(0,3).equals("MSc")){
            if (meanGrades(username,level)<49.5){
                graduate="fail";
            }else if((meanGrades(username,4)>=49.5&&(meanGrades(username,level)<59.5))){
                graduate="pass";
            }else if((meanGrades(username,4)>=59.5&&(meanGrades(username,level)<69.5))){
                graduate="merit";
            }else if((meanGrades(username,4)>=69.5)){
                graduate="distinction";
            }
        }else if(degName.substring(0,3).equals("BSc") || degName.substring(0,3).equals("BEn")){
            if ((meanGrades(username,2)+ meanGrades(username,3)*2)<39.5){
                graduate="fail";
            }else if( ((meanGrades(username,2)+ meanGrades(username,3)*2))  >= 39.5 &&
                    ( (meanGrades(username,2)+ meanGrades(username,3)*2)) <44.5 ){
                graduate="pass (non-honours)";
            }else if( ((meanGrades(username,2)+ meanGrades(username,3)*2))  >= 44.5 &&
                    ( (meanGrades(username,2)+ meanGrades(username,3)*2)) <49.5 ){
                graduate="third class";
            }else if( ((meanGrades(username,2)+ meanGrades(username,3)*2))  >= 49.5 &&
                    ( (meanGrades(username,2)+ meanGrades(username,3)*2)) <59.5 ){
                graduate="lower second";
            }else if( ((meanGrades(username,2)+ meanGrades(username,3)*2))  >= 59.5 &&
                    ( (meanGrades(username,2)+ meanGrades(username,3)*2)) <69.5 ){
                graduate="upper second";
            }else if( ((meanGrades(username,2)+ meanGrades(username,3)*2))  >= 69.5 ){
                graduate="first class";
            }
        }else if(degName.substring(0,3).equals("MCo") || degName.substring(0,3).equals("MEn")){
            if ((meanGrades(username,2)+ meanGrades(username,3)*2 +
                    meanGrades(username,4)*2)<49.5){
                graduate="fail";
            }else if( (meanGrades(username,2)+ meanGrades(username,3)*2 +
                    meanGrades(username,4)*2)  >= 49.5 &&
                    ( meanGrades(username,2)+ meanGrades(username,3)*2 +
                            meanGrades(username,4)*2) <59.5 ){
                graduate="lower second";
            }else if( (meanGrades(username,2)+ meanGrades(username,3)*2 +
                    meanGrades(username,4)*2)  >= 59.5 &&
                    ( (meanGrades(username,2)+ meanGrades(username,3)*2 +
                            meanGrades(username,4)*2) <69.5 )){
                graduate="upper second";
            }else if( (meanGrades(username,2)+ meanGrades(username,3)*2 +
                    meanGrades(username,4)*2)  >= 69.5 ){
                graduate="first class";
            }
        }

        return graduate;
    }
}
