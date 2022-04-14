import java.util.Scanner;

public class dbMain {
    public static void main(String[] args){
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
                    break;
                case 2:
                    System.out.println("------- 레코드 삽입 -------");
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
