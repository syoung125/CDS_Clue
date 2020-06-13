import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.lang.String;

import kr.ac.konkuk.ccslab.cm.*;
import kr.ac.konkuk.ccslab.cm.stub.*;
import kr.ac.konkuk.ccslab.cm.entity.*;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;


public class initGame {

	private static String card[] = {"JAMES", "JIN", "YUMI", "DEER", "WOONG", "BOBBY", "KNIFE", "PIPE", "ROPE", "GUN", "HAMMER", "WRENCH",
			"GARAGE", "BATHROOM", "KITCHEN", "YARD", "LIVINGROOM", "BALCONY", "DININGROOM", "BEDROOM", "LIBRARY"};
	
	private static int character; // card[0] ~ card[5] (cbr)
	private static int weapon; // card[6] ~ card[11] (cbr)
	private static int place; // card[12] ~ card[20] (cbr)
	
	private static int playerCard[] = new int[18]; // 플레이어들의 카드가 저장된 배열 (cbr)
	
	private static String playerTurn[]; // 플레이어들의 순서가 저장된 배열 (cbv)
	
	CMUser cmUser=new CMUser();
	
	
	// 플레이어들의 순서를 지정
	public void playersTurn(String session, String group,CMServerStub cs) {
		
		CMServerStub m_serverStub = cs;
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		
		CMSession se=interInfo.findSession(session);
		
	//	CMMember cmMember = new CMMember();
		Random random = new Random();
		CMGroup cmG=se.findGroup(group);
		
		Vector<CMUser> pVector=cmG.getGroupUsers().getAllMembers();
		int pNum=cmG.getGroupUsers().getMemberNum();
		System.out.println(pVector.size());
		playerTurn=new String[pNum];
		for(int i=0; i<pNum; i++) {//클라이언트마다 순서부여 
			int ran=random.nextInt(pVector.size());
			String str=pVector.get(ran).getName();
			System.out.println(str);
			playerTurn[i] =str;
			pVector.remove(ran);
		}	
		
	}

	// 정답카드 생성
	public void answerCard() {
		
		Random random = new Random();
		character = random.nextInt(6);
		weapon = random.nextInt(6) + 6;
		place = random.nextInt(9) + 12;

	}
	
	 // 정답 카드 제외 분배
	public void distributeCard() {
		
		Random random = new Random();
		
		// 21개의 카드를 나열
		int arr[] = new int[21];
		for(int i=0; i<21; i++) {
			arr[i] = random.nextInt(21);
			for(int j=0; j<i; j++) {
				if(arr[i]==arr[j]) {
					i--;
				}
			}
		}

		// 정답 카드를 제외한 18개의 카드를 분배
		int k = 0;
		
		for(int i=0; i<21; i++) {
			
			if(arr[i] != character && arr[i] != weapon && arr[i] != place) {
				playerCard[k] = arr[i];
				k++;
			}
		}

	}
	
	
	// 게임을 하는 클라이언트에게 정답과 자기 카드 다음사람과 오픈할 카드가 있을 경우 오픈할 카드 보내줌
	public void registerCard(String session, String group,CMServerStub cs) {
		
		CMServerStub m_serverStub = cs;
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		
		CMSession se=interInfo.findSession(session);
		CMGroup cmG=se.findGroup(group);
		
		//게임을 하는 사람들 수
		int gnum=cmG.getGroupUsers().getMemberNum();
				
		StringBuilder sb=new StringBuilder("initGameInfo#");
		sb.append(card[character]);
		sb.append(",");
		sb.append(card[weapon]);
		sb.append(",");
		sb.append(card[place]);

		int cNum = 18 - (gnum*3); // 공개할 카드의 수
		
		CMDummyEvent due=new CMDummyEvent();
		//사용자에게 카드를 분배하고 정답카드와 본인 다음 사람과 처음 시작하는 사람 6명 미만일 경우 오픈할 카드를 보냄
		//initGameInfo#범인,살인도구,장소#본인카드1,본인카드2,본인카드3#다음사람#처음시작하는사람#오픈할카드 없으면 NULL
		StringBuilder turn =new StringBuilder();
		for(int i=0;i<gnum;i++) {
			turn.append(playerTurn[i]);
		}
		String sTurn=turn.toString();
		sTurn=sTurn.substring(0,sTurn.length()-1);
		
		for(int i=0;i<gnum;i++) {			
			String str=card[playerCard[3*i]]+","+card[playerCard[3*i+1]]
					+","+card[playerCard[3*i+2]];//사용자에게 분배되는 카드
			
			if(gnum==6) {//6명일 경우 오픈할 카드가 없음
				if(i==gnum-1) {
					due.setDummyInfo(sb.toString()+"#"+str+"#"+playerTurn[0]+"#"+
							playerTurn[0]+"#"+"NULL"+"#"+sTurn);
				}else {
					due.setDummyInfo(sb.toString()+"#"+str+"#"+playerTurn[i+1]+"#"+
							playerTurn[0]+"#"+"NULL"+"#"+sTurn);
				}
			}
			else {
				StringBuilder open=new StringBuilder("#");
				for(int j=0;j<cNum;j++) {
					open.append(playerCard[gnum*3+j]+",");
				}
				if(i==gnum-1) {
					due.setDummyInfo(sb.toString()+"#"+str+"#"+playerTurn[i+1]+"#"+
							playerTurn[0]+open+"#"+sTurn);
				}else {
					due.setDummyInfo(sb.toString()+"#"+str+"#"+playerTurn[i+1]+"#"+
							playerTurn[0]+open+"#"+sTurn);
				}
			}
			m_serverStub.send(due, playerTurn[i]);
			
		}
	
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
	}

}
