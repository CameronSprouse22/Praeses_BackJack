package sprouse.cameron.blackjack.gameLogic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import sprouse.cameron.blackjack.deck.Card;
import sprouse.cameron.blackjack.deck.EnumCardRank;
import sprouse.cameron.blackjack.deck.EnumCardSuit;

class GameHandTest {

    @Test
    void basicScoringTest() {
        Card ace = new Card( EnumCardSuit.HEARTS, EnumCardRank.ACE);
        Card king = new Card(EnumCardSuit.DIAMONDS, EnumCardRank.KING);
        Card two = new Card(EnumCardSuit.DIAMONDS, EnumCardRank.TWO);

        GameHand hand = new GameHand(ace,king );
        assert(hand.bestHandValue == 21);
        assert(hand.getHandState() == EnumHandState.NaturalBlackJack);
        hand.addCard(king);
        assert(hand.bestHandValue == 21);

        GameHand hand2 = new GameHand(ace,ace );
        assert(hand2.bestHandValue == 12);
        hand2.addCard(ace);
        assert(hand2.bestHandValue == 13);
        hand2.addCard(ace);
        assert(hand2.bestHandValue == 14);
        hand2.addCard(ace);
        assert(hand2.bestHandValue == 15);

        GameHand hand3 = new GameHand(king, king);
        assert(hand3.bestHandValue == 20);
        hand3.addCard(ace);
        assert(hand.bestHandValue == 21);
        assert(hand3.getHandState() == EnumHandState.At21);
        hand3.addCard(ace);
        assert(hand3.bestHandValue == 22);
        assert(hand3.getHandState() == EnumHandState.Bust);


        GameHand hand4 = new GameHand(two, two);
        assert(hand4.bestHandValue == 4);
        hand4.addCard(two);
        assert(hand4.bestHandValue == 6);
        hand4.addCard(two);
        assert(hand4.bestHandValue == 8);
        hand4.addCard(two);
        assert(hand4.bestHandValue == 10);
        assert(hand4.getHandState() == EnumHandState.FiveCards);
    }

    @Test
    void serializesToJson() {
        Card ace = new Card(EnumCardSuit.HEARTS, EnumCardRank.ACE);
        Card king = new Card(EnumCardSuit.DIAMONDS, EnumCardRank.KING);

        String json = new GameHand(ace, king).toJson();

        assertTrue(json.contains("\"bestHandValue\":21"));
        assertTrue(json.contains("\"handState\":\"NaturalBlackJack\""));
        assertTrue(json.contains("\"hand\""));
    }
}
