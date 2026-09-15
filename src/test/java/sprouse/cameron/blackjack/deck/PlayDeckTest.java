package sprouse.cameron.blackjack.deck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.Hashtable;

import org.junit.jupiter.api.Test;

class PlayDeckTest {

    @Test
    void playDeckOneDeckCreatesCorrectCards() throws Exception {
        PlayDeck deck = new PlayDeck(20);

        //Confirming card count.
        assertEquals(1040, deck.numCards);

        Hashtable<String, Integer> cardCount = new Hashtable<>();

        //Confirming correct number of each card in the deck
        for (int i = 0; i < deck.numCards; i++) {
            Card card = deck.dealCard();
            if(cardCount.containsKey(card.toString())) {
                cardCount.put(card.toString(), cardCount.get(card.toString()) + 1);
            } else {
                cardCount.put(card.toString(), 1);
            }
        }


        for (String card : cardCount.keySet()) {
            assertEquals(20, cardCount.get(card));
        }
    }

    @Test
    void checkEndOfDeck() throws Exception {
        PlayDeck deck = new PlayDeck(1);

        for (int i = 0; i < deck.numCards; i++) {
            Card card = deck.dealCard();
        }
        
        assertThrows(java.lang.ArrayIndexOutOfBoundsException.class, () -> deck.dealCard());
        
    }
}
