import java.util.HashMap;
import java.util.Scanner;

public class dbMain {
    public static void main(String[] args){
        HashMap<String, dbTable> db=new HashMap<>();
        dbAPI dbAPI=new dbAPI();
        dbDataDict dbDataDict=new dbDataDict();

        Scanner scanner=new Scanner(System.in);
        int selection;
        while (true) {
            System.out.println("------- 기능 선택 -------");
            System.out.println("1. 테이블 생성");
            System.out.println("2. 레코드 삽입");
            System.out.println("3. 레코드 검색");
            System.out.println("4. 컬럼 검색");
            System.out.println("5. 종료");
            System.out.print("원하는 기능의 번호 입력 : ");

            selection=scanner.nextInt();

            switch(selection){
                case 1:
                    System.out.println("------- 테이블 생성 -------");
                    System.out.print("테이블 명 : ");
                    String tableName=scanner.nextLine();
                    System.out.print("컬럼 개수 : ");
                    int columnsCount=scanner.nextInt();
                    String[] columns=new String[columnsCount];
                    for (int i = 0; i<columnsCount; i++){
                        System.out.printf("%d번째 컬럼 명과 자료형을 하나씩 입력하세요. ex) name varchar 10", i+1);
                        columns[i]=scanner.nextLine();
                    }
                    // db에 테이블 생성
                    db.put(tableName, dbAPI.createTable(tableName, columns, columnsCount));
                    System.out.print("테이블 "+tableName+"이 생성되었습니다.");
                    break;
                case 2:
                    System.out.println("------- 레코드 삽입 -------");
                    System.out.print("레코드를 삽입할 테이블명 입력 : ");
                    tableName=scanner.nextLine();
                    // 컬럼 마다 컬럼 이름, 자료형, 최대 길이 출력해주고 입력받기
                    dbMetaData metaData=dbDataDict.dict.get(tableName);
                    String[] columnsInput=new String[metaData.columns.size()];
                    int idx=0;
                    for (Object key : metaData.columns.keySet()){
                        System.out.print("컬럼 " + key + "("+metaData.columnDataType.get(key)+") 의 데이터 입력");
                        String data=scanner.nextLine();
                        columnsInput[idx]=data;
                        idx++;
                    }
                    // dbAPI로 넘기기
                    dbAPI.insertRecord(db.get(tableName), tableName, columnsInput,dbDataDict.dict.get(tableName));

                    break;
                case 3:
                    System.out.println("------- 레코드 검색 -------");
                    break;
                case 4:
                    System.out.println("------- 컬럼 검색 -------");
                    break;
                case 5:
                    System.out.println("종료 합니다.");
                    System.exit(0);
            }
        }
    }
}
