import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class dbMetaData implements Serializable {
    public String tableName;
    public int tableRecordCount=0;
    public HashMap<String, String> integrityConstraints;
    public LinkedHashMap<String, String> columns;
    public HashMap<String, Integer> columnInputLength;

    // Constructor
    public dbMetaData(String tableName, String[] columnsInput, int columnsCount){
        this.tableName=tableName;
        this.columns=new LinkedHashMap<>();
        this.columnInputLength=new HashMap<>();
        this.integrityConstraints=new HashMap<>();

        // 입력받은 컬럼 정보로 메타 데이터 생성
        for (int i=0; i<columnsCount; i++){
            // 컬럼 정보 입력 순서
            // => name varchar(20)
            String[] column=columnsInput[i].split(" ");
            columns.put(column[0], column[1]);

            // 컬럼 데이터 최대 길이 기록
            String columnName=column[0];
            column=column[1].split("\\(");
            column=column[1].split("\\)");
            columnInputLength.put(columnName, Integer.parseInt(column[0]));
        }
    }
}
