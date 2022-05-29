import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class dbSlottedPage  implements Serializable {
    public final static int MAX_PAGE_SIZE=100;
    public byte[] page;
    public byte recordCount=0;
    public ArrayList<Byte> slots;
    public int freeSpaceStartIdx;
    public int freeSpaceEndIdx;

    // Constructor
    public dbSlottedPage(){
        page=new byte[MAX_PAGE_SIZE];
        // 페이지 맨 앞에 레코드 개수 삽입
        page[0]=recordCount;

        // Slot 삽입 (ArrayList -> Byte[] 변환 하여 page에 삽입)
        slots=new ArrayList<>();
        Byte[] slotsToByte=slots.toArray(new Byte[slots.size()]);
        for (int i=0; i<slotsToByte.length; i++){
            page[i+1]=slotsToByte[i];
        }

        // freeSpace 설정
        // Start -> Slot 정보 바로 다음 부터
        // End -> 아직 레코드가 삽입되기 전이므로 가장 마지막 인덱스
        freeSpaceStartIdx=slotsToByte.length+1;
        freeSpaceEndIdx=MAX_PAGE_SIZE-1;

    }

}
