package sprouse.cameron.blackjack.gameLogic;

import org.junit.jupiter.api.Test;

class GameStateManagerTest {

    Player player1=new Player("one", 1, 100);
    Player player2=new Player("two", 2, 90);
    Player player3=new Player("three", 3, 80);
    Player player4=new Player("four", 4, 70);
    Player player5=new Player("five",5, 60);

    @Test
    void gameStateManagerTest() {
        GameStateManager gameStateManager = new GameStateManager();
        gameStateManager.AddPlayerToGame(player1.playerID,player1.playerName);
    }
}
