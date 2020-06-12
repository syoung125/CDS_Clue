package clue;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;

public class ClueCMDBManager extends CMDBManager {

	// CMDBManager init은 cm.manager> CMInteractionManager 내부에서 함

	public static String[] queryForRanking(CMInfo cmInfo) {
		String strQuery = "select * from  user_table ORDER BY ratio DESC;";
		ResultSet rs = sendSelectQuery(strQuery, cmInfo);
		try {
							
			if(rs != null ) {
				String[] result= {};
				int count=0;
				while(rs.next()) {
					String name=rs.getString("name");
					float ratio=rs.getFloat("ratio");
					result[count]=name+"   "+Float.toString(ratio);
					
				}
				return result;
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CMDBManager.closeDB(cmInfo);
			CMDBManager.closeRS(rs);
		}
		return null; //없는게 -1

	}

	public static int queryAlreadyUser(String strName, CMInfo cmInfo) {
		String strQuery = "select * from  user_table where userName='" + strName + "';";
		ResultSet rs = sendSelectQuery(strQuery, cmInfo);
		try {
			if(rs != null && rs.next())
				return 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CMDBManager.closeDB(cmInfo);
			CMDBManager.closeRS(rs);
		}
		return -1; //없는게 -1

	}

	public static int queryInsertUser(String name, String password, CMInfo cmInfo) {
		String strQuery = "insert into user_table (userName, password) values ('" + name + "','" + password + "');";
		int ret = sendUpdateQuery(strQuery, cmInfo);

		if (ret == -1) {
			System.out.println("ClueCMDBManager.queryInsertUser(), error!");
			return ret;
		}

		if (CMInfo._CM_DEBUG)
			System.out.println("ClueCMDBManager.queryInsertUser(), return value(" + ret + ").");

		return ret;
	}

	public static boolean authenticateUser(String strUserName, String strPassword, CMInfo cmInfo) {
		boolean bValidUser = false;
		String strQuery = "select * from user_table where userName='" + strUserName + "' and password='" + strPassword
				+ "';";
		// cmInfo.getDBInfo().setDBURL(cmInfo.getDBInfo().getDBURL()+"&useSSL=false&serverTimezone=UTC");
		String url = cmInfo.getDBInfo().getDBURL();
		String user = cmInfo.getConfigurationInfo().getDBUser();
		String pass = cmInfo.getConfigurationInfo().getDBPass();

		System.out.println(url + " " + user + " " + pass);
		System.out.println("strQuery: " + strQuery);
		ResultSet rs = sendSelectQuery(strQuery, cmInfo);
		try {
			if (rs != null && rs.next())
				bValidUser = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CMDBManager.closeDB(cmInfo);
			CMDBManager.closeRS(rs);
		}

		return bValidUser;
	}

}
