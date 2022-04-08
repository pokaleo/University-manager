package uk.ac.shef.uniManager.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConn {
    private String dburl = "jdbc:mysql://localhost/team058";

    public String dbdriver = "com.mysql.cj.jdbc.Driver";
    private String dbusername = "root";
    private String dbpassword = "Liyao0220";


    public Connection getCon(){
        try {
            Class.forName(dbdriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection con = null;
        try {
            con = DriverManager.getConnection(dburl, dbusername, dbpassword);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return con;
    }
    public void closeCon(Connection con)throws Exception{
        if(con!=null){
            con.close();
        }
    }
    public static void main(String[] args) {
        DbConn dbconn = new DbConn();
        try{
            dbconn.getCon();
            System.out.println("database success to connect");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("database fail to connect");
        }
    }


}
