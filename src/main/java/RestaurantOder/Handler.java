package RestaurantOder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;


class Handler {
  /** The map stores type as key and list of items as value. */
  static Map<String, List<String>> typeToItemList = new HashMap<>();

  /** The map with size as key and price as value. */
  private static Map<String, Integer> sizeToPrices;

  /** The map stores type as key and price as value. */
  private static Map<String, Integer> typeToPrices;

  /** The map stores drink as key and price as value. */
  private static Map<String, Integer> drinkToPrices;
  
  /** The map stores topping as key and price as value. */
  private static Map<String, Integer> toppingToPrices;

  /** The map stores type as key and recipe as value. */
  private static Map<String, List<String>> typeToRecipe = new HashMap<>();

  /** Constant string for "drink". */
  private static final String DRINK = "drink";

  /** Message for invalid input. */
  private static final String INPUT_ERROR_MESSAGE = "Looks like you typed invalid answer, darling.";

  /**
   * This method is a getter for variable sizeToPrices. Main purpose is for test.
   *
   * @return sizeToPrices
   */
  static Map<String, Integer> getSizeToPrices() {
    return sizeToPrices;
  }

  /**
   * This method is a getter for variable typeToPrices. Main purpose is for test.
   *
   * @return typeToPrices
   */
  static Map<String, Integer> getTypeToPrices() {
    return typeToPrices;
  }

  /**
   * This method is a getter for variable drinkToPrices. Main purpose is for test.
   *
   * @return drinkToPrices
   */
  static Map<String, Integer> getDrinkToPrices() {
    return drinkToPrices;
  }

  /**
   * This method is a getter for variable toppingToPrices. Main purpose is for test.
   *
   * @return toppingToPrices
   */
  static Map<String, Integer> getToppingToPrices() {
    return toppingToPrices;
  }

  /**
   * This method is a getter for variable typeToRecipe. Main purpose is for test.
   *
   * @return typeToRecipe
   */
  static List<String> getTypeToRecipe(String type) {
    return typeToRecipe.get(type);
  }

