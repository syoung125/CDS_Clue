package clue;


import java.util.Iterator;

import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ClueServerEventHandler implements CMAppEventHandler{

	private CMServerStub m_serverStub;
	public ClueServerEventHandler(CMServerStub serverStub) {
		setM_serverStub(serverStub);
	}
	

	
	@Override
	public void processEvent(CMEvent arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getType()) {
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(arg0);
			break;
		default:
			return;
			
			
		}
   
	}
	private void processSessionEvent(CMEvent arg0) {
		
		CMSessionEvent se=(CMSessionEvent) arg0;
		Iterator<CMGroupInfo> iter=se.getGroupInfoList().iterator();
        while(iter.hasNext()) {
			CMGroupInfo gInfo=iter.next();
			System.out.println(gInfo.getGroupName());
			
		}
	}
	public CMServerStub getM_serverStub() {
		return m_serverStub;
	}

	public void setM_serverStub(CMServerStub m_serverStub) {
		this.m_serverStub = m_serverStub;
	}
	

}
