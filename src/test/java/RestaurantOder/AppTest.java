package RestaurantOder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.contrib.java.lang.system.ExpectedSystemExit;



public class AppTest {

  /** Help to test the program exit behaviour. */
  @Rule public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  /** Set up for collecting output stream. */
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  /** Initialize a map with size as key and price as value. */
  private Map<String, Integer> sizeToPricesTest = new HashMap<>();

  /** Initialize a map to store type as key and price as value. */
  private Map<String, Integer> typeToPricesTest = new HashMap<>();

  /** Initialize a map to store drink as key and price as value. */
  private Map<String, Integer> drinkToPricesTest = new HashMap<>();

  /** Initialize a map to store topping as key and price as value. */
  private Map<String, Integer> toppingToPricesTest = new HashMap<>();

  /** Initialize a map to store type as key and recipe as value. */
  private Map<String, List<String>> typeToRecipeTest = new HashMap<>();

  /** Initialize a map to store type as key and list of items as value. */
  private Map<String, List<String>> typeToItemListTest = new HashMap<>();

  @Before
  public void readFilesStreams() {
    System.setOut(new PrintStream(outContent));
  }

  /** Clean up the order after each test. */
  @After
  public void restoreStreams() {
    System.setOut(originalOut);
    Order order = Order.getInstance();
    order.cleanDrinkMap();
    order.cleanPizzaList();
  }

  /** Set up the correct values with mock recipe.csv and mock magherita.txt */
  @Before
  public void readFiles() {
    // Set up default sizeToPricesTest
    sizeToPricesTest.put("small", 7);
    sizeToPricesTest.put("jumbo", 10);

    // Set up default typeToPricesTest
    typeToPricesTest.put("magherita", 1);

    // Set up default drinkToPricesTest
    drinkToPricesTest.put("coke", 2);

    // Set up default toppingToPricesTest
    toppingToPricesTest.put("olives", 1);

    // Set up default typeToRecipeTest
    typeToRecipeTest.put("magherita", Arrays.asList("Ingredient:", "Steps:"));

    // Set up default typeToItemListTest
    typeToItemListTest.put("size", Arrays.asList("small", "jumbo"));
    typeToItemListTest.put("topping", Collections.singletonList("olives"));
    typeToItemListTest.put("drink", Collections.singletonList("coke"));
    typeToItemListTest.put("type", Collections.singletonList("magherita"));
  }

  /**
   * Test readFiles() with wrong prices.csv path.
   */
  @Test
  public void testReadFilesExceptionFirstEmpty() {
    App app = new App();
    app.readFiles("", "");
    String error = " (No such file or directory)\n" + "String index out of range: -7\n";
    assertEquals(error, outContent.toString());
  }

  /**
   * Test readFiles() with wrong recipe folder path.
   */
  @Test
  public void testReadFilesExceptionSecondEmpty() {
    App app = new App();
    app.readFiles("src/test/prices.csv", "");
    String error = "String index out of range: -7\n";
    assertEquals(error, outContent.toString());
  }

  /**
   * Test readFiles() with correct inputs.
   */
  @Test
  public void testReadFiles() {
    App app = new App();
    // Read in "prices.csv". Read in every recipe files in folder
    app.readFiles("src/test/prices.csv", "src/test/recipe");

    assertEquals(sizeToPricesTest, Handler.getSizeToPrices());
    assertEquals(typeToPricesTest, Handler.getTypeToPrices());
    assertEquals(drinkToPricesTest, Handler.getDrinkToPrices());
    assertEquals(toppingToPricesTest, Handler.getToppingToPrices());

    assertEquals(typeToRecipeTest.get("size"), Handler.getTypeToRecipe("size"));
    assertEquals(typeToRecipeTest.get("topping"), Handler.getTypeToRecipe("topping"));
    assertEquals(typeToRecipeTest.get("drink"), Handler.getTypeToRecipe("drink"));
    assertEquals(typeToRecipeTest.get("type"), Handler.getTypeToRecipe("type"));
  }


  /**
   * Test welcomeAndStart() with user view the menu and does not want to create an order.
   */
  @Test
  public void testWelcomeAndStartMenu() {
    exit.expectSystemExitWithStatus(0);
    App app = new App();

    String input = "menu\nno\nno\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
  }


  /**
   * Test welcomeAndStart() with user want to search and does not want to create an order.
   */
  @Test
  public void testWelcomeAndStartSearch() {
    exit.expectSystemExitWithStatus(0);
    App app = new App();

    String input = "search\nsize\nsmall\nno\nno\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();

    String output = "Welcome to 301 Pizza!: \n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "What category would you like to search? \n"
            + "Options: [size, type, topping, drink]\nWhat item would you like to search?\n"
            + "Here are the options: [small, jumbo]\nThe price of small is 7.\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n";
    assertEquals(output, outContent.toString());
  }


