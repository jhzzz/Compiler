package sample.table;

public class MidCodeTable {
    private String operater;
    private String object1;
    private String object2;
    private String result;

    public MidCodeTable(){
    }

    public MidCodeTable(String operater, String object1, String object2, String result){
        this.operater = operater;
        this.object1 = object1;
        this.object2 = object2;
        this.result = result;

    }

    public String getObject1() {
        return object1;
    }

    public String getObject2() {
        return object2;
    }

    public String getOperater() {
        return operater;
    }

    public String getResult() {
        return result;
    }

    public void setObject1(String object1) {
        this.object1 = object1;
    }

    public void setObject2(String object2) {
        this.object2 = object2;
    }

    public void setOperater(String operater) {
        this.operater = operater;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
