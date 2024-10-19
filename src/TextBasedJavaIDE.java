import java.io.*;
import java.util.Scanner;

public class TextBasedJavaIDE {
  public static void main(String[] args) {
    // 스캐너 생성, 파일이름 저장 변수 설정
    Scanner scanner = new Scanner(System.in);
    String fName = null;

    // 프로그램 반복실행
    while (true) {
      mainMenu();
      String choice = scanner.nextLine();

      switch (choice) {
        // 파일 이름 입력
        case "1":
          fName = uploadJavaFile(scanner);
          break;

        // 자바 파일 컴파일
        case "2":
          if (fName == null)
            System.out.println("Error: No Java file uploaded!\n");
          else
            compileJavaFile(fName);
          break;

        // 컴파일된 자바 파일 실행
        case "3":
          // 파일 이름이 입력되지 않았거나 javafile 폴더 내에 컴파일된 class 파일이 존재하지 않는 경우
          if (fName == null || !new File("javafile/" + fName.replace(".java", ".class")).exists())
            System.out.println("Error: No compiled file found!\n");
          else
            run(fName);
          break;

        // 프로그램 상태 초기화, 파일 삭제
        case "4":
          reset(fName);
          fName = null;
          break;

        // 컴파일 오류 로그 출력
        case "5":
          compileErrorFile(scanner);
          break;

        // 프로그램 종료
        case "0":
          System.out.println("Exiting the program.");
          scanner.close();
          return;

        // 위 case 외의 choice 처리
        default:
          System.out.println("Invalid choice!\n");
      }
    }
  }

  // 메뉴 선택지 출력 함수
  private static void mainMenu() {
    System.out.println("##########################");
    System.out.println("1. Java File Upload");
    System.out.println("2. Compile");
    System.out.println("3. Run");
    System.out.println("4. Reset");
    System.out.println("5. Compile Error File");
    System.out.println("0. Exit");
    System.out.print("Choice: ");
  }

  //자바 파일 이름 입력 처리 함수
  private static String uploadJavaFile(Scanner scanner) {
    // 파일 이름 입력 받아서 저장
    System.out.println("\n##########################");
    System.out.print("Type Java Filename: ");
    String fileName = scanner.nextLine();

    // javafile 폴더에서 이름으로 fileName을 갖는 자바 파일에 대한 객체를 생성
    File file = new File("javafile/" + fileName);
    // 파일 존재 여부 및 확장자 명 확인
    if (file.exists() && fileName.endsWith(".java")) {
      System.out.println();
      return fileName;
    }
    else {
      System.out.println("Error: invalid file!\n");
      return null;
    }
  }

  // 자바 파일 컴파일 실행 함수
  private static void compileJavaFile(String fileName) {
    try {
      // 실행 경로와 파일명을 주고 javac(자바 컴파일러) 외부 명령어 실행을 통해 컴파일 수행
      Process oProcess = new ProcessBuilder("javac", "javafile/" + fileName).start();

      // 컴파일 시 프로세스의 표준 오류 출력을 받아들일 입력 버퍼 생성
      BufferedReader stdError = new BufferedReader(new InputStreamReader(oProcess.getErrorStream()));

      // 오류 플래그와 오류의 내용을 저장할 공간 생성 및 오류 저장 수행
      boolean hasError = false;
      String stream;
      StringBuilder errorContent = new StringBuilder();
      while ((stream = stdError.readLine()) != null) {
        hasError = true; // 오류가 발생했으므로 플래그를 true로 전환
        errorContent.append(stream).append("\n"); // 내용 한줄씩 저장
      }

      // 오류 발생 시
      if (hasError) {
        int errorNums = errorContent.lastIndexOf("error") - 2; // 발생한 오류의 수가 저장되어 있는 공간의 인덱스 검색
        System.out.println("\n##########################");
        // 오류의 수 및 저장된 오류 로그 파일의 이름 출력
        System.out.println(errorContent.charAt(errorNums) + " compile errors occurred – " + fileName + ".error\n");
        // 오류 로그 파일 생성 및 저장
        saveErrorFile(fileName, errorContent.toString());
      }
      // 정상적으로 컴파일 완료
      else {
        System.out.println("\n##########################");
        System.out.println("compiled successfully...\n");
      }

      // 버퍼 닫기
      stdError.close();
    } catch (IOException e) {
      System.out.println("Error: compilation failed!\n");
    }
  }

