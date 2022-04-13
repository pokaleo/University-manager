package uk.ac.shef.uniManager.model;

public class Module {
    private String moduleId;
    private String moduleName;
    private String taughtSem;
    public String getModuleId() {
        return moduleId;
    }
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    public String getTaughtSem() {
        return taughtSem;
    }
    public void setTaughtSem(String taughtSem) {
        this.taughtSem = taughtSem;
    }
    @Override
    public String toString(){
        return this.moduleId;
    }
}
