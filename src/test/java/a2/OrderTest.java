package a2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;





public class OrderTest {
  /** Set up new Pizzas for future usage. */
  private static Pizza pizza1;

  private static Pizza pizza2;
  /** Set up a new Order. */
  private static Order order;

  /**
   * Create a new Order to test toString method in Order class.
   * Set up the correct values with prices.csv.
   */
  @BeforeClass
  public static void before() {
    try {
      Handler.readPrices("src/main/prices.csv");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    // Set pizzas for the initialization of the order
    pizza1 =
        new Pizza.Builder()
            .size("small")
            .type("vegetarian")
            .updateToppings("olives", 2)
            .updateToppings("tomatoes", 1)
            .build();
    pizza2 =
        new Pizza.Builder()
            .size("small")
            .type("pepperoni")
            .updateToppings("olives", 1)
            .updateToppings("tomatoes", 2)
            .build();
    // initialize th order for every test
    order = Order.getInstance();
    order.updatePizza(pizza1, 1, -1);
    order.updatePizza(pizza2, 1, -1);
    order.updateDrink("coke", 2);
  }

  /**
   * Test getTotalPrice() and updatePizza() and with mock Order object.
   * Test updatePizza() for adding new pizza, update pizza information and delete pizza.
   */
  @Test
  public void testUpdatePizza() {

    int totalPrice = order.getTotalPrice();
    assertEquals(31, totalPrice);

    // add new pizza when flag is 0
    order.updatePizza(pizza1, 1, -1);
    assertEquals(3, order.getPizzaList().size());
    // add update the pizza  at index (1-1) when flag is 0
    order.updatePizza(pizza2, 0, 1);
    assertEquals(3, order.getPizzaList().size());
    // add new pizza when both flag and index are 0
    order.updatePizza(pizza1, 0, 0);
    assertEquals(4, order.getPizzaList().size());
    // remove the pizza at (1-1) when flag is 1
    order.updatePizza(pizza1, -1, 1);
    assertEquals(3, order.getPizzaList().size());
  }

  /** Test updateDrink() with the mock Order object test for both add/delete/update quantity. */
  @Test
  public void testUpdateDrink() {

    order.updateDrink("pepsi", 2);

    Map<String, Integer> expected = new HashMap<>();
    expected.put("pepsi", 2);
    expected.put("coke", 2);
    // test for add new drinks
    assertEquals(expected, order.getDrinkToQuantityMap());

    order.updateDrink("pepsi", 0);
    expected.remove("pepsi");
    // test for removing pepsi from the order
    assertEquals(1, order.getDrinkToQuantityMap().size());
    assertEquals(expected, order.getDrinkToQuantityMap());

    order.updateDrink("water", 0);
    // test when add drink with no quantity
    assertEquals(1, order.getDrinkToQuantityMap().size());
    assertEquals(expected, order.getDrinkToQuantityMap());

    // test when update the quantity of exsit drink
    expected.put("coke", 3);
    order.updateDrink("coke", 3);
    assertEquals(expected, order.getDrinkToQuantityMap());
  }

  /** Test getDrinkToQuantityMap() with the mock Order object. */
  @Test
  public void testGetDrinkToQuantityMap() {

    Map<String, Integer> expected = new HashMap<>();
    expected.put("coke", 3);
    assertEquals(1, order.getDrinkToQuantityMap().size());
    assertEquals(expected, order.getDrinkToQuantityMap());
  }

  /** Test getPizzaList() with the mock Order object. */
  @Test
  public void testGetPizzaList() {
    // check the size of the pizza list, the content will be handled when testing toString()
    assertEquals(3, order.getPizzaList().size());
  }

  /** Test toString() with the mock Order object Should be in certain format. */
  @Test
  public void testToString() {
    String expected =
        "Pizza: \n\t1.small pepperoni\n\t  Toppings: tomatoes x 2\n\t\t\t    olives x 1\n\n\t"
            + "2.small vegetarian\n\t  Toppings: tomatoes x 1\n\t\t\t    olives x 2\n\n\t"
            + "3.small vegetarian\n\t  Toppings: tomatoes x 1\n\t\t\t    olives x 2\n"
            + "Drinks: \n\tcoke x 3";
    String orderDetails = order.toString();
    assertEquals(expected, orderDetails);
  }

  /** Test toJson() with the mock Order object Should be in certain format. */
  @Test
  public void testToJson() {

    String pizzaDetails =
        "\"Order Details\":\"Pizza: 1.small pepperoni Toppings: tomatoes x 2 olives x 1"
            + " 2.small vegetarian Toppings: tomatoes x 1 olives x 2"
            + " 3.small vegetarian Toppings: tomatoes x 1 olives x 2"
            + " Drinks: coke x 3\",";
    List<String> expected =
        Arrays.asList(
            "{",
            "\"Address\":\"aaa\",",
            pizzaDetails,
            "\"Order Number\":\"" + order.getOrderNumber() + "\"",
            "}");
    List<String> orderDetails = order.toJson("aaa");
    assertEquals(expected, orderDetails);
  }

  /** Test toCsv() with the mock Order object Should be in certain format. */
  @Test
  public void testToCsv() {

    String pizzaDetails =
            "Order Details,Pizza,1.small pepperoni,Toppings,tomatoes x 2,olives x 1,"
                    + "2.small vegetarian,Toppings,tomatoes x 1,olives x 2,"
                    + "3.small vegetarian,Toppings,tomatoes x 1,olives x 2,"
                    + "Drinks,coke x 3";

    List<String> orderDetails = order.toCsv("aaa");
    List<String> expected =
        Arrays.asList("Address,aaa", pizzaDetails, "Order Number," + order.getOrderNumber());
    assertEquals(expected, orderDetails);
  }

  /** Test getOrderNumber() with the mock Order object. */
  @Test
  public void testGetOrderNumber() {
    // can not test the content of the orderNumber directly since it's randomly generated
    String orderNumber = order.getOrderNumber();
    assertEquals(9, orderNumber.length());
    assertTrue(StringUtils.isAlphanumeric(orderNumber));
  }

  @AfterClass
  public static void after() {
    order.cleanPizzaList();
    order.cleanDrinkMap();
  }
}
