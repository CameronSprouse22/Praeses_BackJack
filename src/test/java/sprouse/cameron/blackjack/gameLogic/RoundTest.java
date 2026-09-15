package sprouse.cameron.blackjack.gameLogic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import sprouse.cameron.blackjack.deck.Card;
import sprouse.cameron.blackjack.deck.EnumCardRank;
import sprouse.cameron.blackjack.deck.EnumCardSuit;
import sprouse.cameron.blackjack.deck.PlayDeck;

class RoundTest {

    @Test
    void winLoseBust() {
        ArrayList<Player> playerList = new ArrayList<>();
        Player player = new Player("name1", 489725, 100);
        Player player2 = new Player("name2", 2, 100);
        Player player3 = new Player("name3", 3, 100);
        playerList.add(player);
        playerList.add(player2);
        playerList.add(player3);


        Card card1 = new Card(EnumCardSuit.HEARTS, EnumCardRank.TWO);
        Card card2 = new Card(EnumCardSuit.HEARTS, EnumCardRank.ACE);
        Card card3 = new Card(EnumCardSuit.HEARTS, EnumCardRank.KING);


        PlayDeck playDeck = mock(PlayDeck.class);
        when(playDeck.dealCard()).thenReturn( card1, card1, card1, card1, card1, card1, card2, card3, card1, card1, card1, card1, card1, card1);

        Round round = new Round(playerList, playDeck, 25);

        
        round.addCard(489725, 0,3);
        round.addCard(489725, 0,4);
        round.addCard(489725, 0,5);
        round.holdHand(2, 0);


        assert(round.roundPlayerActionsOver() == true);
        
        round.endRound();
        assert(round.getPlayerWithHands().get(489725).get(0).handResult == EnumHandResult.DRAW);
        assert(round.getPlayerWithHands().get(2).get(0).handResult == EnumHandResult.LOSE);
        assert(round.getPlayerWithHands().get(3).get(0).handResult == EnumHandResult.WIN);
    }


    @Test
    void holdHitTest() {
        ArrayList<Player> playerList = new ArrayList<>();
        Player player = new Player("name", 489725, 100);
        Player player2 = new Player("name", 2, 100);
        playerList.add(player);
        playerList.add(player2);

        Card card1 = new Card(EnumCardSuit.HEARTS, EnumCardRank.TWO);


        PlayDeck playDeck = mock(PlayDeck.class);
        when(playDeck.dealCard()).thenReturn(
                card1);

        Round round = new Round(playerList, playDeck, 25);

        round.holdHand(2, 0);
        round.addCard(489725, 0, 3);

        assert(round.getPlayerWithHands().get(489725).get(0).getNumOfCards() == 3);
        assert(round.getPlayerWithHands().get(2).get(0).getNumOfCards() == 2);
    }

    @Test
    void splitTest() {
        ArrayList<Player> playerList = new ArrayList<>();
        Player player = new Player("name", 489725, 100);
        playerList.add(player);

        Card card1 = new Card(EnumCardSuit.HEARTS, EnumCardRank.TWO);


        PlayDeck playDeck = mock(PlayDeck.class);
        when(playDeck.dealCard()).thenReturn(
                card1);

        Round round = new Round(playerList, playDeck, 25);

        round.splitHand(489725, 0);

        assert(round.getPlayerWithHands().get(489725).size() == 2);
         
        round.splitHand(489725, 0);

        assert(round.getPlayerWithHands().get(489725).size() == 3);
        round.splitHand(489725, 2);
        
        assert(round.getPlayerWithHands().get(489725).size() == 4);
        round.splitHand(489725, 3);
        assert(round.getPlayerWithHands().get(489725).size() == 5);
       
    }

    @Test
    void serializesToJson() {
        ArrayList<Player> playerlist = new ArrayList<>();
        playerlist.add(new Player("name",489725,100));
        playerlist.add(new Player("name",2,100));

        Card card1 = new Card(EnumCardSuit.HEARTS, EnumCardRank.TWO);
        PlayDeck playDeck = mock(PlayDeck.class);
        when(playDeck.dealCard()).thenReturn(card1);

        Round round = new Round(playerlist, playDeck, 25);

        round.addCard(489725, 0, 3);
        String json = round.toJson();
        round.addCard(489725, 0, 4);


        assert(!json.equals(round.toJson()));


    }
}