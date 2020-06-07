import com.mysql.fabric.xmlrpc.Client;

import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class ClueCMClientEventHandler implements CMAppEventHandler {
	private CMClientStub m_clientStub;
	private ClueCMClient m_client;
	public ClueCMClientEventHandler(CMClientStub stub, ClueCMClient client) {
		m_client = client;
		m_clientStub = stub;
	}

	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		System.out.println("processEvent: Client app receives CM event!!");
		switch (cme.getType()) {
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
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
		switch(type) {
			case "reply":
				printMessage(arrMsg[1]+" result: "+arrMsg[2]);
				processReplyEvent(arrMsg[1]);
				break;
		}
		return;
	}
	private void processReplyEvent(String type) {
		switch(type) {
			case "registerUser":
				m_client.initUser(true);
				break;
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
