import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class dbAPI {
    public HashMap<String, dbTable> db;
    public dbDataDict dbDataDict;

    // Constructor
    public dbAPI() throws IOException {
        this.db=new HashMap<>();
        this.dbDataDict=new dbDataDict();
        this.readDB();
    }

    // API method
    // Create table
    public void createTable(String tableName, String[] columnsInput, int columnsCount, String pk){
        // Data dictionary에 메타 데이터 생성, dbTable 생성
        dbDataDict.dict.put(tableName, new dbMetaData(tableName, columnsInput, columnsCount));
        // PK 지정
        dbDataDict.dict.get(tableName).integrityConstraints.put("PK", pk);
        // 테이블 생성
        db.put(tableName, new dbTable());

        System.out.println("columns : "+dbDataDict.dict.get(tableName).columns.keySet());
    }

    // Insert record
    public boolean insertRecord(String tableName, HashMap<String, String> columnsInput) throws UnsupportedEncodingException {
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
        for (String columnName : dbDataDict.dict.get(tableName).columns.keySet()){
            // 컬럼의 자료형 확인
            String dataType=dbDataDict.dict.get(tableName).columns.get(columnName).split("\\(")[0];
            // 해당 컬럼이 null인지 확인
            if (columnsInput.get(columnName).isEmpty()){
                // 해당 컬럼의 nullBitMap 변경
                nullBitMapString.replace(hashIdx, hashIdx+1, "1");
                // Test
                System.out.println("dbAPI -> nullBitMap 변경 : "+nullBitMapString);
            }
            // 컬럼 정보 추가
            tmpRecordString+="00,"+columnsInput.get(columnName).length()+" ";
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
        int lastIdx=0;
        for (int j=0; j<dbDataDict.dict.get(tableName).columns.size(); j++){
            StringBuffer column=new StringBuffer();
            column.append(tmp[j]);
                if(j==0){
                    String[] tmp3=column.toString().split(",");
                    column.replace(0, 2, Integer.toString(columnDataStartIdx));
                    String[] tmp4 = column.toString().split(",");
                    lastIdx=Integer.parseInt(tmp4[0])+Integer.parseInt(tmp4[1])+3;
                    tmp2.append(column);
                }else{
                    String[] tmp3=tmp[j-1].split(",");
                    // 00, 길이 에서 00 치환
                    column.replace(0, 2, Integer.toString(lastIdx));
                    String[] tmp4 = column.toString().split(",");
                    lastIdx=Integer.parseInt(tmp4[0])+Integer.parseInt(tmp4[1])+3;
                    tmp2.append(" ");
                    tmp2.append(column);
            }
        }

        tmpRecordString=tmp2.toString()+" "+Integer.toString(Integer.parseInt(nullBitMapString.toString(), 2));

        // 데이터 입력
        for (String columnName : columnsInput.keySet()){
            // 컬럼의 자료형 확인
            String dataType=dbDataDict.dict.get(tableName).columns.get(columnName).split("\\(")[0];
            // 해당 컬럼이 null인지 확인
            if (columnsInput.get(columnName)==null){
                // null일 경우 통과
                continue;
                // 입력이 null이 아닐 경우
            }else{
                // 데이터 추가
                tmpRecordString+=" '"+columnsInput.get(columnName)+"'";
            }
        }

        // 입력한 레코드 String을 byte로 변경
        byte[] tmpByteRecord=tmpRecordString.getBytes();

        // 새 레코드 객체 생성
        dbRecord newRecord=new dbRecord(dbDataDict.dict.get(tableName), columnsInput, tmpByteRecord.length);
        // 새 레코드 객체에 바이트 배열 복사
        for(int h=0; h<tmpByteRecord.length; h++){
            newRecord.record[h]=tmpByteRecord[h];
        }

        // 테이블의 페이지들을 순회
        for (int i=0; i<db.get(tableName).pages.size(); i++){
            // Test
            System.out.println("dbAPI -> "+db.get(tableName).pages.get(i).page);
            System.out.println("dbAPI -> 레코드 개수 : "+db.get(tableName).pages.get(i).recordCount);
            System.out.print("dbAPI -> 레코드 : ");
            System.out.println(new String(newRecord.record, StandardCharsets.UTF_8));

            // 가득 차지 않은 페이지를 찾으면 그 페이지에 삽입
            // freeSpace가 레코드 길이보다 커야함
            if (db.get(tableName).pages.get(i).freeSpaceEndIdx-db.get(tableName).pages.get(i).freeSpaceStartIdx>newRecord.record.length){
                // 페이지에 슬롯 할당하고 레코드 삽입
                int idx=newRecord.record.length-1;
                for(int j=db.get(tableName).pages.get(i).freeSpaceEndIdx; j>=db.get(tableName).pages.get(i).freeSpaceEndIdx-newRecord.record.length+1; j--){
                    db.get(tableName).pages.get(i).page[j]=newRecord.record[idx];
                    idx--;
                }
                // 레코드의 시작 인덱스를 슬롯에 기록
                db.get(tableName).pages.get(i).slots.add((byte) (db.get(tableName).pages.get(i).freeSpaceEndIdx-newRecord.record.length+1));
                Byte[] slotsToByte=db.get(tableName).pages.get(i).slots.toArray(new Byte[db.get(tableName).pages.get(i).slots.size()]);
                for (int k=0; k<slotsToByte.length; k++){
                    // 레코드 개수 공간 다음 부터 슬롯을 기록
                    db.get(tableName).pages.get(i).page[k+1]=slotsToByte[k];
                }

                // freeSpace 시/종점 지정
                db.get(tableName).pages.get(i).freeSpaceStartIdx=slotsToByte.length+1;
                db.get(tableName).pages.get(i).freeSpaceEndIdx=db.get(tableName).pages.get(i).freeSpaceEndIdx-newRecord.record.length;

                // 페이지의 레코드 개수 증가
                db.get(tableName).pages.get(i).recordCount++;
                dbDataDict.dict.get(tableName).tableRecordCount++;
                db.get(tableName).pages.get(i).page[0]=(byte)db.get(tableName).pages.get(i).recordCount;

            // 페이지들이 가득 찼을 경우
            }else{
                if(i==db.get(tableName).pages.size()-1){
                    isPageFull=true;
                }
            }
        }

        // 페이지들이 가득 찼을 경우 페이지를 추가하고 레코드 삽입
        if(isPageFull){
            db.get(tableName).pages.add(new dbSlottedPage());
            // 페이지에 슬롯 할당하고 레코드 삽입
            int lastPageIdx=db.get(tableName).pages.size()-1;
            int idx=newRecord.record.length-1;
            for(int j=db.get(tableName).pages.get(lastPageIdx).freeSpaceEndIdx; j>=db.get(tableName).pages.get(lastPageIdx).freeSpaceEndIdx+1-newRecord.record.length; j--){
                db.get(tableName).pages.get(lastPageIdx).page[j]=newRecord.record[idx];
                idx--;
            }
            // 레코드의 시작 인덱스를 슬롯에 기록
            db.get(tableName).pages.get(lastPageIdx).slots.add((byte) (db.get(tableName).pages.get(lastPageIdx).freeSpaceEndIdx-newRecord.record.length+1));
            Byte[] slotsToByte=db.get(tableName).pages.get(lastPageIdx).slots.toArray(new Byte[db.get(tableName).pages.get(lastPageIdx).slots.size()]);
            for (int k=0; k<slotsToByte.length; k++){
                // 레코드 개수 공간 다음 부터 슬롯을 기록
                db.get(tableName).pages.get(lastPageIdx).page[k+1]=slotsToByte[k];
            }

            // freeSpace 시/종점 지정
            db.get(tableName).pages.get(lastPageIdx).freeSpaceStartIdx=slotsToByte.length+1;
            db.get(tableName).pages.get(lastPageIdx).freeSpaceEndIdx=db.get(tableName).pages.get(lastPageIdx).freeSpaceEndIdx-newRecord.record.length;

            // 페이지의 레코드 개수 증가
            db.get(tableName).pages.get(lastPageIdx).recordCount++;
            dbDataDict.dict.get(tableName).tableRecordCount++;
            db.get(tableName).pages.get(lastPageIdx).page[0]=(byte)db.get(tableName).pages.get(lastPageIdx).recordCount;
        }
        return true;
    }

    // Search record
    public void searchRecord(String tableName, String pkValue){
        // 컬럼 메타데이터에서 PK의 인덱스 찾기
        List<String> columnOrderList=new ArrayList<String>(dbDataDict.dict.get(tableName).columns.keySet());
        String column=dbDataDict.dict.get(tableName).integrityConstraints.get("PK");
        int columnOrder=columnOrderList.indexOf(column);

        System.out.println("-----------------------------------------------------------------");
        for (String columnName : dbDataDict.dict.get(tableName).columns.keySet()) {
            System.out.printf("|%-20s|", columnName);
        }
        System.out.println();
        System.out.println("-----------------------------------------------------------------");
        for (dbSlottedPage slottedPage : db.get(tableName).pages) {
            // 페이지에서 슬롯을 먼저 읽어옴
            String page = new String(slottedPage.page, StandardCharsets.UTF_8);
            String[] slots=page.substring(1, slottedPage.freeSpaceStartIdx).split(" ");

            if(dbDataDict.dict.get(tableName).tableRecordCount!=0){
                // Slot 정보를 사용하여 레코드 출력
                for (int i=0; i<slottedPage.recordCount; i++){
                    // Slot 추출
                    String slot=page.substring(1, slottedPage.freeSpaceStartIdx);
                    byte[] tmp=slot.getBytes(StandardCharsets.UTF_8);
                    String record=page.substring(tmp[i]);
                    // Offset과 length 추출
                    String[] columnInfoPart=record.split(" ");
                    // Offset과 length로 데이터 출력
                    String[] columnInfo=columnInfoPart[columnOrder].split(",");
                    if(record.substring(Integer.parseInt(columnInfo[0]), Integer.parseInt(columnInfo[0])+Integer.parseInt(columnInfo[1])).equals(pkValue)){
                        System.out.printf("|%-20s|", record.substring(Integer.parseInt(columnInfo[0]), Integer.parseInt(columnInfo[0])+Integer.parseInt(columnInfo[1])));
                    }else{
                        System.out.println("일치하는 레코드가 없습니다.");
                    }
                    System.out.println();
                }
            } else {
                System.out.println("테이블 " + tableName + "에는 아직 레코드가 없습니다.");
                System.out.println();
            }
        }
        System.out.println("-----------------------------------------------------------------");

    }

    // Column search
    public void columnSearch(String tableName, String columnName){
        try{
            // 컬럼 메타데이터에서 찾고자 하는 컬럼의 인덱스 찾기
            List<String> columnOrderList=new ArrayList<String>(dbDataDict.dict.get(tableName).columns.keySet());
            int columnOrder=columnOrderList.indexOf(columnName);

            // 메타 데이터로 컬럼 순회
            for(String c : dbDataDict.dict.get(tableName).columns.keySet()){
                // 입력받은 컬럼 명과 같다면
                if (dbDataDict.dict.get(tableName).integrityConstraints.get(c).equals(columnName)){
                    System.out.println("-----------------------------------------------------------------");
                    System.out.println(columnName+" "+dbDataDict.dict.get(tableName).columns.get(columnName));
                    System.out.println("-----------------------------------------------------------------");
                    for (dbSlottedPage slottedPage : db.get(tableName).pages) {
                        // 페이지에서 슬롯을 먼저 읽어옴
                        String page = new String(slottedPage.page, StandardCharsets.UTF_8);
                        String[] slots=page.substring(1, slottedPage.freeSpaceStartIdx).split(" ");

                        if(dbDataDict.dict.get(tableName).tableRecordCount!=0){
                            // Slot 정보를 사용하여 레코드 출력
                            for (int i=0; i<slottedPage.recordCount; i++){
                                // Slot 추출
                                String slot=page.substring(1, slottedPage.freeSpaceStartIdx);
                                byte[] tmp=slot.getBytes(StandardCharsets.UTF_8);
                                String record=page.substring(tmp[i]);
                                // Offset과 length 추출
                                String[] columnInfoPart=record.split(" ");
                                // Offset과 length로 데이터 출력
                                String[] columnInfo=columnInfoPart[columnOrder].split(",");
                                if(columnInfo[1].equals("0")){
                                    System.out.printf("|%-20s|", "NULL");
                                }else{
                                    System.out.printf("|%-20s|", record.substring(Integer.parseInt(columnInfo[0]), Integer.parseInt(columnInfo[0])+Integer.parseInt(columnInfo[1])));
                                }
                                System.out.println();
                            }
                        } else {
                            System.out.println("테이블 " + tableName + "의 컬럼 "+columnName+"에 해당하는 데이터가 없습니다.");
                            System.out.println();
                        }
                    }
                    System.out.println("-----------------------------------------------------------------");
                }
            }
        }catch (IllegalStateException e){
            System.out.println(tableName+"에는 해당하는 컬럼이 없습니다.");
        }
    }

    // DB Write
    public void writeDB() throws IOException{
       try {
           ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("myDB.hjdb"));
           objectOutputStream.writeObject(db);
           objectOutputStream.writeObject(dbDataDict);
           objectOutputStream.close();
       }catch(IOException e){

       }
    }

    // DB Read
    public void readDB() throws IOException{
        File file=new File("myDB.hjdb");
        if(!file.exists()){
            // DB가 존재하지 않을 경우 파일 생성
            writeDB();
        }
        try{
            ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream("myDB.hjdb"));
            db=(HashMap<String, dbTable>)objectInputStream.readObject();
            dbDataDict=(dbDataDict) objectInputStream.readObject();
        }catch(ClassNotFoundException e){

        }
    }

    public void showAllRecords(String tableName) {
        // 레코드 삽입 결과를 테이블 전체 출력을 통해 확인
        System.out.println("테이블 " + tableName + "에 총 " + dbDataDict.dict.get(tableName).tableRecordCount + "개의 레코드가 있습니다.");
        System.out.println("-----------------------------------------------------------------");
        for (String columnName : dbDataDict.dict.get(tableName).columns.keySet()) {
            System.out.printf("|%-20s|", columnName);
        }
        System.out.println();
        System.out.println("-----------------------------------------------------------------");
        for (dbSlottedPage slottedPage : db.get(tableName).pages) {
            // 페이지에서 슬롯을 먼저 읽어옴
            String page = new String(slottedPage.page, StandardCharsets.UTF_8);
            String[] slots=page.substring(1, slottedPage.freeSpaceStartIdx).split(" ");

            if(dbDataDict.dict.get(tableName).tableRecordCount!=0){
                // Slot 정보를 사용하여 레코드 출력
                for (int i=0; i<slottedPage.recordCount; i++){
                    // Slot 추출
                    String slot=page.substring(1, slottedPage.freeSpaceStartIdx);
                    byte[] tmp=slot.getBytes(StandardCharsets.UTF_8);
                    String record=page.substring(tmp[i]);
                    // Offset과 length 추출
                    String[] columnInfoPart=record.split(" ");
                    for(int j=0; j< dbDataDict.dict.get(tableName).columns.size(); j++){
                        // Offset과 length로 데이터 출력
                        String[] columnInfo=columnInfoPart[j].split(",");
                        if(columnInfo[1].equals("0")){
                            System.out.printf("|%-20s|", "NULL");
                        }else{
                            System.out.printf("|%-20s|", record.substring(Integer.parseInt(columnInfo[0]), Integer.parseInt(columnInfo[0])+Integer.parseInt(columnInfo[1])));
                        }
                    }
                    System.out.println();
                }
            } else {
                System.out.println("테이블 " + tableName + "에는 아직 레코드가 없습니다.");
                System.out.println();
            }
        }
        System.out.println("-----------------------------------------------------------------");
    }

    public void showAllTables(){
        for (String tableName : db.keySet()){
            showAllRecords(tableName);
        }
    }

}
