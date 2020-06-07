import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ClueCMServerEventHandler implements CMAppEventHandler {
	private CMServerStub m_serverStub;
	private ClueCMServer m_server;

	public ClueCMServerEventHandler(CMServerStub serverStub, ClueCMServer server) {
		m_serverStub = serverStub;
		m_server = server;
	}

	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		System.out.println("processEvent: Server app receives CM event!!");
		switch (cme.getType()) {
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(cme);
			break;
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
		default:
			return;
		}
	}

	private void processSessionEvent(CMEvent cme) {
		CMConfigurationInfo confInfo = m_serverStub.getCMInfo().getConfigurationInfo();
		CMSessionEvent se = (CMSessionEvent) cme;
		switch (se.getID()) {
		case CMSessionEvent.LOGIN:
			// System.out.println("["+se.getUserName()+"] requests login.");
			printMessage("[" + se.getUserName() + "] requests login.\n");
			if (confInfo.isLoginScheme()) {
				// user authentication...
				// CM DB must be used in the following authentication..
				boolean ret = ClueCMDBManager.authenticateUser(se.getUserName(), se.getPassword(),
						m_serverStub.getCMInfo());
				if (!ret) {
					printMessage("[" + se.getUserName() + "] authentication fails!\n");
					m_serverStub.replyEvent(cme, 0);
				} else {
					printMessage("[" + se.getUserName() + "] authentication succeeded.\n");
					m_serverStub.replyEvent(cme, 1);
				}
			}
			break;
		case CMSessionEvent.LOGOUT:
			// System.out.println("["+se.getUserName()+"] logs out.");
			printMessage("[" + se.getUserName() + "] logs out.\n");
			break;
		case CMSessionEvent.REQUEST_SESSION_INFO:
			// System.out.println("["+se.getUserName()+"] requests session information.");
			printMessage("[" + se.getUserName() + "] requests session information.\n");
			break;
		case CMSessionEvent.CHANGE_SESSION:
			// System.out.println("["+se.getUserName()+"] changes to
			// session("+se.getSessionName()+").");
			printMessage("[" + se.getUserName() + "] changes to session(" + se.getSessionName() + ").\n");
			break;
		case CMSessionEvent.JOIN_SESSION:
			// System.out.println("["+se.getUserName()+"] requests to join
			// session("+se.getSessionName()+").");
			printMessage("[" + se.getUserName() + "] requests to join session(" + se.getSessionName() + ").\n");
			break;
		case CMSessionEvent.LEAVE_SESSION:
			// System.out.println("["+se.getUserName()+"] leaves a
			// session("+se.getSessionName()+").");
			printMessage("[" + se.getUserName() + "] leaves a session(" + se.getSessionName() + ").\n");
			break;
		case CMSessionEvent.ADD_NONBLOCK_SOCKET_CHANNEL:
			// System.out.println("["+se.getChannelName()+"] request to add SocketChannel
			// with index("
			// +se.getChannelNum()+").");
			printMessage("[" + se.getChannelName() + "] request to add a nonblocking SocketChannel with key("
					+ se.getChannelNum() + ").\n");
			break;
		case CMSessionEvent.REGISTER_USER:
			// System.out.println("User registration requested by
			// user["+se.getUserName()+"].");
			printMessage("User registration requested by user[" + se.getUserName() + "].\n");
			break;
		case CMSessionEvent.DEREGISTER_USER:
			// System.out.println("User deregistration requested by
			// user["+se.getUserName()+"].");
			printMessage("User deregistration requested by user[" + se.getUserName() + "].\n");
			break;
		case CMSessionEvent.FIND_REGISTERED_USER:
			// System.out.println("User profile requested for user["+se.getUserName()+"].");
			printMessage("User profile requested for user[" + se.getUserName() + "].\n");
			break;
		case CMSessionEvent.UNEXPECTED_SERVER_DISCONNECTION:
			printMessage("Unexpected disconnection from [" + se.getChannelName() + "] with key[" + se.getChannelNum()
					+ "]!\n");
			break;
		case CMSessionEvent.INTENTIONALLY_DISCONNECT:
			printMessage("Intentionally disconnected all channels from [" + se.getChannelName() + "]!\n");
			break;
		default:
			return;
		}
	}

	private void processDummyEvent(CMEvent cme) {
		CMDummyEvent due = (CMDummyEvent) cme;
		System.out.println("session(" + due.getHandlerSession() + "), group(" + due.getHandlerGroup() + ")");
		String msg = due.getDummyInfo();
		System.out.println("total message from sender: " + msg); // 클라이언트로부터 수신한 CMDummyEvent의 내용 출력
		String[] arrMsg = msg.split("#");
		String type = arrMsg[0];
		switch (type) {
			case "registerUser":
				registerUser(arrMsg[3],arrMsg[1],arrMsg[2]);
				break;
		}
		
		return;
	}
	
	private void registerUser(String sender,String strName, String strEncPasswd) {
		Boolean ret = false;
		CMDummyEvent due= new CMDummyEvent();
		
		ret = ClueCMDBManager.queryAlreadyUser(strName,m_serverStub.getCMInfo()) == -1 ? true : false; //-1: 기존 아이디에 없음, 1;기존 id에 있음
		if(ret) {
			String str = ClueCMDBManager.queryInsertUser(strName,strEncPasswd,m_serverStub.getCMInfo()) == -1 ? "false" : "true";
			due.setDummyInfo("reply#registerUser#"+str);
		}
		else {
			//해당 아이디 존재
			due.setDummyInfo("reply#registerUser#false");
			
		}
		
		
		//해겷해야하는 문제 -> login group 에 들어가있지 않으면 unicast가 안됨 ! 시도해본 해결 방법: login 실패하는 것처럼 일단 user에 넣고, send 하고 빼는 방법 
		
		m_serverStub.send(due,sender); 
		System.out.println("send message: "+due.getDummyInfo()); //클라이언트로 전송한 CMDummyEvent의 내용 출력
		System.out.println("======\n");
	}

	private void printMessage(String strText) {
		/*
		 * m_outTextArea.append(strText);
		 * m_outTextArea.setCaretPosition(m_outTextArea.getDocument().getLength());
		 */
		System.out.println("printMessage: " + strText);
	}
}
