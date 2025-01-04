package org.cis1200.battleship;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;

public class GameSave {

    private static final String SAVE_DIR = "savedgames";
    private static final int BOARD_SIZE = 10;

    public static void saveGame(String gameName, Battleship battleship) {
        if (gameName == null || gameName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Invalid game name.",
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filepath = SAVE_DIR + "/" + gameName + ".txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            if (battleship.isPlayer1Turn()) {
                bw.write("Player1");
            } else {
                bw.write("Player2");
            }
            bw.newLine();

            bw.write(String.valueOf(battleship.isPlacingShips()));
            bw.newLine();

            bw.write(String.valueOf(battleship.isGameOver()));
            bw.newLine();

            bw.write(String.valueOf(battleship.getShipsPlaced()));
            bw.newLine();


            int[][] p1Board = battleship.getBoard(true);
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    bw.write(String.valueOf(p1Board[r][c]));
                    if (c < BOARD_SIZE - 1) {
                        bw.write(" ");
                    }
                }
                bw.newLine();
            }

            int[][] p2Board = battleship.getBoard(false);
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    bw.write(String.valueOf(p2Board[r][c]));
                    if (c < BOARD_SIZE - 1) {
                        bw.write(" ");
                    }
                }
                bw.newLine();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error saving game: " + e.getMessage(),
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Battleship loadGame(String gameName) {
        if (gameName == null || gameName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Invalid game name. Cannot load.",
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String filepath = SAVE_DIR + "/" + gameName + ".txt";

        if (!Files.exists(Paths.get(filepath))) {
            JOptionPane.showMessageDialog(null,
                    "Saved game file not found: " + filepath,
                    "File Not Found",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            String turnLine = br.readLine();
            if (turnLine == null || (!turnLine.equals("Player1") && !turnLine.equals("Player2"))) {
                throw new IOException("Invalid or missing turn line: " + turnLine);
            }

            String placingShipsLine = br.readLine();
            if (placingShipsLine == null ||
                    (!placingShipsLine.equalsIgnoreCase("true") && !placingShipsLine.equalsIgnoreCase("false"))) {
                throw new IOException("Invalid placingShips data: " + placingShipsLine);
            }
            boolean placingShips = Boolean.parseBoolean(placingShipsLine);

            String gameOverLine = br.readLine();
            if (gameOverLine == null ||
                    (!gameOverLine.equalsIgnoreCase("true") && !gameOverLine.equalsIgnoreCase("false"))) {
                throw new IOException("Invalid gameOver data: " + gameOverLine);
            }
            boolean gameOver = Boolean.parseBoolean(gameOverLine);

            String shipsPlacedLine = br.readLine();
            if (shipsPlacedLine == null) {
                throw new IOException("Missing shipsPlaced line");
            }
            int shipsPlaced;
            try {
                shipsPlaced = Integer.parseInt(shipsPlacedLine);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid shipsPlaced number: " + shipsPlacedLine);
            }

            int[][] p1Board = new int[BOARD_SIZE][BOARD_SIZE];
            for (int r = 0; r < BOARD_SIZE; r++) {
                String line = br.readLine();
                if (line == null) {
                    throw new IOException("Missing Player1 board data at row " + r);
                }
                p1Board[r] = parseBoardLine(line, r, "Player1");
            }

            int[][] p2Board = new int[BOARD_SIZE][BOARD_SIZE];
            for (int r = 0; r < BOARD_SIZE; r++) {
                String line = br.readLine();
                if (line == null) {
                    throw new IOException("Missing Player2 board data at row " + r);
                }
                p2Board[r] = parseBoardLine(line, r, "Player2");
            }

            Battleship loadedGame = new Battleship();
            setBoard(loadedGame, p1Board, true);
            setBoard(loadedGame, p2Board, false);

            if (turnLine.equals("Player2")) {
                loadedGame.switchTurn();
            }

            setPlacingShips(loadedGame, placingShips);
            setGameOver(loadedGame, gameOver);
            setShipsPlaced(loadedGame, shipsPlaced);

            return loadedGame;

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "File not found: " + e.getMessage(),
                    "File Not Found",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error reading game file: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    public static boolean gameExists(String gameName) {
        String filepath = SAVE_DIR + "/" + gameName + ".txt";
        return Files.exists(Paths.get(filepath));
    }

    public static TreeMap<String, Battleship> getAllSavedGames() {
        TreeMap<String, Battleship> games = new TreeMap<>();
        File dir = new File(SAVE_DIR);
        if (dir.exists()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File f : files) {
                    String fname = f.getName().replace(".txt", "");
                    games.put(fname, null);
                }
            }
        }
        return games;
    }

    private static int[] parseBoardLine(String line, int row, String player) throws IOException {
        String[] parts = line.trim().split("\\s+");
        if (parts.length != BOARD_SIZE) {
            throw new IOException("Incorrect number of columns in " + player + " row " + row);
        }
        int[] rowData = new int[BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            try {
                rowData[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid number in " + player + " row " + row + ": " + parts[i]);
            }
        }
        return rowData;
    }

    private static void setBoard(Battleship game, int[][] newBoard, boolean player1Board) {
        int[][] targetBoard = game.getBoard(player1Board);
        for (int r = 0; r < BOARD_SIZE; r++) {
            System.arraycopy(newBoard[r], 0, targetBoard[r], 0, BOARD_SIZE);
        }
    }

    private static void setPlacingShips(Battleship game, boolean placingShips) {

        game.setPlacingShips(placingShips);
    }

    private static void setGameOver(Battleship game, boolean gameOver) {

        game.setGameOver(gameOver);
    }

    private static void setShipsPlaced(Battleship game, int shipsPlaced) {

        game.setShipsPlaced(shipsPlaced);
    }
}