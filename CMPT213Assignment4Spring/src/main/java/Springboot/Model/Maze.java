package Springboot.Model;

// Devon Lutz and John Ordoyo
// SFU CMPT 213 - Assignment 3
// Maze, cheese, cat game.
// February 2020
// Maze.java - Maze object implementation
// Keep track of MazeElements.

public class Maze {

    // Invalid GenerationElement for comparison.
    private static final GenerationElement NO_NEIGHBOR = new GenerationElement(55, 55);
    private MazeElement[][] maze;

    public Maze() {
        // Default maze size is 20x15
        this.maze = new MazeElement[20][15];
        boolean mazeIsValid = false;
        while (!mazeIsValid) {
            generateMaze();
            mazeIsValid = validateMaze();
        }
    }

    public MazeElement getElementAtPos(int x, int y) {
        MazeElement returnElement = null;
        if ((x >= GameMonitor.X_MIN)
                && (x <= GameMonitor.X_MAX)
                && (y >= GameMonitor.Y_MIN)
                && (y <= GameMonitor.Y_MAX)) {
            returnElement = this.maze[x][y];
        }
        return returnElement;
    }

    // TODO: implement all Maze functions

    // Generate maze based on recursive division algorithm.
    // Algorithm description taken from Wikipedia (without pseudo-code)

    private void generateMaze() {
        // The recursive backtracking function I implemented here uses "cells with walls"-style of generation,
        // but the representation we will be using in the game uses "rooms that may be walls or open"-style of
        // representation.
        // Thus, we need to set up a seperate array with a different class to keep track of data,
        // do the generation of the maze, and then convert back into a format usable for the rest of the game.

        // If we keep track of "walls", we have a reduced resolution for the maze:
        int MAX_Y = 6;
        int MAX_X = 8;

        // GenerationElement class has a similar role to MazeElement, but keeps track of unvisited neighbors
        // and cells in all directions - something that MazeElement does not need to do.

        GenerationElement[][] innerMaze = new GenerationElement[9][7];
        for (int x = 0; x <= MAX_X; x++) {
            for (int y = 0; y <= MAX_Y; y++) {
                innerMaze[x][y] = new GenerationElement(x, y);
            }
        }
        // And go again, and for each GenerationElement add to their set of
        // unvisited neighbors:
        for (int y = 0; y <= MAX_Y; y++){
            for (int x = 0; x <= MAX_X; x++) {
                GenerationElement currentElement = innerMaze[x][y];
                // We use a very basic system for enumerating directions, where 0 = up, 1 = right, 2 = down, 3 = left.
                for (int i = 0; i < 4; i++) {
                    GenerationElement potentialNeighbor = getNeighborInDirection(currentElement, i, innerMaze);
                    if (potentialNeighbor != NO_NEIGHBOR) {
                        currentElement.unvisitedNeighbors.add(potentialNeighbor);
                    }
                }
            }
        }

        // Call the recursive backtracker, beginning at element 0,0:
        recursiveBacktrackerGenerator(innerMaze[0][0], innerMaze);
        convertToUsableMaze(innerMaze);
        postConversionTweaks();

        // Now, we need to convert this somehow into a format usable by GameMonitor - which means turning
        // GenerationElements and their walls into MazeElements.

        //

        // TODO UNCOMMENT THIS AND MAKE IT INTO A LOOP
        // if (!validateMaze(this)) {
        // // This means the maze validation failed and we need to regenerate a maze.
        //      needToClear = true;
        //      (somehow loop this again)
        // }
    }

    private void recursiveBacktrackerGenerator(GenerationElement currentElement, GenerationElement[][] innerMaze) {
        // This maze generation function assumes we start with "cells"
        // in a 9 x 7 grid, with walls all in-between.
        // Given a starting cell (0,0), we will randomly choose
        // an unvisited neighboring cell to "visit, knocking
        // down the wall in-between. This continues until
        // we reach a cell with no unvisited neighbors,
        // at which point we return back to the calling function.

        // Implements the "recursive backtracker" algorithm described in the Wikipedia page:
        // https://en.wikipedia.org/wiki/Maze_generation_algorithm#Recursive_backtracker

        // Set our element as visited:
        currentElement.isVisited = true;

        while (currentElement.unvisitedNeighbors.size() > 0) {
            boolean gotNeighbor = false;
            GenerationElement randomNeighbor = NO_NEIGHBOR;
            int directionToVisit = -12;

            while (!gotNeighbor) {
                directionToVisit = (int)(Math.ceil(Math.random() * 4) - 1);
                randomNeighbor = getNeighborInDirection(currentElement, directionToVisit, innerMaze);

                if (currentElement.unvisitedNeighbors.contains(randomNeighbor)) {
                    gotNeighbor = true;
                }

            }

            // Make sure this is a valid element!
            if ((randomNeighbor != NO_NEIGHBOR) && (!randomNeighbor.isVisited)) {
                // Remove the wall between us - ensure it for both elements!
                removeWallInDirection(currentElement, directionToVisit);
                removeWallInDirection(randomNeighbor, getOppositeDirection(directionToVisit));
                // Then recurse:
                recursiveBacktrackerGenerator(randomNeighbor, innerMaze);
            }

            // REMOVE this visited neighbor from the list!
            currentElement.unvisitedNeighbors.remove(randomNeighbor);
        }

    }

