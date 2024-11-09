import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class GUIBasedJavaIDE extends JFrame {
  private JTextField fileOpenField;
  private JTextField fileSaveField;
  private JTextArea editingWindow;
  private JTextArea resultWindow;
  private JButton openBtn, saveBtn, compileBtn, saveErrorsBtn, deleteBtn, clearBtn;
  private String fName = null;
  private StringBuilder errorContent = null;

  public GUIBasedJavaIDE() {
    // GUI 기본 설정
    setTitle("My Java IDE GUI");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(600, 500);
    setLayout(new BorderLayout());

    // 그리드백 레이아웃을 탑재한 컨텐트펜 생성
    JPanel contentPane = new JPanel();
    contentPane.setLayout(new GridBagLayout());
    // 그리드백 레이아웃 내 컴포넌트 배치 설정을 관리하는 객체 생성, 여백 설정
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    // 파일 불러오기를 위한 텍스트필드의 위치 및 너비 설정
    fileOpenField = new JTextField(40);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 5;
    contentPane.add(fileOpenField, gbc);

    // 불러오기 버튼의 위치 및 너비, 이벤트 처리 리스너 설정
    openBtn = new JButton("Open");
    openBtn.addActionListener(new OpenListener());
    gbc.gridx = 5;
    gbc.gridwidth = 1;
    contentPane.add(openBtn, gbc);

    // 파일 저장하기를 위한 텍스트필드의 위치 및 너비 설정
    fileSaveField = new JTextField(40);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 5;
    contentPane.add(fileSaveField, gbc);

    // 저장하기 버튼의 위치 및 너비, 이벤트 처리 리스너 설정
    saveBtn = new JButton("Save");
    saveBtn.addActionListener(new SaveListener());
    gbc.gridx = 5;
    gbc.gridwidth = 1;
    contentPane.add(saveBtn, gbc);

    // 코드 편집을 위한 Editing window 텍스트 에리어의 스크롤, 위치, 너비, 높이비율, 공간 채우기 설정
    editingWindow = new JTextArea();
    JScrollPane editingScrollPane = new JScrollPane(editingWindow);
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 6;
    gbc.weighty = 8;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(editingScrollPane, gbc);

    // 4개 버튼 배치를 위한 패널 생성
    JPanel buttonPannel = new JPanel(new GridLayout(1, 4, 5, 5));

    // 컴파일 버튼, 에러 파일 저장 버튼, 자바 파일 삭제 버튼, 클리어 버튼 생성 및 이벤트 처리 리스너 설정
    compileBtn = new JButton("Compile");
    compileBtn.addActionListener(new compileListener());
    saveErrorsBtn = new JButton("Save Errors");
    saveErrorsBtn.addActionListener(new saveErrorsListener());
    deleteBtn = new JButton("Delete");
    deleteBtn.addActionListener(new deleteListener());
    clearBtn = new JButton("Clear");
    clearBtn.addActionListener(new clearListener());

    // 버튼 패널에 버튼 탑재
    buttonPannel.add(compileBtn);
    buttonPannel.add(saveErrorsBtn);
    buttonPannel.add(deleteBtn);
    buttonPannel.add(clearBtn);

    // 패널의 위치 및 높이비율, 공간 채우기 설정
    gbc.gridy = 4;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add(buttonPannel, gbc);

    // 동작 수행 결과를 나태니기 위한 Result window 텍스트 에리어의 편집 가능 여부, 스크롤, 위치, 높이비율, 공간 채우기 설정
    resultWindow = new JTextArea();
    resultWindow.setEditable(false);
    JScrollPane resultScrollPane = new JScrollPane(resultWindow);
    gbc.gridy = 5;
    gbc.weighty = 5;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(resultScrollPane, gbc);

    // 프레임에 컨텐트펜 탑재
    add(contentPane, BorderLayout.CENTER);
    setVisible(true);
  }

  // 파일 불러오기 버튼의 이벤트 처리 리스너
  private class OpenListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      // 파일 이름 입력 받아서 저장, 에러내용 초기화
      fName = fileOpenField.getText();
      errorContent = null;
      File file = new File("javafile/" + fName);
      // 파일 존재 여부 및 확장자 검사
      if (file.exists() && fName.endsWith(".java")) {
        try {
          // 자바 파일을 읽어들일 입력 버퍼 생성
          BufferedReader reader = new BufferedReader(new FileReader(file));

          // 자바 파일 내용을 Editing Window에 출력
          String stream;
          editingWindow.setText("");
          while ((stream = reader.readLine()) != null) editingWindow.append(stream + "\n");

          // 버퍼 닫기, 프로그램 타이틀 변경, 파일 입력 필드 초기화, 실행 결과 표시
          reader.close();
          setTitle("\"" + fName + "\" - My Java IDE GUI");
          fileOpenField.setText("");
          resultWindow.setText("Java file loaded successfully");
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

    }
  }

  // 불러온 파일 컴파일 버튼의 이벤트 처리 리스너
  private class compileListener implements ActionListener {
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
          while ((stream = stdError.readLine()) != null) {
            hasError = true; // 오류가 발생했으므로 플래그를 true로 전환
            errorContent.append(stream).append("\n"); // 내용 한줄씩 저장
            resultWindow.append(stream + "\n"); // 한줄씩 출력
          }

          // 컴파일 정상 완료 시 출력
          if (!hasError)
            resultWindow.setText("Compiled successfully...");

          // 버퍼 닫기
          stdError.close();
        } catch (IOException error) {
          resultWindow.setText("Error: Compilation failed!");
        }
      }
    }
  }

  private class saveErrorsListener implements ActionListener {
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
        } catch (IOException e) {
          resultWindow.setText("Error: Failed to save error file!");
        }
      }
    }
  }

  // 삭제 버튼의 이벤트 처리 리스너
  private class deleteListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      // 파일 불러오기 여부 검사
      if (fName == null)
        resultWindow.setText("Error: No java file to delete selected!");
      else {
        // editingWindow, resultWindow, errorContent 초기화
        editingWindow.setText("");
        resultWindow.setText("");
        errorContent = null;
        // javafile 폴더에서 이름으로 fileName을 갖는 클래스 파일에 대한 객체를 생성
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
        resultWindow.append("Java file deleted\n\n");

        // 실행 결과 출력 및 프로그램 타이틀 변경
        resultWindow.append("Deletion completed.");
        setTitle("My Java IDE GUI");
      }
    }
  }

  // 클리어 버튼의 이벤트 처리 리스너
  private class clearListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
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

  public static void main(String[] args) {
    new GUIBasedJavaIDE();
  }
}
