/******************************************************************************

ICM Consulting Coding Exercise
Poker Game Implementation - Java
Ramyaa Madhan

*******************************************************************************/

import java.io.*;
import java.util.Arrays;

public class Main {

  // Defining Hand Categories

  static final int HIGH_CARD = 1;
  static final int ONE_PAIR = 2;
  static final int TWO_PAIRS = 3;
  static final int THREE_OF_A_KIND = 4;
  static final int FLUSH = 5;
  static final int STRAIGHT = 6;
  static final int FULL_HOUSE = 7;

  static final int FOUR_OF_A_KIND = 8;
  static final int STRAIGHT_FLUSH = 9;
  static final int ROYAL_FLUSH = 10;

  static int tie = 0;

  public static void main(String[] args) {
    int player1Score = 0;
    int player2Score = 0;

    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Enter Your Card Values for Two players in the following sample format \"6H QC 3D JC KS 6H QC 3D JC KS\" \nOr Press Enter Again To Quit\\n");
      // Looping to get input and exit on Empty line or invalid format
      while (true) {
        String input = br.readLine();
        if (input.isEmpty()) {
          System.out.println("Game Finished. Results are:\n");
          break;
        }
        // a simple input validation using regex
        else if (!input.matches("(?:[2-9TJQKAtqjka][SCHDschd] ){9}[2-9TJQKAtqjka][SCHDschd]")) {
          System.out.println("No proper input in the Game. So Results are:\n");
          break;
        }

        // Splitting the input values for two player
        String[] cards = input.split(" ");
        String[] handOneValue = Arrays.copyOfRange(cards, 0, 5);
        String[] handTwoValue = Arrays.copyOfRange(cards, 5, 10);

        Hand handOne = new Hand(handOneValue);
        Hand handTwo = new Hand(handTwoValue);

        handOne.sortingCards();
        handTwo.sortingCards();

        // Checking for Poker Game Winner
        handOne.calculate();
        handTwo.calculate();
        int res = gameWinner(handOne, handTwo);
        if (res == 1) {
          player1Score++;
        } else if (res == 2) {
          player2Score++;
        } else {
          tie++;
        }
      }

      System.out.println("Player 1: " + player1Score + " hands");
      System.out.println("Player 2: " + player2Score + " hands");
      if (tie != 0) {
        System.out.println("TIES: " + tie);
      }

      System.exit(0);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int gameWinner(Hand hand1, Hand hand2) {

    if (hand1.getHandCategory() > hand2.getHandCategory()) {
      return 1;
    } else if (hand1.getHandCategory() < hand2.getHandCategory()) {
      return 2;
    } else if (hand1.getHandValue() > hand2.getHandValue()) {
      return 1;
    } else if (hand1.getHandValue() < hand2.getHandValue()) {
      return 2;
    } else {
      // final tie break!
      for (int i = 4; i >= 0; i--) {
        if (hand1.getCard(i).getValue() > hand2.getCard(i).getValue()) {
          return 1;
        } else if (hand1.getCard(i).getValue() < hand2.getCard(i).getValue()) {
          return 2;
        }
      }
      // theres a tie here...
      return -1;
    }

  }
}

class Card implements Comparable<Card> {
  private int value;
  private char suit;

  public Card(String str) {
    char v = str.charAt(0);

    if (v == 'T' || v == 't') {
      this.value = 10;
    } else if (v == 'J' || v == 'j') {
      this.value = 11;
    } else if (v == 'Q' || v == 'q') {
      this.value = 12;
    } else if (v == 'K' || v == 'k') {
      this.value = 13;
    } else if (v == 'A' || v == 'a') {
      this.value = 14;
    } else {
      this.value = Integer.parseInt("" + v);
    }
    this.suit = str.charAt(1);

  }

  public int compareTo(Card compareCard) {

    int compareValue = ((Card) compareCard).getValue();
    // ascending order
    return this.value - compareValue;

  }

  public int getValue() {
    return this.value;
  }

  public char getSuit() {
    return this.suit;
  }
}

class Hand {
  public Card[] cards;

  public int category;

  public Integer handValue;

  public Hand(Card[] cards) {
    this.cards = cards;
  }

  public Hand(String[] strArr) {
    if (strArr.length != 5) {
      System.out.println("Wrong hand format. Unable to parse.");
    } else {
      Card[] cards = new Card[5];
      for (int i = 0; i < 5; i++) {
        cards[i] = new Card(strArr[i]);
      }
      this.cards = cards;
    }
  }

  public void sortingCards() {
    Arrays.sort(this.cards);
  }

  public Card getCard(int index) {
    if (index >= 5) {
      return null;
    }
    return cards[index];
  }

  public int getHandCategory() {
    return this.category;
  }

  public Integer getHandValue() {
    return this.handValue;

  }
  // Calculating the Hand Category for each Player

