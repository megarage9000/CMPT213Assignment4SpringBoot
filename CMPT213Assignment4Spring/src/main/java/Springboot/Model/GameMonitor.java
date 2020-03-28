package Springboot.Model;

// Devon Lutz and John Ordoyo
// SFU CMPT 213 - Assignment 3
// Maze, cheese, cat game.
// February 2020
// GameMonitor.java - Main game logic loop. Also does UI for game

import java.util.ArrayList;
import java.util.Scanner;

// Main class. Entry point, and main game loop:
public class GameMonitor {

    public static final Integer X_MIN = 0;
    public static final Integer X_MAX = 19;
    public static final Integer Y_MIN = 0;
    public static final Integer Y_MAX = 14;

    private static GameState state;
    private static ArrayList<Cat> cats;
    private static Mouse mouse;
    private static Actor cheese;
    private static Maze maze;
    private static boolean enableHidingElements = true;
    private static boolean isOneCheese = false;
    private static int numCheese = 5;
    private static int eatenCheese = 0;
    private static ArrayList<Actor> cheeses;


    public static void main(String[] args) {
       initializeGame();
       playGame();
    }

    private static Actor placeCheese() {
        boolean gotSpaceForCheese = false;
        Actor returnCheese = null;
        while (!gotSpaceForCheese) {
            // Generate random coordinates:
            int potentialCheeseX = (int)(Math.round(Math.random() * X_MAX));
            int potentialCheeseY = (int)(Math.round(Math.random() * Y_MAX));
            boolean isPotentialSpotWall = maze.getElementAtPos(potentialCheeseX, potentialCheeseY).isWall();
            if (!isPotentialSpotWall) {
                returnCheese = new Actor(potentialCheeseX, potentialCheeseY);
                gotSpaceForCheese = true;
            }
        }
        return returnCheese;
    }



    // Handles input for cheat codes,
    // help and mouse movement
    public static void processInput(){
        Scanner in = new Scanner(System.in);
        boolean hasMouseMoved = false;

        // Breaks out of the loop only when mouse has moved
        while(!hasMouseMoved){

            System.out.println("Enter a move!(w = up, d = right, s = down, a = left)");
            System.out.print("> ");

            String input = in.next().toLowerCase();
            char move = input.charAt(0);

            // Checks all cases of non-mouse movement first
            // 1. Help Screen
            if (move == '?'){
                displayHelpScreen();
            }
            // 2. Walls Cheat
            else if (move == 'm'){
                enableHidingElements = !enableHidingElements;
                displayScreen();
                if(enableHidingElements){
                    System.out.println("Visible map cheat disabled");
                }
                else{
                    System.out.println("Visible map cheat enabled;");
                }
            }
            // 3. One Cheese Cheat
            else if(move == 'c'){
                if(isOneCheese == false && eatenCheese == 0){
                    setToOneCheese();
                    displayScreen();
                    System.out.println("One cheese cheat enabled");
                }
                else if (eatenCheese > 0){
                    System.out.println("Cheat can't be used(You have already captured a cheese)");
                }
                else{
                    System.out.println("One Cheese cheat has been enabled");
                }
            }

            // Handles mouse movement
            else {
                // Gets the mouse predicted mouse move,
                // checks if it has moved from original position and
                // validates the move
                setMouseMove(move);
                if(mouse.hasMadeMove()) {
                    hasMouseMoved = maze.isValidMove(mouse.getMoveX(), mouse.getMoveY());
                    if (hasMouseMoved) {
                        mouse.moveMouse();
                    }
                    else{
                        System.out.println("Can't move there");
                    }
                }
                else{
                    System.out.println("Invalid input");
                }
            }
        }
    }

    // Sets number of cheese to 1
    // - Gets the first cheese, clears
    // - the cheeses list, and adds
    // - the first cheese back
    private static void setToOneCheese() {
        Actor cheese = cheeses.get(0);
        cheeses.clear();
        cheeses.add(cheese);
        numCheese = cheeses.size();
        isOneCheese = true;
    }

    // Sets the next move for the mouse based on user input
    private static void setMouseMove(char move) {

        int mousePosY = mouse.getPosY();
        int mousePosX = mouse.getPosX();
        boolean mouseHasMoved = false;

        // Calculates the move on original
        // mouse position based on input
        switch (move) {
            case 'w':
                mousePosY--;
                break;
            case 'd':
                mousePosX++;
                break;
            case 's':
                mousePosY++;
                break;
            case 'a':
                mousePosX--;
                break;
            default:
                break;
        }
        mouse.setMoveX(mousePosX);
        mouse.setMoveY(mousePosY);
    }
    public static void initializeGame(){
        // Set up main state variables
        state = GameState.valid;
        mouse = new Mouse(1, 1);
        cats = new ArrayList<Cat>();
        // add Cats for the arrayList:
        cats.add(new Cat(1, 13));
        cats.add(new Cat(18, 1));
        cats.add(new Cat(18, 13));
        // Cheese initialization is left until after the maze is generated
        // as we need to check that the spot the cheese is placed is a free
        // wall.
        maze = new Maze();
        cheeses = new ArrayList<>();
        for(int i = 0; i < numCheese; i++){
            cheeses.add(placeCheese());
        }
    }
    // Calls all necessary updates per player move
    public static void playGame(){
        displayHelpScreen();
        while(state != GameState.won && state != GameState.lost){
            // States are checked twice to handle:
            // 1. cat moving into player
            // 2. player moving into cat
            revealSurroundings();
            displayScreen();
            processInput();
            checkState();
            moveCats();
            checkState();
        }
        
        if (state == GameState.won){
            displayWinScreen();
        }
        else if(state == GameState.lost){
            displayLoseScreen();
        }
    }



