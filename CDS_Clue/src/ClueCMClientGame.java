import java.awt.*;// 그래픽 처리를 위한 클래스들의 경로명
import java.awt.event.*; // AWT 이벤트 사용을 위한 경로명 
import javax.swing.*; // 스윙 컴포넌트 클래스들의 경로명 
import javax.swing.event.*; // 스윙 이벤트를 위한 경로
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import java.util.ArrayList;
import java.util.Random;

public class ClueCMClientGame extends JFrame {
   private String CHARACTER_ARR[] = {"JAMES", "JIN", "YUMI", "DEER", "WOONG", "BOBBY"};
   private String WEAPON_ARR[] = {"KNIFE", "PIPE", "ROPE", "GUN", "HAMMER", "WRENCH"};
   private String PLACE_ARR[] = {"GARAGE", "BATHROOM", "KITCHEN", "YARD", 
         "LIVINGROOM", "BALCONY", "DININGROOM", "BEDROOM", "LIBRARY"};
   
   // GUI 변수
   private JLabel turn;    // 실행 순서
   private JTextPane m_outTextPane;   // 채팅창
   private JTextField inputBox;   //채팅 입력 창
   private JButton inputBtn;   //입력 버튼
   private String inputBtnStr = "입력";
   private ArrayList<JButton> clueBtn = new ArrayList<JButton>();
   
   private BtnListener gamebtnlistener;
   private MyKeyListener gamekeylistener;
   
   // CM
   private CMClientStub m_clientStub;
//   private ClueCMClientGameEventHandler m_eventHandler;
   GameEnvironment env;
   
   CMUser myself;
   String userName = "Seoyoung";
   // 추리 관련 변수
   static Random random = new Random();
   public String beingPlace = "";

   enum pState {
      None,
      ChooseMenu,
      Guessing,
      cardQone,
      cardQtwo,
      cardQthree
   }
   public pState processState = pState.None;
   
   // Game variables
   public class GameEnvironment {
      
      public boolean bStart = false;
      // 서버에서 받아오는 값
      private String answer[] = new String[3];
      private String myCard[] = new String[3];
      private String nextTurn = "";
      public String currentTurn = "";
      
      private String guess[] = new String[3];   // 사용자가 추리한 값
      
      void initEnv(String[] answer, String[] myCard, String nextTurn, String currentTurn
            , String[] openCard, String[] playerTurn) {
         env.bStart = true;   // 게임시작
         
         for (int i = 0; i < answer.length; i++) {
            this.answer[i] = answer[i];
         }
         for (int i = 0; i < myCard.length; i++) {
            this.myCard[i] = myCard[i];
            setMyCard(findBtn(this.myCard[i])); // 내 카드 노란색으로
         }
         if(openCard != null) {
            for (int i = 0; i < openCard.length; i++) {
               setDisable(findBtn(openCard[i])); // 오픈카드 비활성화
            }
         }
         String pTurnStr = "  ";
         if(playerTurn != null) {
            pTurnStr += playerTurn[0];
            for (int i = 1; i < playerTurn.length; i++) {
               pTurnStr += (" --> "+playerTurn[i]);
            }
            turn.setText(pTurnStr);
         }
         this.nextTurn = nextTurn;
         this.currentTurn = currentTurn;
         
         // 채팅창 활성화
         inputBox.setText("");
         inputBox.setEnabled(true);
         inputBox.requestFocus();
         inputBtn.setEnabled(true);
      }
      
      // function for testing initGame
      public void printInitGame() {
         printMessage("\n");
         printMessage("---------initGame---------------\n");
         printMessage("Character: " + answer[0]);
         printMessage("\n");
         printMessage("Weapon: " + answer[1]);
         printMessage("\n");
         printMessage("Place: " + answer[2]);
         printMessage("\n");
         printMessage("MyCard: " + myCard[0] +", "+myCard[1] +", "+myCard[2]);
         printMessage("\n");
         printMessage("nextTurn: "+nextTurn);
         printMessage("\n");
         printMessage("--------------------------------\n");
         
      }
   }

   /////////////////////////////////////////////////////////////
   
