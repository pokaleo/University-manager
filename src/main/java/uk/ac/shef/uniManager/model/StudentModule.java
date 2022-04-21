package uk.ac.shef.uniManager.model;

public class StudentModule {
    private String username;
    private String fullName;
    private String moduleId;
    private int credits;
    private int periodOfStudy;

    public int getPeriodOfStudy() {
        return periodOfStudy;
    }

    public void setPeriodOfStudy(int periodOfStudy) {
        this.periodOfStudy = periodOfStudy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
