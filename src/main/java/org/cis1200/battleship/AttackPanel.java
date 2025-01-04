package org.cis1200.battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class AttackPanel extends JPanel {
    private Image backgroundImage;
    private Timer turnTimer;
    private static final int TURN_DELAY = 3000;

    private Battleship battleship;
    private JLabel status;
    private BoardView boardView;

    private boolean waitingForNext = false;
    private int lastResult = -1;
    JLabel title;

    public AttackPanel(Battleship battleship, Runnable onAttackDone) {
        backgroundImage = new ImageIcon(
                "src/main/java/org/cis1200/battleship/gameImages/BattleshipBackground.png"
        ).getImage();

        setPreferredSize(new Dimension(1000, 1000));
        this.battleship = battleship;
        setLayout(new BorderLayout());

        status = new JLabel("Player 1 attacks first!");
        status.setSize(getWidth(), 50);

        try {
            Font pixelFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File(
                            "/Users/grace/Downloads/hw09_local_temp/src/main/java/org/cis1200/battleship/PressStart2P.ttf"
                    )
            );
            status.setFont(pixelFont.deriveFont(Font.PLAIN, 13)); // Set size to 24
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            status.setFont(new Font("Courier New", Font.BOLD, 13)); // Fallback font
        }

        JPanel statusPanel = new JPanel();
        statusPanel.add(status);
        add(statusPanel, BorderLayout.SOUTH);

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 3, 0));

        title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        titlePanel.add(title);

        add(titlePanel, BorderLayout.NORTH);

        boardView = new BoardView();
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setOpaque(false);
        centeringPanel.add(boardView);
        add(centeringPanel, BorderLayout.CENTER);

        turnTimer = new Timer(TURN_DELAY, e -> {
            if (waitingForNext && lastResult == 0) {
                waitingForNext = false;
                battleship.switchTurn();

                if (battleship.isPlayer1Turn()) {
                    status.setText("Player 1's turn. Make your move.");
                } else {
                    status.setText("Player 2's turn. Make your move.");
                }

                boardView.repaint();
                AttackPanel.this.repaint();
            }
            turnTimer.stop();
        });
        turnTimer.setRepeats(false);

        boardView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!battleship.isPlacingShips() && !waitingForNext) {
                    int x = e.getX() / BoardView.CELL_SIZE;
                    int y = e.getY() / BoardView.CELL_SIZE;
                    int result = battleship.attack(x, y);
                    lastResult = result;

                    if (result == -1) {
                        status.setText("Invalid move! Try again.");
                        return;
                    }
                    boardView.repaint();
                    AttackPanel.this.repaint();

                    if (battleship.isGameOver()) {
                        onAttackDone.run();
                        return;
                    }

                    if (result == 0) {
                        if (battleship.isPlayer1Turn()) {
                            status.setText("Miss! Continuing to Player 2");
                        } else {
                            status.setText("Miss! Continuing to Player 1");
                        }
                        waitingForNext = true;
                        turnTimer.start();
                    } else if (result == 1) {
                        status.setText("Hit! Take another turn.");
                    }
                }
            }
        });
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

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
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

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    hoverX = -1;
                    hoverY = -1;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            g.setColor(Color.WHITE);

            for (int i = 0; i <= BOARD_WIDTH; i += CELL_SIZE) {
                g.drawLine(i, 0, i, BOARD_HEIGHT);
                g.drawLine(0, i, BOARD_WIDTH, i);
            }

            if (hoverX >= 0 && hoverX < 10 && hoverY >= 0 && hoverY < 10) {
                g.setColor(new Color(128, 128, 128, 120));
                g.fillRect(hoverX * CELL_SIZE, hoverY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }

            int[][] targetBoard;
            if (battleship.isPlayer1Turn()) {
                targetBoard = battleship.getBoard(false);
            } else {
                targetBoard = battleship.getBoard(true);
            }

            for (int y = 0; y < 10; y++) {
                for (int x = 0; x < 10; x++) {
                    if (targetBoard[y][x] == 2) {
                        g.setColor(new Color(255, 0, 0, 140)); // Semi-transparent red for hit
                        g.fillOval(
                                x * CELL_SIZE + 5, y * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10
                        );
                    } else if (targetBoard[y][x] == 3) {
                        g.setColor(new Color(0, 0, 0, 140)); // Semi-transparent blue for miss
                        g.fillOval(
                                x * CELL_SIZE + 5, y * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10
                        );
                    }
                }
            }
        }
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

        g.setColor(battleship.isPlayer1Turn() ? new Color(173, 216, 230, 60) : // Light blue for
                                                                               // Player 1
                new Color(230, 190, 230, 60)
        );
        g.fillRect(0, 0, getWidth(), getHeight());
    }

}
