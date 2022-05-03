import java.util.Vector;

public class dbSlottedPage {
    private static final int MAX_PAGE_SIZE=10240;
    private byte recordCount;
    private byte[] slots;
    private dbRecord[] records;

    // Constructor
    public dbSlottedPage(int size){
        recordCount=0;
        slots=new byte[size];
        records=new dbRecord[size];
    }

}
