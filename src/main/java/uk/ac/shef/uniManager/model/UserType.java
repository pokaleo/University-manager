package uk.ac.shef.uniManager.model;

public enum UserType {
    ADMIN("admin",0),REGISTRAR("registrar",1),TEACHER("teacher",2),STUDENT("student",3);
    private String name;
    private int index;
    UserType(String name, int index){
        this.name = name;
        this.index = index;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    @Override
    public String toString(){
        return this.name;
    }
}
