import java.util.Iterator;

import javax.swing.JOptionPane;

import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class ClueCMClientEventHandler implements CMAppEventHandler {

	private CMClientStub m_clientStub;
	private ClueCMClient m_client;
	// private ClueCMClientGame m_game;

	// Dummy info type
	public class DummyType {
		public static final String GameStart = "initGameInfo";
		public static final String SetTurn = "SetTurn";
		public static final String Reasoning = "Reasoning";
		public static final String ReasoningAnswer = "ReasoningAnswer";
		public static final String CastGroup = "CastGroup";
		public static final String Target = "Target";
		public static final String TargetReturn = "TargetReturn";
		public static final String Ranking = "rankingInfo";
	}

	public ClueCMClientEventHandler(CMClientStub stub, ClueCMClient client) {
		m_client = client;
		m_clientStub = stub;
		// m_game = game;
	}

	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		System.out.println("processEvent: Client app receives CM event!!");
		switch (cme.getType()) {
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(cme);
		
		default:
			break;
		}
	}

	private void processDummyEvent(CMEvent cme) {
		CMDummyEvent due = (CMDummyEvent) cme;
		String msg = due.getDummyInfo();
		System.out.println("total message from sender: " + msg);
		String[] arrMsg = msg.split("#");
		String type = arrMsg[0];
		switch (type) {
		case "invite":
			int inviteRequest=JOptionPane.showConfirmDialog(null, arrMsg[1], "초대", JOptionPane.OK_OPTION,JOptionPane.CANCEL_OPTION);
		    CMSessionEvent chk=m_clientStub.syncRequestSessionInfo();
			if(chk!=null&&inviteRequest==0) {
		    	
		    	m_clientStub.joinSession(arrMsg[2]);
		    	m_clientStub.changeGroup("g2");
		    	System.out.println("successtoMakeNewGroup");
		    }
		    else
		    	System.out.println("failtomakeNewGroup");
		    break;
		case "reply":
			printMessage(arrMsg[1] + " result: " + arrMsg[2]);
			break;
		case "userNumInfo":
			int s=m_clientStub.getGroupMembers().getMemberNum();
			if((arrMsg[1].charAt(7)-'0')==s) 
			{
				CMDummyEvent duek = new CMDummyEvent();
				duek.setDummyInfo("startGame#" + arrMsg[1] + "#g1");
				m_clientStub.send(duek, "SERVER"); // notify the sever to start the game
			}
					
			break;
		case DummyType.GameStart:
			System.out.println("GameStart: ");
			String answer[] = arrMsg[1].split(",");
			String myCard[] = arrMsg[2].split(",");
			String nextTurn = arrMsg[3];
			String currentTurn = arrMsg[4];
			String openCard[] = null;
			if (!arrMsg[5].equals("NULL")) {
				openCard = arrMsg[5].split(",");
			}
			String playerTurn[] = arrMsg[6].split(",");
			System.out.println(arrMsg[1]);
			System.out.println(arrMsg[2]);
			System.out.println(arrMsg[3]);
			System.out.println(arrMsg[4]);
			System.out.println(arrMsg[5]);
			System.out.println(arrMsg[6]);
			m_client.play.env.initEnv(answer, myCard, nextTurn, currentTurn, openCard, playerTurn);
			m_client.play.startGame();
			m_client.play.env.printInitGame();
			break;
		case DummyType.SetTurn:
			m_client.play.setCurrentTurn(arrMsg[1]);
			break;
		case DummyType.Reasoning:
			m_client.play.checkReason(msg);
			break;
		case DummyType.ReasoningAnswer:
			m_client.play.receiveReason(msg); // 증명함
			break;
		case DummyType.CastGroup:
			m_client.play.printMessage(arrMsg[1]);
			break;
		case DummyType.Target:
			m_client.play.cardQ_targetReturn(arrMsg[2], arrMsg[1]);
			break;
		case DummyType.TargetReturn:
			m_client.play.openCardToSb(arrMsg[1]);
			break;
		case DummyType.Ranking:
			setRankingInfo(arrMsg);
			
		}
		return;
	}
	private void setRankingInfo(String[] arrMsg) {
		String[] list = new String[arrMsg.length-1]; //0번쨰는 type이니까
		if(arrMsg.length == 2) {
			list[0] = arrMsg[1]; //there is no user
		}
		else {
			for(int i = 1; i<arrMsg.length;i++) {
				list[i-1] = i+"    "+arrMsg[i];
				System.out.println(list[i-1]);
			}	
		}
		m_client.disappearInfoDialog();
		m_client.showRanking(list);
	}

	private void processSessionEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		CMSessionEvent se = (CMSessionEvent) cme;
		switch (se.getID()) {
		case CMSessionEvent.LOGIN_ACK:
			if (se.isValidUser() == 0) {
				System.out.println("This client fails authentication by the default server!\n");
				m_client.disappearInfoDialog();
				JOptionPane.showMessageDialog(null, "등록되지 않은 사용자입니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
				m_client.terminateCM(); //종료
			} else if (se.isValidUser() == -1) {
				System.out.println("This client is already in the login-user list!\n");
			} else {
				System.out.println("This client successfully logs in to the default server.\n");
				m_client.disappearInfoDialog();
				m_client.makeLogin2();
			}
			break;
		case CMSessionEvent.RESPONSE_SESSION_INFO:
			processRESPONSE_SESSION_INFO(se);
			break;
	
		case CMSessionEvent.REGISTER_USER_ACK:
			if(se.getReturnCode()==1) { //등록 성공
				m_client.disappearInfoDialog();
				m_client.initUser();
			}
			else { //실패
				m_client.disappearInfoDialog();
				JOptionPane.showMessageDialog(null, "중복되는 id입니다.", "회원가입 실패", JOptionPane.ERROR_MESSAGE);
				m_client.terminateCM(); //종료
			}
			break;
		}
	}

	
	private void processRESPONSE_SESSION_INFO(CMSessionEvent se) {
		Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();

		printMessage(String.format("%-60s%n", "------------------------------------------------------------"));
		printMessage(String.format("%-20s%-20s%-10s%-10s%n", "name", "address", "port", "user num"));
		printMessage(String.format("%-60s%n", "------------------------------------------------------------"));

		while (iter.hasNext()) {
			CMSessionInfo tInfo = iter.next();
			printMessage(String.format("%-20s%-20s%-10d%-10d%n", tInfo.getSessionName(), tInfo.getAddress(),
					tInfo.getPort(), tInfo.getUserNum()));
		}
	}

	private void printMessage(String strText) {
		/*
		 * m_outTextArea.append(strText);
		 * m_outTextArea.setCaretPosition(m_outTextArea.getDocument().getLength());
		 */
		System.out.println("printMessage: " + strText);
	}

}