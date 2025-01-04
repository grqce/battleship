package org.cis1200.battleship;

public class Move {
    private boolean isPlayer1;
    private int startX, startY;
    private int length;
    private boolean isHorizontal;

    public Move(boolean isPlayer1, int startX, int startY, int length, boolean isHorizontal) {
        this.isPlayer1 = isPlayer1;
        this.startX = startX;
        this.startY = startY;
        this.length = length;
        this.isHorizontal = isHorizontal;
    }

    // Accessor methods
    public boolean isPlayer1() {
        return isPlayer1;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getLength() {
        return length;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }
}