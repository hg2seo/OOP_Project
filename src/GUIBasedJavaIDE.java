import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import static java.awt.Color.BLUE;
import static java.awt.Color.ORANGE;

public class GUIBasedJavaIDE extends JFrame {
  private JTabbedPane editingWindow;
  private final JTextArea resultWindow;

  public GUIBasedJavaIDE() {
    // GUI 기본 설정
    setTitle("My Java IDE GUI");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    createMenu(); // 메뉴 생성, 프레임 삽입
    setSize(815, 765);
    Container c = getContentPane();
    c.setLayout(null); // 레이아웃 매니저 비활성화
    // JTabbedPane 생성(EditingWindow)
    editingWindow = new JTabbedPane();
    editingWindow.setBounds(0, 0, 800, 500);
    c.add(editingWindow);
    // Result Window 생성 후 배치
    resultWindow = new JTextArea();
    resultWindow.setEditable(false);
    JScrollPane resultScrollPane = new JScrollPane(resultWindow);
    resultScrollPane.setBounds(0,505, 800, 200); // 위치, 크기 설정
    resultScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS); // 수평 스크롤바 항상 표시
    resultScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // 수직 스크롤바 항상 표시
    c.add(resultScrollPane);
    setVisible(true);
  }
  // 메뉴 생성 메서드
  private void createMenu() {
    // 메뉴바
    JMenuBar menuBar = new JMenuBar();
    menuBar.setBackground(ORANGE);
    // File 메뉴
    JMenu fileMenu = new JMenu("File");
    JMenuItem fileItem1 = new JMenuItem("Open");
    fileItem1.addActionListener(e -> openFile()); // openFile() 메소드 연결
    JMenuItem fileItem2 = new JMenuItem("Close");
    JMenuItem fileItem3 = new JMenuItem("Save");
    JMenuItem fileItem4 = new JMenuItem("Save As");
    JMenuItem fileItem5 = new JMenuItem("Quit");
    fileItem1.setBackground(new Color(30,144,255));
    fileItem2.setBackground(new Color(30,144,255));
    fileItem3.setBackground(new Color(30,144,255));
    fileItem4.setBackground(new Color(30,144,255));
    fileItem5.setBackground(new Color(30,144,255));
    fileMenu.add(fileItem1);
    fileMenu.add(fileItem2);
    fileMenu.add(fileItem3);
    fileMenu.add(fileItem4);
    fileMenu.add(fileItem5);
    fileMenu.setBackground(BLUE);
    // Run 메뉴
    JMenu runMenu = new JMenu("Run");
    JMenuItem runItem = new JMenuItem("Compile");
    runItem.setBackground(new Color(30,144,255));
    runMenu.add(runItem);
    menuBar.add(fileMenu);
    menuBar.add(runMenu);

    setJMenuBar(menuBar);
  }
  // 파일 열기 메서드
  private void openFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Java Files", "java"));
    int result = fileChooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      try {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line).append("\n");
        }
        reader.close();

        // 새로운 탭에 파일 추가
        JTextArea newTabTextArea = new JTextArea(content.toString());
        JScrollPane newScrollPane = new JScrollPane(newTabTextArea);
        editingWindow.addTab(file.getName(), newScrollPane);
        editingWindow.setSelectedComponent(newScrollPane); // 새로 열린 파일로 포커스 이동

      } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
