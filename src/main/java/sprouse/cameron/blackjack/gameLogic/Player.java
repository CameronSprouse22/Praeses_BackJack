package sprouse.cameron.blackjack.gameLogic;


public class Player {
    String playerName;
    Integer playerID;
    int bankRoll;
    EnumPlayerStatus playerState;

    public Player(String playerName, Integer playerID, int bankRoll) {
        this.playerName = playerName;
        this.playerID = playerID;
        this.bankRoll = bankRoll;
        this.playerState = EnumPlayerStatus.Active;
    }

    public boolean setPlayerState(EnumPlayerStatus playerState) {
        this.playerState = playerState;
        return true;
    }

    public boolean validatePlayerData() {
        if (playerName == null || playerName.isEmpty()) {
            return false;
        }
        if (playerID == null || playerID <= 0) {
            return false;
        }
        return true;
    }

    public String badPlayerDataString() {
        if (playerName == null || playerName.isEmpty()) {
            return "Empty player name received. ";
        }
        if (playerID == null || playerID <= 0) {
            return "Invalid player ID received. ";
        }
        return "Info valid info: " + playerID + " Name info: " + playerName;
    }

    public int getPlayerId() {
        return playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getBankRoll() {
        return bankRoll;
    }

    public EnumPlayerStatus getPlayerState() {
        return playerState;
    }
}