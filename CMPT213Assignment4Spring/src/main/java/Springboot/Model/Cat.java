package Springboot.Model;

// Devon Lutz and John Ordoyo
// SFU CMPT 213 - Assignment 3
// Maze, cheese, cat game.
// February 2020
// Mouse.java - Subclass of Actor

import java.util.*;


// Mouse is identical to Actor, except that it
// implements methods that help generate a pseudo
// random move
enum Direction {Up, Down, Left, Right, NoDirection}

public class Cat extends Actor {

    ArrayList<Direction> possibleMoves;
    Direction previousMove;
    public Cat(int startingX, int startingY) {
        super(startingX, startingY);
        possibleMoves = new ArrayList<>();
        previousMove = Direction.NoDirection;
    }

    // Calls all the move functions associated with cat
    public void updateCat(Maze maze){
        Direction direction = generateMove(maze);
        moveCat(direction);
    }

    public Direction generateMove(Maze maze) {
        int moveX = this.getPosX();
        int moveY = this.getPosY();

        // - Getting all possible moves
        // 1. Going Up
        if(maze.isValidMove(moveX, moveY - 1)){
            possibleMoves.add(Direction.Up);
        }
        // 2. Going Down
        if(maze.isValidMove(moveX, moveY + 1)){
            possibleMoves.add(Direction.Down);
        }
        // 3. Going Left
        if(maze.isValidMove(moveX - 1, moveY)){
            possibleMoves.add(Direction.Left);
        }
        //4. Going Right
        if(maze.isValidMove(moveX + 1, moveY)){
            possibleMoves.add(Direction.Right);
        }

        // - Picking a random move

        Direction dirToMove;
        if(possibleMoves.size() > 1){
            // Removes the move that counters the previous move
            Direction counterMove = getOppositeDirection(previousMove);
            if(possibleMoves.contains(counterMove)){
                possibleMoves.remove(counterMove);
            }
            // Picks a random move out of possible moves
            int randomIndex = (int)Math.random() * (possibleMoves.size() - 1);
            dirToMove = possibleMoves.get(randomIndex);
        }
        else{
            dirToMove = possibleMoves.get(0);
        }

        // - Clear list for next iteration
        possibleMoves.clear();
        previousMove = dirToMove;
        return dirToMove;
    }

    // Converts given direction into a cat move
    public void moveCat(Direction direction){
        switch(direction){
            case Up:
                this.setyPos(this.getPosY() - 1);
                break;
            case Down:
                this.setyPos(this.getPosY() + 1);
                break;
            case Left:
                this.setxPos(this.getPosX() - 1);
                 break;
            case Right:
                this.setxPos(this.getPosX() + 1);
                break;
            default:
                break;
        }
    }

    // Helper function to get the opposite direction for counter move
    Direction getOppositeDirection(Direction direction){
        switch(direction){
            case Up:
                return Direction.Down;
            case Down:
                return Direction.Up;
            case Left:
                return Direction.Right;
            case Right:
                return Direction.Left;
            default:
                break;
        }
        return Direction.NoDirection;
    }
}

