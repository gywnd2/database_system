import java.io.Serializable;
import java.util.HashMap;

public class dbDataDict implements Serializable {
    public HashMap<String, dbMetaData> dict;

    // Constructor
    public dbDataDict(){
        // Map 초기화
        dict=new HashMap<>();
    }
}