package clue;

import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ClueServer {

	private CMServerStub m_serverStub;
    private ClueServerEventHandler m_eventHandler;
    
    public ClueServer() {
    	set_serverStub(new CMServerStub());
    	set_eventHandler(new ClueServerEventHandler(get_serverStub()));
    }
	


	public CMServerStub get_serverStub() {
		return m_serverStub;
	}

	public void set_serverStub(CMServerStub m_serverStub) {
		this.m_serverStub = m_serverStub;
	}

	public ClueServerEventHandler get_eventHandler() {
		return m_eventHandler;
	}

	public void set_eventHandler(ClueServerEventHandler m_eventHandler) {
		this.m_eventHandler = m_eventHandler;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ClueServer server=new ClueServer();
		server.get_serverStub().setAppEventHandler(server.get_eventHandler());
		server.get_serverStub().startCM();
	}

}
