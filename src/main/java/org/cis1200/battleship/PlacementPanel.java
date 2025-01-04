package org.cis1200.battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class PlacementPanel extends JPanel {

    private Image backgroundImage;
    private Battleship battleship;
    private JLabel status;
    private JButton confirmButton;
    private JLabel title;
    private boolean isHorizontal = true;

    public PlacementPanel(Battleship battleship, Runnable onPlacementDone) {
        this.battleship = battleship;
        backgroundImage = new ImageIcon(
                "src/main/java/org/cis1200/battleship/gameImages/BattleshipBackground.png"
        ).getImage();
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        titlePanel.add(title);

        confirmButton = createButton(
                "src/main/java/org/cis1200/battleship/gameImages/Confirm.png", 150, 45
        );
        confirmButton.setVisible(false);
        confirmButton.addActionListener(e -> {
            battleship.confirmPlacement();
            if (!battleship.isPlacingShips()) {
                confirmButton.setVisible(false);
                battleship.switchTurn();
                onPlacementDone.run();
            } else {
                confirmButton.setVisible(false);
                status.setText(
                        "Player 2: Place your ships (0/" + battleship.getTotalShipsPerPlayer() + ")"
                );
            }
        });

        JButton undoBtn = createButton(
                "src/main/java/org/cis1200/battleship/gameImages/Undo.png", 150, 45
        );
        undoBtn.addActionListener(e -> {
            if (battleship.undoLastMove()) {
                status.setText("Last ship placement undone.");
                confirmButton.setVisible(false);
                repaint();
            } else {
                status.setText("No moves to undo.");
            }
        });

        JButton rotateButton = createButton(
                "src/main/java/org/cis1200/battleship/gameImages/Rotate.png", 150, 45
        );
        rotateButton.addActionListener(e -> {
            isHorizontal = !isHorizontal;
            status.setText("Placing ships " + (isHorizontal ? "horizontally" : "vertically"));
        });

        add(titlePanel, BorderLayout.NORTH);
        status = new JLabel(
                "Player 1: Place your ships (0/" + battleship.getTotalShipsPerPlayer() + ")"
        );
        status.setForeground(Color.WHITE);

        try {
            Font pixelFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File(
                            "/Users/grace/Downloads/hw09_local_temp/src/main/java/o" +
                                    "rg/cis1200/battleship/PressStart2P.ttf"
                    )
            );
            status.setFont(pixelFont.deriveFont(Font.PLAIN, 10)); // Set size to 24
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            status.setFont(new Font("Courier New", Font.BOLD, 10)); // Fallback font
        }

        JPanel statusPanel = new JPanel();
        statusPanel.setOpaque(false);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        JPanel horizontalButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        horizontalButtonPanel.setOpaque(false);
        horizontalButtonPanel.add(undoBtn);
        horizontalButtonPanel.add(rotateButton);
        horizontalButtonPanel.add(confirmButton);

        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(horizontalButtonPanel);
        statusPanel.add(Box.createVerticalStrut(5));
        status.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(status);
        statusPanel.add(Box.createVerticalStrut(10));

        add(statusPanel, BorderLayout.SOUTH);

        BoardView boardView = new BoardView();
        JPanel horizontalCenteringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        horizontalCenteringPanel.setOpaque(false); // Transparent background
        horizontalCenteringPanel.add(boardView);
        add(horizontalCenteringPanel, BorderLayout.CENTER);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        if (battleship.isPlayer1Turn()) {
            title.setIcon(
                    new ImageIcon(
                            new ImageIcon(
                                    "src/main/java/org/cis1200/battleship/gameImages/Player1.png"
                            )
                                    .getImage()
                                    .getScaledInstance(175, 30, Image.SCALE_SMOOTH)
                    )
            );
        } else {
            title.setIcon(
                    new ImageIcon(
                            new ImageIcon(
                                    "src/main/java/org/cis1200/battleship/gameImages/Player2.png"
                            )
                                    .getImage()
                                    .getScaledInstance(175, 30, Image.SCALE_SMOOTH)
                    )
            );
        }

    }

    private class BoardView extends JPanel {

        public static final int CELL_SIZE = 50;
        public static final int BOARD_WIDTH = 500;
        public static final int BOARD_HEIGHT = 500;

        private int hoverX = -1;
        private int hoverY = -1;

        public BoardView() {
            setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
            setBorder(BorderFactory.createLineBorder(Color.WHITE));
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (battleship.isPlacingShips()
                            && !battleship.allShipsPlacedForCurrentPlayer()) {
                        int x = (e.getX()) / BoardView.CELL_SIZE;
                        int y = (e.getY()) / BoardView.CELL_SIZE;
                        boolean placed = battleship
                                .placeShip(x, y, battleship.getShipLength(), isHorizontal);
                        if (placed) {
                            status.setText(
                                    "Ship placed! (" + battleship.getShipsPlaced() + "/"
                                            + battleship.getTotalShipsPerPlayer() + ")"
                            );
                        } else {
                            status.setText("Cannot place ship here!");
                            if (battleship.allShipsPlacedForCurrentPlayer()) {
                                status.setText("All ships placed! Click confirm.");

                            }
                        }

                        if (battleship.allShipsPlacedForCurrentPlayer()) {
                            status.setText("All ships placed! Click confirm.");
                            confirmButton.setVisible(true);
                            return;

                        }
                        repaint();
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (battleship.allShipsPlacedForCurrentPlayer()) {
                        hoverX = -1;
                        hoverY = -1;
                        repaint();
                        return;
                    }

                    int newHoverX = (e.getX()) / CELL_SIZE;
                    int newHoverY = (e.getY()) / CELL_SIZE;

                    if (newHoverX >= 0 && newHoverX < 10 && newHoverY >= 0 && newHoverY < 10) {
                        if (hoverX != newHoverX || hoverY != newHoverY) {
                            hoverX = newHoverX;
                            hoverY = newHoverY;
                            repaint();
                        }
                    } else {
                        if (hoverX != -1 || hoverY != -1) {
                            hoverX = -1;
                            hoverY = -1;
                            repaint();
                        }
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.WHITE);
            for (int i = 0; i <= BOARD_WIDTH; i += CELL_SIZE) {
                g.drawLine(i, 0, i, BOARD_HEIGHT);
                g.drawLine(0, i, BOARD_WIDTH, i);
            }

            int[][] currentBoard = battleship.getBoard(battleship.isPlayer1Turn());
            for (int yy = 0; yy < 10; yy++) {
                for (int xx = 0; xx < 10; xx++) {
                    if (currentBoard[yy][xx] == 1) {
                        g.setColor(Color.GRAY);
                        g.fillRect(xx * CELL_SIZE, yy * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }

            if (hoverX >= 0 && hoverY >= 0 && battleship.isPlacingShips()) {
                int shipLength = battleship.getShipLength();
                int[][] board = battleship.getBoard(battleship.isPlayer1Turn());
                boolean canPlace = true;
                for (int i = 0; i < shipLength; i++) {
                    int xx = hoverX;
                    int yy = hoverY;

                    if (isHorizontal) {
                        xx += i;
                    } else {
                        yy += i;
                    }
                    if (xx < 0 || xx >= 10 || yy < 0 || yy >= 10 || board[yy][xx] != 0) {
                        canPlace = false;
                        break;
                    }
                }

                if (canPlace) {
                    g.setColor(new Color(17, 210, 17, 100)); // Green
                } else {
                    g.setColor(new Color(255, 0, 0, 100)); // Red
                }

                for (int i = 0; i < shipLength; i++) {
                    int xx = hoverX;
                    int yy = hoverY;

                    if (isHorizontal) {
                        xx += i;
                    } else {
                        yy += i;
                    }

                    if (xx < 0 || xx >= 10 || yy < 0 || yy >= 10) {
                        break;
                    }

                    g.fillRect(xx * CELL_SIZE, yy * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private JButton createButton(String imagePath, int width, int height) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }
}
