# RestaurantOrder

This is a command-line program. Collaborated with Cami Guo.  
Run PizzaParlour class to start the program.   

### Restaurant Information
Store information for prices and recipes as persisted files.   
The price of items is stored in *prices.csv*. Recipes are stored under the folder called *recipe* folder. Both of them are under *src/main* folder.   

### Program design
#### Object relationships
*Pizza is relatively independent* to others as it only contains informations that takes input from the user and used information in presisted files. Unlike Pizza, Order acutally contains the Pizza objects. But the methods inside Order does not really get deeply inside the details of Pizza, Order contains a list of pizza and a list of drinks. It's just simply using exsited methods in Pizza.  

#### Functions and design patterns 
The program will take user input from the command-line interface, and validate inputs based on information stored in presisted files. In this way, the PizzaParlour owner can dynamically change the menu and recipes ***without changing the code***. The program will wait for user input and validate it to match the acceptable answers. The will not be exceptions on invalid input, the program will let user keep typing until the valid input.  
  - **Builder**   
    Use Builder Design Pattern to construct the Pizza class. The reason for using Builder is that there are three different attrubutes for the pizza - size, type and toppings. The "command-line workflow" is to **take in size and type seperately**, this requires validation for inputs before construct a Pizza instance. Note that **size and type are set as two mandatory fields** to create a pizza, and **topping is an optional field**. So builder is more suitable in this case. With the flexibility of the builder, it's allowed to build a Pizza without topping. In addition to that, the program allowed the user to update the pizza information based on the request, using a builder will allow **update information** easily and re-construct the Pizza efficiently.
    
  - **Singleton**   
    Use the Singleton to construct the Order class with assumption that the program will terminate once the user has submitted a new order. Based on the assumption, the Singleton will **only allow one Order instance when the app is running**.   

### Instructions
Assumption: The program will terminate once the user submit a new order.   
The basic workflow:       
![Alt text](complete_workflow.png?raw=true "Title")

![Alt text](workflow_details.png?raw=true "Title")  

The programs asks user to choose from "view full menu" and "search by category and name". 
  - Display full menu based on if the user types in "menu"
  - Will ask more information about the specific item given the options that are avaiable in the Pizza Parlour if they choose to "search", then display the result

Ask if they want they want to submit order
  - Drink or Pizza for answer "yes"
  - Exit the program for "no"
  
User need drink or Pizza
  - Ask for Drink name and quantity 
  - Ask for Pizza name, type and topping. 
Note that user can always add more drinks or more pizza with multiple toppings, the program will repeately ask questions utill the user satisfy with the current order.

Once the user decide to checkout, display order infomation and ask User if they want to update or cancle the order
  - Ask what they want to update and start over the ordering process

Ask for dilivery method if they decide to place a non-empty order
  - Ask Address information if they choose "delivery"
  - Edit program if they choose "pickup"

### Code Craftsmanship
1. **SonarLint** plugin in the intelllj for coding suggestions.  
2. **Google checkstyle** default in the intelllj checkstyle plugin for programming style.  
3. **google-java-formater** for formating the code.  
