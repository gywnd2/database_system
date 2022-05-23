import java.util.HashMap;
import java.util.Vector;

public class dbMetaData {
    public String tableName;
    public HashMap<String, String> views;
    public String[] integrityConstraints;
    public HashMap<String, String> columns;

    // Constructor
    public dbMetaData(String tableName, String[] columnsInput, int columnsCount){
        this.tableName=tableName;
        this.columns=new HashMap<>();
        // 입력받은 컬럼 정보로 메타 데이터 생성
        for (int i=0; i<columnsCount; i++){
            // name varchar 10
            String[] column=columnsInput[i].split(" ");
            columns.put(column[0], column[1]);
        }
    }
}
