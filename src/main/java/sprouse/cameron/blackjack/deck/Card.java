//Card object stores rank suit and value

package sprouse.cameron.blackjack.deck;

public class Card {
    EnumCardSuit suit;
    EnumCardRank rank;
    int value;

    public Card(EnumCardSuit suit, EnumCardRank rank) {
        this.suit = suit;
        this.rank = rank;
        this.value = pointConverter(rank);
    }

    public EnumCardRank getEnumCardRank(){
        return rank;
    }

    public int getValue() {
        return value;
    }

    // Ace is set to 0 so it can be calculated for the best value
    private int pointConverter(EnumCardRank rank) {
        switch (rank) {
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            case NINE:
                return 9;
            case TEN:
            case JACK:
            case QUEEN:
            case KING:
                return 10;
            //0 will be calculated elsewhere 
            case ACE:
                return 0;
            default:
                throw new IllegalArgumentException("Invalid card rank: " + rank);
        }

    }

    //for testing 
    @Override
    public String toString() {
        return this.rank + " of " + this.suit;
    }

}
