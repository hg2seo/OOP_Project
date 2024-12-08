import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import static java.awt.Color.BLUE;
import static java.awt.Color.ORANGE;

public class GUIBasedJavaIDE extends JFrame {
  private final JTabbedPane editingWindow;
  private final JTextArea resultWindow;

  public GUIBasedJavaIDE() {
    // GUI 기본 설정
    setTitle("My Java IDE GUI");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    createMenu(); // 메뉴 생성, 프레임 삽입
    setSize(815, 765);
    setResizable(false);
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

    c.setFocusable(true);
    c.requestFocus();
  }

  // 메뉴 생성 메서드
  private void createMenu() {
    // 메뉴바
    JMenuBar menuBar = new JMenuBar();
    menuBar.setBackground(ORANGE);

    // File 메뉴
    JMenu fileMenu = new JMenu("File");
    String[] fileMenuItems = {"Open", "Close", "Save", "Save As", "Quit"};
    ActionListener[] fileMenuActions = { e -> openFile(), new CloseListener(), new SaveListener(), new SaveAsListener(), new QuitListener() };
    for (int i=0; i<fileMenuItems.length; i++) {
      JMenuItem menuItem = new JMenuItem(fileMenuItems[i]);
      menuItem.setBackground(new Color(30, 144, 255));
      menuItem.addActionListener(fileMenuActions[i]);
      fileMenu.add(menuItem);
    }
    fileMenu.setBackground(BLUE);
    menuBar.add(fileMenu);

    // Run 메뉴
    JMenu runMenu = new JMenu("Run");
    JMenuItem runItem = new JMenuItem("Compile");
    ActionListener runMenuAction = new CompileListener();
    runItem.addActionListener(runMenuAction);
    runItem.setBackground(new Color(30,144,255));
    runMenu.add(runItem);
    menuBar.add(runMenu);

    setJMenuBar(menuBar);
  }

  // 원활한 저장 작업을 위해 탭별로 파일 경로를 저장하는 커스텀 스크롤팬
  private static class EditorScrollPane extends JScrollPane {
    private String tabPath; // 해당 탭이 불러온 파일의 경로
    private final JTextArea innerTextArea; // 해당 탭의 TextArea

    private EditorScrollPane(JTextArea textArea, String filePath) {
      super(textArea);
      this.tabPath = filePath;
      innerTextArea = textArea;
    }

    // 반환 함수
    public String getTabPath() { return tabPath; }
    public JTextArea getTextArea() { return innerTextArea; }
    // 경로 수정
    public void saveNewTabPath(String tabPath) { this.tabPath = tabPath; }
  }

