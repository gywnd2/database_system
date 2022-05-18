import java.util.Vector;

public class dbRecord {
    public static final int MAX_RECORD_SIZE=10240;
    public byte nullBitmap=1;
    public byte[] record;

    // Constructor
    public dbRecord(dbMetaData metaData, String[] input){
        record=new byte[MAX_RECORD_SIZE];
    }

}