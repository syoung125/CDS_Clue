package clue;

import java.util.Iterator;

import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class Intro2EventHandler implements CMAppEventHandler {
	
	private Intro2 m_intro2;
	
	public Intro2EventHandler( Intro2 intro2) {
		m_intro2 = intro2;
	}
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
			System.out.println("intro2 response session info");
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
		int session_cnt=1;
		while(iter.hasNext()) {
			CMSessionInfo tInfo=iter.next();
			System.out.println(tInfo.getSessionName()+"세션 유저 수: "+tInfo.getUserNum());
			if(tInfo.getUserNum()!=0) {//이미 사용자가 존재하는 세션
				//processJOIN_SESSION_ACK(se);
				m_intro2.getM_clientStub().joinSession(tInfo.getSessionName());
				
			}
		}
	}
	private void processJOIN_SESSION_ACK(CMSessionEvent se) {
		System.out.println("processJOIN_SESSION_ACK");
		Iterator<CMGroupInfo> iter=se.getGroupInfoList().iterator();
        while(iter.hasNext()) {
			CMGroupInfo gInfo=iter.next();
			System.out.println("세션 내 그룹:"+gInfo.getGroupName());
			//현재 조인 세션하면 무조건 g1으로 가게 되어있음.
			//그룹마다 그룹원 수 체크가 불가
			//m_intro2.getM_clientStub().changeGroup(gInfo.getGroupName());
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