    private void convertToUsableMaze(GenerationElement[][] innerMaze) {
        // The "maze" representation we use for the game is actually very simple:
        // More or less just a boolean for if they are "revealed", and if they are a wall.
        // Because of this, the easiest way to convert from GenerationElements
        // to MazeElements is by inspecting our String representation of the innerMaze.
        String mapRepresentation = renderGeneratedMap(innerMaze);
        // Use split() to pull out individual characters:
        String[] rows = mapRepresentation.split("\n");
        for (int y = GameMonitor.Y_MIN; y <= GameMonitor.Y_MAX; y++) {
            String row = rows[y];
            String[] cols = row.split("");
            for (int x = GameMonitor.X_MIN; x <= GameMonitor.X_MAX; x++) {
                String currentCharacter = cols[x];
                if (currentCharacter.equals("#")) {
                    // put a wall in the maze at this point.
                    this.maze[x][y] = new MazeElement(true);
                } else {
                    this.maze[x][y] = new MazeElement(false);
                }
            }
        }
    }

    private void postConversionTweaks() {
        // Due to the cell/wall system we use, we have to randomly remove some walls:
        // This is because we remove walls based on traversing a path to "neighbors",
        // and on the right and bottom edges there are no neighbors!
        // Deal with the farthest-right column:
        int sideWallsToRemove = 6;
        while (sideWallsToRemove > 0) {
            int yVal = (int)(Math.round(Math.random() * (GameMonitor.Y_MAX - 2)) + 1);
            MazeElement randomSelection = this.maze[GameMonitor.X_MAX - 1][yVal];
            boolean useThisSelection = true;
            if (!randomSelection.isWall()) {
                useThisSelection = false;
            }
            if (useThisSelection) {
                // Remove this wall!
                this.maze[GameMonitor.X_MAX - 1][yVal] = new MazeElement(false);
                sideWallsToRemove--;
            }
        }

        // CREATE LOOPS. The recursive backtracker creates ONE path for the entire game.
        // This more or less ensures that you will lose the game to a cat if the cheese
        // generates somewhere far enough away.
        // Solution? Pick a bunch of walls inside the maze... and get rid of them!
        // Due to the nature of the generation algorithm, this is more or less
        // guaranteed to add loops.

        // TODO tweak how many walls to remove, during playtesting
        int loopsToCreate = 10;
        while (loopsToCreate > 0) {
            // Pick some random element INSIDE the maze (not exterior wall):
            int randomX = (int)(Math.round(Math.random() * (GameMonitor.X_MAX - 2)) + 1);
            int randomY = (int)(Math.round(Math.random() * (GameMonitor.Y_MAX - 2)) + 1);
            if (this.maze[randomX][randomY].isWall()) {
                this.maze[randomX][randomY] = new MazeElement(false);
                loopsToCreate--;
            }
        }

        // Finally, clear off the walls we definitely need cleared:
        this.maze[1][1] = new MazeElement(false);
        this.maze[1][13] = new MazeElement(false);
        this.maze[18][1] = new MazeElement(false);
        this.maze[18][13] = new MazeElement(false);
    }

    private String renderGeneratedMap(GenerationElement[][] innerMaze) {
        String returnString = "####################\n";
        // Double loop here
        for (int y = 0; y <= 6; y++){
            String topString = "#";
            String bottomString = "#";
            for (int x = 0; x <= 8; x++) {
                GenerationElement currentElement = innerMaze[x][y];
                // Add char rep for this space:
                topString = topString + " ";
                if (currentElement.wallRight) {
                    topString = topString + "#";
                } else {
                    topString = topString + " ";
                }
                if (currentElement.wallDown) {
                    bottomString = bottomString + "##";
                } else {
                    bottomString = bottomString + " #";
                }
            }
            topString = topString + "#";
            bottomString = bottomString + "#";
            returnString = returnString + topString + "\n" + bottomString + "\n";
        }
        returnString = returnString + "####################\n";
        return returnString;
    }

