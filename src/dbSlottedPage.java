import java.util.Vector;

public class dbSlottedPage {
    public final static int MAX_RECORD_COUNT=5;
    public byte recordCount;
    public byte[] slots;
    public int freeSpaceStartIdx;
    public int freeSpaceEndIdx;
    public dbRecord[] records;

    // Constructor
    public dbSlottedPage(){
        recordCount=0;
        slots=new byte[5];
        records=new dbRecord[5];
    }

}
