// Round logic; it keeps player turns and scores hands
// Round heavily uses card states
package sprouse.cameron.blackjack.gameLogic;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import sprouse.cameron.blackjack.deck.Card;
import sprouse.cameron.blackjack.deck.PlayDeck;

public class Round {
    private ArrayList<Player> playerList;
    private transient PlayDeck playDeck;
    private HashMap<Integer, ArrayList<GameHand>> playerWithHands;
    private int wager;
    private GameHand dealerHand;
    private transient Card dealerCardHolding;
    private int playerActionCount;
    private Integer IdOfPlayersTurn;
    private boolean playerActionsEnded;


    //Starts round, Deals cards, holds one of the deal cards for later
    public Round(ArrayList<Player> playerList, PlayDeck playDeck, int wager) {
        this.playerList = playerList;
        this.playerWithHands = new HashMap<>();
        this.playDeck = playDeck;
        this.wager = wager;

        dealerHand=new GameHand(playDeck.dealCard());
        dealerCardHolding= playDeck.dealCard();

        // Deal initial hands to players
        for (Player player : playerList) {
            Card card1 = playDeck.dealCard();
            Card card2 = playDeck.dealCard();
            GameHand hand = new GameHand(card1, card2);
            playerWithHands.put(player.getPlayerId(), new ArrayList<GameHand>());
            playerWithHands.get(player.getPlayerId()).add(hand);
        }
        playerActionsEnded=false;
        playerActionCount=0;
        IdOfPlayersTurn=playerList.get(playerActionCount).getPlayerId();
    }

    // Splits a hand if it can be split; funds should be checked before calling
    // Returns whether the cards were split
    public boolean splitHand(Integer playerId, int handArrayNum) {
        GameHand targetHand = playerWithHands.get(playerId).get(handArrayNum);
        if(targetHand.canBeSplit){
            Card firstCard = targetHand.getFirstCard();
            Card SecondCard = targetHand.getSecondCard();
            playerWithHands.get(playerId).remove(handArrayNum);
            GameHand gh1 = new GameHand(firstCard, playDeck.dealCard());
            GameHand gh2 = new GameHand(SecondCard, playDeck.dealCard());
            playerWithHands.get(playerId).add(gh1);
            playerWithHands.get(playerId).add(gh2);
            return true;
        } 
        return false;
    }

    //Adds cards, checks hand object for status
    //returns if card was added
    public boolean addCard(int playerId, int handArrayNum, int addedCardNumber) {
        GameHand targetHand = playerWithHands.get(playerId).get(handArrayNum);
        if(targetHand.handIsFinal == false ){
            if(addedCardNumber == (targetHand.getNumOfCards()+1) ){
                targetHand.addCard( playDeck.dealCard());
                checkActionHolder();
                return true;
            }
            return false;
        } 
        return false;
    }

    //Changes status of hand to final 
    public void holdHand(int playerId, int handArrayNum) {
        GameHand targetHand = playerWithHands.get(playerId).get(handArrayNum);
        targetHand.setHandToFinal();
        checkActionHolder();
    }


    //must check in order or it will not set playerActionsEnded to true
    private void checkActionHolder(){
        while(playerActionCount < playerList.size() && actionOnAtLeastOneHandStillNeeded(playerActionCount) == false){
            playerActionCount++;          
        }

        if(playerActionCount >= playerList.size()){
            playerActionsEnded=true;
            IdOfPlayersTurn=null;
        }else{
            IdOfPlayersTurn=playerList.get(playerActionCount).getPlayerId();
        }
        
    }

    //check all player hands(splits) if all hands have passed he it returns false
    private boolean actionOnAtLeastOneHandStillNeeded(int playerPlaceInArray){
        ArrayList<GameHand> currentHands = playerWithHands.get(playerList.get(playerPlaceInArray).playerID);
        for(GameHand gameHand: currentHands){
            if(!gameHand.handIsFinal){
                return true;
            }
        }
        return false;
    }


    // Round ends, scores hands, and sets status
    public void endRound() {
        dealerHand.addCard(dealerCardHolding);
        while(dealerAddCard(dealerHand)==true){
            dealerHand.addCard(playDeck.dealCard());
        }
        int dealerScore= scoreHand(dealerHand);
        for(Integer playerId: playerWithHands.keySet()){
            for(GameHand gameHand:playerWithHands.get(playerId) ){
                if( dealerScore == scoreHand(gameHand)){
                    gameHand.setHandResult(EnumHandResult.DRAW);
                }else if( dealerScore < scoreHand(gameHand)){
                    gameHand.setHandResult(EnumHandResult.WIN);
                }else if( dealerScore > scoreHand(gameHand)){
                    gameHand.setHandResult(EnumHandResult.LOSE);
                }
            }
        }

    }

    //adds card if dealer is below 16 
    private boolean dealerAddCard(GameHand dealerHand){
        if(dealerHand.getHandState() == EnumHandState.Under21 && dealerHand.bestHandValue < 16){
            return true;
        }
        return false;
    }

    //Scores all hands so 21 vs nat 21 can be diff
    private int scoreHand(GameHand gameHand){
        if(gameHand.getHandState() == EnumHandState.Bust){
            return 0;
        }else if(gameHand.getHandState() == EnumHandState.Under21){
            return gameHand.getBestHandValue();
        }else if(gameHand.getHandState() == EnumHandState.At21){
            return 21;
        }else if(gameHand.getHandState() == EnumHandState.FiveCards){
            return 22;
        }else if(gameHand.getHandState() == EnumHandState.NaturalBlackJack){
            return 23;
        }
        return 0;
    }


    //all player actions are complete
    public boolean roundPlayerActionsOver(){
        return playerActionsEnded;
    }


    public HashMap<Integer,ArrayList<GameHand>> getPlayerWithHands(){
        return playerWithHands;
    }

    public int getWager() {
        return wager;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public Player getActionHolder() {
        return playerList.get(playerActionCount);
    }
}