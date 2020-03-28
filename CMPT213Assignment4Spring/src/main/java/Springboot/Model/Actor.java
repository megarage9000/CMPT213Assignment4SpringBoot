package Springboot.Model;

// Devon Lutz and John Ordoyo
// SFU CMPT 213 - Assignment 3
// Maze, cheese, cat game.
// February 2020
// Actor.java - Superclass of Cat, Mouse.

// By default, Actor is immutable,
// but subclasses may not keep this property.
public class Actor {

    private int xPos;
    private int yPos;

    public Actor(int startingX, int startingY) {
        // Actor begins with an X,Y:
        this.xPos = startingX;
        this.yPos = startingY;
    }

    public int getPosY() {
        return this.yPos;
    }

    public int getPosX() {
        return this.xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }
}
