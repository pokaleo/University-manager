package uk.ac.shef.uniManager.model;

public class Grades {
    private String username;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getLevelOfStudy() {
        return levelOfStudy;
    }
    private String modId;
    private int grades1;
    private int grades2;
    private String levelOfStudy;

  
    public int getGrades1() {
        return grades1;
    }
    public String getGrades1Str() {
        return String.valueOf(grades1);
    }
    public void setGrades1(int grades1) {
        this.grades1 = grades1;
    }
    public void setGrades1ByStr(String grades1) {
        this.grades1 = Integer.parseInt(grades1);
    }
    public int getGrades2() {
        return grades2;
    }
    public String getGrades2Str() {
        return String.valueOf(grades2);
    }
    public void setGrades2(int grades2) {
        this.grades2 = grades2;
    }
    public void setGrades2ByStr(String grades2) {
        this.grades2 = Integer.parseInt(grades2);
    }
    public String getLevelOfStudy(String levelOfStudy) {
        return levelOfStudy;
    }
    public void setLevelOfStudy(String levelOfStudy) {
        this.levelOfStudy = levelOfStudy;
    }
	public String getModId() {
		return modId;
	}
	public void setModId(String modId) {
		this.modId = modId;
	}

}