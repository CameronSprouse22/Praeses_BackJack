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
    private ArrayList<Player> playersToAddAtEndOfRound= new ArrayList<>();
    PlayerDataManager playerDataManager= new PlayerDataManager();
    PlayDeck playDeck;
    Round round;
    TableSettings tableSettings;
    MessageManager messageManager;
    int currentWager;

    public GameStateManager() {
        tableSettings=new TableSettings();
        playDeck=new PlayDeck(tableSettings.getNumDecks());
        MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Table Created");
        logger.debug("Game Created");
    }


    public void AddPlayerToGame(int id, String username) {
        for(Player player: players){    
            if(player.playerID == id){
                logger.debug("User already in game in -> id:"+id+" username:"+username);
                MessageManager.playerLogin(id);
                MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Table Created");
                return;
            }
        }

        Player player=playerDataManager.getPlayerData(id,username);
        if(!player.validatePlayerData()){
            logger.debug("Bad player String id:"+id+" username:"+username);
            MessageManager.messageSingleUser("Bad input", id);
            return;
        }

        if(round ==null){
            players.add(player);
        }else{
            playersToAddAtEndOfRound.add(player);
        }
        
        logger.debug("New Player Added:"+id+" username:"+username);

        MessageManager.playerLogin(id);
        MessageManager.messageAllUser("New Player Added:"+username);
        MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Player Added To Game");
    }
    
    //Once for user selects wager
    public void startRound(int wager) {
        players.addAll(playersToAddAtEndOfRound);

        if( wager <= 0 && wager <= tableSettings.getMinWager()){
            MessageManager.messageAllUser("Wager Too Low");
            logger.debug("wager input too low:"+wager);
            return;
        }

        if( wager > tableSettings.getMaxWager()){
            MessageManager.messageAllUser("Wager Too High");
            logger.debug("wager input too High:"+wager);
            return;
        }

        if(playDeck.cardsLeftInDeck()/playDeck.totalCards() < tableSettings.getpercentageDeckUseBeforeShuffle()/100){
            playDeck=new PlayDeck(tableSettings.getNumDecks());
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
            MessageManager.messageSingleUser("Not enough to split", playerId);
        }
        
        if(round.splitHand(playerId, handArrayNum)){
            logger.debug("Split Done", playerId);
            MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Player split: "+targetPlayer.getPlayerName());
        }else{
            logger.debug("Failed Split", playerId);
            MessageManager.messageSingleUser("Failed Split", playerId);
        }
    }

    //hit 
    public void addCard(int playerId, String username, int handArrayNum, int addedCardNumber){
        if(round.getActionHolder().getPlayerId() != playerId){
            logger.debug("Player not at turn", playerId);
            MessageManager.messageSingleUser("Not Player's turn", playerId);
        }else if(round.addCard( playerId, handArrayNum,addedCardNumber)){
            MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Player hit: "+username);
        }else{
            MessageManager.messageSingleUser("You can not hit", playerId);
        }

        if(round.roundPlayerActionsOver()){
            endRound();
        }
    }

    //hold action
    public void holdHand(int playerId, String username, int handArrayNum){
        if(round.getActionHolder().getPlayerId() == playerId){
            MessageManager.messageSingleUser("Not Player's turn", playerId);
        }
        round.holdHand( playerId, handArrayNum);

        if(round.roundPlayerActionsOver()){
            endRound();
        }else{
            MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Player Holding "+playerId);
        }
        
        
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

    public void addCredits(Integer playerId) {
        logger.debug("Adding Credits to:"+ playerId);
        Player targetPlayer = getPlayerFromId(playerId);
        if (targetPlayer == null) {
            logger.debug("Add credits requested for unknown player id: {}", playerId);
            MessageManager.messageSingleUser("Player not found:", playerId);
            return;
        }

        Player updatedPlayer = playerDataManager.addCredits(targetPlayer, tableSettings.getAddCreditsAmount());
        players.set(players.indexOf(targetPlayer), updatedPlayer);
        MessageManager.promptUpdateJson(round == null ? null : round.toJson(), "Credits added to player: " + playerId);
    }



    public void endGame() {
        MessageManager.promptUpdateJson(round.toJson(), "Game Ended");
    }

    public void playerExit(int playerId) {
        Player targetPlayer = getPlayerFromId(playerId);
        if (targetPlayer == null) {
            logger.debug("Player exit requested for unknown player id: {}", playerId);
            if (round != null) {
                MessageManager.promptUpdateJson(round.toJson(), "Player not found: " + playerId);
            }
            return;
        }

        String targetUserName = targetPlayer.getPlayerName();
        players.remove(targetPlayer);

        if (round != null) {
            MessageManager.promptUpdateJson(round.toJson(), "Player Leaving: " + targetUserName);
        }
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