// Player stats were meant to be displayed when a user requested them
// Currently, stats are being saved but not displayed
// Stats were meant to be saved to a file and loaded via Gson
// Hashtable is thread-safe

package sprouse.cameron.blackjack.playerdata;

import java.util.ArrayList;
import java.util.Hashtable;

import com.google.gson.Gson;

import sprouse.cameron.blackjack.configurations.TableSettings;
import sprouse.cameron.blackjack.gameLogic.GameHand;
import sprouse.cameron.blackjack.gameLogic.Player;

public class PlayerDataManager {
    Hashtable<Integer, PlayerStats> playerStatsMap= new Hashtable<Integer, PlayerStats>();

    public void addPlayerData(Player player) {
        PlayerStats playerStats= new PlayerStats(player);
        playerStatsMap.put(player.getPlayerId(), playerStats);
    }

    public Player getPlayerData(int playerId, String playerName) {

        if (!playerStatsMap.containsKey(playerId)) {
            Player player = new Player(playerName, playerId, new TableSettings().getstartWithAmount());
            addPlayerData(player);
            return player;
        }

        return playerStatsMap.get(playerId).getPlayer();
    }

    public boolean buyIn(int playerId, int amount) {
        PlayerStats playerStats= playerStatsMap.get(playerId);
        return playerStats.buyIn(amount);
    }


    public void loadFromJson(String json) {
        PlayerDataManager dataManager = new Gson().fromJson(json, PlayerDataManager.class);
        this.playerStatsMap = dataManager.playerStatsMap;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }


    //array list in case of splits
    public Player updateAndFetchPlayerData(Player player, ArrayList<GameHand> arrayList, int wager) {
        PlayerStats playerStats= playerStatsMap.get(player.getPlayerId());
        for(GameHand gameHand:arrayList){
            playerStats.addHandResult(gameHand, wager);
        }
        return playerStats.getPlayer();
    }

}
