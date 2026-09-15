package sprouse.cameron.blackjack.gameLogic;

import java.util.ArrayList;

import com.google.gson.Gson;

import sprouse.cameron.blackjack.deck.Card;

public class GameHand {
    ArrayList<Card> hand=new ArrayList<>();
    int bestHandValue=0;
    EnumHandResult handResult;
    EnumHandState handState;
    boolean canBeSplit;
    boolean handIsFinal;

    public GameHand( Card one, Card two) {
        this.handResult = EnumHandResult.INPLAY;
        handIsFinal=false;
        hand.add(one);
        hand.add(two);
        if(one.getEnumCardRank() == two.getEnumCardRank()){
            canBeSplit=true;
        }else{
            canBeSplit=false;
        }
        getHandBestValue();
    }

    //dealer cons
    public GameHand( Card one) {
        this.handResult = EnumHandResult.INPLAY;
        handIsFinal=false;
        hand.add(one);
        getHandBestValue();
    }

    public void addCard(Card card) {
        canBeSplit=false;
        hand.add(card);
        getHandBestValue();
    }

    public void getHandBestValue() {
        int numAces = 0;
        int calHandValue = 0;
        handIsFinal=true;

        for (Card card : hand) {
            if (card.getValue() == 0) {
                numAces++;
            }else {
                calHandValue += card.getValue();
            }
        }

        if (numAces > 0) {

            //only one ace at most will be 11 so +1 for each ace after the first one
            calHandValue += (numAces-1);
            //Last/Only Ace
            if(calHandValue + 11 <= 21) {
                calHandValue += 11; // Add 11 for one ace if it doesn't bust
            } else {
                calHandValue += 1; // Add 1 for one ace if adding 11 would bust
            }
        }

        if(calHandValue > 21) {
            handState = EnumHandState.Bust;
        } else if (calHandValue == 21) {
            if(hand.size() == 2) {
                handState = EnumHandState.NaturalBlackJack;
            } else {
                handState = EnumHandState.At21;
            }   
        } else if (hand.size() >= 5) {
            handState = EnumHandState.FiveCards;
        } else {
            handState = EnumHandState.Under21;
            handIsFinal=false;
        }
        
        bestHandValue = calHandValue;
    }

    public EnumHandState getHandState() {
        return handState;
    }

    public void setHandToFinal(){
        handIsFinal=true;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public Card getFirstCard() {
        return hand.get(0);
    }

    public Card getSecondCard() {
        return hand.get(1); 
    }

    public int getNumOfCards(){
        return hand.size();
    }
    
    public int getBestHandValue(){
        return bestHandValue;
    }

    public void setHandResult(EnumHandResult enumHandResult){
        handResult=enumHandResult;
    }

    
    public EnumHandResult getHandResult(){
        return handResult;
    }
    
}