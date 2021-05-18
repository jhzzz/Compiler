package sample.table;

public class returnValue {
    private String place;
    private String  tc;
    private String  fc;

    public returnValue(){
        this.place = "";
        this.tc = "0";
        this.fc = "0";
    }
    public returnValue(String place, String tc, String fc){
        this.place = place;
        this.fc = fc;
        this.tc = tc;
    }

    public String getFc() {
        return fc;
    }

    public String getTc() {
        return tc;
    }

    public String getPlace() {
        return place;
    }

    public void setFc(String fc) {
        this.fc = fc;
    }

    public void setPlace(String place) {
        this.place = place;
    }
    public void setTc(String tc) {
        this.tc = tc;
    }

}