    private void removeWallInDirection(GenerationElement elementToModify, int directionToRemove) {
        switch(directionToRemove) {
            case 0:
                elementToModify.wallUp = false;
                break;
            case 1:
                elementToModify.wallRight = false;
                break;
            case 2:
                elementToModify.wallDown = false;
                break;
            case 3:
                elementToModify.wallLeft = false;
                break;
            default:
                // Do nothing.
        }

        return;
    }

    private int getOppositeDirection(int directionToFlip) {
        // Helper function:
        // With directions of 0, 1, 2, 3, subtracting 2 will get it UNLESS 0, or 1.
        // Then we have to overflow/swap.

        int flippedDirection = directionToFlip - 2;
        if (flippedDirection == -1) {
            flippedDirection = 3;
        }
        if (flippedDirection == -2) {
            flippedDirection = 2;
        }

        return flippedDirection;
    }

    private GenerationElement getNeighborInDirection(GenerationElement currentElement,
                                                     int direction,
                                                     GenerationElement[][] innerMaze) {
        // Again, as before:
        // 0 is "up" (north)
        // 1 is "right" (east)
        // 2 is "down" (south)
        // 3 is "left" (west)

        int startingX = currentElement.cellX;
        int startingY = currentElement.cellY;

        // This maze representation is 9x7
        // Thus by 0-indexing maximum indices are 8x6.
        int maxX = 8;
        int maxY = 6;

        // Set the return coordinates to invalid numbers to begin with:
        int returnY = -1;
        int returnX = -1;

        // Now, switch based on direction input:

        switch(direction) {
            case 0:
                // They are trying to move up.
                returnY = startingY - 1;
                returnX = startingX;
                break;
            case 1:
                // They are trying to move right.
                returnY = startingY;
                returnX = startingX + 1;
                break;
            case 2:
                // They are trying to move down.
                returnY = startingY + 1;
                returnX = startingX;
                break;
            case 3:
                // They are trying to move left.
                returnY = startingY;
                returnX = startingX - 1;
                break;
            default:
                // Invalid direction..?
                // Do nothing.
        }

        // Initially set our returning element to be the descriptor of an invalid neighbor:
        GenerationElement returnElement = NO_NEIGHBOR;
        // Now, validate the coordinates we calculated:
        if ((returnX >= 0) && (returnX <= maxX)) {
            if ((returnY >= 0) && (returnY <= maxY)) {
                // Valid coordinates.
                returnElement = innerMaze[returnX][returnY];
            }
        } else {
            // INVALID coordinates calculated.
            // This most likely means that we are an edge piece.
            returnElement = NO_NEIGHBOR;
        }

        return returnElement;
    }

    public boolean validateMaze() {
        // Go through a bunch of checks to ensure the maze is "legal" as per the design doc.
        boolean mazeIsValid = true;
        for (int x = GameMonitor.X_MIN; x <= GameMonitor.X_MAX; x++) {
            for (int y = GameMonitor.Y_MIN; y <= GameMonitor.Y_MAX; y++) {
                // Do some checks based on this cell.
                // First - all open cells (i.e. cell is NOT a wall) have to have some passageway open:
                // Check all directions.
                if (!this.maze[x][y].isWall()) {
                    boolean openPassageway = false;
                    if (x > 0) {
                        if (!this.maze[x - 1][y].isWall()) {
                            openPassageway = true;
                        }
                    }
                    if (x < GameMonitor.X_MAX) {
                        if (!this.maze[x + 1][y].isWall()) {
                            openPassageway = true;
                        }
                    }
                    if (y > 0) {
                        if (!this.maze[x][y - 1].isWall()) {
                            openPassageway = true;
                        }
                    }
                    if (y < GameMonitor.Y_MAX) {
                        if (!this.maze[x][y + 1].isWall()) {
                            openPassageway = true;
                        }
                    }
                    if (!openPassageway) {
                        mazeIsValid = false;
                    }
                }

                // Also, check for blocks of four open walls:
                // (Due to our maze generation algorithm, it is impossible to have four closed walls)
                boolean squareExists = false;
                if ((x < (GameMonitor.X_MAX - 1)) && (y < (GameMonitor.Y_MAX - 1))) {
                    if ((!this.maze[x][y].isWall())
                            && (!this.maze[x + 1][y].isWall())
                            && (!this.maze[x][y + 1].isWall())
                            && (!this.maze[x + 1][y + 1].isWall())) {
                        // Open square exists.
                        squareExists = true;
                    }
                }
                if (squareExists) {
                    // Failed generation.
                    mazeIsValid = false;
                }
            }
        }
        return mazeIsValid;
    }

    // Returns whether the given cell to move into
    // is a valid cell to move into
    public boolean isValidMove(int x, int y){
        if(x >= 0 && x <= GameMonitor.X_MAX && y >= 0 && y <= GameMonitor.Y_MAX){
            return (!maze[x][y].isWall());
        }
        else{
            return false;
        }
    }

}