  /**
   * Test askDrinkPizza() with user add one coke to the order and checkout.
   */
  @Test
  public void testAskDrinkPizzaDrink() {
    App app = new App();

    String input = "menu\nno\nyes\ndrink\ncoke\n1\nyes\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();

    String finalOutput = "Welcome to the Pizza Oder System!\n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What drink would you like to add?:\n"
            + "Here are options we offer:[coke]\n"
            + "How many coke would you like to add ? "
            + "[E.g. 1]: [Drink will not be added to cart if 0]\n"
            + "Would you like to check out? (yes/no):\n";
    assertEquals(finalOutput, outContent.toString());
  }


  /**
   * Test askDrinkPizza() with user add small magherita with no topping.
   */
  @Test
  public void testAskDrinkPizzaPizzaWithoutTopping() {
    App app = new App();

    String input = "menu\nno\nyes\npizza\nsmall\nmagherita\nno\nyes\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();

    String output = "Welcome to the Pizza Oder System!\n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What size of pizza would you like to add?:\n"
            + "Here are options: [small, jumbo]\n"
            + "What type of pizza would you like to add?:\n"
            + "Here are options: [magherita]\n"
            + "Would you like to add topping? (yes/no):\n"
            + "Would you like to check out? (yes/no):\n";
    assertEquals(output, outContent.toString());
  }

  /**
   * Test askDrinkPizza() with user add small magherita with one olives as topping.
   */
  @Test
  public void testAskDrinkPizzaPizzaWithTopping() {
    App app = new App();

    String input = "menu\nno\nyes\npizza\nsmall\nmagherita\nyes\nolives\n1\nno\nyes\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();

    String output = "Welcome to the Pizza Oder System!\n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What size of pizza would you like to add?:\n"
            + "Here are options: [small, jumbo]\n"
            + "What type of pizza would you like to add?:\n"
            + "Here are options: [magherita]\n"
            + "Would you like to add topping? (yes/no):\n"
            + "What topping would you like to add?:\n"
            + "Here are options:[olives]\n"
            + "How many olives would you like to add? [E.g. 1] "
            + "[Topping will not be added to cart if 0]:\n"
            + "Would you like to add more topping? (yes/no):\n"
            + "Would you like to check out? (yes/no):\n";
    assertEquals(output, outContent.toString());
  }

  /**
   * Test askUpdate() with user update the drink quantity.
   */
  @Test
  public void testAskUpdateChangeDrink() {
    App app = new App();

    String input = "menu\nno\nyes\ndrink\ncoke\n1\nyes\nupdate\ndrink\ncoke\n2\nyes\nnone\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();
    app.askUpdate();

    String output =
        "Welcome to the Pizza Oder System!\n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What drink would you like to add?:\n"
            + "Here are options we offer:[coke]\n"
            + "How many coke would you like to add ? [E.g. 1]: "
            + "[Drink will not be added to cart if 0]\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\nDrinks: \n\tcoke x 1\n"
            + "------------------- total price: $2\n"
            + "Would you like to update a pizza or drink? (pizza/drink):\n"
            + "What drink would you like to update?:\n"
            + "Here are options we offer:[coke]\n"
            + "How many coke would you like to update ? [E.g. 1]: [0 for delete]\n"
            + "Below is the update order details:\nDrinks: \n\tcoke x 2\n"
            + "------------------- total price: $4\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\nDrinks: \n\tcoke x 2\n"
            + "------------------- total price: $4\n";
    assertEquals(output, outContent.toString());
  }

  /**
   * Test askUpdate() with user update the drink quantity.
   */
  @Test
  public void testAskUpdateCancel() {
    exit.expectSystemExitWithStatus(0);
    App app = new App();

    String input = "menu\nno\nyes\ndrink\ncoke\n1\nyes\ncancel\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();
    app.askUpdate();

    String output =
        "Welcome to the Pizza Oder System! \n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What drink would you like to add?:\n"
            + "Here are options we offer:[coke]\n"
            + "How many coke would you like to add ? "
            + "[E.g. 1]: [Drink will not be added to cart if 0]\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\n"
            + "Drinks: \n"
            + "\tcoke x 1\n"
            + "------------------- total price: $2\n"
            + "The order has been cancelled.\n";
    assertEquals(output, outContent.toString());
  }

