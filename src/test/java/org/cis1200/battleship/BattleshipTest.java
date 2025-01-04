package org.cis1200.battleship;

import org.junit.jupiter.api.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

public class BattleshipTest {

    private Battleship game;

    @Test
    public void testInitialState() {
        game = new Battleship();
        assertTrue(game.isPlacingShips());
        assertFalse(game.isGameOver());
        assertTrue(game.isPlayer1Turn());
        assertEquals(0, game.getShipsPlaced());
    }

    @Test
    public void testPlaceShipHorizontally() {
        game = new Battleship();
        boolean placed = game.placeShip(0, 0, game.getShipLength(), true);
        assertTrue(placed);
        assertEquals(1, game.getShipsPlaced());
        assertEquals(1, game.getBoard(true)[0][0]);
        assertEquals(1, game.getBoard(true)[0][1]);
        assertEquals(1, game.getBoard(true)[0][2]);
    }

    @Test
    public void testPlaceShipVertically() {
        game = new Battleship();
        boolean placed = game.placeShip(5, 5, game.getShipLength(), false);
        assertTrue(placed);
        assertEquals(1, game.getShipsPlaced());
        assertEquals(1, game.getBoard(true)[5][5]);
        assertEquals(1, game.getBoard(true)[6][5]);
        assertEquals(1, game.getBoard(true)[7][5]);
    }

    @Test
    public void testPlaceShipOutOfBounds() {
        game = new Battleship();
        boolean placed = game.placeShip(8, 0, game.getShipLength(), true);
        assertFalse(placed, "Should not place ship out of bounds");
        assertEquals(0, game.getShipsPlaced(), "No ships placed");
    }

    @Test
    public void testPlaceShipOnAnotherShip() {
        game = new Battleship();
        game.placeShip(0, 0, game.getShipLength(), true);
        boolean placed = game.placeShip(0, 0, game.getShipLength(), true);
        assertFalse(placed, "Cannot place overlapping ship");
        assertEquals(1, game.getShipsPlaced());
    }

    @Test
    public void testUndoLastMove() {
        game = new Battleship();
        game.placeShip(0, 0, game.getShipLength(), true);
        boolean undone = game.undoLastMove();
        assertTrue(undone, "Should undo last move");
        assertEquals(0, game.getShipsPlaced());
        assertEquals(0, game.getBoard(true)[0][0], "No ships placed");
    }

    @Test
    public void testConfirmPlacementAfterAllShipsPlaced() {
        game = new Battleship();

        assertTrue(game.placeShip(0, 0, game.getShipLength(), true));
        assertTrue(game.placeShip(3, 0, game.getShipLength(), true));
        assertTrue(game.placeShip(6, 0, game.getShipLength(), true));
        assertTrue(game.placeShip(0, 1, game.getShipLength(), true));
        assertTrue(game.placeShip(3, 1, game.getShipLength(), true));

        assertTrue(game.allShipsPlacedForCurrentPlayer());

        game.confirmPlacement();
        assertFalse(game.isPlayer1Turn(), "Should now be Player 2's turn");
        assertEquals(0, game.getShipsPlaced());
        assertTrue(game.isPlacingShips(), "Still placing ships (Player 2)");
    }

    @Test
    public void testAttackMiss() {
        game = new Battleship();
        for (int i = 0; i < game.getTotalShipsPerPlayer(); i++) {
            assertTrue(
                    game.placeShip(i, 0, game.getShipLength(), false),
                    "Player 1 ship " + i + " should be placed successfully."
            );
        }
        game.confirmPlacement();
        for (int i = 0; i < game.getTotalShipsPerPlayer(); i++) {
            assertTrue(
                    game.placeShip(i, 1, game.getShipLength(), false),
                    "Player 2 ship " + i + " should be placed successfully."
            );
        }
        game.confirmPlacement();
        game.switchTurn();
        int result = game.attack(9, 9);
        assertEquals(0, result, "Should be a miss");
        assertEquals(3, game.getBoard(false)[9][9]); // Marked as miss
    }

    @Test
    public void testAttackHit() {
        game = new Battleship();

        for (int i = 0; i < game.getTotalShipsPerPlayer(); i++) {
            assertTrue(
                    game.placeShip(i, 0, game.getShipLength(), false),
                    "Player 1 ship " + i + " should be placed successfully."
            );
        }
        game.confirmPlacement();
        for (int i = 0; i < game.getTotalShipsPerPlayer(); i++) {
            assertTrue(
                    game.placeShip(i, 1, game.getShipLength(), false),
                    "Player 2 ship " + i + " should be placed successfully."
            );
        }
        game.confirmPlacement();
        game.switchTurn();

        int result = game.attack(0, 1);
        assertEquals(1, result, "Should be a hit");
        assertEquals(
                2, game.getBoard(false)[1][0],
                "The attacked cell on Player 2's board should be marked as hit (2)."
        );
    }

    @Test
    public void testGameOverCondition() {
        game = new Battleship();
        for (int i = 0; i < game.getTotalShipsPerPlayer(); i++) {
            game.placeShip(i, 0, game.getShipLength(), true);
        }
        game.confirmPlacement();
        for (int i = 0; i < game.getTotalShipsPerPlayer(); i++) {
            game.placeShip(i, 1, game.getShipLength(), true);
        }
        game.confirmPlacement();
        for (int i = 0; i < game.getShipLength() * game.getTotalShipsPerPlayer(); i++) {
            game.attack(i % 10, 1 + (i / game.getShipLength())); // Hit all player 2 ships'
                                                                 // positions
        }

        assertTrue(game.isGameOver());
    }

    @Test
    public void testConfirmPlacementWithNoShips() {
        game = new Battleship();
        game.confirmPlacement();
        assertTrue(game.isPlayer1Turn());
        assertTrue(game.isPlacingShips());
        assertEquals(0, game.getShipsPlaced());
    }

    @Test
    public void testUndoWithoutAnyShipsPlaced() {
        game = new Battleship();
        assertFalse(game.undoLastMove(), "Should not undo when no moves are made");
        assertEquals(0, game.getShipsPlaced());
    }

    @Test
    public void testAttackAlreadyHitCell() {
        game = new Battleship();

        for (int i = 0; i < game.getTotalShipsPerPlayer(); i++) {
            assertTrue(game.placeShip(i, 0, game.getShipLength(), false));
        }
        game.confirmPlacement();
        for (int i = 0; i < game.getTotalShipsPerPlayer(); i++) {
            assertTrue(game.placeShip(i, 1, game.getShipLength(), false));
        }
        game.confirmPlacement();
        game.switchTurn();

        int firstAttack = game.attack(0, 1);
        assertEquals(1, firstAttack, "Should be a hit on first attack");

        int secondAttack = game.attack(0, 1);
        assertEquals(-1, secondAttack, "Attacking the same cell again should be invalid");
    }

    @Test
    public void testAttackOutOfBounds() {
        game = new Battleship();
        for (int i = 0; i < game.getTotalShipsPerPlayer(); i++) {
            assertTrue(game.placeShip(i, 0, game.getShipLength(), false));
        }
        game.confirmPlacement();
        for (int i = 0; i < game.getTotalShipsPerPlayer(); i++) {
            assertTrue(game.placeShip(i, 1, game.getShipLength(), false));
        }
        game.confirmPlacement();
        game.switchTurn();

        int result = game.attack(10, 10);
        assertEquals(-1, result, "Attacking out of bounds should return -1");
    }
}
