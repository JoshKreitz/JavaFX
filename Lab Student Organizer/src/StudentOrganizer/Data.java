package StudentOrganizer;

import java.io.Serializable;

public class Data implements Serializable{

    private String key, val;

    public Data(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String toString() {
        return key + ":" + val;
    }
}
