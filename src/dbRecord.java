import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

public class dbRecord implements Serializable {
    public byte nullBitmap=1;
    public byte[] record;

    // Constructor
    public dbRecord(dbMetaData metaData, HashMap<String, String> columnsInput, int size){
        record=new byte[size];
    }

}