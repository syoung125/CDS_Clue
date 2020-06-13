import java.awt.*;// 그래픽 처리를 위한 클래스들의 경로명
import java.awt.event.*; // AWT 이벤트 사용을 위한 경로명 
import javax.swing.*; // 스윙 컴포넌트 클래스들의 경로명 
import javax.swing.event.*; // 스윙 이벤트를 위한 경로
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import org.json.*;
import java.util.ArrayList;

public class ClueCMClientGame extends JFrame {
	// GUI 변수
	private JTextPane m_outTextPane;	// 채팅창
	private JTextField inputBox;	//채팅 입력 창
	private JButton inputBtn;	//입력 버튼
	private String inputBtnStr = "입력";
	private ArrayList<JButton> clueBtn = new ArrayList<JButton>();
	
	private BtnListener gamebtnlistener;
	private MyKeyListener gamekeylistener;
	
	// CM
	private CMClientStub m_clientStub;
	private ClueCMClientGameEventHandler m_eventHandler;
	GameEnvironment env;
	
	// Game variables
	public class GameEnvironment {
		private String PLACE_ARR[] = {"GARAGE", "BATHROOM", "KITCHEN", "YARD", 
				"LIVINGROOM", "BALCONY", "DININGROOM", "BEDROOM", "LIBRARY"};
		private String CHARACTER_ARR[] = {"JAMES", "JIN", "YUMI", "DEER", "WOONG", "BOBBY"};
		private String WEAPON_ARR[] = {"KNIFE", "PIPE", "ROPE", "GUN", "HAMMER", "WRENCH"};
		
		public boolean bStart = false;
		private String answer[] = new String[3];
	}

	/////////////////////////////////////////////////////////////
	
	public ClueCMClientGame() {
		// create a CM object and set the event handler
		m_clientStub = new CMClientStub();
		m_eventHandler = new ClueCMClientGameEventHandler(m_clientStub, this);
		env = new GameEnvironment();
		
		makeGameGUI();
				
		// start CM
//		startCM();
		startGame();
	}
	
	public void makeGameGUI() {
		setTitle("CLUE");
		setSize(800,600);
		
		gamebtnlistener=new BtnListener();
		gamekeylistener=new MyKeyListener();
		Container contentPane = getContentPane(); 
		contentPane.setLayout(new BorderLayout(0, 0));
	    setContentPane(contentPane);

		
		JPanel pLeft = new JPanel(); 
		pLeft.setPreferredSize(new Dimension(400, 600));
		contentPane.add(pLeft, BorderLayout.WEST);
		
		JLabel label1 = new JLabel("범인 ");
		label1.setPreferredSize(new Dimension(400, 30));
		pLeft.add(label1);
		pLeft.setLayout(new FlowLayout());
		for(int i=0;i<env.CHARACTER_ARR.length;i++) {
			JButton tBtn = new JButton(env.CHARACTER_ARR[i]);
			tBtn.setPreferredSize(new Dimension(100, 30));
			pLeft.add(tBtn);
			clueBtn.add(tBtn);
		}
		JLabel label2 = new JLabel("무기 ");
		label2.setPreferredSize(new Dimension(400, 30));
		pLeft.add(label2);
		for(int i=0;i<env.WEAPON_ARR.length;i++) {
			JButton tBtn = new JButton(env.WEAPON_ARR[i]);
			tBtn.setPreferredSize(new Dimension(100, 30));
			pLeft.add(tBtn);
			clueBtn.add(tBtn);
		}
		JLabel label3 = new JLabel("장소 ");
		label3.setPreferredSize(new Dimension(400, 30));
		pLeft.add(label3);
		for(int i=0;i<env.PLACE_ARR.length;i++) {
			JButton tBtn = new JButton(env.PLACE_ARR[i]);
			tBtn.setPreferredSize(new Dimension(120, 30));
			pLeft.add(tBtn);
			clueBtn.add(tBtn);
		}
		
		// init clueBtn
		for(int i=0;i<clueBtn.size();i++) {
			clueBtn.get(i).setBackground(Color.BLACK);
			clueBtn.get(i).setForeground(Color.WHITE);
			clueBtn.get(i).addActionListener(gamebtnlistener);
		}
		
		///////////////////////////////////////////////////
		
		JPanel pRight = new JPanel();
		pRight.setPreferredSize(new Dimension(400, 600));
		pRight.setLayout(new BorderLayout(0, 0));
		contentPane.add(pRight, BorderLayout.CENTER);
		
		// ** 채팅창
		JPanel pChatting = new JPanel(); 
		m_outTextPane = new JTextPane();
		m_outTextPane.setBackground(new Color(255,255,255));
		m_outTextPane.setPreferredSize(new Dimension(350,500));
		m_outTextPane.setEditable(false);
		pChatting.add(m_outTextPane);
		JScrollPane centerScroll = new JScrollPane(m_outTextPane);
		pRight.add(centerScroll, BorderLayout.CENTER);
		
		// ** 채팅입력 창
		JPanel pInput = new JPanel(); 
		inputBox = new JTextField();
		inputBox.addKeyListener(gamekeylistener);
		inputBox.setPreferredSize(new Dimension(280,30));
		pInput.add(inputBox);
		// ** 입력버튼
		inputBtn = new JButton(inputBtnStr);
		inputBtn.addActionListener(gamebtnlistener);
		inputBtn.setPreferredSize(new Dimension(70,30));
		pInput.add(inputBtn);
		pRight.add(pInput, BorderLayout.SOUTH);
		
		// 채팅창 비활성화
		inputBox.setText("대기 중입니다.");
		inputBox.setEnabled(false);
		inputBtn.setEnabled(false);
		
		
		setVisible(true); // 프레임 출력
	}
	
