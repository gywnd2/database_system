import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class dbMetaData {
    public String tableName;
    public int tableRecordCount=0;
    public HashMap<String, String> views;
    public String[] integrityConstraints;
    public HashMap<String, String> columns;
    public HashMap<String, Integer> columnInputLength;

    // Constructor
    public dbMetaData(String tableName, String[] columnsInput, int columnsCount){
        this.tableName=tableName;
        this.columns=new HashMap<>();
        this.columnInputLength=new HashMap<>();
        // 입력받은 컬럼 정보로 메타 데이터 생성
        for (int i=0; i<columnsCount; i++){
            // name varchar(20)
            String[] column=columnsInput[i].split(" ");
            columns.put(column[0], column[1]);
            // 가변 자료형일 경우 길이 제한 추가
            try{
                String columnName=column[0];
                column=column[1].split("\\(");
                if (column[0].equals("varchar")){
                    column=column[1].split("\\)");
                    columnInputLength.put(columnName, Integer.parseInt(column[0]));
                }
            }catch(IllegalStateException e){

            }
        }
    }
}
