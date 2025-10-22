/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package connectfourgame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ConnectFourGame extends JFrame {
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final int CELL_SIZE = 80;
    private static final int DISC_SIZE = 70;
    
    private int[][] board = new int[ROWS][COLS];
    private GamePanel gamePanel;
    private JLabel statusLabel;
    private int currentPlayer = 1; // 1 = Red, 2 = Yellow
    private boolean gameOver = false;
    private int[] columnHeights = new int[COLS];
    private int animatingColumn = -1;
    private int animatingRow = -1;
    private double animatingY = 0;
    private Timer animationTimer;
    
    private static final Color PLAYER1_COLOR = new Color(255, 71, 87);   // Red
    private static final Color PLAYER2_COLOR = new Color(255, 209, 102); // Yellow
    private static final Color BOARD_COLOR = new Color(52, 73, 255);     // Blue
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 60);
    private static final Color HOVER_COLOR = new Color(100, 100, 150);
    
    private int hoverColumn = -1;
    
    public ConnectFourGame() {
        setTitle("Connect Four");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        
        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        statusLabel = new JLabel("Red Player's Turn");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statusLabel.setForeground(PLAYER1_COLOR);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(statusLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Game panel
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(
            COLS * CELL_SIZE + 40,
            ROWS * CELL_SIZE + 40
        ));
        gamePanel.setBackground(BACKGROUND_COLOR);
        
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameOver && animationTimer == null) {
                    int col = (e.getX() - 20) / CELL_SIZE;
                    if (col >= 0 && col < COLS) {
                        dropDisc(col);
                    }
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hoverColumn = -1;
                gamePanel.repaint();
            }
        });
        
        gamePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!gameOver && animationTimer == null) {
                    int col = (e.getX() - 20) / CELL_SIZE;
                    if (col >= 0 && col < COLS && columnHeights[col] < ROWS) {
                        hoverColumn = col;
                    } else {
                        hoverColumn = -1;
                    }
                    gamePanel.repaint();
                }
            }
        });
        
        add(gamePanel, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16));
        newGameButton.setBackground(new Color(106, 137, 255));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusPainted(false);
        newGameButton.setBorderPainted(false);
        newGameButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newGameButton.addActionListener(e -> newGame());
        
        bottomPanel.add(newGameButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void newGame() {
        board = new int[ROWS][COLS];
        columnHeights = new int[COLS];
        currentPlayer = 1;
        gameOver = false;
        hoverColumn = -1;
        updateStatus("Red Player's Turn", PLAYER1_COLOR);
        gamePanel.repaint();
    }
    
    private void dropDisc(int col) {
        if (columnHeights[col] >= ROWS) {
            return;
        }
        
        int row = ROWS - 1 - columnHeights[col];
        animatingColumn = col;
        animatingRow = row;
        animatingY = -CELL_SIZE;
        
        animationTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animatingY += 15;
                double targetY = row * CELL_SIZE;
                
                if (animatingY >= targetY) {
                    animatingY = targetY;
                    ((Timer)e.getSource()).stop();
                    
                    board[row][col] = currentPlayer;
                    columnHeights[col]++;
                    animationTimer = null;
                    
                    if (checkWin(row, col)) {
                        gameOver = true;
                        String winner = currentPlayer == 1 ? "Red" : "Yellow";
                        updateStatus(winner + " Player Wins!", getCurrentPlayerColor());
                        highlightWin();
                    } else if (isBoardFull()) {
                        gameOver = true;
                        updateStatus("It's a Draw!", Color.WHITE);
                    } else {
                        currentPlayer = 3 - currentPlayer;
                        String player = currentPlayer == 1 ? "Red" : "Yellow";
                        updateStatus(player + " Player's Turn", getCurrentPlayerColor());
                    }
                }
                gamePanel.repaint();
            }
        });
        animationTimer.start();
    }
    
    private boolean checkWin(int row, int col) {
        int player = board[row][col];
        
        // Check horizontal
        int count = 1;
        for (int c = col - 1; c >= 0 && board[row][c] == player; c--) count++;
        for (int c = col + 1; c < COLS && board[row][c] == player; c++) count++;
        if (count >= 4) return true;
        
        // Check vertical
        count = 1;
        for (int r = row - 1; r >= 0 && board[r][col] == player; r--) count++;
        for (int r = row + 1; r < ROWS && board[r][col] == player; r++) count++;
        if (count >= 4) return true;
        
        // Check diagonal (top-left to bottom-right)
        count = 1;
        for (int i = 1; row - i >= 0 && col - i >= 0 && board[row - i][col - i] == player; i++) count++;
        for (int i = 1; row + i < ROWS && col + i < COLS && board[row + i][col + i] == player; i++) count++;
        if (count >= 4) return true;
        
        // Check diagonal (top-right to bottom-left)
        count = 1;
        for (int i = 1; row - i >= 0 && col + i < COLS && board[row - i][col + i] == player; i++) count++;
        for (int i = 1; row + i < ROWS && col - i >= 0 && board[row + i][col - i] == player; i++) count++;
        if (count >= 4) return true;
        
        return false;
    }
    
    private boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (columnHeights[col] < ROWS) return false;
        }
        return true;
    }
    
    private void highlightWin() {
        Timer flashTimer = new Timer(500, null);
        final int[] flashCount = {0};
        
        flashTimer.addActionListener(e -> {
            flashCount[0]++;
            gamePanel.repaint();
            if (flashCount[0] >= 6) {
                ((Timer)e.getSource()).stop();
            }
        });
        flashTimer.start();
    }
    
    private void updateStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }
    
    private Color getCurrentPlayerColor() {
        return currentPlayer == 1 ? PLAYER1_COLOR : PLAYER2_COLOR;
    }
    
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int offsetX = 20;
            int offsetY = 20;
            
            // Draw hover indicator
            if (hoverColumn >= 0 && !gameOver) {
                g2d.setColor(HOVER_COLOR);
                g2d.fillRoundRect(
                    offsetX + hoverColumn * CELL_SIZE + 5,
                    offsetY - 15,
                    CELL_SIZE - 10,
                    10,
                    5, 5
                );
            }
            
            // Draw board background
            g2d.setColor(BOARD_COLOR);
            g2d.fillRoundRect(offsetX, offsetY, COLS * CELL_SIZE, ROWS * CELL_SIZE, 20, 20);
            
            // Draw discs
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    int x = offsetX + col * CELL_SIZE + (CELL_SIZE - DISC_SIZE) / 2;
                    int y = offsetY + row * CELL_SIZE + (CELL_SIZE - DISC_SIZE) / 2;
                    
                    if (board[row][col] == 0) {
                        // Empty slot
                        g2d.setColor(BACKGROUND_COLOR);
                        g2d.fillOval(x, y, DISC_SIZE, DISC_SIZE);
                    } else {
                        // Player disc
                        Color discColor = board[row][col] == 1 ? PLAYER1_COLOR : PLAYER2_COLOR;
                        drawDisc(g2d, x, y, discColor);
                    }
                }
            }
            
            // Draw animating disc
            if (animationTimer != null && animatingColumn >= 0) {
                int x = offsetX + animatingColumn * CELL_SIZE + (CELL_SIZE - DISC_SIZE) / 2;
                int y = offsetY + (int)animatingY + (CELL_SIZE - DISC_SIZE) / 2;
                Color discColor = currentPlayer == 1 ? PLAYER1_COLOR : PLAYER2_COLOR;
                drawDisc(g2d, x, y, discColor);
            }
            
            // Draw grid lines
            g2d.setColor(new Color(40, 60, 200));
            g2d.setStroke(new BasicStroke(3));
            for (int col = 0; col <= COLS; col++) {
                int x = offsetX + col * CELL_SIZE;
                g2d.drawLine(x, offsetY, x, offsetY + ROWS * CELL_SIZE);
            }
            for (int row = 0; row <= ROWS; row++) {
                int y = offsetY + row * CELL_SIZE;
                g2d.drawLine(offsetX, y, offsetX + COLS * CELL_SIZE, y);
            }
        }
        
        private void drawDisc(Graphics2D g2d, int x, int y, Color color) {
            // Shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(x + 3, y + 3, DISC_SIZE, DISC_SIZE);
            
            // Main disc
            g2d.setColor(color);
            g2d.fillOval(x, y, DISC_SIZE, DISC_SIZE);
            
            // Highlight
            GradientPaint gradient = new GradientPaint(
                x + DISC_SIZE / 4, y + DISC_SIZE / 4,
                color.brighter().brighter(),
                x + DISC_SIZE, y + DISC_SIZE,
                color.darker()
            );
            g2d.setPaint(gradient);
            g2d.fillOval(x, y, DISC_SIZE, DISC_SIZE);
            
            // Inner highlight
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillOval(x + 10, y + 10, DISC_SIZE / 3, DISC_SIZE / 3);
            
            // Border
            g2d.setColor(color.darker().darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x, y, DISC_SIZE, DISC_SIZE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConnectFourGame game = new ConnectFourGame();
            game.setVisible(true);
        });
    }
}