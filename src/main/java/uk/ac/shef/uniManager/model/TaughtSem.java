package uk.ac.shef.uniManager.model;

public enum TaughtSem {
    autumn("autumn",0),spring("spring",1),year("accross the year",2);
    private String name;
    private int index;
    private TaughtSem(String name, int index){
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
