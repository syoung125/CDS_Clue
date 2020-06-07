package clue;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;

import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;


public class Intro2 extends JFrame {


	private CMClientStub m_clientStub;
	private Intro2EventHandler m_eventHandler;
	private boolean m_bRun;
	private Scanner m_scan = null;
	
	private JButton fastStartBtn;
	private JButton makeRoomBtn;
	private JButton showRankingBtn;
	
	private JButton session3Btn;
	private JButton session4Btn;
	private JButton session5Btn;
	private JButton session6Btn;
	private BtnListener login2btnlistener;

	public Intro2() {
		m_clientStub=new CMClientStub();
		m_eventHandler=new Intro2EventHandler();
		m_bRun = true;
		makeLogin2();
	}
	public CMClientStub getM_clientStub() {
		return m_clientStub;
	}
	public void setM_clientStub(CMClientStub m_clientStub) {
		this.m_clientStub = m_clientStub;
	}
	public Intro2EventHandler getM_eventHandler() {
		return m_eventHandler;
	}
	public void setM_eventHandler(Intro2EventHandler m_eventHandler) {
		this.m_eventHandler = m_eventHandler;
	}
	
public void makeLogin2() {
		
		setTitle("게임 준비");
		setSize(600,300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER,40,50));
		
        login2btnlistener=new BtnListener();
        fastStartBtn=new JButton("빠른 시작");
        fastStartBtn.setPreferredSize(new Dimension(150,100));
        fastStartBtn.addActionListener(login2btnlistener);
        makeRoomBtn=new JButton("방 만들기");
        makeRoomBtn.setPreferredSize(new Dimension(150,100));
        makeRoomBtn.addActionListener(login2btnlistener);
        showRankingBtn=new JButton("랭킹 조회");
        showRankingBtn.setPreferredSize(new Dimension(150,100));
        showRankingBtn.addActionListener(login2btnlistener);

        add(fastStartBtn);
        add(makeRoomBtn);
        add(showRankingBtn);
        
        setVisible(true);

	}
	public class BtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton button = (JButton) e.getSource();
			if(button.getText().equals("빠른 시작"))
			{
				fastStart();
			}
			else if(button.getText().equals("방 만들기"))
			{
				makeRoom();
			}
			else if(button.getText().equals("랭킹 조회"))
			{
				showRanking();
			}
			else if(button.getText().toString().charAt(1)=='인')
			{
				System.out.println(button.getText().toString().charAt(0));
				//그 세션에 활성화된 그룹들을 불러와서 임의로 배정.

			}
		}
		
	}

	public void fastStart() {
		
		System.out.println("fastStart");
		//현재 활성화된 세션을 조사 (세션 별로 활성화된 그룹 수 띄우기)
		getM_clientStub().requestSessionInfo();
		//세션과 그룹에 등록 후
		test frame = new test();

	}
	public void makeRoom() {
			
		System.out.println("makeRoom");
	    //세션 선택 ( 방의 인원 선택 ) *
		//그룹 만들기
		JFrame makingRoomFrame =new JFrame();
		makingRoomFrame.setTitle("방 선택");
		makingRoomFrame.setSize(600,600);
		makingRoomFrame.setVisible(true);
		makingRoomFrame.setLayout(new GridLayout(2,2));

        login2btnlistener=new BtnListener();

        getM_clientStub().requestServerInfo();
		session3Btn=new JButton("3인");
        session3Btn.setPreferredSize(new Dimension(100,100));
        session3Btn.addActionListener(login2btnlistener);
        session4Btn=new JButton("4인");
        session4Btn.setPreferredSize(new Dimension(100,100));
        session4Btn.addActionListener(login2btnlistener);
        session5Btn=new JButton("5인");
        session5Btn.setPreferredSize(new Dimension(100,100));
        session5Btn.addActionListener(login2btnlistener);
        session6Btn=new JButton("6인");
        session6Btn.setPreferredSize(new Dimension(100,100));
        session6Btn.addActionListener(login2btnlistener);

        makingRoomFrame.add(session3Btn);
        makingRoomFrame.add(session4Btn);
        makingRoomFrame.add(session5Btn);
        makingRoomFrame.add(session6Btn);


        setVisible(true);
	}
	public void showRanking() {
		
		System.out.println("showRanking");
	    //랭킹 조회 페이지 생성 
		//디비에서 유저 정보 가져오기
		//순서대로 나열하기(쿼리로)
		JFrame rankingFrame =new JFrame();
		rankingFrame.setTitle("랭킹 조회");
		rankingFrame.setSize(600,800);
		rankingFrame.setLayout(new BorderLayout());
		
		JTextField rankingText=new JTextField("RANKING");
		rankingText.setHorizontalAlignment(JTextField.CENTER);;
		String[] list= {"1. sushi","2.burrito"};
		JList listPane=new JList(list);
		listPane.setAlignmentX(CENTER_ALIGNMENT);;

		//TextArea text=new TextArea(10,2);
			
		rankingFrame.add(rankingText,BorderLayout.NORTH);
		rankingFrame.add(listPane,BorderLayout.CENTER);
		//rankingFrame.add(text);
		rankingFrame.setVisible(true);
		
		
		
		
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Intro2 frame = new Intro2();
		frame.getM_clientStub().setAppEventHandler(frame.getM_eventHandler());
		frame.getM_clientStub().startCM();
		
		frame.getM_clientStub().loginCM("testID", "test");
		
	
	}

}