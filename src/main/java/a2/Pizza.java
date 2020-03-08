package a2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Pizza {

  /** The type of the Pizza. */
  private String type;

  /** The size of the Pizza. */
  private String size;

  /** The map stores topping name as key and quantity as value. */
  private Map<String, Integer> toppings;

  /** The list of strings contain the ingredients and steps for making the specific pizza. */
  private List<String> preparationMethod;

  /** The total price for this pizza. */
  private Integer totalPrice = 0;

  /** Private constructor to use the builder for a new Pizza. */
  private Pizza(Builder builder) {
    this.type = builder.type;
    this.size = builder.size;
    this.toppings = builder.toppings;
    this.preparationMethod = builder.preparationMethod;
  }

  /**
   * This method calculate the total price of this pizza.
   *
   * @return totalPrice the total price for this pizza
   */
  public Integer getPrice() {
    totalPrice = 0;
    totalPrice += Handler.getItemPrice("type", this.type);
    totalPrice += Handler.getItemPrice("size", this.size);
    toppings
        .keySet()
        .forEach(key -> totalPrice += toppings.get(key) * Handler.getItemPrice("topping", key));
    return totalPrice;
  }

  /**
   * This method is a getter function for specific preparation method based on the pizza type.
   *
   * @return preparationMethod the list of strings of ingredients and steps for specific pizza
   */
  public List<String> getPreparation() {
    return this.preparationMethod;
  }

  /**
   * This method transfers Pizza object in to a string in certain format.
   *
   * @return String the string contains the major information of this pizza
   */
  public String toString() {
    StringBuilder pizzaStringBuilder = new StringBuilder();
    pizzaStringBuilder.append(this.size).append(" ").append(type).append("\n\t  ");
    String toppingMapToString =
        toppings.size() == 0
            ? ""
            : "Toppings: "
                + toppings.keySet().stream()
                    .map(key -> key + " x " + toppings.get(key))
                    .collect(Collectors.joining("\n\t\t\t    "));
    return pizzaStringBuilder.toString() + toppingMapToString;
  }

  /** The static build class for the Pizza. */
  public static class Builder {

    /** The type of the Pizza. */
    private String type;

    /** The size of the Pizza. */
    private String size;

    /** The list of strings contain the ingredients and steps for making the specific pizza. */
    private List<String> preparationMethod;

    /** The map stores topping name as key and quantity as value. */
    private Map<String, Integer> toppings = new HashMap<>();

    /**
     * Set up the type, and set up the specific preparation based on the type.
     *
     * @param newType the specific type
     * @return Builder the Pizza Builder
     */
    public Builder type(String newType) {
      this.type = newType;
      this.preparationMethod = Handler.getTypeToRecipe(type);
      return this;
    }

    /**
     * Set up the size.
     *
     * @param newSize the specific size
     * @return Builder the Pizza Builder
     */
    public Builder size(String newSize) {
      this.size = newSize;
      return this;
    }

    /**
     * Set up the topping with given quantity.
     * Remove the topping from map when quantity is zero.
     * Add/update the topping for quantity not equal to zero.
     * Will not add topping with zero to the map when there is none in the map.
     *
     * @param newTopping the specific topping
     * @param quantity the quantity of the topping
     * @return Builder the Pizza Builder
     */
    public Builder updateToppings(String newTopping, Integer quantity) {
      if (toppings.containsKey(newTopping) && quantity == 0) {
        this.toppings.remove(newTopping);
      } else if (quantity != 0) {
        this.toppings.put(newTopping, quantity);
      }
      return this;
    }

    /**
     * The actual function to build the pizza with information provided.
     *
     * @return Pizza the Pizza
     */
    public Pizza build() {
      return new Pizza(this);
    }
  }
}
