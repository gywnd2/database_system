import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class dbMain {
    public static void main(String[] args) throws IOException {
        // API 객체 생성
        dbAPI dbAPI=new dbAPI();
        boolean isInvalidInput=false;

        // DB 읽어오기
        dbAPI.readDB();

        Scanner scanner=new Scanner(System.in);
        int selection=0;
        while (true) {
            System.out.println("------- 기능 선택 -------");
            System.out.println("1. 테이블 생성");
            System.out.println("2. 레코드 삽입");
            System.out.println("3. 레코드 검색");
            System.out.println("4. 컬럼 검색");
            System.out.println("5. 종료");
            System.out.print("원하는 기능의 번호 입력 : ");

            try{
                selection=scanner.nextInt();
                // nextInt 다음에는 nextLine 해주어 flush
                scanner.nextLine();
            }catch (InputMismatchException e){
                System.out.println("1~5 사이의 숫자를 입력하세요.");
                isInvalidInput=true;
                scanner.nextLine();
            }

            switch(selection){
                case 1:
                    System.out.println("------- 테이블 생성 -------");
                    System.out.print("테이블 명 : ");
                    String tableName;
                    while(true){
                        tableName=scanner.nextLine();
                        try {
                            dbAPI.db.get(tableName);

                            System.out.print("컬럼 개수 : ");
                            int columnsCount=scanner.nextInt();
                            scanner.nextLine();
                            String[] columns=new String[columnsCount];
                            for (int i = 0; i<columnsCount; i++){
                                System.out.printf("%d번째 컬럼 명과 자료형을 하나씩 입력하세요. ex) name varchar(20) : ", i+1);
                                columns[i]=scanner.nextLine();
                            }
                            // PK 지정
                            System.out.print("PK로 지정할 컬럼명 : ");
                            String pk=scanner.nextLine();

                            // db에 테이블 생성
                            dbAPI.createTable(tableName, columns, columnsCount, pk);
                            System.out.println("테이블 "+tableName+"이 생성되었습니다.");

                            // 테이블 내용 출력
                            dbAPI.showAllRecords(tableName);

                            // 디스크에 쓰기
                            dbAPI.writeDB();

                            break;
                        } catch (IllegalStateException e){
                            System.out.print(tableName+" 은(는) 이미 존재합니다. 다른 이름을 입력하세요.");
                        }
                    }
                    break;
                case 2:
                    System.out.println("------- 레코드 삽입 -------");
                    System.out.print("레코드를 삽입할 테이블명 입력 : ");
                    while(true){
                        tableName=scanner.nextLine();
                        try{
                            // 테이블이 존재하는지 조회 시도
                            dbAPI.db.get(tableName);

                            // 컬럼 마다 컬럼 이름, 자료형, 최대 길이 출력해주고 입력받기
                            HashMap<String, String> columnsInput=new HashMap<>();
                            int idx=0;

                            // 컬럼; 자료형에 맞게 입력 받기
                            for (Object key : dbAPI.dbDataDict.dict.get(tableName).columns.keySet()){
                                System.out.println("컬럼 " + key + "("+dbAPI.dbDataDict.dict.get(tableName).columns.get(key)+") 의 데이터 입력");
                                    // 입력 조건에 맞는지 확인
                                    while(true){
                                        String data=scanner.nextLine();
                                        if(data.length()>dbAPI.dbDataDict.dict.get(tableName).columnInputLength.get(key)){
                                            System.out.print(dbAPI.dbDataDict.dict.get(tableName).columnInputLength.get(key)+" 이하의 데이터를 입력하세요. : ");
                                        }else{
                                            // 컬럼 정보를 입력 받는 HashMap에 삽입
                                            columnsInput.put(key.toString(), data);
                                            idx++;
                                            break;
                                        }
                                    }
                                }
                            // dbAPI로 입력 넘기기
                            if(dbAPI.insertRecord(tableName, columnsInput)){
                                // 디스크에 쓰기
                                dbAPI.writeDB();
                                // 테이블 내용 출력
                                dbAPI.showAllRecords(tableName);
                            }
                            break;

                        }catch(IllegalStateException e){
                            System.out.print(tableName + "은(는) 존재하지 않습니다. 다른 이름을 입력하세요. : ");
                        }
                    }

                    break;
                case 3:
                    System.out.println("------- 레코드 검색 -------");
                    System.out.print("레코드를 검색할 테이블명 입력 : ");
                    while(true){
                        tableName=scanner.nextLine();
                        try{
                            // 테이블이 존재하는지 조회 시도
                            dbAPI.db.get(tableName);
                            break;
                        }catch(NullPointerException e){
                            System.out.print(tableName + "은(는) 존재하지 않는 테이블입니다. 다른 이름을 입력하세요. : ");
                        }
                    }

                    // PK 값 입력 받아서 검색
                    System.out.print("PK값 입력 : ");
                    String pk=scanner.nextLine();
                    dbAPI.searchRecord(tableName, pk);

                    break;
                case 4:
                    System.out.println("------- 컬럼 검색 -------");
                    System.out.print("검색할 컬럼명 입력 : ");
                    while(true){
                        tableName=scanner.nextLine();
                        try{
                            // 테이블이 존재하는지 조회 시도
                            dbAPI.db.get(tableName);

                            // 컬럼 이름 입력받아 검색
                            System.out.print("컬럼 이름 입력 : ");
                            while(true){
                                String columnName=scanner.nextLine();
                                try{
                                    dbAPI.dbDataDict.dict.get(tableName).columns.get(columnName);
                                    dbAPI.columnSearch(tableName, columnName);
                                    break;
                                }catch(IllegalStateException e){
                                    System.out.print(columnName + "은(는) 존재하지 않습니다. 다른 이름을 입력하세요. : ");
                                }
                            }
                            break;
                        }catch(IllegalStateException e){
                            System.out.print(tableName + "은(는) 존재하지 않습니다. 다른 이름을 입력하세요. : ");
                        }
                    }

                    break;
                case 5:
                    // 종료 시 기록
                    dbAPI.writeDB();
                    // 테이블 내용 출력
                    dbAPI.readDB();
                    dbAPI.showAllTables();
                    System.out.println("종료 합니다.");
                    System.exit(0);
                default:
                    if(isInvalidInput){
                        isInvalidInput=false;
                    }else{
                        System.out.println("1~5 사이의 숫자를 입력하세요.");
                        break;
                    }
            }
        }
    }
}
