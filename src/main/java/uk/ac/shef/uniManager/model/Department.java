package uk.ac.shef.uniManager.model;

public class Department {
    private String depId;
    private String depName;
    public String getDepId() {
        return depId;
    }
    public void setDepId(String depId) {
        this.depId = depId;
    }
    public String getDepName() {
        return depName;
    }
    public void setDepName(String depName) {
        this.depName = depName;
    }
    @Override
    public String toString(){
        return this.depId;
    }
}
