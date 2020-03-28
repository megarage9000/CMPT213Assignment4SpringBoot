package Springboot.Model;

// Devon Lutz and John Ordoyo
// SFU CMPT 213 - Assignment 3
// Maze, cheese, cat game.
// February 2020
// MazeElement.java - Building block of Mazes.

public class MazeElement {

    private boolean isRevealed;
    private boolean isWall;

    public MazeElement(boolean isWall) {
        isRevealed = false;
        this.isWall = isWall;
    }

    public boolean getIsRevealed() {
        return this.isRevealed;
    }

    public void setAsRevealed() {
        this.isRevealed = true;
    }

    public boolean isWall() {
        return isWall;
    }

}