package sprouse.cameron.blackjack.playerdata;

import org.junit.jupiter.api.Test;

import sprouse.cameron.blackjack.gameLogic.Player;

class PlayerDataManagerTest {

	@Test
	void playerDataManagerTest() {
        Player player1 = new Player("Player1", 1, 1000);
        Player player2 = new Player("Player2", 2, 1000);
        PlayerDataManager playerDataManager =new PlayerDataManager();
        assert (playerDataManager.playerStatsMap.size() == 0);


        playerDataManager.addPlayerData(player1);
        playerDataManager.addPlayerData(player2);


        PlayerDataManager playerDataManager2 =new PlayerDataManager();

        playerDataManager2.loadFromJson(playerDataManager.toJson());
        assert(playerDataManager.toJson().equals(playerDataManager2.toJson()));
	}
}