    private static void checkState() {
        if(state == GameState.valid){
            if(catHasEatenMouse()){
                state = GameState.lost;
            }
            else if(isCheeseAtPos(mouse.getPosX(), mouse.getPosY())){
                removeCheeseAtPos(mouse.getPosX(), mouse.getPosY());
                eatenCheese++;
                if(numCheese == eatenCheese){
                    state = GameState.won;
                }
            }
        }
    }

    private static boolean catHasEatenMouse() {
        for(Cat cat: cats){
            if (cat.getPosX() == mouse.getPosX() && cat.getPosY() == mouse.getPosY()){
                return true;
            }
        }
        return false;
    }

    private static void moveCats(){
        for(Cat cat : cats){
            cat.updateCat(maze);
        }
    }



    // Reveals the surroundings of the mouse
    private static void revealSurroundings(){
        int mousePosX = mouse.getPosX();
        int mousePosY = mouse.getPosY();

        for (int x = -1; x <= 1; x++){
            for(int y = -1; y <= 1; y++){

                int elementPosX = mousePosX + x;
                int elementPosY = mousePosY + y;

                // checks if the cell is within bounds
                if(elementPosX >= 0 && elementPosX <= X_MAX
                && elementPosY >= 0 && elementPosY <= Y_MAX){
                    maze.getElementAtPos(elementPosX, elementPosY).setAsRevealed();
                }
            }
        }
    }

    // Checks if the cheese is at a position x and y
    private static boolean isCheeseAtPos(int x, int y){
        for(int i = 0; i < cheeses.size(); i++){
            Actor cheese = cheeses.get(i);
            if(cheese.getPosX() == x && cheese.getPosY() == y){
                return true;
            }
        }
        return false;
    }

    // Removes cheese at given position.
    private static void removeCheeseAtPos(int x, int y){
        for(int i = 0; i < cheeses.size(); i++){
            Actor cheese = cheeses.get(i);
            if(cheese.getPosX() == x && cheese.getPosY() == y){
                cheeses.remove(cheese);
                return;
            }
        }
    }

    // --- All output related stuff


    private static String renderMaze() {
        String returnString = "";
        for (int y = Y_MIN; y <= Y_MAX; y++) {
            for (int x = X_MIN; x <= X_MAX; x++) {
                String charToAppend = ".";

                if (maze.getElementAtPos(x, y).isWall()) {
                    charToAppend = "#";
                } else {
                    charToAppend = " ";
                }
                // Allow hidden elements ONLY IF our setting is flagged.
                if ((!maze.getElementAtPos(x, y).getIsRevealed()) && enableHidingElements) {
                    if(x != X_MIN && x != X_MAX && y != Y_MIN && y != Y_MAX)
                        charToAppend = ".";
                }
                // Checks for Actor positions, in order of precedence:
                if (isCheeseAtPos(x, y)) {
                    charToAppend = "$";
                }

                if ((x == mouse.getPosX()) && (y == mouse.getPosY())) {
                    charToAppend = "@";
                }
                for (Cat tmpCat : cats) {
                    if ((x == tmpCat.getPosX()) && (y == tmpCat.getPosY())) {
                        charToAppend = "!";
                        break;
                    }
                }
                // Add this to the returnString:
                returnString = returnString + charToAppend;
            }
            // Every new Y value (row) we add a newline:
            returnString = returnString + "\n";
        }

        return returnString;
    }

    private static void displayScreen(){
        System.out.println(renderMaze());
        System.out.println(eatenCheese + " cheese out of " + numCheese);
    }

    private static void displayLoseScreen() {
        enableHidingElements = false;
        System.out.println(renderMaze());
        System.out.println("Game lost, you were eaten by a cat!");

    }

    private static void displayWinScreen() {
        enableHidingElements = false;
        System.out.println(renderMaze());
        System.out.println("Congratulations, you win!");
    }

    private static void displayHelpScreen(){
        String helpString = "";
        helpString += "GAME OBJECTIVE: Find all cheese while avoiding the blind cats! \n";
        helpString += "GAME ENTITIES:\n";
        helpString += "- . : Unrevealed cell\n";
        helpString += "-   : Empty cell\n";
        helpString += "- # : Blocked cell\n";
        helpString += "- @ : Mouse, which is you the player!\n";
        helpString += "- ! : Blind cats\n";
        helpString += "- $ : Cheese to get\n";
        helpString += "GAMEPLAY:\n";
        helpString += "- Use the WASD keys for navigation: w -> up, s -> down, a -> left, d -> right\n";
        System.out.println(helpString);
    }
}


// TODO: implement all GameMonitor functions

// Basic enum to keep track of all possible
// game states
enum GameState {
    won,
    lost,
    valid,
    illegal
}
