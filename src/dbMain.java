import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class dbMain {
    public static void main(String[] args) throws IOException {
        HashMap<String, dbTable> db=new HashMap<>();
        dbAPI dbAPI=new dbAPI();
        dbDataDict dataDict=new dbDataDict();
        boolean isInvalidInput=false;

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
                    String tableName=scanner.nextLine();
                    System.out.print("컬럼 개수 : ");
                    int columnsCount=scanner.nextInt();
                    scanner.nextLine();
                    String[] columns=new String[columnsCount];
                    for (int i = 0; i<columnsCount; i++){
                        System.out.printf("%d번째 컬럼 명과 자료형을 하나씩 입력하세요. ex) name varchar(20) : ", i+1);
                        columns[i]=scanner.nextLine();
                    }
                    // db에 테이블 생성
                    db.put(tableName, dbAPI.createTable(tableName, columns, columnsCount, dataDict));
                    System.out.println("테이블 "+tableName+"이 생성되었습니다.");

                    // 디스크에 쓰기
                    dbAPI.writeDB(db, dataDict);
                    break;
                case 2:
                    System.out.println("------- 레코드 삽입 -------");
                    System.out.print("레코드를 삽입할 테이블명 입력 : ");
                    tableName=scanner.nextLine();
                    // 컬럼 마다 컬럼 이름, 자료형, 최대 길이 출력해주고 입력받기
                    // 테이블 이름에 해당하는 메타데이터 가져오기
                    dbMetaData metaData=dataDict.dict.get(tableName);
                    HashMap<String, String> columnsInput=new HashMap<>();
                    int idx=0;
                    // 컬럼; 자료형에 맞게 입력 받기
                    for (Object key : metaData.columns.keySet()){
                        System.out.println("컬럼 " + key + "("+metaData.columns.get(key)+") 의 데이터 입력");
                        String data=scanner.nextLine();
                        // 컬럼 정보를 입력 받는 HashMap에 삽입
                        columnsInput.put(key.toString(), data);
                        idx++;
                    }
                    // dbAPI로 입력 넘기기
                    if(dbAPI.insertRecord(db.get(tableName), tableName, columnsInput,dataDict.dict.get(tableName))){
                        // 디스크에 쓰기
                        dbAPI.writeDB(db, dataDict);
                    }
                    break;
                case 3:
                    System.out.println("------- 레코드 검색 -------");
                    break;
                case 4:
                    System.out.println("------- 컬럼 검색 -------");
                    break;
                case 5:
                    // 종료 시 기록
                    dbAPI.writeDB(db, dataDict);
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
