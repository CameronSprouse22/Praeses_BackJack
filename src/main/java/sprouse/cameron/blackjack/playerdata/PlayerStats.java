// Player stats were meant to be displayed when a user requested them
// Currently, stats are being saved but not displayed
// Stats were meant to be saved to a file and loaded via Gson
package sprouse.cameron.blackjack.playerdata;

import sprouse.cameron.blackjack.gameLogic.EnumHandResult;
import sprouse.cameron.blackjack.gameLogic.GameHand;
import sprouse.cameron.blackjack.gameLogic.Player;

public class PlayerStats {
    Integer playerId;
    String playerName;
    Integer wins;
    Integer draws;
    Integer losses;
    Integer netAccount;
    Integer gamesPlayed;
    Integer currentAccountTotal;

    public PlayerStats(Player player) {
        this.playerId = player.getPlayerId();
        this.playerName = player.getPlayerName();
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.netAccount = player.getBankRoll();
        this.gamesPlayed = 0;
        this.currentAccountTotal = player.getBankRoll();
    }  

    public void addHandResult(GameHand hand, int wager) {
        this.gamesPlayed++;
        if (hand.getHandResult() == EnumHandResult.WIN) {
            this.wins++;
            this.netAccount += wager;
            this.currentAccountTotal += wager;
        } else if (hand.getHandResult() == EnumHandResult.DRAW) {
            this.draws++;
        } else {
            this.losses++;
            this.netAccount -= wager;
            this.currentAccountTotal -= wager;
        }
    }

    public int getPlayerId() {
        return playerId;
    }
    
    public boolean buyIn(int amount) {
        currentAccountTotal += amount;
        return true;
    }

    public Player getPlayer() {
        return new Player(playerName, playerId, currentAccountTotal);
    }
}
