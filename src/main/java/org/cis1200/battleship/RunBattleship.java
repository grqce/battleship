package org.cis1200.battleship;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class RunBattleship implements Runnable {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Battleship battleship;
    private MenuPanel menuPanel;

    public static final String MENU_STATE = "MENU_STATE";
    private static final String PLACEMENT_STATE = "PLACEMENT_STATE";
    private static final String ATTACK_STATE = "ATTACK_STATE";
    private static final String GAMEOVER_STATE = "GAMEOVER_STATE";

    private String currentGameName;

    @Override
    public void run() {
        battleship = new Battleship();

        frame = new JFrame("Battleship");
        frame.setLocation(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        menuPanel = new MenuPanel(
                this::startNewGame,
                this::showInstructionsDialog,
                this::loadPastGame
        );

        mainPanel.add(menuPanel, MENU_STATE);

        frame.setVisible(true);
        frame.setPreferredSize(new Dimension(700, 750));

        frame.add(mainPanel);
        frame.pack();

        cardLayout.show(mainPanel, MENU_STATE);
    }

    private void startNewGame() {
        battleship.reset();
        currentGameName = menuPanel.getLastGameName();
        if (currentGameName == null || currentGameName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a valid game name.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        showPlacementPanel();
    }

    private void showPlacementPanel() {
        PlacementPanel placementPanel = new PlacementPanel(battleship, this::showAttackPanel);
        mainPanel.add(placementPanel, PLACEMENT_STATE);
        cardLayout.show(mainPanel, PLACEMENT_STATE);
        mainPanel.revalidate();
        mainPanel.repaint();
        showHelpPanel();
    }

    private void showAttackPanel() {
        AttackPanel attackPanel = new AttackPanel(battleship, this::showGameOverPanel);
        mainPanel.add(attackPanel, ATTACK_STATE);
        cardLayout.show(mainPanel, ATTACK_STATE);
        mainPanel.revalidate();
        mainPanel.repaint();
        showHelpPanel();
    }

    private void showGameOverPanel() {
        GameOverPanel gameOverPanel = new GameOverPanel(battleship);
        mainPanel.add(gameOverPanel, GAMEOVER_STATE);
        cardLayout.show(mainPanel, GAMEOVER_STATE);
        mainPanel.revalidate();
        mainPanel.repaint();
        showHelpPanel();
    }

    private void showHelpPanel() {
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Menu");

        JMenuItem instructionsItem = new JMenuItem("Instructions");
        instructionsItem.addActionListener(e -> showInstructionsDialog());

        JMenuItem backToMenuItem = new JMenuItem("Back to Menu");
        backToMenuItem.addActionListener(e -> {
            cardLayout.show(mainPanel, MENU_STATE);
        });

        JMenuItem saveGameItem = new JMenuItem("Save Game");

        saveGameItem.addActionListener(e -> {
            if (battleship.isPlacingShips()) {
                JOptionPane.showMessageDialog(
                        frame,
                        "You cannot save the game while still placing ships.",
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            if (currentGameName != null && !currentGameName.isEmpty()) {
                GameSave.saveGame(currentGameName, battleship);
                JOptionPane.showMessageDialog(
                        frame,
                        "Game saved successfully as: " + currentGameName,
                        "Save Game",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "No game name found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JMenuItem loadGameItem = new JMenuItem("Load Game");
        loadGameItem.addActionListener(e -> loadPastGame());

        helpMenu.add(instructionsItem);
        helpMenu.add(backToMenuItem);
        helpMenu.add(saveGameItem);
        helpMenu.add(loadGameItem);

        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);
    }

    private void showInstructionsDialog() {
        String instructions = "Battleship Instructions:\n\n" +
                "1. Player 1 will place 5 ships (length 3) on thei" +
                "r board horizontally or vertically.\n" +
                "2. Player 1 will confirm placement when done and then Player 2 will place ships.\n"
                +
                "3. After both players place ships, the attack phase begins.\n" +
                "4. Click cells to attack the opponent’s ships. Red = hit, Grey = miss.\n" +
                "5. If you hit your opponent's ship, you get another turn to attack.\n" +
                "5. Game ends when all ships of a player are sunk by the winner";
        JOptionPane.showMessageDialog(
                frame, instructions, "Instructions", JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void loadPastGame() {
        Set<String> savedGames = GameSave.getAllSavedGames().keySet();
        if (savedGames.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "No saved games available.",
                    "Load Game",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
                frame,
                "Select a saved game to load:",
                "Load Game",
                JOptionPane.PLAIN_MESSAGE,
                null,
                savedGames.toArray(),
                savedGames.toArray()[0]
        );

        if (selected != null && GameSave.gameExists(selected)) {
            Battleship loaded = GameSave.loadGame(selected);
            if (loaded != null) {
                battleship = loaded; // Replace current battleship state
                currentGameName = selected;
                showAttackPanel();
                JOptionPane.showMessageDialog(
                        frame,
                        "Game '" + selected + "' loaded successfully!",
                        "Load Game",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new RunBattleship());
    }
}
