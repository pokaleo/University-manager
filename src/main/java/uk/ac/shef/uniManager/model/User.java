package uk.ac.shef.uniManager.model;

public class User {
    private String type;
    private String username;
    private String password;
    private String salt;
    private int userId;
    public String getSalt() {
        return salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;

    }
    public String toString(){
        return this.type;
    }


    public int  getUserID() {
        return userId;
    }
    public void setUserID(int userId){
        this.userId=userId;
    }
}

