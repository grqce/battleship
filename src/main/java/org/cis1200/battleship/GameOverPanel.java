package org.cis1200.battleship;

import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends JPanel {
    private Image backgroundImage;
    private ImageIcon winningImage;
    private Battleship battleship;

    public GameOverPanel(Battleship battleship) {
        this.battleship = battleship;

        backgroundImage = new ImageIcon(
                "src/main/java/org/cis1200/battleship/gameImages/BattleshipBackground.png"
        ).getImage();
        if (battleship.isPlayer1Turn()) {
            winningImage = new ImageIcon(
                    "src/main/java/org/cis1200/battleship/gameImages/Player1Wins.png"
            );
        } else {
            winningImage = new ImageIcon(
                    "src/main/java/org/cis1200/battleship/gameImages/Player2Wins.png"
            );
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        JLabel winningLabel = new JLabel();
        Image scaled = winningImage.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        winningImage = new ImageIcon(scaled);
        winningLabel.setIcon(winningImage);
        winningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalGlue());
        add(winningLabel);

        ImageIcon menuIcon = new ImageIcon(
                "src/main/java/org/cis1200/battleship/gameImages/Menu.png"
        );
        Image scaledMenu = menuIcon.getImage().getScaledInstance(200, 60, Image.SCALE_SMOOTH);
        menuIcon = new ImageIcon(scaledMenu);
        JButton menuBtn = new JButton(menuIcon);
        menuBtn.setContentAreaFilled(false);
        menuBtn.setBorderPainted(false);
        menuBtn.setFocusPainted(false);
        menuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuBtn.addActionListener(e -> {
            CardLayout cl = (CardLayout) getParent().getLayout();
            cl.show(getParent(), RunBattleship.MENU_STATE);
        });

        add(Box.createRigidArea(new Dimension(0, 20)));
        add(menuBtn);
        add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        g.setColor(
                battleship.isPlayer1Turn() ? new Color(173, 216, 230, 60)
                        : new Color(230, 190, 230, 60)
        );
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
