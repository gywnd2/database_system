import java.util.Vector;

public class dbSlottedPage {
    private int recordCount;
    private int[] slots;
    private int freeSpaceStartIdx;
    private dbRecord[] records;

    // Constructor
    public dbSlottedPage(int size){
        recordCount=0;
        slots=new int[size];
        records=new dbRecord[size];
        freeSpaceStartIdx=0;
    }
}
