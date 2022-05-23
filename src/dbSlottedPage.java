import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class dbSlottedPage {
    public final static int MAX_PAGE_SIZE=100;
    public byte[] page;
    public byte recordCount=0;
    public ArrayList<Byte> slots;
    public int freeSpaceStartIdx;
    public int freeSpaceEndIdx;

    // Constructor
    public dbSlottedPage(){
        // 레코드 개수 = 레코드 배열 인덱스 (0부터 시작)
        page=new byte[MAX_PAGE_SIZE];
        // 페이지 맨 앞에 레코드 개수 삽입
        page[0]=recordCount;

        // Slot 삽입
        slots=new ArrayList<>();
        Byte[] slotsToByte=slots.toArray(new Byte[slots.size()]);
        for (int i=0; i<slotsToByte.length; i++){
            page[i+1]=slotsToByte[i];
        }

        System.out.println("dbSlottedPage -> 페이지에 slot이 삽입되었습니다. 길이 : "+slotsToByte.length);

        // freeSpace 설정
        freeSpaceStartIdx=slotsToByte.length+1;
        freeSpaceEndIdx=MAX_PAGE_SIZE-1;

        System.out.print("dbSlottedPage -> ");
        for(int j : page){
            System.out.print(j+" ");
        }

//        Byte[] bytes = (Byte[]) arrayList.toArray(); //  ArrayList -> Byte[]
//
//        arrayList = new ArrayList(Arrays.asList(bytes )); // Byte[] -> ArrayList
    }

}