  /**
   * This method is a helper function to read "prices.csv" and initialize four maps that each of
   * them stores names and corresponding prices. Also update 'typeToItemList', used for main to get
   * list of items.
   *
   * @param filename the file that stores the prices
   * @throws IOException occur if cannot find the file
   */
  static void readPrices(String filename) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line = reader.readLine();
    while (line != null) {
      String[] str = line.split(",");
      if (str[0].equalsIgnoreCase("type")) {
        typeToPrices = new HashMap<>();
        for (int i = 1; i < str.length; i += 2) {
          typeToPrices.put(str[i], Integer.valueOf(str[i + 1]));
        }
        List<String> typeList = new ArrayList<>(typeToPrices.keySet());
        typeToItemList.put("type", typeList);
      } else if (str[0].equalsIgnoreCase("size")) {
        sizeToPrices = new HashMap<>();
        for (int i = 1; i < str.length; i += 2) {
          sizeToPrices.put(str[i], Integer.valueOf(str[i + 1]));
        }
        List<String> sizeList = new ArrayList<>(sizeToPrices.keySet());
        typeToItemList.put("size", sizeList);
      } else if (str[0].equalsIgnoreCase("topping")) {
        toppingToPrices = new HashMap<>();
        for (int i = 1; i < str.length; i += 2) {
          toppingToPrices.put(str[i], Integer.valueOf(str[i + 1]));
        }
        List<String> toppingList = new ArrayList<>(toppingToPrices.keySet());
        typeToItemList.put("topping", toppingList);
      } else if (str[0].equalsIgnoreCase(DRINK)) {
        drinkToPrices = new HashMap<>();
        for (int i = 1; i < str.length; i += 2) {
          drinkToPrices.put(str[i], Integer.valueOf(str[i + 1]));
        }
        List<String> drinkList = new ArrayList<>(drinkToPrices.keySet());
        typeToItemList.put(DRINK, drinkList);
      }
      line = reader.readLine();
    }
    reader.close();
  }

  /**
   * This method is a helper function for read every recipe file.
   *
   * @param filename the absolute path of recipe file
   * @return string list with everything in recipe file
   * @throws IOException when the file is not found
   */
  private static List<String> getDetails(String filename) throws IOException {
    List<String> details = new ArrayList<>();
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line = reader.readLine();
    while (line != null) {
      details.add(line);
      line = reader.readLine();
    }
    reader.close();
    return details;
  }

  /**
   * This is a helper function for read in all recipes for every type of pizza.
   *
   * @param path the absolute path of recipe folder
   * @throws IOException when the path does not exist
   * @throws StringIndexOutOfBoundsException when the path is empty
   */
  static void readRecipe(String path) throws IOException, StringIndexOutOfBoundsException {
    List<String> recipes = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(Paths.get(path))) {
      paths.filter(Files::isRegularFile).forEach(s -> recipes.add(String.valueOf(s)));
    }
    for (String recipe : recipes) {
      String folderName = "recipe";
      int fileStartIndex = recipe.lastIndexOf(folderName) + folderName.length() + 1;
      int fileEndIndex = recipe.lastIndexOf(".txt");
      switch (recipe.substring(fileStartIndex, fileEndIndex)) {
        case "magherita":
          typeToRecipe.put("magherita", getDetails(recipe));
          break;
        case "neapolitan":
          typeToRecipe.put("neapolitan", getDetails(recipe));
          break;
        case "vegetarian":
          typeToRecipe.put("vegetarian", getDetails(recipe));
          break;
        case "pepperoni":
          typeToRecipe.put("pepperoni", getDetails(recipe));
          break;
        default:
          throw new IOException("Recipe file name does not match offered type of pizza.");
      }
    }
  }

  /**
   * This is a helper function for writing files, support both csv and json file.
   *
   * @param details order details, order number, address
   * @param answer type of delivery method
   * @throws IOException occur when can not write file
   */
  static void writeFile(List<String> details, String answer, String orderNumber, String fileType)
      throws IOException {
    PrintWriter writer = new PrintWriter("src/main/" + answer + "-" + orderNumber + fileType);
    for (String line : details) {
      writer.println(line);
    }
    writer.close();
  }

  /**
   * This method is a helper function for constructing a full menu according to the "prices.csv"
   *
   * @return full menu
   */
  static StringBuilder getMenu() {
    StringBuilder menu = new StringBuilder("Here is the full menu: \n");
    String doubleTab = "        ";
    String dollarTab = " ------------- $";
    menu.append("   Sizes: \n");
    for (Map.Entry<String, Integer> entry : sizeToPrices.entrySet()) {
      menu.append(doubleTab)
          .append(entry.getKey())
          .append(dollarTab)
          .append(entry.getValue())
          .append("\n");
    }
    menu.append("\n   Types: \n");
    for (Map.Entry<String, Integer> entry : typeToPrices.entrySet()) {
      Integer lowestPrice = sizeToPrices.get("small") + entry.getValue();
      Integer highestPrice = sizeToPrices.get("jumbo") + entry.getValue();
      menu.append(doubleTab)
          .append(entry.getKey())
          .append(dollarTab)
          .append(lowestPrice)
          .append(" - ")
          .append(highestPrice)
          .append("\n");
    }
    menu.append("\n   Toppings: \n");
    for (Map.Entry<String, Integer> entry : toppingToPrices.entrySet()) {
      menu.append(doubleTab)
          .append(entry.getKey())
          .append(dollarTab)
          .append(entry.getValue())
          .append("\n");
    }
    menu.append("\n   Drinks: \n");
    for (Map.Entry<String, Integer> entry : drinkToPrices.entrySet()) {
      menu.append(doubleTab)
          .append(entry.getKey())
          .append(dollarTab)
          .append(entry.getValue())
          .append("\n");
    }
    return menu;
  }

  /**
   * This method is a helper function to get the price of a specific item in specific category.
   *
   * @param category one of four categories - type, size, topping, drink
   * @param name the name of item in specific category, E.g. pepperoni
   * @return the price
   */
  static Integer getItemPrice(String category, String name) {
    if (category.equalsIgnoreCase("type")) {
      return typeToPrices.get(name);
    } else if (category.equalsIgnoreCase("size")) {
      return sizeToPrices.get(name);
    } else if (category.equalsIgnoreCase(DRINK)) {
      return drinkToPrices.get(name);
    } else {
      return toppingToPrices.get(name);
    }
  }

  /**
   * This method is a helper function for search one specific item with given category. If the item
   * is in "type" category, then construct a string to show price range of calculated price with
   * small size and jumbo size.
   *
   * @param category one of four categories - type, size, topping, drink
   * @param name the name of item in specific category, E.g. pepperoni
   * @return a string of price
   */
  static String searchItemPrice(String category, String name) {
    String itemPrice = "The price of " + name + " is ";
    if (category.equalsIgnoreCase("type")) {
      int lower =
          getItemPrice("size", typeToItemList.get("size").get(0)) + getItemPrice(category, name);
      int higher =
          getItemPrice(
                  "size", typeToItemList.get("size").get(typeToItemList.get("size").size() - 1))
              + getItemPrice(category, name);
      itemPrice += "in range " + lower + " - " + higher + ".";
    } else {
      itemPrice += getItemPrice(category, name) + ".";
    }
    return itemPrice;
  }

  /**
   * This method is a helper function for checking valid answer(menu, search). If not valid, send
   * error message and return false.
   *
   * @param menuType user input answer
   * @return true for valid, false for invalid
   */
  static Boolean menuSearch(String menuType) {
    if (StringUtils.containsIgnoreCase(menuType, "menu")) {
      App.setAnswer("menu");
      return true;
    } else if (StringUtils.containsIgnoreCase(menuType, "search")) {
      App.setAnswer("search");
      return true;
    }
    System.out.println(INPUT_ERROR_MESSAGE);
    return false;
  }

  /**
   * This method is a helper function for checking valid answer(item is offered). If not valid, send
   * error message and return false.
   *
   * @param category one of "type", "size", "topping" and "drink"
   * @param itemName user input answer
   * @return true for valid, false for invalid
   */
  static Boolean validateItem(String category, String itemName) {
    if (!typeToItemList.get(category).contains(itemName)) {
      System.out.println(INPUT_ERROR_MESSAGE);
      return false;
    } else {
      App.setAnswer(itemName);
      return true;
    }
  }

  /**
   * This method is a helper function for checking valid answer(must be an int, greater than zero).
   * If not valid, send error message and return false.
   *
   * @param quantity user input answer
   * @return true for valid, false for invalid
   */
  static Boolean validateInt(String quantity) {
    try {
      Integer.parseInt(quantity);
      return Integer.valueOf(quantity) >= 0;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * This method is a helper function for checking valid answer(int, less than
   * pizzaBuilderList.length). If not valid, send error message and return false.
   *
   * @param pizzaIndex user input answer
   * @param length length of pizzaBuilderList
   * @return true for valid, false for invalid
   */
  static Boolean validateIndex(String pizzaIndex, Integer length) {
    if (validateInt(pizzaIndex) && Integer.valueOf(pizzaIndex) <= length) {
      App.setAnswer(pizzaIndex);
      return true;
    }
    System.out.println(INPUT_ERROR_MESSAGE);
    return false;
  }

  /**
   * This method is a helper function for checking valid answer. If not valid, send error message
   * and return false.
   *
   * @param ans user input answer
   * @param validInput list of valid inputs
   * @return true for valid, false for invalid
   */
  static Boolean validateAnswer(String ans, List<String> validInput) {
    if (!StringUtils.isEmpty(ans)) {
      // Check address
      if (validInput == null) {
        return true;
      }

      for (String s : validInput) {
        if (StringUtils.containsIgnoreCase(s, ans)) {
          App.setAnswer(s);
          return true;
        }
      }
    }
    System.out.println(INPUT_ERROR_MESSAGE);
    return false;
  }
}
