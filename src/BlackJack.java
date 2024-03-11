import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlackJack {
    private Deck deck;
    private List<ArrayList<Card>> playerHands;
    private ArrayList<Card> dealerHand;
    private boolean dealerStand;
    private int playerBankroll;
    private List<Integer> playerBets;

    private Scanner scanner;

    public BlackJack(int startingBankroll) {
        deck = new Deck();
        playerHands = new ArrayList<>();
        dealerHand = new ArrayList<>();
        dealerStand = false;
        playerBankroll = startingBankroll;
        playerBets = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    private void initialDeal() {
        // Deal two cards to each: player and dealer
        for (int i = 0; i < 2; i++) {
            playerHands.get(0).add(deck.getCard());
            dealerHand.add(deck.getCard());
        }

        // Offer split if the two cards of the player have the same value
        if (playerHands.get(0).get(0).getValue() == playerHands.get(0).get(1).getValue()) {
            offerSplit();
        }

        // Check for Blackjacks after dealing
        checkForBlackjacks();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Blackjack!");
        System.out.println("Controls:\n" +
                "- To place your bet, enter the amount when prompted.\n" +
                "- To hit (take another card), enter '1' when asked for your action.\n" +
                "- To stand (hold your current hand), enter '2'.\n" +
                "- If you have a pair and want to split, enter '1' when offered the option to split. To not split, 2.\n" +
                "- After each round, you will be asked if you want to play another round. Enter '1' to continue playing, or 2 to exit.");


        while (playerBankroll > 0) {
            System.out.println("You have " + playerBankroll + " currency in your bankroll.");

            // Bet placement and initial setup
            int playerBet = placeBet(scanner);

            // Reset for a new game
            resetForNewGame(playerBet);

            // Initial dealing
            initialDeal();

            // Player's turn
            for (int i = 0; i < playerHands.size(); i++) {
                while (true) {
                    System.out.println("Your hand " + (i + 1) + ": " + playerHands.get(i) + " (Value: " + getHandValue(playerHands.get(i)) + ")");
                    if (getHandValue(playerHands.get(i)) >= 21) {
                        break;
                    }
                    System.out.println("Dealer's hand: [" + dealerHand.get(0) + ", ?]");
                    System.out.println("Would you like to (1) hit, (2) stand, or (3) double down?");
                    int action = scanner.nextInt();

// Double down option
                    if (action == 3) {
                        // Check if the player has enough balance to double down
                        if (playerBankroll >= playerBets.get(i)) {
                            playerDoubleDown(i);
                            if (getHandValue(playerHands.get(i)) <= 21) {
                                // Proceed to dealer's actions if player hasn't busted
                                dealerActions();
                            }
                            endGame();
                            return; // End the player's turn after doubling down
                        } else {
                            System.out.println("Not enough funds to double down.");
                            // If not enough funds, ask again (could refine this to loop back to the choice)
                        }
                    }
                }
            }

            // Dealer's turn
            dealerActions();

            // End of game
            endGame();
        }
    }

    public void playerDoubleDown(int handIndex) {
        // Double the player's bet
        int currentBet = playerBets.get(handIndex);
        playerBankroll -= currentBet; // Deduct the additional bet from player's bankroll
        playerBets.set(handIndex, currentBet * 2); // Update the bet to double
        System.out.println("Your bet has been doubled to: " + playerBets.get(handIndex));

        // Deal one more card to the player's hand
        playerHands.get(handIndex).add(deck.getCard());
        System.out.println("You draw: " + playerHands.get(handIndex).get(playerHands.get(handIndex).size() - 1));

        // Automatically end the player's turn after doubling down
        if (getHandValue(playerHands.get(handIndex)) > 21) {
            System.out.println("Bust! You lose your doubled bet.");
        }
    }

    private int placeBet(Scanner scanner) {
        int playerBet;
        do {
            System.out.println("How much would you like to bet?");
            playerBet = scanner.nextInt();
            if (playerBet > playerBankroll) {
                System.out.println("You cannot bet more than your current bankroll. Please enter a valid bet.");
            }
        } while (playerBet > playerBankroll);
        return playerBet;
    }

    private void resetForNewGame(int playerBet) {
        playerHands.clear();
        dealerHand.clear();
        dealerStand = false;
        deck = new Deck();
        deck.shuffle();
        playerBets.clear();
        playerHands.add(new ArrayList<>());
        playerBets.add(playerBet);
    }


    private void offerSplit() {
        System.out.println("Your hand: " + playerHands.get(0) + ". You have a pair! Would you like to split? (Yes = 1, No = 2)");
        Scanner scanner = new Scanner(System.in);
        int decision = scanner.nextInt();
        if (decision == 1) {
            splitPairs();
        }
    }

    private void splitPairs() {
        ArrayList<Card> originalHand = playerHands.remove(0);
        int originalBet = playerBets.remove(0);

        ArrayList<Card> hand1 = new ArrayList<>();
        hand1.add(originalHand.get(0));
        hand1.add(deck.getCard());

        ArrayList<Card> hand2 = new ArrayList<>();
        hand2.add(originalHand.get(1));
        hand2.add(deck.getCard());

        playerHands.add(hand1);
        playerHands.add(hand2);

        playerBets.add(originalBet);
        playerBets.add(originalBet);
        playerBankroll -= originalBet; // Deduct the additional bet for the second hand
    }

    public void playerHit(int handIndex) {
        playerHands.get(handIndex).add(deck.getCard());
        System.out.println("You draw: " + playerHands.get(handIndex).get(playerHands.get(handIndex).size() - 1));
    }


    //method to check for blackjack on initialDeal
    private void checkForBlackjacks() {
        boolean playerBlackjack = isBlackjack(playerHands.get(0));
        boolean dealerBlackjack = isBlackjack(dealerHand);

        if (playerBlackjack || dealerBlackjack) {
            showDealerHand(); // Reveal dealer's hand early for comparison

            if (playerBlackjack && dealerBlackjack) {
                System.out.println("Both you and the dealer have Blackjack! It's a push.");
                // Handle tie
            } else if (playerBlackjack) {
                System.out.println("Your hand: " + playerHands.get(0) + " (Value: " + getHandValue(playerHands.get(0)) + ")");
                System.out.println("You have Blackjack! You win!");
                playerBankroll += playerBets.get(0) * 1.5; // Pay 3:2 for Blackjack
            } else {
                System.out.println("Dealer has Blackjack. Dealer wins.");
                playerBankroll -= playerBets.get(0); // Player loses their bet
            }

            prepareForNextRound(); // Reset hands and ask if the player wants to play another round
            return;
        }
        // Continue with the game if no Blackjacks
    }

    private void prepareForNextRound() {
        // Clear hands for the next round
        playerHands.clear();
        dealerHand.clear();
        playerBets.clear();

        // Add a new hand and bet for the player (if continuing)
        playerHands.add(new ArrayList<Card>());
        playerBets.add(0); // You will set this in the betting phase of the next round

        // Optionally shuffle the deck here if you're not using a fresh deck every round
        deck.shuffle();

        // Ask the player if they want to continue
        boolean validInputReceived = false;
        while (!validInputReceived) {
            // Ask the player if they want to continue
            System.out.println("Would you like to play another round? (Yes = 1, No = 2)");
            int decision = scanner.nextInt();

            if (decision == 1) {
                // Player wants to continue
                System.out.println("Starting a new round...");
                validInputReceived = true; // Valid input received, proceed with a new round
            } else if (decision == 2) {
                // Player wants to exit
                System.out.println("Thank you for playing! Your final bankroll is: " + playerBankroll);
                scanner.close(); // Close the scanner before exiting
                System.exit(0); // Exit the game
            } else {
                System.out.println("Invalid input. Please pick 1 or 2.");
                // No need to set validInputReceived to false here, as it's already false by default
            }
        }
    }

    private boolean isBlackjack(ArrayList<Card> hand) {
        return getHandValue(hand) == 21 && hand.size() == 2;
    }

    private void showDealerHand() {
        System.out.println("Dealer's hand: " + dealerHand + " (Value: " + getHandValue(dealerHand) + ")");
    }


    private void dealerActions() {
        System.out.println("Dealer's turn.");
        while (getHandValue(dealerHand) < 17) {
            dealerHand.add(deck.getCard());
            System.out.println("Dealer draws: " + dealerHand.get(dealerHand.size() - 1));
        }
        dealerStand = true;
    }

    private int getHandValue(ArrayList<Card> hand) {
        int value = 0;
        int aces = 0;

        for (Card card : hand) {
            int cardValue = card.getValue();
            if (cardValue > 10 && cardValue < 14) { // Face cards
                value += 10;
            } else if (cardValue == 14) { // Ace
                value += 11;
                aces++;
            } else {
                value += cardValue;
            }
        }

        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }

        return value;
    }

    private void endGame() {
        int dealerValue = getHandValue(dealerHand);
        System.out.println("Dealer's final hand: " + dealerHand + " (Value: " + dealerValue + ")"); // Print dealer's hand once before the loop

        for (int i = 0; i < playerHands.size(); i++) {
            int playerValue = getHandValue(playerHands.get(i));
            System.out.println("Player's hand " + (i + 1) + ": " + playerHands.get(i) + " (Value: " + playerValue + ")");
            if (playerValue > 21) {
                System.out.println("Player busts! Dealer wins.");
                playerBankroll -= playerBets.get(i);
            } else if (dealerValue > 21 || playerValue > dealerValue) {
                System.out.println("Player wins!");
                playerBankroll += playerBets.get(i);
            } else if (dealerValue == playerValue) {
                System.out.println("It's a tie!");
                // No change to bankroll on a tie
            } else {
                System.out.println("Dealer wins!");
                playerBankroll -= playerBets.get(i);
            }
        }
        System.out.println("Your current bankroll is " + playerBankroll + ".");
        prepareForNextRound();
    }

    public static void main(String[] args) {
        int startingBankroll = 1000; // Example starting bankroll
        BlackJack game = new BlackJack(startingBankroll);
        game.run();
    }
}