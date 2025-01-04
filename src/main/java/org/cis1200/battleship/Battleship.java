package org.cis1200.battleship;

import java.util.LinkedList;

public class Battleship {

    private int[][] board;
    private int[][] opponentBoard;
    private boolean isPlayer1Turn;
    private boolean gameOver;
    private boolean placingShips;
    private int shipsPlaced;

    private LinkedList<Move> moveHistory = new LinkedList<>();

    private static final int TOTAL_SHIPS_PER_PLAYER = 5;
    private static final int SHIP_LENGTH = 3; // Example length

    public Battleship() {
        reset();
    }

    public void reset() {
        board = new int[10][10];
        opponentBoard = new int[10][10];
        isPlayer1Turn = true;
        gameOver = false;
        placingShips = true;
        shipsPlaced = 0;
    }

    public boolean placeShip(int x, int y, int length, boolean isHorizontal) {
        if (!placingShips || x < 0 || y < 0 || x >= 10 || y >= 10 || gameOver) {
            return false;
        }

        int[][] targetBoard;
        if (isPlayer1Turn) {
            targetBoard = board;
        } else {
            targetBoard = opponentBoard;
        }

        for (int i = 0; i < length; i++) {
            int newX = x;
            int newY = y;

            if (isHorizontal) {
                newX = x + i;
            } else {
                newY = y + i;
            }

            if (newX >= 10 || newY >= 10 || targetBoard[newY][newX] != 0) {
                return false;
            }
        }

        for (int i = 0; i < length; i++) {

            int newX = x;
            int newY = y;

            if (isHorizontal) {
                newX = x + i;
            } else {
                newY = y + i;
            }

            targetBoard[newY][newX] = 1;
        }

        shipsPlaced++;
        moveHistory.push(new Move(isPlayer1Turn, x, y, length, isHorizontal));
        return true;
    }

    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) {
            return false;
        }

        Move lastMove = moveHistory.pop();

        int[][] targetBoard;
        if (lastMove.isPlayer1()) {
            targetBoard = board;
        } else {
            targetBoard = opponentBoard;
        }

        for (int i = 0; i < lastMove.getLength(); i++) {
            int newX = lastMove.getStartX();
            int newY = lastMove.getStartY();

            if (lastMove.isHorizontal()) {
                newX += i;
            } else {
                newY += i;
            }

            targetBoard[newY][newX] = 0;
        }

        shipsPlaced--;
        return true;
    }

    public int attack(int x, int y) {
        if (placingShips || x < 0 || y < 0 || x >= 10 || y >= 10 || gameOver) {
            return -1;
        }

        int[][] targetBoard;
        if (isPlayer1Turn) {
            targetBoard = opponentBoard;
        } else {
            targetBoard = board;
        }

        if (targetBoard[y][x] == 0) {
            targetBoard[y][x] = 3;
            return 0;
        } else if (targetBoard[y][x] == 1) {
            targetBoard[y][x] = 2;
            return 1;
        }

        return -1;
    }

    public boolean isGameOver() {
        if (shipsPlaced == 0) {
            return false;
        }

        int[][] targetBoard;
        if (isPlayer1Turn) {
            targetBoard = opponentBoard;
        } else {
            targetBoard = board;
        }

        for (int row = 0; row < targetBoard.length; row++) {
            for (int col = 0; col < targetBoard[row].length; col++) {
                if (targetBoard[row][col] == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }

    public boolean isPlacingShips() {
        return placingShips;
    }

    public int[][] getBoard(boolean isPlayer1) {
        if (isPlayer1) {
            return board;
        } else {
            return opponentBoard;
        }
    }

    public boolean allShipsPlacedForCurrentPlayer() {
        return shipsPlaced >= TOTAL_SHIPS_PER_PLAYER;
    }

    public void confirmPlacement() {
        if (!allShipsPlacedForCurrentPlayer()) {
            return;
        }

        if (isPlayer1Turn) {
            isPlayer1Turn = false;
            shipsPlaced = 0;
        } else {
            placingShips = false;
        }
    }

    public int getShipsPlaced() {
        return shipsPlaced;
    }

    public int getTotalShipsPerPlayer() {
        return TOTAL_SHIPS_PER_PLAYER;
    }

    public int getShipLength() {
        return SHIP_LENGTH;
    }

    public void switchTurn() {
        isPlayer1Turn = !isPlayer1Turn;
    }

    public void setPlacingShips(boolean placingShips) {
        this.placingShips = placingShips;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setShipsPlaced(int shipsPlaced) {
        this.shipsPlaced = shipsPlaced;
    }
}
