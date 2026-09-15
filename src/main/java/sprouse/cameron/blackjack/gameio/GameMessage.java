// Catch-all game messages used to retrieve values in the GameState manager
package sprouse.cameron.blackjack.gameio;

public class GameMessage {
    private String action;
    private Integer id;
    private String username;
    private Integer wager;
    private Integer playerId;
    private Integer handArrayNum;
    private Integer addedCardNumber;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getWager() {
        return wager;
    }

    public void setWager(Integer wager) {
        this.wager = wager;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getHandArrayNum() {
        return handArrayNum;
    }

    public void setHandArrayNum(Integer handArrayNum) {
        this.handArrayNum = handArrayNum;
    }

    public Integer getAddedCardNumber() {
        return addedCardNumber;
    }

    public void setAddedCardNumber(Integer addedCardNumber) {
        this.addedCardNumber = addedCardNumber;
    }
}