  // 파일 열기 메서드
  private void openFile() {
    JFileChooser fileChooser = new JFileChooser(); // 파일 탐색기 인터페이스
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
        newTabTextArea.addKeyListener(new MyKeyListener());
        EditorScrollPane newScrollPane = new EditorScrollPane(newTabTextArea, file.getPath());
        resultWindow.setText("");
        editingWindow.addTab(file.getName(), newScrollPane);
        editingWindow.setSelectedComponent(newScrollPane); // 새로 열린 파일로 포커스 이동

      } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  // 탭 닫기 메뉴 아이템의 리스너
  private class CloseListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      // 열려있는 탭이 존재하지 않으면 오류 메시지 출력
      if (editingWindow.getTabCount() == 0) {
        JOptionPane.showMessageDialog(null, "Please open a file first.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // 탭 닫을건지 재확인
      int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to close this tab?", "Confirm", JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        // 포커스를 갖고 있는 탭 닫기
        resultWindow.setText("");
        int selectedIndex = editingWindow.getSelectedIndex();
        editingWindow.removeTabAt(selectedIndex);
      }
    }
  }

  // 저장 메뉴 아이템의 리스너
  private class SaveListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      // 열려있는 탭이 존재하지 않으면 오류 메시지 출력
      if (editingWindow.getTabCount() == 0) {
        JOptionPane.showMessageDialog(null, "Please open a file first.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      // 저장할건지 재확인
      int result = JOptionPane.showConfirmDialog(null, "Do you want to save the changes?", "Confirm", JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        // 포커스를 가진 탭의 저장 경로와 편집 내용 가져오기
        EditorScrollPane currentEditorScrollPane = (EditorScrollPane) editingWindow.getSelectedComponent();
        JTextArea currentEditingWindow = currentEditorScrollPane.getTextArea();
        File file = new File(currentEditorScrollPane.getTabPath());

        try {
          // 파일에 내용을 저장하기 위한 출력 버퍼 생성
          BufferedWriter writer = new BufferedWriter(new FileWriter(file));

          writer.write(currentEditingWindow.getText()); // 포커스를 가진 Editing Window 의 텍스트를 파일에 저장
          writer.flush(); // 버퍼 비우기
          writer.close(); // 버퍼 닫기

          // 성공 메시지 출력
          JOptionPane.showMessageDialog(null, "File saved successfully", "Alert", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
          JOptionPane.showMessageDialog(null, "Failed to save the file: " + e.getMessage(),
                  "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  // 다른 이름으로 저장 메뉴 아이템의 리스너
  private class SaveAsListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      // 열려있는 탭이 존재하지 않으면 오류 메시지 출력
      if (editingWindow.getTabCount() == 0) {
        JOptionPane.showMessageDialog(null, "Please open a file first.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      // FileChooser 로 저장 경로와 이름 입력 받기
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Java File", "java"));
      int saveResult = fileChooser.showSaveDialog(null);

      if (saveResult == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();

        // 사용자에게 입력받은 이름이 .java 형태가 아니면 .java 확장자로 변환
        if (!file.getName().endsWith(".java"))
          file = new File(file.getAbsolutePath() + ".java");

        // 이미 같은 이름의 파일이 경로에 존재하면 진짜로 덮어씌울건지 확인
        if (file.exists()) {
          int overwriteResult = JOptionPane.showConfirmDialog(null, "There is already a file with the same name in this location. Would you like to overwrite the file??", "Confirm", JOptionPane.YES_NO_OPTION);
          if (overwriteResult != JOptionPane.YES_OPTION) {
            return;
          }
        }

        try {
          // 포커스를 가진 탭의 편집 내용 가져오기
          EditorScrollPane currentEditorScrollPane = (EditorScrollPane) editingWindow.getSelectedComponent();
          JTextArea currentEditingWindow = currentEditorScrollPane.getTextArea();
          BufferedWriter writer = new BufferedWriter(new FileWriter(file));

          writer.write(currentEditingWindow.getText()); // 포커스를 가진 Editing Window 의 텍스트를 파일에 저장
          writer.flush(); // 버퍼 비우기
          writer.close(); // 버퍼 닫기

          // 입력받은 이름으로 탭의 이름 수정
          int selectedIndex = editingWindow.getSelectedIndex();
          editingWindow.setTitleAt(selectedIndex, file.getName());

          // 스크롤팬이 저장하고 있는 경로 수정
          currentEditorScrollPane.saveNewTabPath(file.getPath());
          // 성공 메시지 출력
          JOptionPane.showMessageDialog(null, "File saved successfully", "Alert", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
          JOptionPane.showMessageDialog(null, "Failed to save the file: " + e.getMessage(),
                  "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  // 프로그램 종료 메뉴 아이템의 리스너
  private class QuitListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      // 종료할건지 재확인
      int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit this program?", "Confirm", JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION)
        System.exit(0);
    }
  }

  // 불러온 파일 컴파일 버튼의 이벤트 처리 리스너
  private class CompileListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      // 파일 불러오기 여부 검사
      if (editingWindow.getTabCount() == 0)
        JOptionPane.showMessageDialog(null, "Please open a file first.", "Error", JOptionPane.ERROR_MESSAGE);
      else {
        EditorScrollPane currentEditorScrollPane = (EditorScrollPane) editingWindow.getSelectedComponent();
        File file = new File(currentEditorScrollPane.getTabPath());

        try {
          // javac(자바 컴파일러) 외부 명령어 실행을 통해 컴파일 수행
          Process oProcess = new ProcessBuilder("javac", "-d", "class","-encoding", "utf-8", file.getAbsolutePath()).start();

          // 컴파일 시 프로세스의 표준 오류 출력을 받아들일 입력 버퍼 생성
          BufferedReader stdError = new BufferedReader(new InputStreamReader(oProcess.getErrorStream()));

          // 오류 플래그와 오류의 내용을 저장할 공간 생성 및 오류 출력, 저장 수행
          boolean hasError = false;
          String stream;
          resultWindow.setText("");
          while ((stream = stdError.readLine()) != null) {
            hasError = true; // 오류가 발생했으므로 플래그를 true 로 전환
            resultWindow.append(stream + "\n"); // 한줄씩 출력
          }

          // 컴파일 정상 완료 시 출력
          if (!hasError)
            resultWindow.setText("Compiled successfully: " + file.getName().replace(".java", ".class"));

          // 버퍼 닫기
          stdError.close();
        } catch (IOException e) {
          JOptionPane.showMessageDialog(null, "Failed to compile the file: " + e.getMessage(),
                  "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }


  private class MyKeyListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      super.keyPressed(e);
      if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R) {
        ActionEvent actionEvent = new ActionEvent(e.getSource(), e.getID(), "Ctrl + R");
        CompileListener compileListener = new CompileListener();

        compileListener.actionPerformed(actionEvent);
      }
    }
  }

  public static void main(String[] args) {
    new GUIBasedJavaIDE();
  }
}