   public ClueCMClientGame(CMClientStub clientStub) {
      // create a CM object and set the event handler
//      m_clientStub = new CMClientStub();
	   m_clientStub = clientStub;
//      m_eventHandler = new ClueCMClientGameEventHandler(m_clientStub, this);
      env = new GameEnvironment();
      CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
       myself = interInfo.getMyself();
//       userName = myself.getName(); // TODO: 주석풀기
       
      
            
      // start CM
//      startGame();
   }
   public void showDialog() {
	   makeGameGUI();
   }
   
   public void makeGameGUI() {
      setTitle("CLUE");
      setSize(800,600);
      
      gamebtnlistener=new BtnListener();
      gamekeylistener=new MyKeyListener();
      Container contentPane = getContentPane(); 
      contentPane.setLayout(new BorderLayout(0, 0));
       setContentPane(contentPane);

      
      JPanel pLeft = new JPanel(); 
      pLeft.setPreferredSize(new Dimension(400, 600));
      contentPane.add(pLeft, BorderLayout.WEST);
      JLabel turnLabel = new JLabel("    플레이 순서");
      turnLabel.setPreferredSize(new Dimension(400, 30));
      pLeft.add(turnLabel);
      turn = new JLabel("");
      turn.setPreferredSize(new Dimension(400, 30));
      pLeft.add(turn);
      JLabel label1 = new JLabel("    범인 ");
      label1.setPreferredSize(new Dimension(400, 30));
      pLeft.add(label1);
      pLeft.setLayout(new FlowLayout());
      for(int i=0;i<CHARACTER_ARR.length;i++) {
         JButton tBtn = new JButton(CHARACTER_ARR[i]);
         tBtn.setPreferredSize(new Dimension(100, 30));
         pLeft.add(tBtn);
         clueBtn.add(tBtn);
      }
      JLabel label2 = new JLabel("    무기 ");
      label2.setPreferredSize(new Dimension(400, 30));
      pLeft.add(label2);
      for(int i=0;i<WEAPON_ARR.length;i++) {
         JButton tBtn = new JButton(WEAPON_ARR[i]);
         tBtn.setPreferredSize(new Dimension(100, 30));
         pLeft.add(tBtn);
         clueBtn.add(tBtn);
      }
      JLabel label3 = new JLabel("    장소 ");
      label3.setPreferredSize(new Dimension(400, 30));
      pLeft.add(label3);
      for(int i=0;i<PLACE_ARR.length;i++) {
         JButton tBtn = new JButton(PLACE_ARR[i]);
         tBtn.setPreferredSize(new Dimension(120, 30));
         pLeft.add(tBtn);
         clueBtn.add(tBtn);
      }
      
      // init clueBtn
      for(int i=0;i<clueBtn.size();i++) {
         clueBtn.get(i).setBackground(Color.BLACK);
         clueBtn.get(i).setForeground(Color.WHITE);
         clueBtn.get(i).addActionListener(gamebtnlistener);
      }
      
      ///////////////////////////////////////////////////
      
      JPanel pRight = new JPanel();
      pRight.setPreferredSize(new Dimension(400, 600));
      pRight.setLayout(new BorderLayout(0, 0));
      contentPane.add(pRight, BorderLayout.CENTER);
      
      // ** 채팅창
      JPanel pChatting = new JPanel(); 
      m_outTextPane = new JTextPane();
      m_outTextPane.setBackground(new Color(255,255,255));
      m_outTextPane.setPreferredSize(new Dimension(350,500));
      m_outTextPane.setEditable(false);
      pChatting.add(m_outTextPane);
      JScrollPane centerScroll = new JScrollPane(m_outTextPane);
      pRight.add(centerScroll, BorderLayout.CENTER);
      
      // ** 채팅입력 창
      JPanel pInput = new JPanel(); 
      inputBox = new JTextField();
      inputBox.addKeyListener(gamekeylistener);
      inputBox.setPreferredSize(new Dimension(280,30));
      pInput.add(inputBox);
      // ** 입력버튼
      inputBtn = new JButton(inputBtnStr);
      inputBtn.addActionListener(gamebtnlistener);
      inputBtn.setPreferredSize(new Dimension(70,30));
      pInput.add(inputBtn);
      pRight.add(pInput, BorderLayout.SOUTH);
      
      // 채팅창 비활성화
      inputBox.setText("대기 중입니다.");
      inputBox.setEnabled(false);
      inputBtn.setEnabled(false);
      
      
      setVisible(true); // 프레임 출력
   }
   
   public class BtnListener implements ActionListener{

