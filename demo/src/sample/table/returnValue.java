package sample.table;

public class returnValue {
    private String place;
    private String  tc;
    private String  fc;
    private String chain;

    public returnValue(){
        this.place = "";
        this.tc = "0";
        this.fc = "0";
        this.chain = "0";
    }
    public returnValue(String place, String tc, String fc, String chain){
        this.place = place;
        this.fc = fc;
        this.tc = tc;
        this.chain = chain;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
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
