import java.awt.*;// 그래픽 처리를 위한 클래스들의 경로명
import java.awt.event.*; // AWT 이벤트 사용을 위한 경로명 
import javax.swing.*; // 스윙 컴포넌트 클래스들의 경로명 
import javax.swing.event.*; // 스윙 이벤트를 위한 경로

public class test extends JFrame {
	private JTextPane m_outTextPane;
	private JTextField m_inTextField;
	private JButton m_startStopButton;
	
	private String CHARACTER_ARR[] = {"JAMES", "JIN", "YUMI", "DEER", "WOONG", "BOBBY"};
	private String WEAPON_ARR[] = {"KNIFE", "PIPE", "ROPE", "GUN", "HAMMER", "WRENCH"};
	private String PLACE_ARR[] = {"GARAGE", "BATHROOM", "KITCHEN", "YARD", 
						"LIVINGROOM", "BALCONY", "DININGROOM", "BEDROOM", "LIBRARY"};
	

	public test() {
		setTitle("CLUE");
		setSize(800,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		
		
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
		
		JPanel pChatting = new JPanel(); 
		JEditorPane jEditorPane = new JEditorPane();
		jEditorPane.setPreferredSize(new Dimension(350,500));
		pChatting.add(jEditorPane);
		pRight.add(pChatting, BorderLayout.CENTER);
		
		JPanel pInput = new JPanel(); 
		JEditorPane inputBox = new JEditorPane();
		inputBox.setPreferredSize(new Dimension(280,30));
		pInput.add(inputBox);
		JButton inputBtn = new JButton("입력");
		inputBtn.setPreferredSize(new Dimension(70,30));
		pInput.add(inputBtn);
		pRight.add(pInput, BorderLayout.SOUTH);
		
		
		setVisible(true); // 프레임 출력
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		test frame = new test();
	}

}