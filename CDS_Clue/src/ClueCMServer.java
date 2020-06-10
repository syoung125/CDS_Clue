import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;


public class ClueCMServer {
	
	private CMServerStub m_serverStub;
	private ClueCMServerEventHandler m_eventHandler;

	public ClueCMServer() {
		m_serverStub = new CMServerStub();
		m_eventHandler = new ClueCMServerEventHandler(m_serverStub, this);
	}
	
	public CMServerStub getServerStub()
	{
		return m_serverStub;
	}
	
	public ClueCMServerEventHandler getServerEventHandler()
	{
		return m_eventHandler;
	}
	public void startCM()
	{
		// get current server info from the server configuration file
		String strSavedServerAddress = null;
		String strCurServerAddress = null;
		int nSavedServerPort = -1;
		String strNewServerAddress = null;
		String strNewServerPort = null;
		int nNewServerPort = -1;
		
		strSavedServerAddress = m_serverStub.getServerAddress();
		strCurServerAddress = CMCommManager.getLocalIP();
		nSavedServerPort = m_serverStub.getServerPort();
		
		// ask the user if he/she would like to change the server info
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("========== start CM");
		System.out.println("detected server address: "+strCurServerAddress);
		System.out.println("saved server port: "+nSavedServerPort);
		
		try {
			System.out.print("new server address (enter for detected value): ");
			strNewServerAddress = br.readLine().trim();
			if(strNewServerAddress.isEmpty()) strNewServerAddress = strCurServerAddress;

			System.out.print("new server port (enter for saved value): ");
			strNewServerPort = br.readLine().trim();
			try {
				if(strNewServerPort.isEmpty()) 
					nNewServerPort = nSavedServerPort;
				else
					nNewServerPort = Integer.parseInt(strNewServerPort);				
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
			// update the server info if the user would like to do
			if(!strNewServerAddress.equals(strSavedServerAddress))
				m_serverStub.setServerAddress(strNewServerAddress);
			if(nNewServerPort != nSavedServerPort)
				m_serverStub.setServerPort(Integer.parseInt(strNewServerPort));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		boolean bRet = m_serverStub.startCM(); //CMDBMANAGER init
		if(!bRet)
		{
			System.err.println("CM initialization error!");
			return;
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClueCMServer server = new ClueCMServer();
		CMServerStub cmStub = server.getServerStub();
		cmStub.setAppEventHandler(server.getServerEventHandler());
		server.startCM();
	}


}
