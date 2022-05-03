import java.util.Vector;

public class dbRecord {
    private static final int MAX_RECORD_SIZE=512;
    private byte[] columns;
    private byte nullBitmap;
    private byte[] data;

    // Constructor
    public dbRecord(int columnsCount, byte nullBit, Vector<Object> recordData){
        columns=new byte[columnsCount];
        nullBitmap=nullBit;

    }

    public void setRecordForm(){

    }
}