// Play deck computed from an input number of decks
// Stores cards; PlayDeck is created for shuffling

package sprouse.cameron.blackjack.deck;

import java.util.Random;

public class PlayDeck {
    private int cardPlaceInArray=0;
    Card [] cardsArray;
    int numCards;

    public PlayDeck(int numberOfDecks) {
        this.numCards = numberOfDecks * 52;
        cardsArray = new Card[numCards];
        //Generate the cards for the specified number of decks
        int index = 0;

        for (int deck = 0; deck < numberOfDecks; deck++) {
            for (EnumCardSuit suit : EnumCardSuit.values()) {
                for (EnumCardRank rank : EnumCardRank.values()) {
                    cardsArray[index++] = new Card(suit, rank);
                }
            }
        }
        this.shuffleDeck(cardsArray, numberOfDecks);
    }


    private void shuffleDeck(Card[] cardsArray, int numberOfDecks){
        Random rand = new Random();
        
        for (int i = numCards - 1; i > 0; i--) {
            int ranInt = rand.nextInt(i + 1);
            
            Card temp = cardsArray[i];
            cardsArray[i] = cardsArray[ranInt];
            cardsArray[ranInt] = temp;
        }
    }


    public int cardsLeftInDeck() {
        return numCards - cardPlaceInArray;
    }


    public int totalCards() {
        return numCards;
    }


    public Card dealCard() {
        return cardsArray[cardPlaceInArray++];
    }

}
