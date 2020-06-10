import java.awt.*;// 그래픽 처리를 위한 클래스들의 경로명
import java.awt.event.*; // AWT 이벤트 사용을 위한 경로명 
import javax.swing.*; // 스윙 컴포넌트 클래스들의 경로명 
import javax.swing.event.*; // 스윙 이벤트를 위한 경로
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class ClueCMClientGame extends JFrame {
	// GUI 변수
	private JTextPane m_outTextPane;	// 채팅창
	private JTextField inputBox;	//채팅 입력 창
	private JButton inputBtn;	//입력 버튼
	private String inputBtnStr = "입력";
	
	private BtnListener gamebtnlistener;
	private MyKeyListener gamekeylistener;
	
	private String CHARACTER_ARR[] = {"JAMES", "JIN", "YUMI", "DEER", "WOONG", "BOBBY"};
	private String WEAPON_ARR[] = {"KNIFE", "PIPE", "ROPE", "GUN", "HAMMER", "WRENCH"};
	private String PLACE_ARR[] = {"GARAGE", "BATHROOM", "KITCHEN", "YARD", 
						"LIVINGROOM", "BALCONY", "DININGROOM", "BEDROOM", "LIBRARY"};
	
	
	private CMClientStub m_clientStub;
	private ClueCMClientGameEventHandler m_eventHandler;
	

	public ClueCMClientGame() {
		makeGameGUI();
		
		// create a CM object and set the event handler
		m_clientStub = new CMClientStub();
		m_eventHandler = new ClueCMClientGameEventHandler(m_clientStub, this);
		
		// start CM
//		testStartCM();
		
		inputBox.requestFocus();

	}
	
	public void makeGameGUI() {
		setTitle("CLUE");
		setSize(800,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		
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
		for(int i=0;i<CHARACTER_ARR.length;i++) {
			JButton tBtn = new JButton(CHARACTER_ARR[i]);
			tBtn.setPreferredSize(new Dimension(100, 30));
			pLeft.add(tBtn);
		}
		JLabel label2 = new JLabel("무기 ");
		label2.setPreferredSize(new Dimension(400, 30));
		pLeft.add(label2);
		for(int i=0;i<WEAPON_ARR.length;i++) {
			JButton tBtn = new JButton(WEAPON_ARR[i]);
			tBtn.setPreferredSize(new Dimension(100, 30));
			pLeft.add(tBtn);
		}
		JLabel label3 = new JLabel("장소 ");
		label3.setPreferredSize(new Dimension(400, 30));
		pLeft.add(label3);
		for(int i=0;i<PLACE_ARR.length;i++) {
			JButton tBtn = new JButton(PLACE_ARR[i]);
			tBtn.setPreferredSize(new Dimension(120, 30));
			pLeft.add(tBtn);
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
		
		setVisible(true); // 프레임 출력
	}
	
	public class BtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton button = (JButton) e.getSource();
			if(button.getText().equals(inputBtnStr))
			{
				String temp = inputBox.getText();
				printMessage(temp+"\n");
			}
		}
		
	}
	
	public class MyKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER)
			{
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
	
	public void printMessage(String strText)
	{
		/*
		m_outTextArea.append(strText);
		m_outTextArea.setCaretPosition(m_outTextArea.getDocument().getLength());
		*/
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
	

	public static void main(String[] args) {

		ClueCMClientGame gameFrameMain = new ClueCMClientGame();
		
//		for(int i=0;i<40;i++) {
//			gameFrameMain.printMessage("하이하이\n");
//		}
	
	}

}