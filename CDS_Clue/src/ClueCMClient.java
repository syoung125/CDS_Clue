import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import kr.ac.konkuk.ccslab.cm.util.CMUtil;
public class ClueCMClient {
	
	private CMClientStub m_clientStub;
	private ClueCMClientEventHandler m_eventHandler;
	private boolean m_bRun;
	private Scanner m_scan = null;
	public ClueCMClient(){
		m_clientStub = new CMClientStub();
		m_eventHandler = new ClueCMClientEventHandler(m_clientStub,this);
		m_bRun = true;
	}
	public CMClientStub getClientStub()
	{
		return m_clientStub;
	}
	
	public ClueCMClientEventHandler getClientEventHandler()
	{
		return m_eventHandler;
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
	
	
	public Boolean initUser(Boolean isOnlyLogin) {
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
	private void printMessage(String strText)
	{
		/*
		m_outTextArea.append(strText);
		m_outTextArea.setCaretPosition(m_outTextArea.getDocument().getLength());
		*/
		System.out.println("printMessage: "+strText);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClueCMClient client = new ClueCMClient();
		CMClientStub cmStub = client.getClientStub();
		cmStub.setAppEventHandler(client.getClientEventHandler());
		client.testStartCM();
		System.out.println("Client application is terminated.");
	}

}