	public class BtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton button = (JButton) e.getSource();
			if(button.getText().equals(inputBtnStr))
			{
				// 같은동작 함수로**
				String temp = inputBox.getText();
				printMessage(temp+"\n");
			}else if(clueBtn.contains(button)){
				setBtnColor(button);
			}
		}
		
		// 클릭에 따라 버튼 색을 변경하는 함수
		public void setBtnColor(JButton button) {
			Color c = button.getBackground();
			if(c.equals(Color.BLACK)) {
				button.setBackground(Color.GREEN);
				button.setForeground(Color.BLACK);
			}else if(c.equals(Color.GREEN)) {
				button.setBackground(Color.RED);
				button.setForeground(Color.WHITE);
			}else if(c.equals(Color.RED)) {
				button.setBackground(Color.BLACK);
				button.setForeground(Color.WHITE);
			}
		}
	}
	
	public class MyKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER)
			{
				// 같은동작 함수로**
				JTextField input = (JTextField)e.getSource();
				String strText = input.getText();
				printMessage(strText+"\n");

				input.setText("");
				input.requestFocus();
			}
		}
		
		public void keyReleased(KeyEvent e){}
		public void keyTyped(KeyEvent e){}
	}
	
	
	
	public void initGame(String[] answer, String[] myCard) {
		env.bStart = true;	// 게임시작
		// 채팅창 활성화
		inputBox.setText("");
		inputBox.setEnabled(true);
		inputBox.requestFocus();
		inputBtn.setEnabled(true);
		
		
		// 게임 초기화 
		// 서버에서 받고 하는 클라이언트에서 할 작업
//		String str = "{ \"answer\": [\""+env.PLACE_ARR[0]+"\","
//				+ " \""+env.CHARACTER_ARR[0]+"\","
//				+ " \""+env.WEAPON_ARR[0]+"\"], "
//						+ "\"mycard\": 20 }";
//		JSONObject obj = new JSONObject(str);
//		JSONArray arr = obj.getJSONArray("answer");
//		for (int i = 0; i < arr.length(); i++) {
//			env.answer[i] = arr.getString(i);
//		}
		for (int i = 0; i < answer.length; i++) {
			env.answer[i] = answer[i];
		}
		// 내 카드 노란색으로
		for(int i=0;i<myCard.length;i++) {
			setMyCard(findBtn(myCard[i]));
		}
	}
	
	public void startGame() {
		// temp
		///////////// -- 이벤트 핸들러로 넘길부분
		String serverAnswer[] = {env.PLACE_ARR[0], env.CHARACTER_ARR[0], env.WEAPON_ARR[0]};
		String myCard[] = {env.PLACE_ARR[6], env.CHARACTER_ARR[4], env.WEAPON_ARR[3]};
		initGame(serverAnswer,myCard);
		/////////////
		
		if(env.bStart) {
			printMessage("===== Game Start =====");
			printMessage("\n");
			printMessage("Place: " + env.answer[0]);
			printMessage("\n");
			printMessage("Character: " + env.answer[1]);
			printMessage("\n");
			printMessage("Weapon: " + env.answer[2]);
			printMessage("\n");
			printTurnMenu();
			// 이걸로 내카드 노란색을 설정 또는 disable하기 
//			setDisable(findBtn(env.PLACE_ARR[0]));
//			setDisable(findBtn(env.CHARACTER_ARR[3]));
			
			
			// obj -> string으로 (서버에서 전송할때)
//			JSONObject obj = new JSONObject();
//
//		      obj.put("name", "foo");
//		      obj.put("num", 100);
//		      obj.put("balance", 1000.21);
//		      obj.put("is_vip", true);
//
//		      printMessage(obj.toString());
		}
	}
	
	public void printMessage(String strText)
	{
		StyledDocument doc = m_outTextPane.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), strText, null);
			m_outTextPane.setCaretPosition(m_outTextPane.getDocument().getLength());

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
	}
	
	// Find button by string
	public JButton findBtn(String s) {
		for(int i=0;i<clueBtn.size();i++) {
			if(clueBtn.get(i).getText().equals(s)) {
				return clueBtn.get(i);
			}
		}
		return null;
	}
	
	public void setDisable(JButton btn) {
		btn.setEnabled(false);
		btn.setBackground(Color.GRAY);
		btn.setForeground(Color.WHITE);
	}
	
	public void setMyCard(JButton btn) {
		btn.setBackground(Color.YELLOW);
		btn.setEnabled(false);
	}
	
	public void printTurnMenu() {
		printMessage("===============================\n");
		printMessage("1. 물음표 카드 뽑기\n");
		printMessage("2. 랜덤으로 추리할 방 부여받기\n");
		printMessage("===============================\n");
	}

	public static void main(String[] args) {

		ClueCMClientGame gameFrameMain = new ClueCMClientGame();
		
	
	}

}