  // 오류 파일 저장 처리 함수
  private static void saveErrorFile(String fileName, String errorContent) {
    try {
      // 오류 파일을 log 폴더에 생성하고 저장할 출력 버퍼 생성
      BufferedWriter writer = new BufferedWriter(new FileWriter("log/" + fileName.concat( ".error")));
      // 오류 내용을 main() 내의 입력 버퍼로부터 String 형태로 넘겨받아 출력 버퍼로 작성
      writer.write(errorContent);
      // 입력 버퍼 비우고 닫기
      writer.flush();
      writer.close();
    } catch (IOException e) {
      System.out.println("Error: failed to save error file!\n");
    }
  }

  // 컴파일된 java 파일 실행 함수
  private static void run(String fileName) {
    try {
      // 실행 경로와 파일명을 주고 java 외부 명령어 실행을 통해 클래스 파일 실행 수행
      Process oProcess = new ProcessBuilder("java", "-cp", "javafile", fileName.replace(".java", "")).start();

      // 컴파일 된 파일 실행 시 프로세스의 표준 출력과 표준 오류 출력을 받아들일 입력 버퍼 생성
      BufferedReader stdOut = new BufferedReader(new InputStreamReader(oProcess.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(oProcess.getErrorStream()));

      // 표준 출력과 표준 오류 출력을 프로그램에 출력
      String stream;
      while ((stream = stdOut.readLine()) != null) System.out.println(stream);
      while ((stream = stdError.readLine()) != null) System.out.println(stream);
      // 프로세스의 종료 코드 출력
      System.out.println("\nExit Code: " + oProcess.exitValue() + "\n");

      // 버퍼 닫기
      stdOut.close();
      stdError.close();
    } catch (IOException e) {
      System.out.println("Error: failed to run the java program!\n");
    }
  }

  // 입력된 파일 및 오류 파일 리셋(삭제) 함수
  private static void reset(String fileName) {
    if (fileName != null) {
      // javafile 폴더에서 이름으로 fileName을 갖는 클래스 파일에 대한 객체를 생성
      File classFile = new File("javafile/" + fileName.replace(".java", ".class"));
      // 클래스 파일 존재 시 삭제
      if (classFile.exists()) {
        classFile.delete();
        System.out.println("Compile class file deleted.");
      }

      // log 폴더에서 이름으로 fileName을 갖는 오류 파일에 대한 객체를 생성
      File errorFile = new File("log/" + fileName.concat(".error"));
      // 오류 파일 존재 시 삭제
      if (errorFile.exists()) {
        errorFile.delete();
        System.out.println("Compile error file deleted.");
      }

      System.out.println("Reset completed.\n");
    }
    else
      System.out.println("No files to reset.\n");
  }

  // 컴파일 오류 내용 출력 함수
  private static void compileErrorFile(Scanner scanner) {
    System.out.println("\n##########################");
    System.out.print("Type Error Filename: ");
    // log 폴더에서 사용자가 입력한 이름을 갖는 오류 파일에 대한 객체 생성
    File errorFile = new File("log/" + scanner.nextLine());

    // 오류 파일 존재 시
    if (errorFile.exists()) {
      try {
        // 오류 파일을 읽어들일 입력 버퍼 생성
        BufferedReader reader = new BufferedReader(new FileReader(errorFile));

        // 오류 파일 내용을 프로그램에 출력
        String stream;
        System.out.println();
        while ((stream = reader.readLine()) != null) System.out.println(stream);
        System.out.println();

        // 버퍼 닫기
        reader.close();
      } catch (IOException e) {
        System.out.println("Error: Failed to read the error file!\n");
      }
    }
    else
      System.out.println("Error: No compile error file found!\n");
  }
}
