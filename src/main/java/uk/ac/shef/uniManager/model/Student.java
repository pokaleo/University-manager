package uk.ac.shef.uniManager.model;

public class Student {
    private String regDeg;
    private String name;
    private int regNumber;
    private String email;
    private String  tutor;
    private String periodOfStudy;
    private String forename;
    private String surname;
    private String title;
    private String username;
    public String getTitle(){return title;}
    public void setTitle(String title){this.title=title;}
    public String getForename(){return forename;}
    public void setForeName(String forename) {
        this.forename = forename;
    }
    public String getSurname(){return surname;}
    public void setSurName(String surname) {
        this.surname = surname;
    }
    public int getRegNumber() {
        return regNumber;
    }
    public void setRegNumber(int id) {
        this.regNumber = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRegDeg() {
        return regDeg;
    }
    public void setRegDeg(String regDeg) {
        this.regDeg = regDeg;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTutor(){
        return tutor;
    }
    public void setTutor(String tutor){
        this.tutor=tutor;
    }
    public String getPeriodOfStudy(){
        return periodOfStudy;
    }
    public void setPeriodOfStudy(String periodOfStudy){
        this.periodOfStudy=periodOfStudy;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFullName() {
        return forename + " " + surname;
    }
    @Override
    public String toString(){
        return this.username;
    }
}
