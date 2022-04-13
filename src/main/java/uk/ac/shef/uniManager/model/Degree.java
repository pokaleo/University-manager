package uk.ac.shef.uniManager.model;

public class Degree {
    private String degId;
    private String degName;
    private String leadDep;
    public String getDegId() {
        return degId;
    }
    public void setDegId(String degId) {
        this.degId = degId;
    }
    public String getDegName() {
        return degName;
    }
    public void setDegName(String degName) {
        this.degName = degName;
    }
    public String getLeadDep() {
        return leadDep;
    }
    public void setLeadDep(String leadDep) {
        this.leadDep = leadDep;
    }
    @Override
    public String toString(){
        return this.degId;
    }
}
