//Meant to be pulled from a file at start up and edit via the UI and saved to disk 
//Leaving as is

package sprouse.cameron.blackjack.configurations;

public class TableSettings {
    int minWager=1;
    int maxWager=20;
    int startWithAmount=500;
    int addCreditsAmount=50;
    int tableWaitTime;
    int percentageDeckUseBeforeShuffle=50;
    int numDecks=10;

    public int getMinWager() {
        return minWager;
    }

    public int getMaxWager() {
        return maxWager;
    }

    public int getstartWithAmount(){
        return startWithAmount;
    }

    public int getpercentageDeckUseBeforeShuffle() {
        return percentageDeckUseBeforeShuffle;
    }

    public int getNumDecks() {
        return numDecks;
    }

    public int getAddCreditsAmount(){
        return addCreditsAmount;
    }

}
