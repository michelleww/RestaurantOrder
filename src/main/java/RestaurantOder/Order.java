package RestaurantOder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Order {

  /** A string pattern for generating the alphanumeric order number. */
  private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  /** Initialize an order instance. */
  private static Order instance = null;

  /** A randomly generated unique order number. */
  private String orderNumber;

  /** The list stores all the pizza. */
  private List<Pizza> pizzaList;

  /** The total price for this order. */
  private Integer totalPrice = 0;

  /** The map stores drink name as key, quantity as value. */
  private Map<String, Integer> drinkToQuantityMap;

  /** Private constructor. */
  private Order() {
    this.orderNumber = randomAlphaNumeric();
    this.pizzaList = new ArrayList<>();
    this.drinkToQuantityMap = new HashMap<>();
  }

  /** This method is to ensure there is only one instance is created. */
  static Order getInstance() {
    if (instance == null) {
      instance = new Order();
    }
    return instance;
  }

  /**
   * This method is a helper function to randomly generate a string contains both number and
   * alphabetic character in length 9.
   */
  private static String randomAlphaNumeric() {
    StringBuilder builder = new StringBuilder();
    int count = 9;
    while (count-- != 0) {
      int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
      builder.append(ALPHA_NUMERIC_STRING.charAt(character));
    }
    return builder.toString();
  }

  /**
   * This method is a getter for variable pizzaList.
   *
   * @return pizzaList a list of Pizza stored in the order
   */
  List<Pizza> getPizzaList() {
    return this.pizzaList;
  }

  /**
   * This method is a getter for variable drinkToQuantityMap.
   *
   * @return drinkToQuantityMap a map stores drink name as key, quantity as value
   */
  Map<String, Integer> getDrinkToQuantityMap() {
    return this.drinkToQuantityMap;
  }

  /**
   * This method is a getter for variable orderNumber.
   *
   * @return orderNumber the order number of this order with length 9
   */
  String getOrderNumber() {
    return this.orderNumber;
  }

  /** This method is a for testing purpose only -> clean up the drinkToQuantityMap. */
  void cleanDrinkMap() {
    this.drinkToQuantityMap = new HashMap<>();
  }

  /** his method is a for testing purpose only -> clean up the drinkToQuantityMap. */
  void cleanPizzaList() {
    this.pizzaList = new ArrayList<>();
  }

  /**
   * This method transfers Order object in to a string in certain format.
   *
   * @return String the string contains the major information of this order
   */
  public String toString() {
    AtomicInteger index = new AtomicInteger();
    String pizzaListDetails =
        pizzaList.isEmpty()
            ? ""
            : "Pizza: \n"
                + pizzaList.stream()
                    .map(pizza -> "\t" + index.incrementAndGet() + "." + pizza.toString())
                    .collect(Collectors.joining("\n\n"))
                + "\n";
    String drinkToQuantityMapDetails =
        drinkToQuantityMap.size() == 0
            ? ""
            : "Drinks: \n\t"
                + drinkToQuantityMap.keySet().stream()
                    .map(key -> key + " x " + drinkToQuantityMap.get(key))
                    .collect(Collectors.joining("\n\t"));

    return pizzaListDetails + drinkToQuantityMapDetails;
  }

  /**
   * This method transfers Order object in to a list of String to be write to file in Json format.
   *
   * @param address the address for the delivery
   * @return the string contains the major information of this order
   */
  List<String> toJson(String address) {
    String pre = "{";
    String addressString = "\"Address\":\"" + address + "\",";
    String orderDetailString =
        "\"Order Details\":\""
            + this.toString()
                .replaceAll("\n", " ")
                .replaceAll("\t{1,2}", "")
                .replaceAll("\\s{2,5}", " ")
            + "\",";
    String oderNumberString = "\"Order Number\":\"" + this.orderNumber + "\"";
    String pos = "}";
    return Arrays.asList(pre, addressString, orderDetailString, oderNumberString, pos);
  }

  /**
   * This method transfers Order object in to a list of String to be write to file in csv format.
   *
   * @param address the address for the delivery
   * @return the string contains the major information of this order
   */
  List<String> toCsv(String address) {
    String addressString = "Address," + address;
    String orderDetailString =
        "Order Details,"
            + this.toString()
                .replaceAll("\n\t|:\\s", ",")
                .replaceAll("\t\t", "")
                .replaceAll("\n", ",")
                .replaceAll(",,", ",")
                .replaceAll("\\s{2,4}", "");
    String oderNumberString = "Order Number," + this.orderNumber;
    return Arrays.asList(addressString, orderDetailString, oderNumberString);
  }

  /**
   * This method calculate the total price for all the items stored in the order.
   *
   * @return totalPrice the total price for this order
   */
  Integer getTotalPrice() {
    totalPrice = 0;
    pizzaList.forEach(pizza -> totalPrice += pizza.getPrice());
    drinkToQuantityMap
        .keySet()
        .forEach(
            key -> totalPrice += drinkToQuantityMap.get(key) * Handler.getItemPrice("drink", key));
    return totalPrice;
  }

  /**
   * This method is to update the variable drinkToQuantityMap. Remove the drink from map when
   * quantity is zero. Add/update the drink for quantity not equal to zero. Will not add drink with
   * zero to the map when there is none in the map.
   *
   * @param drink the name of the drink
   * @param quantity the number of drinks
   */
  void updateDrink(String drink, Integer quantity) {
    if (drinkToQuantityMap.containsKey(drink) && quantity == 0) {
      this.drinkToQuantityMap.remove(drink);
    } else if (quantity != 0) {
      this.drinkToQuantityMap.put(drink, quantity);
    }
  }

  /**
   * This method is to update the variable pizzaList. Add new pizza when flag is 1. Remove pizza
   * when flag is -1. Update pizza when flag is 0.
   *
   * @param newPizza the pizza need to be updated
   * @param flag the flag indicates add/remove/update pizza inside the order
   * @param index the index of the pizza user wants to update
   */
  void updatePizza(Pizza newPizza, Integer flag, Integer index) {
    // flag = -1, remove pizza at specific index
    // flag = 0, update pizza
    // flag = 1, add pizza
    if (flag == 1 || (flag == 0 && index == 0)) {
      this.pizzaList.add(newPizza);
    } else if (flag == 0) {
      this.pizzaList.set(index - 1, newPizza);
    } else if (flag == -1) {
      this.pizzaList.remove(index - 1);
    }
  }
}
