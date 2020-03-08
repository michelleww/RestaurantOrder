package a2;

import java.util.Scanner;

public class PizzaParlour {
  /** Calling scanner and methods to run the whole program. */
  public static void main(String[] args) {
    App app = new App();

    // Initialize scanner
    app.setScanner(new Scanner(System.in));

    // Read prices.csv and all recipe files
    app.readFiles("src/main/prices.csv", "src/main/recipe");

    // Start the program with welcome message
    app.welcomeAndStart();

    // Let user to choose from adding a pizza or adding a drink to the order
    app.askDrinkPizza();

    // Let user to choose whether modify the order or not
    app.askUpdate();

    // Let user to choose delivery method.
    app.askDelivery();

    // Exit the program
    app.exit();
  }
}
