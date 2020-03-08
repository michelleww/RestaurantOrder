package a2;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class PizzaTest {
  /** Set up a new Pizza.Builder. */
  private static Pizza.Builder builder;
  /** Set up a new Pizza. */
  private Pizza pizza;

  /** Set up the correct values with mock prices.csv and mock recipe files. */
  @Before
  public void before() {

    try {
      Handler.readPrices("src/main/prices.csv");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    // Read in every recipe files in folder
    try {
      Handler.readRecipe("src/main/recipe");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    // initialize a common builder that will be used for every class
    builder =
        new Pizza.Builder()
            .size("small")
            .type("vegetarian")
            .updateToppings("olives", 1)
            .updateToppings("tomatoes", 1);
  }

  /** Test toString() with mock Pizza object Should follow certain format. */
  @Test
  public void testToString() {
    pizza = builder.build();
    String pizzaDetails = pizza.toString();
    String expected = "small vegetarian\n\t  Toppings: tomatoes x 1\n\t\t\t    olives x 1";
    assertEquals(expected, pizzaDetails);
  }

  /**
   * Test toString() with mock Pizza object, after one topping is removed only have one topping.
   * this case Should follow certain format
   */
  @Test
  public void testToStringRemove() {
    builder.updateToppings("tomatoes", 0);
    pizza = builder.build();
    String pizzaDetails = pizza.toString();
    String expected = "small vegetarian\n\t  Toppings: olives x 1";
    assertEquals(expected, pizzaDetails);
  }

  /**
   * Test getPrice() with mock Pizza object. The prices for each item are from the mock prices.csv
   * files
   */
  @Test
  public void testGetPrice() {
    pizza = builder.build();
    int price = pizza.getPrice();
    assertEquals(13, price);
  }

  /**
   * Test getPreparation() with mock Pizza object The specific recipe is from the mock recipe file.
   */
  @Test
  public void testGetPreparation() {
    pizza = builder.build();
    List<String> preparation = Arrays.asList("Ingredients:", "Steps:");
    assertEquals(preparation, pizza.getPreparation());
  }
}
