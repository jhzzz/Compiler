package sample.table;

public class VariabeTable {
    private String name;
    private String scope;
    private String type;
    private String value;
    private String addr;

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }

    public String getScope() {
        return scope;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
