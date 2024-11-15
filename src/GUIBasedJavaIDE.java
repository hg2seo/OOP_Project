import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class GUIBasedJavaIDE extends JFrame {
  private final JTextField fileOpenField;
  private final JTextField fileSaveField;
  private final JTextArea editingWindow;
  private final JTextArea resultWindow;
  private String fName = null;
  private StringBuilder errorContent = null;

  public GUIBasedJavaIDE() {
    // GUI 기본 설정
    setTitle("My Java IDE GUI");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 720);
    Container container = getContentPane();
    container.setLayout(new BorderLayout());

    // 그리드백 레이아웃을 탑재한 컨텐트펜 생성
    JPanel contentPane = new JPanel(new GridBagLayout());
    contentPane.setBorder(BorderFactory.createEmptyBorder(0, 120, 0, 120));
    // 그리드백 레이아웃 내 컴포넌트 배치 설정을 관리하는 객체 생성, 여백 설정
    GridBagConstraints contentGBC = new GridBagConstraints();
    contentGBC.insets = new Insets(5, 5, 5, 5);

    // 파일 불러오기를 위한 텍스트필드의 위치 및 너비 설정
    fileOpenField = new JTextField(40);
    contentGBC.gridx = 0;
    contentGBC.gridy = 0;
    contentGBC.gridwidth = 5;
    contentGBC.weightx = 1;
    contentGBC.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add(fileOpenField, contentGBC);

    // 불러오기 버튼의 위치 및 너비, 이벤트 처리 리스너 설정
    JButton openBtn = new JButton("Open");
    openBtn.addActionListener(new OpenListener());
    contentGBC.gridx = 5;
    contentGBC.gridwidth = 1;
    contentGBC.weightx = 0;
    contentGBC.fill = GridBagConstraints.NONE;
    contentPane.add(openBtn, contentGBC);

    // 파일 저장하기를 위한 텍스트필드의 위치 및 너비 설정
    fileSaveField = new JTextField(40);
    contentGBC.gridx = 0;
    contentGBC.gridy = 1;
    contentGBC.gridwidth = 5;
    contentGBC.weightx = 1;
    contentGBC.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add(fileSaveField, contentGBC);

    // 저장하기 버튼의 위치 및 너비, 이벤트 처리 리스너 설정
    JButton saveBtn = new JButton("Save");
    saveBtn.addActionListener(new SaveListener());
    contentGBC.gridx = 5;
    contentGBC.gridwidth = 1;
    contentGBC.weightx = 0;
    contentGBC.fill = GridBagConstraints.NONE;
    contentPane.add(saveBtn, contentGBC);

    // 코드 편집을 위한 Editing window 텍스트 에리어의 스크롤, 위치, 너비, 높이비율, 공간 채우기 설정
    editingWindow = new JTextArea();
    JScrollPane editingScrollPane = new JScrollPane(editingWindow);
    contentGBC.gridx = 0;
    contentGBC.gridy = 3;
    contentGBC.gridwidth = 6;
    contentGBC.weighty = 8;
    contentGBC.fill = GridBagConstraints.BOTH;
    contentPane.add(editingScrollPane, contentGBC);

    // 4개 버튼 배치를 위한 패널 생성
    JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));

    // 컴파일 버튼, 에러 파일 저장 버튼, 자바 파일 삭제 버튼, 클리어 버튼 생성 및 이벤트 처리 리스너 설정
    JButton compileBtn = new JButton("Compile");
    compileBtn.addActionListener(new CompileListener());
    JButton saveErrorsBtn = new JButton("Save Errors");
    saveErrorsBtn.addActionListener(new SaveErrorsListener());
    JButton deleteBtn = new JButton("Delete");
    deleteBtn.addActionListener(new DeleteListener());
    JButton clearBtn = new JButton("Clear");
    clearBtn.addActionListener(new ClearListener());

    // 버튼 패널에 버튼 탑재
    buttonPanel.add(compileBtn);
    buttonPanel.add(saveErrorsBtn);
    buttonPanel.add(deleteBtn);
    buttonPanel.add(clearBtn);

    // 패널의 위치 및 높이비율, 공간 채우기 설정
    contentGBC.gridy = 4;
    contentGBC.weighty = 1;
    contentGBC.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add(buttonPanel, contentGBC);

    // 동작 수행 결과를 나태니기 위한 Result window 텍스트 에리어의 편집 가능 여부, 스크롤, 위치, 높이비율, 공간 채우기 설정
    resultWindow = new JTextArea();
    resultWindow.setEditable(false);
    JScrollPane resultScrollPane = new JScrollPane(resultWindow);
    contentGBC.gridy = 5;
    contentGBC.weighty = 5;
    contentGBC.fill = GridBagConstraints.BOTH;
    contentPane.add(resultScrollPane, contentGBC);

    // 프레임에 컨텐트펜 탑재
    container.add(contentPane, BorderLayout.CENTER);
    setVisible(true);
  }

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

  public static void main(String[] args) {
    new GUIBasedJavaIDE();
  }
}
