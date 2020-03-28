package Springboot.Model;

// Devon Lutz and John Ordoyo
// SFU CMPT 213 - Assignment 3
// Maze, cheese, cat game.
// February 2020
// Mouse.java - Subclass of Actor

// Mouse is identical to Actor, except that it
// implements move functions to generate a
// new move
public class Mouse extends Actor {

    private int moveX;
    private int moveY;

    public Mouse(int startingX, int startingY) {
        super(startingX, startingY);
    }

    public void moveMouse() {
        setxPos(moveX);
        setyPos(moveY);
    }

    public int getMoveX() {
        return moveX;
    }

    public void setMoveX(int moveX) {
        this.moveX = moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public void setMoveY(int moveY) {
        this.moveY = moveY;
    }

    // To check whether the move made was actually a move
    public boolean hasMadeMove(){
        return (moveX != getPosX() || moveY != getPosY());
    }
}
