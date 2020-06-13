import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
public class ClueCMClientGameEventHandler implements CMAppEventHandler {
	
	private ClueCMClientGame m_client;
	private CMClientStub m_clientStub;
	
	// Dummy info type
	public class DummyType { 
		public static final String GameStart = "initGameInfo";
		public static final String SetTurn = "SetTurn";
		public static final String Reasoning = "Reasoning";
		public static final String ReasoningAnswer = "ReasoningAnswer";
		public static final String CastGroup = "CastGroup";
		public static final String Target = "Target";
		public static final String TargetReturn = "TargetReturn";
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
		String msg = due.getDummyInfo();
		System.out.println("total message from sender: " + msg);
		String[] arrMsg = msg.split("#");
		String type = arrMsg[0];
		switch(type) {
			case DummyType.GameStart:
				String answer[] = arrMsg[1].split(",");
				String myCard[] = arrMsg[2].split(",");
				String nextTurn = arrMsg[3];
				String currentTurn = arrMsg[4];
				String openCard[] = null;
				if(!arrMsg[5].equals("NULL")) {
					openCard = arrMsg[5].split(",");
				}
				String playerTurn[] = arrMsg[6].split(",");
				m_client.env.initEnv(answer, myCard, nextTurn, currentTurn, openCard, playerTurn);
				m_client.startGame();
				break;
			case DummyType.SetTurn:
				m_client.setCurrentTurn(arrMsg[1]);
				break;
			case DummyType.Reasoning:
				m_client.checkReason(msg);
				break;
			case DummyType.ReasoningAnswer:
				m_client.receiveReason(msg); // 증명함
				break;
			case DummyType.CastGroup:
				m_client.printMessage(arrMsg[1]);
				break;
			case DummyType.Target:
				m_client.cardQ_targetReturn(arrMsg[2], arrMsg[1]);
				break;
			case DummyType.TargetReturn:
				
				break;
		}
		return;
	}
	
	
}
