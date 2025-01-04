package org.cis1200.battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MenuPanel extends JPanel {
    Image backgroundImage;
    private String lastGameName;

    public MenuPanel(Runnable onNewGame, Runnable onInstructions, Runnable onLoadGame) {
        backgroundImage = new ImageIcon(
                "src/main/java/org/cis1200/battleship/menuImages/MenuBackground.png"
        ).getImage();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(400, 300));

        ImageIcon titleIcon = new ImageIcon(
                "src/main/java/org/cis1200/battleship/menuImages/BattleshipTitle.png"
        );
        Image scaledTitle = titleIcon.getImage().getScaledInstance(350, 60, Image.SCALE_SMOOTH);
        titleIcon = new ImageIcon(scaledTitle);
        JLabel title = new JLabel(titleIcon);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon ngIcon = new ImageIcon(
                "src/main/java/org/cis1200/battleship/menuImages/NewGamesButton.png"
        );
        Image scaledNg = ngIcon.getImage().getScaledInstance(200, 60, Image.SCALE_SMOOTH);
        ngIcon = new ImageIcon(scaledNg);
        JButton newGameBtn = new JButton(ngIcon);
        newGameBtn.setContentAreaFilled(false);
        newGameBtn.setBorderPainted(false);
        newGameBtn.setFocusPainted(false);
        newGameBtn.addActionListener((ActionEvent e) -> {
            String gameName = promptForGameName();
            if (gameName != null) {
                // saveGameMetadata(gameName);
                lastGameName = gameName;
                onNewGame.run(); // Start the new game
            }
        });

        ImageIcon psIcon = new ImageIcon(
                "src/main/java/org/cis1200/battleship/menuImages/PastGameButton.png"
        );
        Image scaledPs = psIcon.getImage().getScaledInstance(200, 60, Image.SCALE_SMOOTH);
        psIcon = new ImageIcon(scaledPs);
        JButton pastGameBtn = new JButton(psIcon);
        pastGameBtn.setContentAreaFilled(false);
        pastGameBtn.setBorderPainted(false);
        pastGameBtn.setFocusPainted(false);
        pastGameBtn.addActionListener((ActionEvent e) -> onLoadGame.run());

        // Create a new panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.add(newGameBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add space between buttons
        buttonPanel.add(pastGameBtn);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon instructionsIcon = new ImageIcon(
                "src/main/java/org/cis1200/battleship/menuImages/InstructionsButton.png"
        );
        Image scaledinstructions = instructionsIcon.getImage()
                .getScaledInstance(200, 60, Image.SCALE_SMOOTH);
        instructionsIcon = new ImageIcon(scaledinstructions);
        JButton instructionsBtn = new JButton(instructionsIcon);
        instructionsBtn.setContentAreaFilled(false);
        instructionsBtn.setBorderPainted(false);
        instructionsBtn.setFocusPainted(false);
        instructionsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsBtn.addActionListener((ActionEvent e) -> onInstructions.run());

        add(Box.createRigidArea(new Dimension(0, 180)));
        add(title);
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(buttonPanel); // Add the button panel instead of individual buttons
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(instructionsBtn);
        add(Box.createVerticalGlue());
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    private String promptForGameName() {
        // Create a panel for custom content
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Enter a name for the new game:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField textField = new JTextField(15);
        // Add padding
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(textField);

        // Show custom dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "New Game",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String gameName = textField.getText().trim();
            if (!gameName.isEmpty()) {
                return gameName;
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Game name cannot be empty!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
        return null; // Cancelled
    }

    public String getLastGameName() {
        return lastGameName;
    }

}