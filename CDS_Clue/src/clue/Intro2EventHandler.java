package clue;

import java.util.Iterator;

import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;

public class Intro2EventHandler implements CMAppEventHandler {
	
	private void processDummyEvent(CMEvent cme)
	{
		CMDummyEvent due = (CMDummyEvent) cme;
		System.out.println(">>>>> REPLY FROM ["+due.getSender()+"] MSG: "+due.getDummyInfo());
		return;
	}
	
	private void processSessionEvent(CMEvent cme)
	{
		CMSessionEvent se = (CMSessionEvent) cme;
		switch(se.getID()) {
		
		case CMSessionEvent.RESPONSE_SESSION_INFO:
			processRESPONSE_SESSION_INFO(se);
			break;
		case CMSessionEvent.JOIN_SESSION_ACK:
			processJOIN_SESSION_ACK(se);
			break;
		default:
			return;
		
		}
		return;
	}
	private void processRESPONSE_SESSION_INFO(CMSessionEvent se) {
		
		Iterator<CMSessionInfo> iter=se.getSessionInfoList().iterator();
		while(iter.hasNext()) {
			CMSessionInfo tInfo=iter.next();
			System.out.println(tInfo.getSessionName()+"세션 유저 수: "+tInfo.getUserNum());
			
		}
	}
	private void processJOIN_SESSION_ACK(CMSessionEvent se) {
		
		Iterator<CMGroupInfo> iter=se.getGroupInfoList().iterator();
        while(iter.hasNext()) {
			CMGroupInfo gInfo=iter.next();
			System.out.println(gInfo.getGroupName());
			
		}
	}
	@Override
	public void processEvent(CMEvent arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getType()) {
		
			case CMInfo.CM_DUMMY_EVENT:
				processDummyEvent(arg0);
				break;
				
			case CMInfo.CM_SESSION_EVENT:
				processSessionEvent(arg0);
				break;
			default:
				return;
			
		
		}
   
	}

}
