import javax.annotation.processing.Filer;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
    public boolean insertRecord(dbTable table, String tableName, HashMap<String, String> columnsInput, dbMetaData metaData) throws UnsupportedEncodingException {
        // 페이지 순회 결과 기록
        boolean isPageFull=false;
        // 컬럼 부분 수정을 위한 인덱스
        // 컬럼 정보 부분의 마지막 글자 위치, 컬럼 데이터 부분의 첫 글자의 위치
        int columnInfoEndIdx, columnDataStartIdx;

        // nullBitmap String 생성
        StringBuffer nullBitMapString=new StringBuffer();
        nullBitMapString.append("00000000");

        // 사용자 입력으로 레코드 생성
        // 레코드의 전체 길이 계산을 위해 임시로 컬럼 부분 생성
        // 레코드의 컬럼 정보 부분
        String tmpRecordString="";
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

        columnInfoEndIdx=tmpRecordString.length();

        // nullBitmap 삽입
        tmpRecordString+=Integer.toString(Integer.parseInt(nullBitMapString.toString(), 2));

        columnDataStartIdx=tmpRecordString.length()+2;

        // 레코드에 임시로 기록했던 레코드 길이 수정
        String[] tmp=tmpRecordString.split(" ");
        StringBuffer tmp2=new StringBuffer();
        for (int j=0; j<metaData.columns.size(); j++){
            StringBuffer column=new StringBuffer();
            column.append(tmp[j]);
            if(j==0){
                column.replace(0, 2, Integer.toString(columnDataStartIdx));
                tmp2.append(column);
            }else{
                String[] tmp3=tmp[j-1].split(",");
                int a=columnDataStartIdx+Integer.parseInt(tmp3[1])+3;
                column.replace(0, 2, Integer.toString(a));
                tmp2.append(" ");
                tmp2.append(column);
            }
        }

        tmpRecordString=tmp2.toString()+" "+Integer.toString(Integer.parseInt(nullBitMapString.toString(), 2));

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
        byte[] tmpByteRecord=tmpRecordString.getBytes(StandardCharsets.UTF_8);

        // 새 레코드 객체 생성
        dbRecord newRecord=new dbRecord(metaData, columnsInput, tmpByteRecord.length);
        // 새 레코드 객체에 바이트 배열 복사
        for(int h=0; h<tmpByteRecord.length; h++){
            newRecord.record[h]=tmpByteRecord[h];
        }

        // 테이블의 페이지들을 순회
        for (int i=0; i<table.pages.size(); i++){
            // Test
            System.out.println("dbAPI -> "+table.pages.get(i).page);
            System.out.println("dbAPI -> 레코드 개수 : "+table.pages.get(i).recordCount);
            System.out.print("dbAPI -> 레코드 : ");
            System.out.println(new String(newRecord.record, StandardCharsets.UTF_8));

            // 가득 차지 않은 페이지를 찾으면 그 페이지에 삽입
            // freeSpace가 레코드 길이보다 커야함
            if (table.pages.get(i).freeSpaceEndIdx-table.pages.get(i).freeSpaceStartIdx>=newRecord.record.length){
                // 페이지에 슬롯 할당하고 레코드 삽입
                int idx=newRecord.record.length-1;
                for(int j=table.pages.get(i).page.length-1; j>table.pages.get(i).page.length-1-newRecord.record.length; j--){
                    table.pages.get(i).page[j]=newRecord.record[idx];
                    idx--;
                }
                // 레코드의 시작 인덱스를 슬롯에 기록
                table.pages.get(i).slots.add((byte) (table.pages.get(i).page.length-newRecord.record.length));
                Byte[] slotsToByte=table.pages.get(i).slots.toArray(new Byte[table.pages.get(i).slots.size()]);
                for (int k=0; k<slotsToByte.length; k++){
                    // 레코드 개수 공간 다음 부터 슬롯을 기록
                    table.pages.get(i).page[k+1]=slotsToByte[k];
                }

                // freeSpace 시/종점 지정
                table.pages.get(i).freeSpaceStartIdx=slotsToByte.length+1;
                table.pages.get(i).freeSpaceEndIdx=table.pages.get(i).page.length-newRecord.record.length;

                // 페이지의 레코드 개수 증가
                table.pages.get(i).recordCount++;
                metaData.tableRecordCount++;
                table.pages.get(i).page[0]=(byte)table.pages.get(i).recordCount;

            // 페이지들이 가득 찼을 경우
            }else{
                if(i==table.pages.size()-1){
                    isPageFull=true;
                }
            }
        }

        // 페이지들이 가득 찼을 경우 페이지를 추가하고 레코드 삽입
        if(isPageFull){
            table.pages.add(new dbSlottedPage());
            // 페이지에 슬롯 할당하고 레코드 삽입
            int lastPageIdx=table.pages.size()-1;
            int idx=newRecord.record.length-1;
            for(int j=table.pages.get(lastPageIdx).page.length-1; j>=table.pages.get(lastPageIdx).page.length-1-newRecord.record.length; j--){
                table.pages.get(lastPageIdx).page[j]=newRecord.record[idx];
                idx--;
            }
            // 레코드의 시작 인덱스를 슬롯에 기록
            table.pages.get(lastPageIdx).slots.add((byte) (table.pages.get(lastPageIdx).page.length-1-newRecord.record.length));
            Byte[] slotsToByte=table.pages.get(lastPageIdx).slots.toArray(new Byte[table.pages.get(lastPageIdx).slots.size()]);
            for (int k=0; k<slotsToByte.length; k++){
                // 레코드 개수 공간 다음 부터 슬롯을 기록
                table.pages.get(lastPageIdx).page[k+1]=slotsToByte[k];
            }

            // 페이지의 레코드 개수 증가
            table.pages.get(lastPageIdx).recordCount++;
            metaData.tableRecordCount++;
        }

        // 레코드 삽입 결과를 테이블 전체 출력을 통해 확인
        System.out.println("테이블 "+tableName+"에 총 "+metaData.tableRecordCount+"개의 레코드가 있습니다.");
        System.out.println("-----------------------------------------------------------------");
        for (Object columnName : metaData.columns.keySet()){
            System.out.printf("|%-20s|", columnName);
        }
        System.out.println();
        for (dbSlottedPage slottedPage : table.pages){
            // 바이트 배열을 다시 String으로 변환 후 공백을 기준으로 분리
            String record=new String(slottedPage.page, StandardCharsets.UTF_8);
            record=record.substring(slottedPage.freeSpaceEndIdx);
            String[] splitedRecord=record.split(" ");
            for (int i=0; i<metaData.columns.size(); i++){
                String[] column=splitedRecord[i].split(",");
                // 레코드에 기록된 offset과 length로 데이터 출력
                System.out.printf("|%-20s|", record.substring(Integer.parseInt(column[0]), Integer.parseInt(column[0])+Integer.parseInt(column[1])));
            }
            System.out.println();
            System.out.println("-----------------------------------------------------------------");
            System.out.println();
        }

        return true;
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

    // DB Write
    public void writeDB(HashMap<String, dbTable> db, dbDataDict dataDict) throws IOException{
        BufferedWriter bufferedWriter=null;
        File file=new File("myDB.hjdb");
        if(!file.exists()){
            file.createNewFile();
        }
        bufferedWriter=new BufferedWriter(new FileWriter(file));
        for (byte ch : db.toString().getBytes()) {
            bufferedWriter.write(ch);
        }
        bufferedWriter.write("\n");
        for (byte ch : dataDict.toString().getBytes()){
            bufferedWriter.write(ch);
        }
        bufferedWriter.close();
    }

    // DB Read
    public void readDB() throws IOException{
        BufferedReader bufferedReader=null;
        File file=new File("myDB.hjdb");
//        if(!file.exists()){
//            file.createNewFile();
//        }
        bufferedReader=new BufferedReader(new FileReader(file));


    }
}
