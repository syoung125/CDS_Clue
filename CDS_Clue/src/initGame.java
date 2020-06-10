import java.util.Random;
import java.util.Vector;
import java.lang.String;

import kr.ac.konkuk.ccslab.cm.*;
import kr.ac.konkuk.ccslab.cm.stub.*;
import kr.ac.konkuk.ccslab.cm.entity.*;

public class initGame {

	public static String card[] = {"JAMES", "JIN", "YUMI", "DEER", "WOONG", "BOBBY", "KNIFE", "PIPE", "ROPE", "GUN", "HAMMER", "WRENCH",
			"GARAGE", "BATHROOM", "KITCHEN", "YARD", "LIVINGROOM", "BALCONY", "DININGROOM", "BEDROOM", "LIBRARY"};
	
	public static int character; // card[0] ~ card[5] (cbr)
	public static int weapon; // card[6] ~ card[11] (cbr)
	public static int place; // card[12] ~ card[20] (cbr)
	public static int playerCard[] = new int[18]; // 플레이어들의 카드가 저장된 배열 (cbr)
	
	public static String playerTurn[]; // 플레이어들의 순서가 저장된 배열 (cbv)
	
	// 플레이어들의 순서를 지정
	private void playerTurn() {
		
		CMMember cmMember = new CMMember();
		Random random = new Random();
		
		int pNum = cmMember.getMemberNum(); /* CM method 사용 */
		Vector<CMUser> pVector = cmMember.getAllMembers(); /* CM method 사용 */
				
		// 플레이어 수 만큼의 숫자 랜덤 나열
		int arr[] = new int[pNum];
		for(int i=0; i<pNum; i++) {
			arr[i] = random.nextInt(pNum);
			for(int j=0; j<i; j++) {
				if(arr[i]==arr[j]) {
					i--;
				}
			}
		}	

	}

	// 정답카드 생성
	private void answerCard() {
		
		Random random = new Random();
		character = random.nextInt(6);
		weapon = random.nextInt(6) + 6;
		place = random.nextInt(9) + 12;

	}
	
	// 정답 카드 제외 분배
	private void distributeCard() {
		
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
	
	// 카드 분배 후 공개할 카드
	private void openCard() {
		
		CMMember cmMember = new CMMember();
		int pNum = cmMember.getMemberNum(); /* CM method 사용 */
		int cNum = 18 - (pNum*3); // 공개할 카드의 수
		int[] openCard = new int[cNum]; // 공개할 카드의 배열 (cbr)
		
		for(int i =0; i<cNum; i++) {
			openCard[i] = playerCard[pNum*3 + i];
		}		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		initGame initGame = new initGame();
		initGame.answerCard();
		initGame.distributeCard();
	}

}
