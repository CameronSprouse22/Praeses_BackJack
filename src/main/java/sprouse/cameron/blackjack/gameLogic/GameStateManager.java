package sprouse.cameron.blackjack.gameLogic;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sprouse.cameron.blackjack.configurations.TableSettings;
import sprouse.cameron.blackjack.deck.PlayDeck;
import sprouse.cameron.blackjack.gameio.MessageManager;
import sprouse.cameron.blackjack.playerdata.PlayerDataManager;

public class GameStateManager {

    private static final Logger logger = LogManager.getLogger(GameStateManager.class);

    private ArrayList<Player> players= new ArrayList<>();
    PlayerDataManager playerDataManager= new PlayerDataManager();
    PlayDeck playDeck;
    Round round;
    TableSettings tableSettings;
    MessageManager messageManager;
    int currentWager;

    public GameStateManager() {
        messageManager=new MessageManager();
        tableSettings=new TableSettings();
        playDeck=new PlayDeck(tableSettings.getNumDecks());
        MessageManager.gameTableCreated("Table Created");
        MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Table Created");
        logger.debug("Game Created");
    }


    public void AddPlayerToGame(int id, String username) {
        for(Player player: players){    
            if(player.playerID == id){
                logger.debug("User already in game in -> id:"+id+" username:"+username);
                MessageManager.playerAlreadyAdded("id:"+id+" username:"+username);
                return;
            }
        }

        Player player=playerDataManager.getPlayerData(id,username);
        if(!player.validatePlayerData()){
            logger.debug("Bad player String id:"+id+" username:"+username);
            MessageManager.messageUser(player.badPlayerDataString());
            return;
        }
        players.add(player);
        logger.debug("New Player Added:"+id+" username:"+username);

        MessageManager.playerAdded("id:"+id+" username:"+username);
        MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Player Added To Game");
    }
    
    //Once for user selects wager
    public void startRound(int wager) {
        if( wager <= 0 && wager <= tableSettings.getMinWager()){
            MessageManager.messageUser("Wager Too Low");
            logger.debug("wager input too low:"+wager);
            return;
        }

        if( wager > tableSettings.getMaxWager()){
            MessageManager.messageUser("Wager Too High");
            logger.debug("wager input too High:"+wager);
            return;
        }


        if(playDeck.cardsLeftInDeck()/playDeck.totalCards() < tableSettings.getpercentageDeckUseBeforeShuffle()/100){
            playDeck=new PlayDeck(tableSettings.getNumDecks());
            MessageManager.messageUser("Shuffling Cards");
            logger.debug("Shuffling Cards");
        }

        int ActivePlayerCount=0;
        for(Player player:players){
            if(player.bankRoll < wager){
                player.playerState=EnumPlayerStatus.LowFunds;
                logger.debug("Low Funds player:"+player.playerID);
            }
            
            if(player.playerState == EnumPlayerStatus.Active){
                ActivePlayerCount++;
            }
        }

        if(ActivePlayerCount <= 0){
            MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "No Players");
            logger.debug("No Player in game");
            return;
        }

        currentWager=wager;
        logger.debug("Starting round with wager {}", wager);
        round=new Round(players, playDeck, wager);
        logger.debug("Round Starting", wager);
        MessageManager.promptUpdateJson(round.toJson(), "Starting Round wager and player set");
    }

    //split no limits on spliting
    public void splitHand(int playerId, int handArrayNum){
        Player targetPlayer = getPlayerFromId(playerId);
        int bankRollCheck = targetPlayer.getBankRoll();
        int currentHands = round.getPlayerWithHands().get(playerId).size();

        if(bankRollCheck < (currentHands + 1)*currentWager){
            logger.debug("Not enough to split", playerId);
            MessageManager.promptUpdateJson(round.toJson(), "Not enough to split: "+targetPlayer.getPlayerName());
        }
        
        if(round.splitHand(playerId, handArrayNum)){
            logger.debug("Split Done", playerId);
            MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Player split: "+targetPlayer.getPlayerName());
        }else{
            logger.debug("Failed Split", playerId);
            MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Failed Player split: "+targetPlayer.getPlayerName());
        }
    }

    //hit 
    public void addCard(int playerId, String username, int handArrayNum, int addedCardNumber){
        if(round.getActionHolder().getPlayerId() != playerId){
            logger.debug("Player not at turn", playerId);
            MessageManager.promptUpdateJson(round.toJson(), "Not Player's turn: "+username);
        }else if(round.addCard( playerId, handArrayNum,addedCardNumber)){
            MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Player hit: "+username);
        }else{
            MessageManager.promptUpdateJson(round.toJson(), "Can not hit for: "+username);
        }

        if(round.roundPlayerActionsOver()){
            endRound();
        }
        
    }

    //hold action
    public void holdHand(int playerId, String username, int handArrayNum){
        if(round.getActionHolder().getPlayerId() == playerId){
            MessageManager.promptUpdateJson(round.toJson(), "Not Player's turn: "+username);
        }
        round.holdHand( playerId, handArrayNum);
        MessageManager.promptUpdateJson(round.toJson(), "Player Holding: "+username);

        if(round.roundPlayerActionsOver()){
            endRound();
        }
        
        MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Player Holding "+playerId);
    }


    //ends round dealer counts results
    public void endRound() {
        round.endRound();
        MessageManager.promptUpdateJson(round.toJson(), "Round Ending");
    }

    //updates accounts
    public void updateAccountRound() {
        HashMap<Integer,ArrayList<GameHand>> playerWithHands = round.getPlayerWithHands();

        for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
            Player player = players.get(playerIndex);
            ArrayList<GameHand> playerHands = playerWithHands.getOrDefault(player.playerID, new ArrayList<>());
            logger.debug("Updating account for player {} with bankroll {}", player.playerID, player.bankRoll);
            Player updatedPlayer = playerDataManager.updateAndFetchPlayerData(player, playerHands, currentWager);
            logger.debug("Updated account for player {} with bankroll {}", updatedPlayer.playerID, updatedPlayer.bankRoll);
            players.set(playerIndex, updatedPlayer);
        }

        ArrayList<Integer> inactivePlayerIds = new ArrayList<>();
        for (Player player : players) {
            if(player.getPlayerState() == EnumPlayerStatus.InActive){
                inactivePlayerIds.add(player.getPlayerId());
            }
        }
        for (Integer playerId : inactivePlayerIds) {
            playerExit(playerId);
        }

        round=null;
        MessageManager.promptUpdateJson(round == null ? null : round.toJson(),"Updating Accounts");
    }


    
    public void endGame() {
        MessageManager.promptUpdateJson(round.toJson(), "Game Ended");
    }

    public void playerExit(int playerId) {
        Player targetPlayer=getPlayerFromId(playerId);
        String targetUserName=targetPlayer.getPlayerName();
        players.remove(targetPlayer);

        MessageManager.promptUpdateJson(round.toJson(), "Player Leaving: "+targetUserName);
    }

    public ArrayList<Player> getPlayerList() {
        return players;
    }

    private Player getPlayerFromId(int id){
        for(Player player: players){
            if(player.getPlayerId() == id){
                return player;
            }
        }
        return null;
    }
}