import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import org.json.*;
public class ClueCMClientGameEventHandler implements CMAppEventHandler {
	
	private ClueCMClientGame m_client;
	private CMClientStub m_clientStub;
	
	// Dummy info type
	public class DummyType { 
		public static final String GameStart = "GameStart";
	
	}
	
	public ClueCMClientGameEventHandler(CMClientStub clientStub, ClueCMClientGame client)
	{
		m_client = client;
		m_clientStub = clientStub;
	}
	
	@Override
	public void processEvent(CMEvent cme) {
		switch(cme.getType())
		{
		case CMInfo.CM_SESSION_EVENT:
//			processSessionEvent(cme);
			break;
		case CMInfo.CM_INTEREST_EVENT:
//			processInterestEvent(cme);
			break;
		case CMInfo.CM_DATA_EVENT:
//			processDataEvent(cme);
			break;
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
		case CMInfo.CM_USER_EVENT:
//			processUserEvent(cme);
			break;
		case CMInfo.CM_FILE_EVENT:
//			processFileEvent(cme);
			break;
		case CMInfo.CM_SNS_EVENT:
//			processSNSEvent(cme);
			break;
		case CMInfo.CM_MULTI_SERVER_EVENT:
//			processMultiServerEvent(cme);
			break;
		case CMInfo.CM_MQTT_EVENT:
//			processMqttEvent(cme);
			break;
		default:
			return;
		}	
	}
	
	///////////////////////////////////////////////////////////
	
	private void processDummyEvent(CMEvent cme)
	{
		CMDummyEvent due = (CMDummyEvent) cme;
//		printMessage("session("+due.getHandlerSession()+"), group("+due.getHandlerGroup()+")\n");
//		printMessage("dummy msg: "+due.getDummyInfo()+"\n");
		//parse해서 (type#msg) 이런식으로 전송
		if(due.getDummyInfo() == DummyType.GameStart) {
			// 서버에서 받은 것일 때 (할수있나)(왜냐면 채팅에서 GameStart칠수도있으니까)
			// msg parse -> 인자로 게임초기화 정보 보냄
//			m_client.setGameStart();
		}
		return;
	}
	
	///////////////////////////////////////////////////////////
	
	private void printMessage(String strText)
	{
		m_client.printMessage(strText);
		return;
	}
	
	
}
