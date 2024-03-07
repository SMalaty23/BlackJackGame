import java.util.ArrayList;
import java.util.Scanner;

public class BlackJack {
    private Deck deck;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    private boolean playerStand;
    private boolean dealerStand;
    private int playerBankroll;
    private int playerBet;

    public BlackJack(int startingBankroll) {
        deck = new Deck();
        deck.shuffle();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        playerStand = false;
        dealerStand = false;
        playerBankroll = startingBankroll; // Initialize player's bankroll
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Blackjack!");

        while (playerBankroll > 0) {
            System.out.println("You have " + playerBankroll + " currency in your bankroll.");
            do {
                System.out.println("How much would you like to bet?");
                playerBet = scanner.nextInt();
                if (playerBet > playerBankroll) {
                    System.out.println("You cannot bet more than your current bankroll. Please enter a valid bet.");
                }
            } while (playerBet > playerBankroll);

            // Reset hands and deck for a new game
            playerHand.clear();
            dealerHand.clear();
            playerStand = false;
            dealerStand = false;
            deck = new Deck(); // Reinitialize the deck to simulate a new deck each hand
            deck.shuffle();

            // Initial dealing
            playerHand.add(deck.getCard());
            dealerHand.add(deck.getCard());
            playerHand.add(deck.getCard());
            dealerHand.add(deck.getCard());

            // Player's turn
            while (!playerStand && !dealerStand) {
                System.out.println("Your hand: " + playerHand + " (Value: " + getHandValue(playerHand) + ")");
                System.out.println("Dealer's hand: [" + dealerHand.get(0) + ", ?]");
                System.out.println("Would you like to (1) hit or (2) stand?");
                int action = scanner.nextInt();

                if (action == 1) {
                    playerHit();
                } else if (action == 2) {
                    playerStand();
                    break;
                } else {
                    System.out.println("Invalid option. Please choose (1) hit or (2) stand.");
                }
            }

            // Dealer's turn, if player hasn't busted
            if (!playerStand || getHandValue(playerHand) <= 21) {
                dealerActions();
            }

            // End of game logic
            if (playerBankroll <= 0) {
                System.out.println("You've run out of currency. Game over!");
                break;
            }
        }

        scanner.close();
    }



    public void playerHit() {
        playerHand.add(deck.getCard());
        System.out.println("You draw: " + playerHand.get(playerHand.size() - 1));
        if (getHandValue(playerHand) > 21) {
            System.out.println("Player busts!");
            playerStand = true; // End player turn
            dealerStand = true; // No need for dealer to play
            endGame();
        }
    }

    public void playerStand() {
        System.out.println("You decide to stand.");
        playerStand = true;
    }

    private void dealerActions() {
        System.out.println("Dealer's turn.");
        while (getHandValue(dealerHand) < 17) {
            dealerHand.add(deck.getCard());
            System.out.println("Dealer draws: " + dealerHand.get(dealerHand.size() - 1));
        }
        dealerStand = true;
        System.out.println("Dealer's final hand: " + dealerHand + " (Value: " + getHandValue(dealerHand) + ")");
        endGame();
    }

    private int getHandValue(ArrayList<Card> hand) {
        int value = 0;
        int aces = 0;

        for (Card card : hand) {
            int cardValue = card.getValue();
            if (cardValue >= 11 && cardValue <= 13) {
                value += 10;
            } else if (cardValue == 14) {
                value += 11;
                aces++;
            } else {
                value += cardValue;
            }
        }

        while (value > 21 && aces > 0) {
            value -= 10; // Convert Ace from 11 to 1
            aces--;
        }

        return value;
    }

    private void endGame() {
        int playerValue = getHandValue(playerHand);
        int dealerValue = getHandValue(dealerHand);

        System.out.println("Final hands:");
        System.out.println("Player: " + playerHand + " (Value: " + playerValue + ")");
        System.out.println("Dealer: " + dealerHand + " (Value: " + dealerValue + ")");

        if (playerValue > 21 || (dealerValue <= 21 && dealerValue > playerValue)) {
            System.out.println("Dealer wins!");
            playerBankroll -= playerBet;
        } else if (dealerValue > 21 || playerValue > dealerValue) {
            System.out.println("Player wins!");
            playerBankroll += playerBet;
        } else {
            System.out.println("It's a tie!");
            // No change to player's bankroll on a tie
        }

        System.out.println("Your current bankroll is " + playerBankroll + ".");
    }

    public static void main(String[] args) {
        int startingBankroll = 1000; // Example starting bankroll
        BlackJack game = new BlackJack(startingBankroll);
        game.run();
    }
}