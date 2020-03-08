package RestaurantOder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class App {
  /** Constant string for "update". */
  private static final String UPDATE = "update";

  /** Constant string for "topping". */
  private static final String TOPPING = "topping";

  /** Constant string for "drink". */
  private static final String DRINK = "drink";

  /** Message for ending the program. */
  private static final String THANKS = "Thank you for using Pizza Oder System!";

  /** The valid input. */
  private static String answer;

  /** The scanner used to get user input. */
  private Scanner scanner;

  /** Initialize a nextStep flag for continuing program. */
  private boolean nextStep = false;

  /** The list of acceptable input. */
  private List<String> validInput;

  /** The Order instance for this app. */
  private static Order order;

  /** The instance of Pizza builder. */
  private Pizza.Builder builder;

  /** The list that stores Pizza builders. */
  private List<Pizza.Builder> pizzaBuilderList = new ArrayList<>();

  /**
   * This method is a getter for variable answer.
   *
   * @return the previous answer
   */
  static String getAnswer() {
    return answer;
  }

  /**
   * This method is a setter for variable answer.
   *
   * @param name the valid answer
   */
  static void setAnswer(String name) {
    answer = name;
  }

  /**
   * This method is a helper function to setup the scanner.
   *
   * @param in for test, a scanner with defult input, for main, a scanner with system in
   */
  void setScanner(Scanner in) {
    scanner = in;
  }

  /**
   * This method is a helper function for main to repeatedly check asking any yes-no question.
   *
   * @param question the yes-no question
   */
  private void askYesNo(String question) {
    String yesNo;
    do {
      System.out.println(question);
      yesNo = scanner.nextLine();
      validInput = Arrays.asList("yes", "no");
    } while (!Handler.validateAnswer(yesNo, validInput));
  }

  /** This method is a helper function to repeatedly check for yes-no question. */
  private void askCheckOut() {
    askYesNo("Would you like to check out? (yes/no):");
    if (answer.equalsIgnoreCase("yes")) {
      nextStep = true;
    }
  }

  /**
   * This method is a helper function welcomeAndStart() to repeatedly check for menu options,
   * and if user input is "search", follow search steps.
   */
  private void askMenu() {
    String menuType;
    do {
      System.out.println(
          "Would you like to see the full menu, or search by category and name? (menu/search):");
      menuType = scanner.nextLine();
    } while (!Handler.menuSearch(menuType));

    // Check the answer from user: search or menu
    if (answer.equalsIgnoreCase("menu")) {
      System.out.println(Handler.getMenu());
    } else if (answer.equalsIgnoreCase("search")) {
      // Ask for specific category
      String category;
      do {
        System.out.println(
            "What category would you like to search? \nOptions: [size, type, topping, drink]");
        category = scanner.nextLine();
        validInput = Arrays.asList("size", "type", TOPPING, DRINK);
      } while (!Handler.validateAnswer(category, validInput));

      // Ask for specific name of item
      String itemName;
      do {
        System.out.println("What item would you like to search?");
        System.out.println("Here are the options: " + Handler.typeToItemList.get(category));
        itemName = scanner.nextLine();
      } while (!Handler.validateItem(category, itemName));

      System.out.println(Handler.searchItemPrice(category, itemName));
    }

    askYesNo("Would you still want to search or get full menu? (yes/no):");
    if (answer.equalsIgnoreCase("no")) {
      nextStep = true;
    }
  }

  /**
   * This method is a helper function for askDrinkPizza().
   * To repeatedly check for drink and get quantity.
   *
   * @param indicator "add" or "update"
   */
  private void askDrink(String indicator) {
    String drinkOption;
    do {
      System.out.println("What drink would you like to " + indicator + "?:");
      System.out.println("Here are options we offer:" + Handler.typeToItemList.get(DRINK));

      drinkOption = scanner.nextLine();
    } while (!Handler.validateItem(DRINK, drinkOption));

    // Ask for number of drink
    String quantity;
    do {
      String message =
          "How many " + drinkOption + " would you like to " + indicator + " ? [E.g. 1]:";
      if (indicator.equalsIgnoreCase(UPDATE)) {
        message += " [0 for delete]";
      } else {
        message += " [Drink will not be added to cart if 0]";
      }
      System.out.println(message);
      quantity = scanner.nextLine();
    } while (!Handler.validateInt(quantity));

    // Add drink request to order, and display the total price for now.
    order.updateDrink(drinkOption, Integer.valueOf(quantity));
  }

  /**
   * This method is a helper function for askDrinkPizza()
   * to repeatedly check pizza's type and size to build a basic pizza.
   * Also handle update and add any pizza.
   *
   * @param indicator "add" or "update"
   * @param pizzaIndex the index of pizza in pizzaBuilderList
   */
  private void askPizzaBasic(String indicator, Integer pizzaIndex) {
    String sizeOption;
    do {
      System.out.println("What size of pizza would you like to " + indicator + "?:");
      System.out.println("Here are options: " + Handler.typeToItemList.get("size"));
      sizeOption = scanner.nextLine();
    } while (!Handler.validateItem("size", sizeOption));

    // Ask for specific type of pizza
    String typeOption;
    do {
      System.out.println("What type of pizza would you like to " + indicator + "?:");
      System.out.println("Here are options: " + Handler.typeToItemList.get("type"));
      typeOption = scanner.nextLine();
    } while (!Handler.validateItem("type", typeOption));

    // Add pizza request to order, and display total price for now
    if (indicator.equalsIgnoreCase("add") || pizzaIndex == 0) {
      builder = new Pizza.Builder();
      builder.type(typeOption).size(sizeOption);
      pizzaBuilderList.add(builder);
    } else {
      builder = pizzaBuilderList.get(pizzaIndex - 1);
      builder.type(typeOption).size(sizeOption);
      pizzaBuilderList.set(pizzaIndex - 1, builder);
    }

    // Display total price for now
  }

  /**
   * This method is a helper function for askToppingRepeat().
   * To repeatedly check topping and quantity.
   * Also handle update and add topping.
   *
   * @param indicator "add" or "update"
   */
  private void askTopping(String indicator) {
    String toppingOption;
    do {
      System.out.println("What topping would you like to " + indicator + "?:");
      System.out.println("Here are options:" + Handler.typeToItemList.get(TOPPING));
      toppingOption = scanner.nextLine();
    } while (!Handler.validateItem(TOPPING, toppingOption));

    // Ask for number of topping
    String quantity;
    do {
      System.out.println(
          "How many "
              + toppingOption
              + " would you like to "
              + indicator
              + "? [E.g. 1] [Topping will not be added to cart if 0]:");
      quantity = scanner.nextLine();
    } while (!Handler.validateInt(quantity));

    // Add topping request, and display total price for now
    builder.updateToppings(toppingOption, Integer.valueOf(quantity));
  }

  /**
   * This method is a helper function for askDrinkPizza().
   * To repeatedly check whether user want to add a topping or not.
   * Call askTopping() inside.
   *
   * @param indicator "add" or "update"
   */
  private void askToppingRepeat(String indicator) {
    boolean stopTopping = false;
    String iter = "";
    do {
      // Ask for add topping or not
      askYesNo("Would you like to " + indicator + iter + " topping? (yes/no):");

      if (answer.equalsIgnoreCase("yes")) {
        // Ask for topping
        askTopping(indicator);
      } else {
        stopTopping = true;
      }

      iter = " more";
    } while (!stopTopping);
  }

  private void displayOrder(String indicator) {
    System.out.println("Below is the" + indicator + "order details:");
    System.out.println(order.toString());
    System.out.println("------------------- total price: $" + order.getTotalPrice());
  }

  // ---------------Below are the helper functions to run app -------------------------------------

  /**
   * This method is a helper function for main to import prices.csv and all recipe files.
   */
  void readFiles(String pricesPath, String recipePath) {
    // Read in "prices.csv"
    try {
      Handler.readPrices(pricesPath);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    // Read in every recipe files in folder
    try {
      Handler.readRecipe(recipePath);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * THis method is a helper function for main to display welcome message, and ask user
   * for whether they want to submit a order or exit program.
   */
  void welcomeAndStart() {
    // Display welcome message
    System.out.println("Welcome to the Pizza Oder System!");

    // Start the program by asking for menu or searching items
    do {
      askMenu();
    } while (!nextStep);

    // Ask for create a new order
    askYesNo("Would you like to create a new order? (yes/no):");

    // Check the answer from user: yes or no
    if (!answer.equalsIgnoreCase("yes")) {
      exit();
    }
  }

  /**
   * This method is a helper function for main to ask whther they want to add
   * a drink or add a piazza.
   */
  void askDrinkPizza() {
    // Ask for pizza or drink
    nextStep = false;
    do {
      String pizzaDrink;
      order = Order.getInstance();
      do {
        System.out.println("Would you like to add a pizza or drink? (pizza/drink):");
        pizzaDrink = scanner.nextLine();
        validInput = Arrays.asList("pizza", DRINK);
      } while (!Handler.validateAnswer(pizzaDrink, validInput));

      // Check the answer from user: pizza or drink
      if (answer.equalsIgnoreCase(DRINK)) {
        // Ask for specific drink item name
        askDrink("add");

        // Ask for check out
        askCheckOut();
      } else {
        // Ask for specific size of pizza
        askPizzaBasic("add", -1);

        // Repeatedly ask for topping
        askToppingRepeat("add");

        // Create pizza instance using all information, and add pizza to order
        Pizza pizza = builder.build();
        order.updatePizza(pizza, 1, -1);

        // Ask for check out
        askCheckOut();
      }
    } while (!nextStep);
  }

  /**
   * This method is a helper function for main to display order and ask user whether
   * they want to modify the order.
   */
  void askUpdate() {
    // Ask for update order or cancel order
    nextStep = false;
    do {
      String changeOption;
      do {
        // Display current order information
        System.out.println(
            "Would you like to update order, cancel order, or none? (update/cancel/none):");
        displayOrder("");
        changeOption = scanner.nextLine();
        validInput = Arrays.asList(UPDATE, "cancel", "none");
      } while (!Handler.validateAnswer(changeOption, validInput));

      // Check the answer from user: update, cancel, none
      if (answer.equalsIgnoreCase(UPDATE)) {
        String pizzaDrink;
        do {
          System.out.println("Would you like to update a pizza or drink? (pizza/drink):");
          pizzaDrink = scanner.nextLine();
          validInput = Arrays.asList("pizza", DRINK);
        } while (!Handler.validateAnswer(pizzaDrink, validInput));

        if (answer.equalsIgnoreCase(DRINK)) {
          // Ask for what drink to update
          askDrink(UPDATE);

          // Display updated order
          displayOrder(" " + UPDATE + " ");

          // Ask for check out
          askCheckOut();
          nextStep = false;
        } else {
          String pizzaIndex;
          do {
            System.out.println("Which pizza would you like to update? [E.g. 1] [0 for add pizza]:");
            pizzaIndex = scanner.nextLine();
          } while (!Handler.validateIndex(pizzaIndex, order.getPizzaList().size()));

          if (Integer.valueOf(answer) != 0) {
            String removeYesNo;
            do {
              System.out.println("Would you like to delete this pizza from order? (yes/no):");
              removeYesNo = scanner.nextLine();
              validInput = Arrays.asList("yes", "no");
            } while (!Handler.validateAnswer(removeYesNo, validInput));
          }

          if (answer.equalsIgnoreCase("yes")) {
            int index = Integer.parseInt(pizzaIndex);
            order.updatePizza(null, -1, index);
            pizzaBuilderList.remove(index - 1);
          } else {
            // Ask for specific size of pizza
            askPizzaBasic(UPDATE, Integer.valueOf(pizzaIndex));

            // Repeatedly ask for topping
            askToppingRepeat(UPDATE);

            // Create pizza instance using all information, and update pizza to order
            Pizza pizza = builder.build();
            order.updatePizza(pizza, 0, Integer.valueOf(pizzaIndex));
          }

          // Display updated order
          displayOrder(" " + UPDATE + " ");

          // Ask for check out
          askCheckOut();
          nextStep = false;
        }
      } else if (answer.equalsIgnoreCase("cancel")
          || (answer.equalsIgnoreCase("none") && order.getTotalPrice() == 0)) {
        if (answer.equalsIgnoreCase("none")) {
          System.out.println("Darling, you do have anything inside you order.");
          System.out.println(
              "The order will not be placed since you do not have anything inside your order.");
        } else {
          System.out.println("The order has been cancelled.");
        }
        exit();
      } else {
        nextStep = true;
      }
    } while (!nextStep);
  }

  /** This method is a helper function for main to ask delivery methods. */
  void askDelivery() {
    // Ask for pickup or delivery
    String deliverType;
    do {
      System.out.println("Would you like to pick up, or make a delivery order? (pickup/delivery):");
      deliverType = scanner.nextLine();
      validInput = Arrays.asList("pickup", "delivery");
    } while (!Handler.validateAnswer(deliverType, validInput));

    if (answer.equalsIgnoreCase("delivery")) {
      // Ask for delivery method
      String deliveryMethod;
      do {
        System.out.println("What delivery method you like? (in-house/Uber Eats/Foodora):");
        deliveryMethod = scanner.nextLine();
        validInput = Arrays.asList("in-house", "uber eats", "foodora", "uber");
      } while (!Handler.validateAnswer(deliveryMethod, validInput));

      // Ask for address
      String address;
      System.out.println("What is the address you prefer?:");
      do {
        address = scanner.nextLine();
      } while (!Handler.validateAnswer(address, null));

      if (answer.equalsIgnoreCase("Uber Eats") || answer.equalsIgnoreCase("in-house")) {
        try {
          Handler.writeFile(order.toJson(address), answer, order.getOrderNumber(), ".json");
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      } else {
        try {
          Handler.writeFile(order.toCsv(address), answer, order.getOrderNumber(), ".csv");
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      }

      // Tell user the delivery method is set, the order is on their way.
      System.out.println("You delivery method is set! You food is on the way!");
    }
  }

  /** This method is a helper function for main to exit the app. */
  void exit() {
    // Exit the program after finishing one order request with related delivery method, or
    // user don't want to create a order
    scanner.close();
    System.out.println(THANKS);
    System.exit(0);
  }
}