// 기존에 활용했던 리스너
/*
  // 파일 불러오기 버튼의 이벤트 처리 리스너
  private class OpenListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      // 파일 이름 읽어오기
      String openName = fileOpenField.getText();
      File file = new File("javafile/" + openName);
      // 파일 존재 여부 및 확장자 검사
      if (file.exists() && openName.endsWith(".java")) {
        try {
          // 자바 파일을 읽어들일 입력 버퍼 생성
          BufferedReader reader = new BufferedReader(new FileReader(file));

          // 자바 파일 내용을 Editing Window 에 출력
          String stream;
          editingWindow.setText("");
          while ((stream = reader.readLine()) != null) editingWindow.append(stream + "\n");

          // 버퍼 닫기, 파일 입력 필드 초기화, 실행 결과 표시, 파일 이름 저장, 에러 내용 초기화, 프로그램 타이틀 변경
          reader.close();
          fileOpenField.setText("");
          resultWindow.setText("Java file loaded successfully.");
          fName = openName;
          errorContent = null;
          setTitle("\"" + openName + "\" - My Java IDE GUI");
        } catch (IOException error) {
          resultWindow.setText("Error: Failed to read the java file!");
        }
      }
      else {
        resultWindow.setText("Error: Invalid file!");
      }
    }
  }

  // 파일 저장 버튼의 이벤트 처리 리스너
  private class SaveListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      // 덮어쓰기 플래그
      boolean overwrite = false;
      // 파일 저장 이름 가져오기
      String saveFileName = fileSaveField.getText().strip(); // 저장할 파일 이름 가져오기 (앞뒤 공백 제거)

      // 저장할 파일 이름이 제공되지 않은 경우, 현재 열려 있는 파일에 덮어쓰기
      if (saveFileName.isEmpty()) {
        if (fName == null) { // 현재 열려 있는 파일이 없으면 에러 메시지 출력
          resultWindow.setText("Error: No file to overwrite! Please open a file to overwrite or enter a filename.");
          return;
        }
        saveFileName = fName; // 저장할 파일 이름을 현재 열려 있는 파일 이름으로 설정
        overwrite = true; // 덮어쓰기 플래그 활성화
      }
      // 저장할 파일 이름이 제공되었으나 확장자가 .java 가 아니라면 에러 메시지 출력
      else if (!saveFileName.endsWith(".java")) {
        resultWindow.setText("Error: Invalid filename extension! Please enter a filename which ends with '.java'.");
        return;
      }

      // 저장할 파일 생성
      File file = new File("javafile/" + saveFileName);

      // 새로운 파일을 생성하거나 현재 열려있는 파일의 변경 사항을 저장(덮어쓰기)하는 경우
      if (!file.exists() || overwrite) {
        try {
          // 파일에 내용을 저장하기 위한 출력 버퍼 생성
          BufferedWriter writer = new BufferedWriter(new FileWriter(file));
          writer.write(editingWindow.getText()); // Editing Window 의 텍스트를 파일에 저장
          writer.flush(); // 버퍼 비우기
          writer.close(); // 버퍼 닫기

          // 성공 메시지 출력 및 필드 초기화
          resultWindow.setText("File saved successfully: " + saveFileName);
          fileSaveField.setText(""); // 파일 저장 필드 초기화
          fName = saveFileName; // 열려있는 파일 교체
          errorContent = null; // 에러 내용 초기화
          setTitle("\"" + saveFileName + "\" - My Java IDE GUI"); // 타이틀 업데이트
        } catch (IOException error) {
          resultWindow.setText("Error: Failed to save the file! Check if filename contains invalid characters.");
        }
      }
      // 동일한 이름의 파일이 이미 존재하는데 파일을 생성하려 경우 에러 메시지 출력
      else
        resultWindow.setText("Error: File already exists!\n" +
                "    1) If you want to create a new file, please enter a not existing filename.\n" +
                "    2) If you want to overwrite a file, please open the file first and empty name field before saving.");
    }
  }

  // 불러온 파일 컴파일 버튼의 이벤트 처리 리스너
  private class CompileListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      // 파일 불러오기 여부 검사
      if (fName == null)
        resultWindow.setText("Error: No java file to compile selected!");
      else {
        try {
          // 실행 경로와 파일명을 주고 javac(자바 컴파일러) 외부 명령어 실행을 통해 컴파일 수행
          Process oProcess = new ProcessBuilder("javac", "javafile/" + fName).start();

          // 컴파일 시 프로세스의 표준 오류 출력을 받아들일 입력 버퍼 생성
          BufferedReader stdError = new BufferedReader(new InputStreamReader(oProcess.getErrorStream()));

          // 오류 플래그와 오류의 내용을 저장할 공간 생성 및 오류 출력, 저장 수행
          boolean hasError = false;
          String stream;
          errorContent = new StringBuilder();
          resultWindow.setText("");
          while ((stream = stdError.readLine()) != null) {
            hasError = true; // 오류가 발생했으므로 플래그를 true 로 전환
            errorContent.append(stream).append("\n"); // 내용 한줄씩 저장
            resultWindow.append(stream + "\n"); // 한줄씩 출력
          }

          // 컴파일 정상 완료 시 출력
          if (!hasError)
            resultWindow.setText("Compiled successfully: " + fName.replace(".java", ".class"));

          // 버퍼 닫기
          stdError.close();
        } catch (IOException error) {
          resultWindow.setText("Error: Compilation failed!");
        }
      }
    }
  }

  private class SaveErrorsListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      // 저장할 오류 내용의 존재 여부 확인
      if (errorContent == null || errorContent.length() == 0)
        resultWindow.setText("Error: No error file to save exists!");
      else {
        try {
          // 오류 파일을 log 폴더에 생성하고 저장할 출력 버퍼 생성
          BufferedWriter writer = new BufferedWriter(new FileWriter("log/" + fName.concat(".error")));
          // errorContent 객체에 저장된 오류 내용을 String 으로 변환하여 저장
          writer.write(errorContent.toString());
          // 입력 버퍼 비우고 닫기, 실행 결과 출력 및 errorContent 초기화
          writer.flush();
          writer.close();
          resultWindow.append("\n\n-------------------------------------------------------------------\n\n"
                  + "saved error file successfully... - " + fName + ".error");
          errorContent = null;
        } catch (IOException error) {
          resultWindow.setText("Error: Failed to save error file!");
        }
      }
    }
  }

  // 삭제 버튼의 이벤트 처리 리스너
  private class DeleteListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      // 파일 불러오기 여부 검사
      if (fName == null)
        resultWindow.setText("Error: No java file to delete selected!");
      else {
        // editingWindow, resultWindow, errorContent 초기화
        editingWindow.setText("");
        resultWindow.setText("");
        errorContent = null;
        // javafile 폴더에서 이름으로 fileName 을 갖는 클래스 파일에 대한 객체를 생성
        File classFile = new File("javafile/" + fName.replace(".java", ".class"));
        // 클래스 파일 존재 시 삭제
        if (classFile.exists()) {
          classFile.delete();
          resultWindow.append("Class file deleted.\n");
        }

        // javafile 폴더에서 소스가 된 자바 파일에 대한 객체를 생성
        File javaFile = new File("javafile/" + fName);
        // 자바 파일 삭제
        javaFile.delete();
        resultWindow.append("Java file deleted.\n\n");

        // 실행 결과 출력 및 프로그램 타이틀 변경, 파일 이름 초기화
        resultWindow.append("Deletion completed.");
        fName = null;
        setTitle("My Java IDE GUI");
      }
    }
  }

  // 클리어 버튼의 이벤트 처리 리스너
  private class ClearListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      // 필드, 윈도우, 파일 이름, 에러 내용 전부 초기화, 프로그램 타이틀 변경
      fileOpenField.setText("");
      fileSaveField.setText("");
      editingWindow.setText("");
      resultWindow.setText("");
      fName = null;
      errorContent = null;
      setTitle("My Java IDE GUI");
    }
  }
*/
  public static void main(String[] args) {
    new GUIBasedJavaIDE();
  }
}
