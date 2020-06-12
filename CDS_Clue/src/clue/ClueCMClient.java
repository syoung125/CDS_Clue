package clue;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.*;
import javax.swing.text.*;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import clue.Intro2.BtnListener;
import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMServer;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import kr.ac.konkuk.ccslab.cm.util.CMUtil;
public class ClueCMClient extends JFrame {
	
	private static final int WARNING_MESSAGE = 0;
	private CMClientStub m_clientStub;
	private ClueCMClientEventHandler m_eventHandler;
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
	private ArrayList groupnum=null;
	
	public ClueCMClient(){
		m_clientStub = new CMClientStub();
		m_eventHandler = new ClueCMClientEventHandler(m_clientStub,this);
		m_bRun = true;
		
		testStartCM();

	}
	public CMClientStub getClientStub()
	{
		return m_clientStub;
	}
	
	public ClueCMClientEventHandler getClientEventHandler()
	{
		return m_eventHandler;
	}
	
	public void makeLogin2() {
		//m_clientStub("1234","1324");
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
				int sessionnum=button.getText().toString().charAt(0)-'0';
				System.out.println("session"+Integer.toString(sessionnum-2));
				String sessiontojoin="session"+Integer.toString(sessionnum-2);
				//그 세션에 활성화된 그룹들을 불러와서 임의로 배정.
				//아무래도 그룹 지정은 m_clientStub.changeGroup(strGroupName);
				m_clientStub.leaveSession("SERVER");
				m_clientStub.joinSession(sessiontojoin);
				test frame = new test();

			}
		}
		
	}
	public void fastStart() {
		
		System.out.println("fastStart");
		//현재 활성화된 세션을 조사 (세션 별로 활성화된 그룹 수 띄우기)
		
		//세션과 그룹에 등록 후
		//getSessionGroupInfo();
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself=interInfo.getMyself();
		if(myself.getState()!=CMInfo.CM_LOGIN) {
			System.out.println("설마");
		}
		if(myself.getState()!=CMInfo.CM_SESSION_JOIN) {
			System.out.print("session missed");
			m_clientStub.requestServerInfo();
			m_clientStub.requestSessionInfo(m_clientStub.getDefaultServerName());
		}
		
		
		Iterator<CMSession> iter=interInfo.getSessionList().iterator();
		String temp="session1";
		int count=0;
		
		String[] restRoom= {};
		while(iter.hasNext()) {
			CMSession session=iter.next();
			int sessionNum=session.getSessionName().charAt(7)-'0';
			System.out.println("sessionNum:"+sessionNum);
			if(session.getUserNum()==sessionNum-1) {
				m_clientStub.syncJoinSession(session.getSessionName());
				m_clientStub.changeGroup("g1");
				m_clientStub.requestSessionInfo();
                
				test frame = new test();
				return;
			}
			else if(session.getUserNum()==0){
				temp=session.getSessionName();
				count++;
			}
			
			
		}
		//빠른 시작이 안 되는 경우
	  
	    	CMSessionEvent chk=m_clientStub.syncRequestSessionInfo();
	    	Iterator<CMSessionInfo> k=chk.getSessionInfoList().iterator();
	    	String messagestr="";
	    	while(k.hasNext()) {
	    		CMSessionInfo kinfo=k.next();
	    		messagestr+=kinfo.getUserNum();
	    		if(kinfo.getUserNum()==0)
	    			count++;
	    	}
	    	if(count==4) {
	    		Object[] message= {
						"현재 바로 시작할 수 있는 게임 방이 없습니다. 방을 직접 만들고 대기하세요."
				};
				int option=JOptionPane.showConfirmDialog(null, message,"게임 시작 불가", JOptionPane.OK_OPTION,JOptionPane.CANCEL_OPTION);
				if(option==JOptionPane.OK_OPTION) {
				   makeRoom();	
				   return;
				}
				else 
					return;
	    	}
	    	
		    Object[] message= {
		    		"현재 바로 시작할 수 있는 게임 방이 없습니다.    \n대기할 방을 입력하세요(인원 수 입력)",
		    		"3인 방: "+messagestr.charAt(0)+"명 대기 중","4인 방: "+messagestr.charAt(1)+"명 대기 중",
		    		"5인 방: "+messagestr.charAt(2)+"명 대기 중","6인 방: "+messagestr.charAt(3)+"명 대기 중"
		    };
		    String ret=JOptionPane.showInputDialog(null, message, "게임 시작 불가", WARNING_MESSAGE);
		    if(ret==null)
		    	return;
		    int sessionNum=Integer.parseInt(ret)-2;
		    if(3<=sessionNum&&sessionNum<7) {
		    	m_clientStub.joinSession("session"+sessionNum);
		    	m_clientStub.changeGroup("g1");
				m_clientStub.requestSessionInfo();
				//대기
				System.out.println("waiting for people");
		    	test frame=new test();
		    	return;
		    }
		
		return;		

	}
	public void makeRoom() {
			
		String strSessionName = null;
		String sessiontojoin=null;
		CMSessionEvent bRequestResult = null;
		m_clientStub.requestServerInfo();
		System.out.println("server info: "+m_clientStub.getServerAddress());
		System.out.println("<<<<<게임 인원 선택>>>>>");
		strSessionName = JOptionPane.showInputDialog("게임 인원을 입력하세요(3-6):");
		if(strSessionName != null)
		{
			int sessionnum=strSessionName.charAt(0)-'0';
			sessiontojoin="session"+Integer.toString(sessionnum-2);
			
			CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
			CMConfigurationInfo confInfo = m_clientStub.getCMInfo().getConfigurationInfo();
			CMUser myself = interInfo.getMyself();

			if(myself.getState() != CMInfo.CM_SESSION_JOIN) {
				//어떤 세션에도 가입 x
				bRequestResult = m_clientStub.syncJoinSession(sessiontojoin);
			}
			else {
				//이미 가입한 세션 존재
				m_clientStub.leaveSession();
				bRequestResult = m_clientStub.syncJoinSession(sessiontojoin);

			}
			

			if(bRequestResult!=null)
			{
				System.out.print("successfully sent the session-join request.\n");
				
			}
			else
				System.out.print("failed the session-join request!\n");
		}
		//확인용
		m_clientStub.requestSessionInfo();
		testCurrentUserStatus();
		/*
		getGroupInfoInSession(sessiontojoin);
		String strGroupName = JOptionPane.showInputDialog(sessiontojoin+"의 그룹 별 현재 인원\n g1:"+groupnum.get(0).toString()+" g2:"+groupnum.get(1).toString()+" g3:"+groupnum.get(2).toString()+" g4:"+groupnum.get(3));
		groupnum.clear();
		if(strGroupName != null) {
			m_clientStub.changeGroup(strGroupName);
			testPrintGroupMembers();
		}*/
		m_clientStub.changeGroup("g1");
		//getSessionGroupInfo();
		test frame=new test();
	}
	public void testCurrentUserStatus()
	{
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		CMConfigurationInfo confInfo = m_clientStub.getCMInfo().getConfigurationInfo();

		System.out.println("------ for the default server\n");
		System.out.println("name("+myself.getName()+"), session("+myself.getCurrentSession()+"), group("
				+myself.getCurrentGroup()+"), udp port("+myself.getUDPPort()+"), state("
				+myself.getState()+"), attachment download scheme("+confInfo.getAttachDownloadScheme()+").\n");
		
		// for additional servers
		Iterator<CMServer> iter = interInfo.getAddServerList().iterator();
		while(iter.hasNext())
		{
			CMServer tserver = iter.next();
			if(tserver.getNonBlockSocketChannelInfo().findChannel(0) != null)
			{
				System.out.println("------ for additional server["+tserver.getServerName()+"]\n");
				System.out.println("current session("+tserver.getCurrentSessionName()+
						"), current group("+tserver.getCurrentGroupName()+"), state("
						+tserver.getClientState()+").");
				
			}
		}
		
		return;
	}
	public void getGroupInfoInSession(String sessionName) {
		//세션에 맞는 그룹 정보 출력
		

		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMConfigurationInfo confInfo = m_clientStub.getCMInfo().getConfigurationInfo();
		CMUser myself = interInfo.getMyself();
		CMSession session=interInfo.findSession(sessionName);
		groupnum=new ArrayList();
		System.out.println("getGroupInfoInSession:"+sessionName);
		System.out.println("getGroupInfoInSession:"+session.getSessionName());
	
		Iterator<CMGroup> iter=session.getGroupList().iterator();
		
		
		while(iter.hasNext()) {
			CMGroup group=iter.next();
			int numOfGroupMem=group.getGroupUsers().getMemberNum();
			groupnum.add(numOfGroupMem);
			System.out.println(group.getGroupName()+"의 인원 수는"+Integer.toString(numOfGroupMem)+"누구:"+group.getGroupUsers().toString());
		}
	}
	public void getSessionGroupInfo() {
		//현재 내가 속한 그룹의 정보조회
		System.out.println("현재 내가 속한 그룹 정보:"+m_clientStub.getGroupMembers().toString());
		//서버에 속한 세션들 조회
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		Iterator<CMSession> iter=interInfo.getSessionList().iterator();
		while(iter.hasNext()) {
			CMSession session=iter.next();
			System.out.println(session.getSessionName());

			Iterator<CMGroup> iter2=session.getGroupList().iterator();
			System.out.println(session.getSessionName()+"내 그룹 리스트 탐색 시작");
			while(iter2.hasNext()) {
				CMGroup group=iter2.next();
				int numofgroupmember=group.getGroupUsers().getMemberNum();
				System.out.println(group.getGroupName()+"의 인원 수는"+Integer.toString(numofgroupmember));
			}
			System.out.println(session.getSessionName()+"내 그룹 리스트 탐색 완료");

		
		}
		//서버 내 세션 마다 그룹 정보 조회
		
	}
	public void testPrintGroupMembers()
	{
		System.out.println("====== print group members\n");
		CMMember groupMembers = m_clientStub.getGroupMembers();
		CMUser myself = m_clientStub.getMyself();
		System.out.println("My name: "+myself.getName()+"\n");
		if(groupMembers == null || groupMembers.isEmpty())
		{
			System.out.println("No group member yet!\n");
			return;
		}
		System.out.println(groupMembers.toString()+"\n");
	}
	public void getGroupInfoInSession() {
		//각 세션의 정보 얻기 > 그룹별 사람 수
		CMMember groupMembers = m_clientStub.getGroupMembers();
		CMUser myself = m_clientStub.getMyself();
		System.out.println("My name: "+myself.getName());
		if(groupMembers == null || groupMembers.isEmpty())
		{
			System.out.println("No group member yet!");
			return;
		}
		System.out.println(groupMembers.toString());		
	}
	
	public void showRanking() {
		//getGroupInfoInSession("session1");

		System.out.println("showRanking");
	    //랭킹 조회 페이지 생성 
		//디비에서 유저 정보 가져오기
		//순서대로 나열하기(쿼리로)
		JFrame rankingFrame =new JFrame();
		rankingFrame.setTitle("랭킹 조회");
		rankingFrame.setSize(600,800);
		rankingFrame.setLayout(new BorderLayout());
		
		JTextField rankingText=new JTextField("RANKING");
		rankingText.setHorizontalAlignment(JTextField.CENTER);
		
		ClueCMDBManager cluedbmanager=new ClueCMDBManager();
		
		String[] ret=cluedbmanager.queryForRanking(m_clientStub.getCMInfo());
		JList listPane=null;
		if(ret!=null) {
			listPane=new JList(ret);
			listPane.setAlignmentX(CENTER_ALIGNMENT);;
		}
		

		//TextArea text=new TextArea(10,2);
			
		rankingFrame.add(rankingText,BorderLayout.NORTH);
		rankingFrame.add(listPane,BorderLayout.CENTER);
		//rankingFrame.add(text);
		rankingFrame.setVisible(true);
		
		
		
		
		
		
	}
	public void testStartCM()
	{
		// get current server info from the server configuration file
		String strCurServerAddress = null;
		int nCurServerPort = -1;
		String strNewServerAddress = null;
		String strNewServerPort = null;
		
		strCurServerAddress = m_clientStub.getServerAddress();
		nCurServerPort = m_clientStub.getServerPort();
		
		// ask the user if he/she would like to change the server info
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("========== start CM");
		System.out.println("current server address: "+strCurServerAddress);
		System.out.println("current server port: "+nCurServerPort);
		
		try {
			System.out.print("new server address (enter for current value): ");
			strNewServerAddress = br.readLine().trim();
			System.out.print("new server port (enter for current value): ");
			strNewServerPort = br.readLine().trim();

			// update the server info if the user would like to do
			if(!strNewServerAddress.isEmpty() && !strNewServerAddress.equals(strCurServerAddress))
				m_clientStub.setServerAddress(strNewServerAddress);
			if(!strNewServerPort.isEmpty() && Integer.parseInt(strNewServerPort) != nCurServerPort)
				m_clientStub.setServerPort(Integer.parseInt(strNewServerPort));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
/*
		boolean bRet = m_clientStub.startCM();
		if(!bRet)
		{
			System.err.println("CM initialization error!");
			return;
		}
		
		bRet =initUser(false);
		if(!bRet) {
			System.err.println("CM init(login or signup) user error!");
			return;
		}*/
		System.out.println("startCM");
	
	
		boolean chk=initUser(false);
		m_clientStub.syncJoinSession("session1");
		m_clientStub.changeGroup("g1");
		testCurrentUserStatus();
		

		//testSyncLoginDS();
		//frame.setM_clientStub(m_clientStub);
		//frame.getM_clientStub().setAppEventHandler(m_eventHandler);
		//frame.getM_clientStub().setAppEventHandler(frame.getM_eventHandler());
		//frame.getM_clientStub().startCM();
         
		if(chk) makeLogin2();
		else {
			JOptionPane.showConfirmDialog(null, "로그인 실패");
			initUser(false);
		}
		//로그인에 성공한 이후니까, 방선택하기 코드가 들어갈 자리입니다.
		
		startTest();
	}
	
		
		
	public void startTest()
	{
		System.out.println("client application starts.");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		m_scan = new Scanner(System.in);
		String strInput = null;
		int nCommand = -1;
		while(m_bRun)
		{
			System.out.println("Type \"0\" for menu.");
			System.out.print("> ");
			try {
				strInput = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
			try {
				nCommand = Integer.parseInt(strInput);
			} catch (NumberFormatException e) {
				System.out.println("Incorrect command number!");
				continue;
			}
			
			switch(nCommand)
			{
			case 0:
				//printAllMenus();
				break;
			case 1:
				//testDummyEvent();
				break;
			default:
				System.err.println("Unknown command.");
				break;
			}
		}
		
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_scan.close();
	}
	public void testSyncLoginDS()
	{
		String strUserName = null;
		String strPassword = null;
		CMSessionEvent loginAckEvent = null;

		System.out.print("====== synchronous login to default server\n");
		JTextField userNameField = new JTextField();
		JPasswordField passwordField = new JPasswordField();
		Object[] message = {
				"User Name:", userNameField,
				"Password:", passwordField
		};
		int option = JOptionPane.showConfirmDialog(null, message, "Login Input", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION)
		{
			strUserName = userNameField.getText();
			strPassword = new String(passwordField.getPassword()); // security problem?
			
			m_eventHandler.setStartTime(System.currentTimeMillis());
			loginAckEvent = m_clientStub.syncLoginCM(strUserName, strPassword);
			//long lDelay = System.currentTimeMillis() - m_eventHandler.getStartTime();
			if(loginAckEvent != null)
			{
				// print login result
				if(loginAckEvent.isValidUser() == 0)
				{
					System.out.print("This client fails authentication by the default server!\n");		
				}
				else if(loginAckEvent.isValidUser() == -1)
				{
					System.out.print("This client is already in the login-user list!\n");
				}
				else
				{
					//System.out.print("return delay: "+lDelay+" ms.\n");
					System.out.print("This client successfully logs in to the default server.\n");
					CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
					
					// Change the title of the client window
					setTitle("CM Client ("+interInfo.getMyself().getName()+")");

					// Set the appearance of buttons in the client frame window
					//setButtonsAccordingToClientState();
				}				
			}
			else
			{
				System.out.print("failed the login request!\n");
			}
			
		}
		
	}

	
	public Boolean initUser(Boolean isOnlyLogin) {
	      /*로그인을 테스트하기 위해서는
	       * 1. server conf. login scheme , dbscheme 을 1로 만든당
	       * 2. server conf에서 db정보를 자신의 로컬 디비 환경과 맞춘다(이 부분은 추후에 데스크톱에 서버를 돌리게 되면 고정아이피로 누구나 접속할 수 있도록 수정하겠습니다!)
	       * ex.DB_USE      1
	            DB_HOST      localhost
	            DB_USER      root
	            DB_PASS      자기비밀번호
	            DB_PORT      3306
	            DB_NAME   cluedb
	        3. sql에서  cluedb 스킴을 만들고, create table을 한다.
	         CREATE TABLE user_table(
	           id INTEGER NOT NULL AUTO_INCREMENT,
	           userName VARCHAR(20) NOT NULL,
	           password VARCHAR(40) NOT NULL,
	           ratio Float Not NULL DEFAULT 0.0,
	           creationTime Date NOT NULL DEFAULT CURRENT_TIMESTAMP,
	           PRIMARY KEY(id)    
	         );
	       4. insert 테스트 
	         INSERT INTO user_table(userName,password)
	         VALUES ('ddeung', sha1("12345"));
	       5. select로 확인
	         select *from user_table;

	      */

	   
		boolean bRequestResult = false;
		
		
		String strUserName = null;
		String strPassword = null;

		JTextField userNameField = new JTextField();
		JPasswordField passwordField = new JPasswordField();
		ButtonGroup group = new ButtonGroup();
		JRadioButton loginButton = new JRadioButton("login",true);
		JRadioButton signUpButton = new JRadioButton("signUp");
		group.add(loginButton);
		group.add(signUpButton);
		if(isOnlyLogin) {
			signUpButton.setEnabled(false);;
		}
		Object[] message = {
				"User Name:", userNameField,
				"Password:", passwordField,
				loginButton,
				signUpButton
		};
	    
	    
		int option = JOptionPane.showConfirmDialog(null, message, "Login Input", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION)
		{	
			strUserName = userNameField.getText();
			strPassword = new String(passwordField.getPassword()); // security problem?
			System.out.println(strUserName+ " " + strPassword);
			if(loginButton.isSelected()) {
				bRequestResult = m_clientStub.loginCM(strUserName, strPassword); //만약 등록안된 사용자라면 false 리턴함
				if(bRequestResult)
				{
					System.out.println("successfully sent the login request.\n");
				}
				else
				{
					System.out.println("failed the login request!\n");
				}
			}
			else {
				//m_clientStub.registerUser(strUserName, strPassword); //이미 존재하는 아이디라면 CMInteractionManager에서 걸러줌! -> 이거 server event thread 에서 캐치하는데 default로 처리해주는 함수랑 우리 db구조랑 안맞아서 dummyevent로 재정의함
				bRequestResult = registerUser(strUserName, strPassword);
				if(bRequestResult)
				{
					Object[] message2 = {"please wait until register your info...."};
					JOptionPane.showMessageDialog(null, message2);
					System.out.println("successfully sent the register request.\n");
				}
				else
				{
					System.out.println("failed the register request!\n");
				}
			}

		}

		return bRequestResult;
	}
	public Boolean registerUser(String strName, String strPasswd)
	{
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		int nState = -1;
		String strEncPasswd = null;
		nState = m_clientStub.getMyself().getState();
		if( nState == CMInfo.CM_INIT )
		{
			System.out.println("CMClientStub.registerUser(), client is not connected to "
					+ "the default server!");
			return false;
		}
		// encrypt password
		strEncPasswd = CMUtil.getSHA1Hash(strPasswd);
		if(CMInfo._CM_DEBUG)
			System.out.println("CMClientStub.registerUser(), user("+strName+") requested.");

		CMDummyEvent due = new CMDummyEvent();
		System.out.println(m_clientStub.getCMInfo().getConfigurationInfo().getMyAddress());
		String host = m_clientStub.getCMInfo().getConfigurationInfo().getMyAddress();
		due.setDummyInfo("registerUser#"+strName+"#"+strEncPasswd+"#"+host);
		m_clientStub.send(due, "SERVER"); //
		System.out.println("getSender: "+due.getSender());
		System.out.println("send message: "+due.getDummyInfo()); //서버로 전송한 CMDummyEvent의 내용 출력
		System.out.println("======\n");
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClueCMClient client = new ClueCMClient();
		CMClientStub cmStub = client.getClientStub();
		cmStub.setAppEventHandler(client.getClientEventHandler());
		//cmStub.loginCM("user1", "1234");
		System.out.println("Client application is terminated.");
	}

}
