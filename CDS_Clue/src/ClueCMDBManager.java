import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;

public class ClueCMDBManager extends CMDBManager {

	// CMDBManager init은 cm.manager> CMInteractionManager 내부에서 함

	public static ArrayList<String> queryGetRanking(CMInfo cmInfo)
	{
		String url = cmInfo.getDBInfo().getDBURL();
		String user = cmInfo.getConfigurationInfo().getDBUser();
		String pass = cmInfo.getConfigurationInfo().getDBPass();

		System.out.println(url + " " + user + " " + pass);
		ResultSet rs = null;
		ArrayList<String> userList = null;
		String strQuery = "select userName, ratio from user_table order by ratio desc";
		System.out.println("strQuery: " + strQuery);

		rs = sendSelectQuery(strQuery, cmInfo);
		if(rs != null)
			userList = new ArrayList<String>();
		
		try {
			while(rs != null && rs.next())
			{
				userList.add(rs.getString("userName")+"            "+rs.getString("ratio"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CMDBManager.closeDB(cmInfo);
			CMDBManager.closeRS(rs);
		}
		return userList;
	}
	
	

}
