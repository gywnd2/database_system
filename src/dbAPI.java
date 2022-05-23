import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class dbAPI {
    // Constructor
    public dbAPI(){}

    // API method
    // Create table
    public dbTable createTable(String tableName, String[] columnsInput, int columnsCount, dbDataDict dbDataDict){
        // 메타데이터 생성
        dbMetaData metaData=new dbMetaData(tableName, columnsInput, columnsCount);
        dbTable table=new dbTable();
        // Data dictionary에 메타 데이터 생성
        System.out.println("columns : "+metaData.columns.keySet());
        dbDataDict.dict.put(tableName, metaData);
        return table;
    }

    // Insert record
    public void insertRecord(dbTable table, String tableName, HashMap<String, String> columnsInput, dbMetaData metaData) throws UnsupportedEncodingException {
        // nullBitmap String 생성
        StringBuffer nullBitMapString=new StringBuffer();
        nullBitMapString.append("00000000");

        // 사용자 입력으로 Byte 배열 생성
        // 레코드의 전체 길이 계산을 위해 임시로 컬럼 부분 생성
        // 레코드의 컬럼 정보 부분
        String tmpRecordString="";


        // 테이블의 페이지들을 순회
        for (int i=0; i<table.pages.size(); i++){
            // 가득 차지 않은 페이지를 찾으면 그 페이지에 삽입
            if (table.pages.get(i).freeSpaceStartIdx!=table.pages.get(i).freeSpaceEndIdx){
                // 해시맵을 순회하기 위한 인덱스
                int hashIdx=0;
                for (Object columnName : metaData.columns.keySet()){
                    // 컬럼의 자료형 확인
                    String dataType=metaData.columns.get(columnName).split("\\(")[0];
                    // 해당 컬럼이 null인지 확인
                    if (columnsInput.get(columnName).isEmpty()){
                        // 해당 컬럼의 nullBitMap 변경
                        nullBitMapString.replace(hashIdx, hashIdx+1, "1");
                        // Test
                        System.out.println("dbAPI -> nullBitMap 변경 : "+nullBitMapString);
                    }
                    // 컬럼 정보 추가
                    // 가변 자료형일 경우
                    if(dataType.equals("varchar")){
                        tmpRecordString+="00,"+columnsInput.get(columnName).length()+" ";
                    // 가변 자료형 아닌 경우
                    }else{
                        tmpRecordString+=columnsInput.get(columnName)+" ";
                    }
                    // 인덱스 증가
                    hashIdx++;
                }

                // 바이트 배열(레코드)에 컬럼 정보 삽입
                tmpRecordString+=Integer.toString(Integer.parseInt(nullBitMapString.toString(), 2));

                // 데이터 입력
                for (Object columnName : columnsInput.keySet()){
                    // 컬럼의 자료형 확인
                    String dataType=metaData.columns.get(columnName).split("\\(")[0];
                    // 해당 컬럼이 null인지 확인
                    if (columnsInput.get(columnName)==null){
                        // null일 경우 통과
                        continue;
                    // 입력이 null이 아닐 경우
                    }else{
                        if (dataType.equals("varchar")){
                            // 데이터 추가
                            tmpRecordString+=" '"+columnsInput.get(columnName)+"'";
                        }else{
                            // 고정 길이 컬럼의 경우 이미 컬럼 정보 쪽에 입력되어 있으므로 통과
                            continue;
                        }
                    }
                }

                // 입력한 레코드 String을 byte로 변경
                byte[] tmpByteRecord=tmpRecordString.getBytes();

                // 새 레코드 객체 생성
                dbRecord newRecord=new dbRecord(metaData, columnsInput, tmpByteRecord.length);
                // 새 레코드 객체에 바이트 배열 복사
                for(int h=0; h<tmpByteRecord.length; h++){
                    newRecord.record[h]=tmpByteRecord[h];
                }

                int idx=newRecord.record.length-1;
                for (int j=table.pages.get(i).MAX_PAGE_SIZE-1; j>table.pages.get(i).MAX_PAGE_SIZE-1-newRecord.record.length; j--){
                    table.pages.get(i).page[j]=newRecord.record[idx];
                    idx--;
                }
                // 페이지의 레코드 개수 증가
                table.pages.get(i).recordCount++;

                // Test
                System.out.println("dbAPI -> "+tmpRecordString);
                System.out.println("dbAPI -> 레코드 개수 : "+table.pages.get(i).recordCount);
                System.out.print("dbAPI -> 레코드 : ");
                System.out.println(new String(newRecord.record, "UTF-8"));

            // 페이지들이 가득 찼을 경우
            }else{
                table.pages.add(new dbSlottedPage());
                
            }
        }
    }

    // Search record
    public void searchRecord(dbTable table){
        // Search
//        dbSlottedPage slot=new dbSlottedPage();
//        return slot;
    }

    // Column search
    public void columnSearch(String columnName){
        // Find by column
    }


}
