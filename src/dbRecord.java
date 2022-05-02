import java.util.Vector;

public class dbRecord {
    private int[] columns;
    private byte nullBitmap;
    private Vector<Object> data;

    // Constructor
    public dbRecord(int columnsCount, byte nullBit, Vector<Object> recordData){
        columns=new int[columnsCount];
        nullBitmap=nullBit;
        data=recordData;
    }

    public void setRecordForm(){

    }
}