  /**
   * Test askUpdate() with user update the pizza topping.
   */
  @Test
  public void testAskUpdateChangePizza() {
    App app = new App();

    String input = "menu\nno\nyes\npizza\nsmall\nmagherita\nyes\nolives\n2\n"
            + "no\nyes\nupdate\npizza\n1\nno\nsmall\nmagherita\nyes\nolives\n3\nno\nyes\nnone\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();
    app.askUpdate();

    String output = "Welcome to the Pizza Oder System!\n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What size of pizza would you like to add?:\n"
            + "Here are options: [small, jumbo]\n"
            + "What type of pizza would you like to add?:\n"
            + "Here are options: [magherita]\n"
            + "Would you like to add topping? (yes/no):\n"
            + "What topping would you like to add?:\n"
            + "Here are options:[olives]\n"
            + "How many olives would you like to add? "
            + "[E.g. 1] [Topping will not be added to cart if 0]:\n"
            + "Would you like to add more topping? (yes/no):\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\nPizza: \n\t1.small magherita\n\t  Toppings: olives x 2\n"
            + "\n------------------- total price: $10\n"
            + "Would you like to update a pizza or drink? (pizza/drink):\n"
            + "Which pizza would you like to update? [E.g. 1] [0 for add pizza]:\n"
            + "Would you like to delete this pizza from order? (yes/no):\n"
            + "What size of pizza would you like to update?:\n"
            + "Here are options: [small, jumbo]\n"
            + "What type of pizza would you like to update?:\n"
            + "Here are options: [magherita]\n"
            + "Would you like to update topping? (yes/no):\n"
            + "What topping would you like to update?:\n"
            + "Here are options:[olives]\n"
            + "How many olives would you like to update? "
            + "[E.g. 1] [Topping will not be added to cart if 0]:\n"
            + "Would you like to update more topping? (yes/no):\n"
            + "Below is the update order details:\nPizza: \n\t1.small magherita\n\t"
            + "  Toppings: olives x 3\n\n"
            + "------------------- total price: $11\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\nPizza: \n\t1.small magherita\n\t"
            + "  Toppings: olives x 3\n\n"
            + "------------------- total price: $11\n";
    assertEquals(output, outContent.toString());
  }

  /**
   * Test askUpdate() with user update the pizza topping.
   */
  @Test
  public void testAskUpdateDeletePizza() {
    exit.expectSystemExitWithStatus(0);
    App app = new App();

    String input = "menu\nno\nyes\npizza\nsmall\nmagherita\nno\nyes\n"
            + "update\npizza\n1\nyes\nyes\nnone\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();
    app.askUpdate();

    String output = "Welcome to the Pizza Oder System！\n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What size of pizza would you like to add?:\n"
            + "Here are options: [small, jumbo]\n"
            + "What type of pizza would you like to add?:\n"
            + "Here are options: [magherita]\n"
            + "Would you like to add topping? (yes/no):\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\nPizza: \n\t1.small magherita\n\t  \n\n"
            + "------------------- total price: $8\n"
            + "Would you like to update a pizza or drink? (pizza/drink):\n"
            + "Which pizza would you like to update? [E.g. 1] [0 for add pizza]:\n"
            + "Would you like to delete this pizza from order? (yes/no):\n"
            + "Below is the update order details:\n\n"
            + "------------------- total price: $0\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\n\n"
            + "------------------- total price: $0\n"
            + "Darling, you do have anything inside you order.\n"
            + "The order will not be placed since you do not have anything inside your order.\n";
    assertEquals(output, outContent.toString());
  }

  /**
   * Test askDelivery() with user want to pickup.
   */
  @Test
  public void testAskDeliveryPickup() {
    App app = new App();

    String input = "menu\nno\nyes\ndrink\ncoke\n1\nyes\nnone\npickup\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();
    app.askUpdate();
    app.askDelivery();

    String output = "Welcome to the Pizza Oder System!\n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What drink would you like to add?:\n"
            + "Here are options we offer:[coke]\n"
            + "How many coke would you like to add ? "
            + "[E.g. 1]: [Drink will not be added to cart if 0]\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\nDrinks: \n\tcoke x 1\n"
            + "------------------- total price: $2\n"
            + "Would you like to pick up, or make a delivery order? (pickup/delivery):\n";
    assertEquals(output, outContent.toString());
  }


  /**
   * Test askDelivery() with user want to delivery with UberEats.
   */
  @Test
  public void testAskDeliveryUberEats() throws IOException {
    App app = new App();

    String input = "menu\nno\nyes\ndrink\ncoke\n1\nyes\nnone\n"
            + "delivery\nUber Eats\n40 st.George street\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();
    app.askUpdate();
    app.askDelivery();

    String output = "Welcome to the Pizza Oder System!\n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What drink would you like to add?:\n"
            + "Here are options we offer:[coke]\n"
            + "How many coke would you like to add ? "
            + "[E.g. 1]: [Drink will not be added to cart if 0]\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\nDrinks: \n\tcoke x 1\n"
            + "------------------- total price: $2\n"
            + "Would you like to pick up, or make a delivery order? (pickup/delivery):\n"
            + "What delivery method you like? (in-house/Uber Eats/Foodora):\n"
            + "What is the address you prefer?:\n"
            + "You delivery method is set! You food is on the way!\n";
    assertEquals(output, outContent.toString());

    List<String> files = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(Paths.get("src/main"))) {
      paths.filter(Files::isRegularFile).forEach(s -> files.add(String.valueOf(s)));
    }

