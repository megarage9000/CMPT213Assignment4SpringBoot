package Springboot.Model;

// Devon Lutz and John Ordoyo
// SFU CMPT 213 - Assignment 3
// Maze, cheese, cat game.
// February 2020
// GenerationElement.java - Very basic class.
// Used for the recursive backtracker,
// after which it is mapped to MazeElements.

import java.util.HashSet;
import java.util.Set;

public class GenerationElement {

    public boolean wallUp;
    public boolean wallDown;
    public boolean wallLeft;
    public boolean wallRight;
    public boolean isVisited;
    public boolean allNeighborsVisited;
    int cellX;
    int cellY;

    Set<GenerationElement> unvisitedNeighbors;

    public GenerationElement(int cellX, int cellY) {
        this.wallUp = true;
        this.wallDown = true;
        this.wallLeft = true;
        this.wallRight = true;
        this.isVisited = false;
        this.allNeighborsVisited = false;
        this.cellX = cellX;
        this.cellY = cellY;
        unvisitedNeighbors = new HashSet<>();
        }

}