      @Override
      public void actionPerformed(ActionEvent e) {
         // TODO Auto-generated method stub
         JButton button = (JButton) e.getSource();
         if(button.getText().equals(inputBtnStr))
         {
            inputMsg();
         }else if(clueBtn.contains(button)){
            setBtnColor(button);
         }
      }
      
      // 클릭에 따라 버튼 색을 변경하는 함수
      public void setBtnColor(JButton button) {
         Color c = button.getBackground();
         if(c.equals(Color.BLACK)) {
            button.setBackground(Color.GREEN);
            button.setForeground(Color.BLACK);
         }else if(c.equals(Color.GREEN)) {
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
         }else if(c.equals(Color.RED)) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
         }
      }
   }
   
   public class MyKeyListener implements KeyListener {
      public void keyPressed(KeyEvent e)
      {
         int key = e.getKeyCode();
         if(key == KeyEvent.VK_ENTER)
         {
            inputMsg();
         }
      }
      
      public void keyReleased(KeyEvent e){}
      public void keyTyped(KeyEvent e){}
   }
   
   public void inputMsg() {
      String strText = inputBox.getText();
      if(env.currentTurn.equals(userName)) {
         // 메뉴선택하기
         // 메뉴 선택하면 해당턴 진행
         // 답 못맞추면 다음 플레이어턴(nextTurn에게 cast)
         switch(processState) {
         case None:
            printMessage(userName+": "+strText+"\n");
            processInput(strText);
            break;
         case Guessing:
            checkGuess(strText + ", " + beingPlace);
            break;
         case cardQone:
            movePlace(strText);
            break;
         case cardQtwo:
            cardQ_target(strText, userName, "two");   
            break;
         case cardQthree:
            cardQ_target(strText, userName, "three"); 
            break;
         default:
            break;
         }
         
//         env.currentTurn = "imttoki";
      }else {
         printMessage(userName+": "+strText+"\n");
         // dummy event로 그룹 내 전체에게 전송
      }
      
      inputBox.setText("");
      inputBox.requestFocus();
   }
   
   public void processInput(String strInput) {
      int nCommand = -1;
      try {
         nCommand = Integer.parseInt(strInput);
      } catch (NumberFormatException e) {
         printMessage("Incorrect command number!\n");
         return;
      }
      
      switch(nCommand)
      {
      case 1:
         choiceCardQ();
         break;
      case 2:
         choiceCardR();
         break;
      default:
         printMessage("1과 2중 선택해주세요.\n");
         printMenus();
         break;
      }
   }
   
   public void choiceCardQ() {
        // if the player choose the question mark card
         
         int num = random.nextInt(5) + 1; // 랜덤으로 카드 줌
         switch(num) {
         case 1:
            cardQone();
            break;
         case 2:
            cardQtwo();
            break;
         case 3:
            cardQthree();
            break;
         case 4:
            cardQfour();
            break;
         case 5:
            cardQfive();
            break;
         default:
            break;
         }
   }
   
   public void cardQone() {
      printMessage("[물음표카드 1 - 원하는 장소로 가서 추리!!]\n");
      printMessage("장소를 입력하세요: \n");
      processState = pState.cardQone;
   }
   
   // open an another player's card to everyone 한 명 지목해서 단서카드 모두에게 오픈시키기
   public void cardQtwo() {
      printMessage("[물음표카드 2 - 원하는 플레이어의 카드를 한장 볼 수 있습니다!!]\n");
      printMessage("플레이어를 입력하세요: \n");
      processState = pState.cardQtwo;
   }
   public void cardQthree() {
      printMessage("[물음표카드 3 - 원하는 플레이어의 카드를 한장 모두에게 오픈합니다!!]\n");
      printMessage("플레이어를 입력하세요: \n");
      processState = pState.cardQthree;
   }

   public void cardQfour() {
      printMessage("[물음표카드 4 - 지금 방에서 한 번 더 추리!!]\n");
      printMessage(beingPlace+"에서 추리를 시작하세요(형식: 사람, 무기): \n");
      processState = pState.Guessing;
   }
   public void cardQfive() {
      printMessage("[물음표카드 5 - 꽝 다음차례로 넘어갑니다!!]\n");
      _NextTurn();
      processState = pState.None;
   }
   
   public void cardQ_target(String targetPlayer, String player, String cardNum) {
   //지목된 플레이어에게 메세지 보냄
     printMessage(player+"님이 "+targetPlayer+"님을 지목했습니다.\n");
      String strInput = null;
      CMDummyEvent due = new CMDummyEvent();
            
      switch(cardNum) {
         case "two":
            strInput = "Target" + "#" + player + "#" + "two";         
            break;
            
         case "three":
            strInput = "Target" + "#" + player + "#" + "three";
            break;
         
         default: break;
      }
      
      due.setDummyInfo(strInput);
      m_clientStub.send(due, targetPlayer);
      processState = pState.None;
   }
   
   public void cardQ_targetReturn(String cardNum, String player) {
   //지목당한 플레이어가 다음 동작을 함
      
      CMDummyEvent due = new CMDummyEvent();
      
      int x = random.nextInt(3);
      String returnCard = "TargetReturn" + "#" + env.myCard[x];
      
      due.setDummyInfo(returnCard);
      
      switch(cardNum) {
         case "two":
             m_clientStub.send(due, player);
             break;
         case "three":
            m_clientStub.cast(due, myself.getCurrentSession(), myself.getCurrentGroup());
            break; 
         default: break;
      }
   }
   
   public void choiceCardR() {
      // if the player choose to go to a random place
      int nextPlaceNum = random.nextInt(PLACE_ARR.length-1);
      movePlace(PLACE_ARR[nextPlaceNum]);
   }
   
   public void movePlace(String place) {
      beingPlace = place;
      printMessage("\n");
      printMessage("<"+beingPlace+"> 로 이동하였습니다.\n");
      printMessage(beingPlace+"에서 추리를 시작하세요(형식: 사람, 무기): \n");
      processState = pState.Guessing;
   }
   
   // 파싱해서 정답 맞나 보기
   public void checkGuess(String _guess) {
      printMessage("*** "+userName+"의 추리 : "+_guess+"\n");
      
      // check my answer
      String[] guess = _guess.split(",");
      env.guess[0] = guess[0].trim();
      env.guess[1] = guess[1].trim();
      env.guess[2] = guess[2].trim();
   
      if(env.guess[0].equals(env.answer[0]) 
            &&  env.guess[1].equals(env.answer[1]) 
                  && env.guess[2].equals(env.answer[2]) ) {
         printMessage("정답!^^\n");
         sendWinner();
      }else {
         printMessage("틀림^^\n");
         _NextTurn();
      }
     
      processState = pState.None;
   }
   
   // 다음차례로 넘어감
   public void _NextTurn() {
      CMDummyEvent due = new CMDummyEvent();
      String strInput = "SetTurn" + "#" + env.nextTurn;
      due.setHandlerSession(myself.getCurrentSession());
      due.setHandlerGroup(myself.getCurrentGroup());
      due.setDummyInfo(strInput);
      m_clientStub.cast(due, myself.getCurrentSession(), myself.getCurrentGroup());
   }
   
   public void castGroup(String msg) {
      
         CMDummyEvent due = new CMDummyEvent();
         
         String strInput = "CastGroup" + "#" + msg;
         due.setHandlerSession(myself.getCurrentSession());
         due.setHandlerGroup(myself.getCurrentGroup());
         due.setDummyInfo(strInput);
         
         m_clientStub.cast(due, myself.getCurrentSession(), myself.getCurrentGroup());
   }
   
   
   public void sendWinner() {
   // send Server that I got the answer
      CMDummyEvent due = new CMDummyEvent();
      due.setDummyInfo(userName); 
      m_clientStub.send(due, "SERVER");
      castGroup(userName+"님이 추리에 성공하셨습니다!!");
   }
   
   public void findReason() {
   // find reason card
      CMDummyEvent due = new CMDummyEvent();
      
      String strInput = "Reasoning" + "#" 
               + env.guess[0] + "#" + env.guess[1] + "#" + env.guess[2] 
               + "#" + userName;
      due.setDummyInfo(strInput);
      
      m_clientStub.send(due, env.nextTurn);
   }
   
   public void checkReason(String receivedRaw) {
      // check if i have reason card
      CMDummyEvent due = new CMDummyEvent();
      CMDummyEvent _due = new CMDummyEvent();
      
      String[] received = receivedRaw.split("#");
      
      due.setDummyInfo(receivedRaw);
      
      int result = 1;
      String resultCard = null;
      
      int i = 1;
      
      while(i < 4) {
         if(received[i] != env.myCard[0] && received[i] != env.myCard[1] && received[i] != env.myCard[2]) {
            result = 2;
            resultCard = received[i];
            i++;
         }else
            break;
      }
      
      switch(result) {
         case 1:
            returnReason(resultCard, received[4]);
            castGroup(userName + "님이 일치하는 카드를 가지고 있습니다.");
            break;
       case 2:
            m_clientStub.send(due, env.nextTurn);
            break;     
       default:
          break;
      }

   }
   
   
   public void returnReason(String resultCard, String player) {
   // if i have a reason card, return to the client who first send
      
      CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
      CMUser myself = interInfo.getMyself();
      CMDummyEvent due = new CMDummyEvent();
      
      String strInput = "ReasoningAnswer"+ "#" + resultCard + "#" + myself.getName();
      due.setDummyInfo(strInput);
      
      m_clientStub.send(due, player);
   }
   
   public void receiveReason(String receivedRaw) {
      String[] received = receivedRaw.split("#");
      System.out.println(received[1] + "카드를" + received[2] + "(이)가 가지고 있습니다.");
      
      CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
      CMUser myself = interInfo.getMyself();
      CMDummyEvent due = new CMDummyEvent();
      due.setDummyInfo("SetTurn#"+env.nextTurn);
      
      m_clientStub.cast(due, myself.getCurrentSession(), myself.getCurrentGroup());
   }
   
   public void setCurrentTurn(String currentTurn) {
      env.currentTurn = currentTurn;
   }
   
   public void startGame() {
//      String answer[] = {CHARACTER_ARR[0], WEAPON_ARR[0],PLACE_ARR[0]};
//      String myCard[] = { CHARACTER_ARR[4], WEAPON_ARR[3],PLACE_ARR[6]};
//      String nextTurn = "imttoki";
//      String currentTurn  = "Seoyoung";
//      String openCard[] = {CHARACTER_ARR[1], WEAPON_ARR[2],PLACE_ARR[3],PLACE_ARR[4]};
//      String playerTurn[] = {"player1","player4","player3","player2","player3","player2"};
//      env.initEnv(answer, myCard, nextTurn, currentTurn, openCard, playerTurn);
      if(env.bStart) {
         printMessage("===== Game Start =====\n");
         env.printInitGame();
         printTurnMenu(env.currentTurn);

//         setDisable(findBtn(env.PLACE_ARR[0]));
//         setDisable(findBtn(env.CHARACTER_ARR[3]));
         
         
         // obj -> string으로 (서버에서 전송할때)
//         JSONObject obj = new JSONObject();
//
//            obj.put("name", "foo");
//            obj.put("num", 100);
//            obj.put("balance", 1000.21);
//            obj.put("is_vip", true);
//
//            printMessage(obj.toString());
      }
   }
   
   public void printMenus() {
      printMessage("\n");
      printMessage("===============================\n");
      printMessage("1. 물음표 카드 뽑기\n");
      printMessage("2. 랜덤으로 추리할 방 부여받기\n");
      printMessage("===============================\n");
      printMessage(userName+"님 메뉴를 입력하세요 : \n");
   }
   
   public void printMessage(String strText)
   {
      StyledDocument doc = m_outTextPane.getStyledDocument();
      try {
         doc.insertString(doc.getLength(), strText, null);
         m_outTextPane.setCaretPosition(m_outTextPane.getDocument().getLength());

      } catch (BadLocationException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return;
   }
   
   // Find button by string
   public JButton findBtn(String s) {
      for(int i=0;i<clueBtn.size();i++) {
         if(clueBtn.get(i).getText().equals(s)) {
            return clueBtn.get(i);
         }
      }
      return null;
   }
   
   public void setDisable(JButton btn) {
      btn.setEnabled(false);
      btn.setBackground(Color.GRAY);
      btn.setForeground(Color.WHITE);
   }
   
   public void setMyCard(JButton btn) {
      btn.setBackground(Color.YELLOW);
      btn.setEnabled(false);
   }
   
   public void printTurnMenu(String currentTurn) {
      printMessage("\n");
      if(userName.equals(currentTurn)) {
         printMenus();
      }else {
         printMessage(currentTurn+"님의 차례입니다.\n");
      }
      
   }

   public static void main(String[] args) {

//      ClueCMClientGame gameFrameMain = new ClueCMClientGame();
      
   
   }

}