    // Check file extension is json
    boolean extension = false;
    for (String f : files) {
      if (FilenameUtils.getExtension(f).equals("json")) {
        File file = new File(f);
        extension = file.delete();
        break;
      }
    }
    assertTrue(extension);
  }

  /**
   * Test askDelivery() with user want to delivery with UberEats.
   */
  @Test
  public void testAskDeliveryUberEatsEmpty() throws IOException {
    App app = new App();

    String input = "menu\nno\nyes\ndrink\ncoke\n1\nyes\nnone\n"
            + "delivery\nUber Eats\n\n40 st.George street\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();
    app.askUpdate();
    app.askDelivery();

    String output = "Welcome to the Pizza Oder System!\n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What drink would you like to add?:\n"
            + "Here are options we offer:[coke]\n"
            + "How many coke would you like to add ? "
            + "[E.g. 1]: [Drink will not be added to cart if 0]\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\nDrinks: \n\tcoke x 1\n"
            + "------------------- total price: $2\n"
            + "Would you like to pick up, or make a delivery order? (pickup/delivery):\n"
            + "What delivery method you like? (in-house/Uber Eats/Foodora):\n"
            + "What is the address you prefer?:\n"
            + "Looks like you typed invalid answer, darling.\n"
            + "You delivery method is set! You food is on the way!\n";
    assertEquals(output, outContent.toString());

    List<String> files = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(Paths.get("src/main"))) {
      paths.filter(Files::isRegularFile).forEach(s -> files.add(String.valueOf(s)));
    }

    // Check file extension is json
    boolean extension = false;
    for (String f : files) {
      if (FilenameUtils.getExtension(f).equals("json")) {
        File file = new File(f);
        extension = file.delete();
        break;
      }
    }
    assertTrue(extension);
  }

  /**
   * Test askDelivery() with user want to pickup.
   */
  @Test
  public void testAskDeliveryFoodora() throws IOException {
    App app = new App();

    String input = "menu\nno\nyes\ndrink\ncoke\n1\nyes\nnone\n"
            + "delivery\nfoodora\n40 st.George street\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    app.setScanner(new Scanner(System.in));

    app.readFiles("src/test/prices.csv", "src/test/recipe");
    app.welcomeAndStart();
    app.askDrinkPizza();
    app.askUpdate();
    app.askDelivery();

    String output = "Welcome to the Pizza Oder System!\n"
            + "Would you like to see the full menu, "
            + "or search by category and name? (menu/search):\n"
            + "Here is the full menu: \n"
            + "   Sizes: \n        small ------------- $7\n        jumbo ------------- $10\n\n"
            + "   Types: \n        magherita ------------- $8 - 11\n\n   Toppings: \n"
            + "        olives ------------- $1\n\n   Drinks: \n        coke ------------- $2\n\n"
            + "Would you still want to search or get full menu? (yes/no):\n"
            + "Would you like to create a new order? (yes/no):\n"
            + "Would you like to add a pizza or drink? (pizza/drink):\n"
            + "What drink would you like to add?:\n"
            + "Here are options we offer:[coke]\n"
            + "How many coke would you like to add ? "
            + "[E.g. 1]: [Drink will not be added to cart if 0]\n"
            + "Would you like to check out? (yes/no):\n"
            + "Would you like to update order, cancel order, or none? (update/cancel/none):\n"
            + "Below is theorder details:\n"
            + "Drinks: \n"
            + "\tcoke x 1\n"
            + "------------------- total price: $2\n"
            + "Would you like to pick up, or make a delivery order? (pickup/delivery):\n"
            + "What delivery method you like? (in-house/Uber Eats/Foodora):\n"
            + "What is the address you prefer?:\n"
            + "You delivery method is set! You food is on the way!\n";
    assertEquals(output, outContent.toString());

    List<String> files = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(Paths.get("src/main"))) {
      paths.filter(Files::isRegularFile).forEach(s -> files.add(String.valueOf(s)));
    }

    String prices = "src/main/prices.csv";
    files.remove(prices);

    // Check file extension is json
    boolean extension = false;
    for (String f : files) {
      if (FilenameUtils.getExtension(f).equals("csv")) {
        File file = new File(f);
        extension = file.delete();
        break;
      }
    }
    assertTrue(extension);
  }
}
