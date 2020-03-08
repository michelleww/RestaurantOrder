package a2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;

public class HandlerTest {

  /** Set up for collecting output stream. */
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  @Rule public ExpectedException thrown = ExpectedException.none();

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
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @After
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  /** Set up the correct values with mock recipe.csv and mock magherita.txt. */
  @Before
  public void setUp() {
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
   * Test readPrices() with empty filename, should catch IOException.
   *
   * @throws IOException read in empty filename
   */
  @Test
  public void testReadPricesException() throws IOException {
    thrown.expect(IOException.class);
    Handler.readPrices("");
  }

  /**
   * Test readPrice() with mock recipe folder, should read in correct prices for every category.
   *
   * @throws IOException filename is not one type of pizza
   */
  @Test
  public void testReadPrices() throws IOException {
    Handler.readPrices("src/test/prices.csv");
    assertEquals(sizeToPricesTest, Handler.getSizeToPrices());
    assertEquals(typeToPricesTest, Handler.getTypeToPrices());
    assertEquals(drinkToPricesTest, Handler.getDrinkToPrices());
    assertEquals(toppingToPricesTest, Handler.getToppingToPrices());
  }

  /**
   * Test readRecipe() with wrong folder path (folder does not exist).
   *
   * @throws IOException folder does not exist
   */
  @Test
  public void testReadRecipeExceptionIO() throws IOException {
    thrown.expect(IOException.class);
    Handler.readRecipe("src/test/recip");
  }

  /**
   * Test readRecipe() with folder that does not contain recipes.
   *
   * @throws IOException folder name does not exist
   * @throws StringIndexOutOfBoundsException wrong folder path
   */
  @Test
  public void testReadRecipeExceptionSIOBF() throws IOException, StringIndexOutOfBoundsException {
    thrown.expect(StringIndexOutOfBoundsException.class);
    thrown.expectMessage("String index out of range: -7");
    Handler.readRecipe("src/test/");
  }

  /**
   * Test readRecipe() with wrong file name.
   *
   * @throws IOException folder name does not exist
   */
  @Test
  public void testReadRecipeDefault() throws IOException {
    thrown.expect(IOException.class);
    thrown.expectMessage("Recipe file name does not match offered type of pizza.");
    PrintWriter writer = new PrintWriter("src/test/recipe/test.txt");
    writer.close();

    Handler.readRecipe("src/test/recipe");
  }

  /**
   * Test readRecipe() with correct path, and check for correct read in.
   *
   * @throws IOException folder name does not exist
   */
  @Test
  public void testReadRecipe() throws IOException {
    Handler.readRecipe("src/test/recipe");
    assertEquals(typeToRecipeTest.get("size"), Handler.getTypeToRecipe("size"));
    assertEquals(typeToRecipeTest.get("topping"), Handler.getTypeToRecipe("topping"));
    assertEquals(typeToRecipeTest.get("drink"), Handler.getTypeToRecipe("drink"));
    assertEquals(typeToRecipeTest.get("type"), Handler.getTypeToRecipe("type"));
  }

  /**
   * Test writeFile() with in-house.json, check content in file is correct.
   *
   * @throws IOException file has been used from other program
   */
  @Test
  public void testWriteFile() throws IOException {
    Handler.writeFile(Collections.singletonList("firstline"), "in-house", "4IRQGR2XR", ".json");
    File tempFile = new File("src/main/in-house-4IRQGR2XR.json");
    assertTrue(tempFile.exists());

    BufferedReader reader = new BufferedReader(new FileReader("src/main/in-house-4IRQGR2XR.json"));
    String line = reader.readLine();
    while (line != null) {
      assertEquals("firstline", line);
      line = reader.readLine();
    }
    reader.close();

    boolean success = tempFile.delete();
  }

  /** Test getMenu() with mock prices.csv, check output format and content is correct */
  @Test
  public void testGetMenu() {
    String menu =
        "Here is the full menu: \n"
            + "   Sizes: \n"
            + "        small ------------- $7\n        "
            + "jumbo ------------- $10\n"
            + "\n   Types: \n"
            + "        magherita ------------- $8 - 11\n"
            + "\n   Toppings: \n"
            + "        olives ------------- $1\n"
            + "\n   Drinks: \n"
            + "        coke ------------- $2\n";
    assertEquals(menu, Handler.getMenu().toString());
  }

  /** Test getItemPrice() with correct price. */
  @Test
  public void testGetItemPrice() {
    int price = Handler.getItemPrice("type", "magherita");
    assertEquals(1, price);

    price = Handler.getItemPrice("size", "small");
    assertEquals(7, price);

    price = Handler.getItemPrice("topping", "olives");
    assertEquals(1, price);

    price = Handler.getItemPrice("drink", "coke");
    assertEquals(2, price);
  }

  /** Test searchItemPrice() with correct output in correct format. */
  @Test
  public void testSearchItemPrice() {
    String sizeTest = "The price of small is 7.";
    assertEquals(sizeTest, Handler.searchItemPrice("size", "small"));

    String typeTest = "The price of magherita is in range 8 - 11.";
    assertEquals(typeTest, Handler.searchItemPrice("type", "magherita"));
  }

  /** Test menuSearch() with correct input "menu"/"search". */
  @Test
  public void testMenuSearch() {
    assertTrue(Handler.menuSearch("menu"));
    assertTrue(Handler.menuSearch("search"));
    assertFalse(Handler.menuSearch("aha"));
  }

  /** Test validateItem() to check item is valid in specific category. */
  @Test
  public void testValidateItem() {
    assertFalse(Handler.validateItem("topping", "mushrooms"));
    assertTrue(Handler.validateItem("size", "small"));
  }

  /** Test validateInt() to check integer greater than 0 should return true. */
  @Test
  public void testValidateInt() {
    assertTrue(Handler.validateInt("0"));
    assertTrue(Handler.validateInt("999"));
    assertFalse(Handler.validateInt("-1"));
    assertFalse(Handler.validateInt("not int"));
  }

  /**
   * Test validateIndex() to check integer greater than 0 and less than pizzaBuilderList.length.
   * should return true
   */
  @Test
  public void testValidateIndex() {
    assertTrue(Handler.validateIndex("1", 5));
    assertTrue(Handler.validateIndex("0", 5));
    assertFalse(Handler.validateIndex("10", 3));
  }

  /** Test validateAnswer() to check ans should in validInput list. */
  @Test
  public void testValidateAnswer() {
    assertTrue(Handler.validateAnswer("yes", Arrays.asList("yes", "no")));
    assertEquals("yes", App.getAnswer());

    assertFalse(Handler.validateAnswer("none", Arrays.asList("yes", "no")));
    assertEquals("Looks like you typed invalid answer, darling.\n", outContent.toString());
    assertFalse(Handler.validateAnswer(" ", Arrays.asList("yes", "no")));
  }

  /** Delete the test.txt. */
  @After
  public void deleteFile() {
    // delete the test.txt from other test to avoid IOException
    File file = new File("src/test/recipe/test.txt");
    if (file.exists()) {
      boolean success = file.delete();
    }
  }
}