  public void calculate() {

    if (this.allSameSuit() != -1 && this.straight() != -1) {
      if (this.getCard(0).getValue() == 10) {
        this.category = Main.ROYAL_FLUSH;
        this.handValue = 9999;
        return;
      } else {
        this.category = Main.STRAIGHT_FLUSH;
        return;
      }
    }

    if (this.fours() != -1) {
      this.category = Main.FOUR_OF_A_KIND;
      return;
    }

    if (this.fullHouse() != -1) {
      this.category = Main.FULL_HOUSE;
      return;
    }

    if (this.allSameSuit() != -1) {
      this.category = Main.FLUSH;
      return;
    }

    if (this.straight() != -1) {
      this.category = Main.STRAIGHT;
      return;
    }

    if (this.triple() != -1) {
      this.category = Main.THREE_OF_A_KIND;
      return;
    }

    if (this.twoPairs() != -1) {
      this.category = Main.TWO_PAIRS;
      return;
    }

    if (this.pair() != -1) {
      this.category = Main.ONE_PAIR;
      return;
    }

    this.handValue = this.getCard(4).getValue();
    this.category = Main.HIGH_CARD;
  }

  private int pair() {
    int previous = this.cards[4].getValue();
    int total = 0, nOfCards = 1;

    for (int i = 3; i >= 0; i--) {
      if (this.cards[i].getValue() == previous) {
        total += this.cards[i].getValue();
        nOfCards++;
      }

      if (nOfCards == 2) {
        break;
      }
      previous = this.cards[i].getValue();
    }

    if (nOfCards == 2) {
      this.handValue = total;
      return total;
    }
    return -1;
  }

  private int twoPairs() {
    int previous = this.cards[4].getValue();
    int i = 3, total = 0, nOfCards = 1;

    for (; i >= 0; i--) {
      if (this.cards[i].getValue() == previous) {
        total += this.cards[i].getValue();
        nOfCards++;
      }

      if (nOfCards == 2) {

        break;
      } else {
        total = 0;
        nOfCards = 1;
      }
      previous = this.cards[i].getValue();
    }

    if (nOfCards == 2 && i > 0) {
      nOfCards = 1;
      previous = this.cards[i - 1].getValue();
      for (i = i - 2; i >= 0; i--) {
        if (this.cards[i].getValue() == previous) {
          total += this.cards[i].getValue();
          nOfCards++;
        }
        if (nOfCards == 2) {
          break;
        } else {
          total = 0;
          nOfCards = 1;
        }
        previous = this.cards[i].getValue();
      }
    } else {
      return -1;
    }

    if (nOfCards == 2) {
      this.handValue = total;
      return total;
    }
    return -1;
  }

  private int triple() {
    int previous = this.cards[4].getValue();
    int total = 0, nOfCards = 1;

    for (int i = 3; i >= 0; i--) {
      if (this.cards[i].getValue() == previous) {
        total += this.cards[i].getValue();
        nOfCards++;
      } else {
        total = 0;
        nOfCards = 1;
      }

      previous = this.cards[i].getValue();
    }

    if (nOfCards == 3) {
      this.handValue = total;
      return total;
    }
    return -1;
  }

  private int fullHouse() {

    boolean changed = false;
    int previous = this.cards[4].getValue();
    int total = 0, nOfCards = 1;

    for (int i = 3; i >= 0; i--) {
      if (this.cards[i].getValue() == previous) {
        total += this.cards[i].getValue();
        nOfCards++;

      } else if (changed == false) {
        changed = true;
        if (nOfCards < 2) {
          this.handValue = -1;
          return -1;
        }

        if (nOfCards == 3)
          this.handValue = total;

      } else {
        this.handValue = -1;
        return -1;
      }
      previous = this.cards[i].getValue();
    }

    this.handValue = total;
    return total;

  }

  private int fours() {

    int previous = this.cards[4].getValue();
    int total = 0, nOfCards = 1;

    for (int i = 3; i >= 0 && nOfCards < 4; i--) {
      if (this.cards[i].getValue() == previous) {
        total += this.cards[i].getValue();
        nOfCards++;
      } else {
        total = 0;
        nOfCards = 1;
      }

      previous = this.cards[i].getValue();
    }

    if (nOfCards == 4) {
      this.handValue = total;
      return total;
    }
    return -1;
  }

  private int allSameSuit() {

    char previous = this.cards[0].getSuit();
    int total = this.cards[0].getValue();

    for (int i = 1; i < 5; i++) {
      if (this.cards[i].getSuit() != previous) {
        return -1;
      }
      total += this.cards[i].getValue();
      previous = this.cards[i].getSuit();
    }
    this.handValue = total;
    return total;
  }

  private int straight() {

    int previous = this.cards[0].getValue();
    int total = previous;
    for (int i = 1; i < 5; i++) {
      if (this.cards[i].getValue() != previous + 1) {
        return -1;
      }
      previous = this.cards[i].getValue();
      total += 1;
    }
    this.handValue = total;
    return total;
  }
}
