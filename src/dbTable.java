import java.io.Serializable;
import java.util.ArrayList;

public class dbTable implements Serializable {
    // Table class members
    public ArrayList<dbSlottedPage> pages;

    // Constructor
    public dbTable() {
        pages=new ArrayList<>();
        // 페이지 하나 기본으로 생성
        pages.add(new dbSlottedPage());
    }
}
