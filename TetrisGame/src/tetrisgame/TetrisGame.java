/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tetrisgame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TetrisGame extends JFrame {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    
    private int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
    private GamePanel gamePanel;
    private Timer timer;
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JLabel linesLabel;
    
    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;
    private int currentX, currentY;
    private int currentPiece;
    private int currentRotation;
    private boolean gameOver = false;
    
    private static final Color[] COLORS = {
        new Color(60, 60, 90),    // Empty
        new Color(0, 240, 240),   // I - Cyan
        new Color(240, 160, 0),   // L - Orange
        new Color(0, 0, 240),     // J - Blue
        new Color(240, 240, 0),   // O - Yellow
        new Color(0, 240, 0),     // S - Green
        new Color(160, 0, 240),   // T - Purple
        new Color(240, 0, 0)      // Z - Red
    };
    
    private static final int[][][][] PIECES = {
        // I piece
        {{{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}},
         {{0,0,1,0}, {0,0,1,0}, {0,0,1,0}, {0,0,1,0}},
         {{0,0,0,0}, {0,0,0,0}, {1,1,1,1}, {0,0,0,0}},
         {{0,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,1,0,0}}},
        // L piece
        {{{0,0,0,0}, {2,2,2,0}, {2,0,0,0}, {0,0,0,0}},
         {{0,2,0,0}, {0,2,0,0}, {0,2,2,0}, {0,0,0,0}},
         {{0,0,2,0}, {2,2,2,0}, {0,0,0,0}, {0,0,0,0}},
         {{2,2,0,0}, {0,2,0,0}, {0,2,0,0}, {0,0,0,0}}},
        // J piece
        {{{0,0,0,0}, {3,3,3,0}, {0,0,3,0}, {0,0,0,0}},
         {{0,3,3,0}, {0,3,0,0}, {0,3,0,0}, {0,0,0,0}},
         {{3,0,0,0}, {3,3,3,0}, {0,0,0,0}, {0,0,0,0}},
         {{0,3,0,0}, {0,3,0,0}, {3,3,0,0}, {0,0,0,0}}},
        // O piece
        {{{0,0,0,0}, {0,4,4,0}, {0,4,4,0}, {0,0,0,0}},
         {{0,0,0,0}, {0,4,4,0}, {0,4,4,0}, {0,0,0,0}},
         {{0,0,0,0}, {0,4,4,0}, {0,4,4,0}, {0,0,0,0}},
         {{0,0,0,0}, {0,4,4,0}, {0,4,4,0}, {0,0,0,0}}},
        // S piece
        {{{0,0,0,0}, {0,5,5,0}, {5,5,0,0}, {0,0,0,0}},
         {{0,5,0,0}, {0,5,5,0}, {0,0,5,0}, {0,0,0,0}},
         {{0,0,0,0}, {0,5,5,0}, {5,5,0,0}, {0,0,0,0}},
         {{5,0,0,0}, {5,5,0,0}, {0,5,0,0}, {0,0,0,0}}},
        // T piece
        {{{0,0,0,0}, {6,6,6,0}, {0,6,0,0}, {0,0,0,0}},
         {{0,6,0,0}, {0,6,6,0}, {0,6,0,0}, {0,0,0,0}},
         {{0,6,0,0}, {6,6,6,0}, {0,0,0,0}, {0,0,0,0}},
         {{0,6,0,0}, {6,6,0,0}, {0,6,0,0}, {0,0,0,0}}},
        // Z piece
        {{{0,0,0,0}, {7,7,0,0}, {0,7,7,0}, {0,0,0,0}},
         {{0,0,7,0}, {0,7,7,0}, {0,7,0,0}, {0,0,0,0}},
         {{0,0,0,0}, {7,7,0,0}, {0,7,7,0}, {0,0,0,0}},
         {{0,7,0,0}, {7,7,0,0}, {7,0,0,0}, {0,0,0,0}}}
    };
    
    public TetrisGame() {
        setTitle("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(30, 30, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new GridLayout(1, 3, 20, 0));
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(255, 215, 0));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        levelLabel = new JLabel("Level: 1");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 18));
        levelLabel.setForeground(new Color(0, 240, 240));
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        linesLabel = new JLabel("Lines: 0");
        linesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        linesLabel.setForeground(new Color(0, 240, 0));
        linesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        topPanel.add(scoreLabel);
        topPanel.add(levelLabel);
        topPanel.add(linesLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(
            BOARD_WIDTH * BLOCK_SIZE + 1,
            BOARD_HEIGHT * BLOCK_SIZE + 1
        ));
        gamePanel.setBackground(new Color(20, 20, 40));
        gamePanel.setFocusable(true);
        
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) return;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        moveRight();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        moveDown();
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_SPACE:
                        rotate();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        togglePause();
                        break;
                }
                gamePanel.repaint();
            }
        });
        
        add(gamePanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(30, 30, 60));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 14));
        newGameButton.setBackground(new Color(255, 107, 107));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusPainted(false);
        newGameButton.setBorderPainted(false);
        newGameButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newGameButton.addActionListener(e -> newGame());
        
        JButton pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Arial", Font.BOLD, 14));
        pauseButton.setBackground(new Color(106, 137, 255));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFocusPainted(false);
        pauseButton.setBorderPainted(false);
        pauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pauseButton.addActionListener(e -> togglePause());
        
        bottomPanel.add(newGameButton);
        bottomPanel.add(pauseButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        
        timer = new Timer(getSpeed(), e -> {
            if (!gameOver) {
                moveDown();
                gamePanel.repaint();
            }
        });
        
        newGame();
    }
    
    private void newGame() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        score = 0;
        level = 1;
        linesCleared = 0;
        gameOver = false;
        updateLabels();
        spawnPiece();
        timer.stop();
        timer.setDelay(getSpeed());
        timer.start();
        gamePanel.requestFocus();
    }
    
    private void togglePause() {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            timer.start();
        }
        gamePanel.requestFocus();
    }
    
    private int getSpeed() {
        return Math.max(100, 500 - (level - 1) * 50);
    }
    
    private void spawnPiece() {
        Random rand = new Random();
        currentPiece = rand.nextInt(7);
        currentRotation = 0;
        currentX = BOARD_WIDTH / 2 - 2;
        currentY = 0;
        
        if (collides(currentX, currentY, currentPiece, currentRotation)) {
            gameOver = true;
            timer.stop();
            JOptionPane.showMessageDialog(this, 
                "Game Over!\nScore: " + score + "\nLines: " + linesCleared,
                "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void moveLeft() {
        if (!collides(currentX - 1, currentY, currentPiece, currentRotation)) {
            currentX--;
        }
    }
    
    private void moveRight() {
        if (!collides(currentX + 1, currentY, currentPiece, currentRotation)) {
            currentX++;
        }
    }
    
    private void moveDown() {
        if (!collides(currentX, currentY + 1, currentPiece, currentRotation)) {
            currentY++;
        } else {
            placePiece();
            clearLines();
            spawnPiece();
        }
    }
    
    private void rotate() {
        int newRotation = (currentRotation + 1) % 4;
        if (!collides(currentX, currentY, currentPiece, newRotation)) {
            currentRotation = newRotation;
        }
    }
    
    private boolean collides(int x, int y, int piece, int rotation) {
        int[][] shape = PIECES[piece][rotation];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (shape[i][j] != 0) {
                    int boardX = x + j;
                    int boardY = y + i;
                    
                    if (boardX < 0 || boardX >= BOARD_WIDTH || 
                        boardY >= BOARD_HEIGHT) {
                        return true;
                    }
                    
                    if (boardY >= 0 && board[boardY][boardX] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void placePiece() {
        int[][] shape = PIECES[currentPiece][currentRotation];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (shape[i][j] != 0) {
                    int boardX = currentX + j;
                    int boardY = currentY + i;
                    if (boardY >= 0 && boardY < BOARD_HEIGHT && 
                        boardX >= 0 && boardX < BOARD_WIDTH) {
                        board[boardY][boardX] = shape[i][j];
                    }
                }
            }
        }
    }
    
    private void clearLines() {
        int cleared = 0;
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] == 0) {
                    full = false;
                    break;
                }
            }
            
            if (full) {
                cleared++;
                for (int k = i; k > 0; k--) {
                    board[k] = board[k - 1].clone();
                }
                board[0] = new int[BOARD_WIDTH];
                i++;
            }
        }
        
        if (cleared > 0) {
            linesCleared += cleared;
            score += cleared * cleared * 100 * level;
            level = linesCleared / 10 + 1;
            timer.setDelay(getSpeed());
            updateLabels();
        }
    }
    
    private void updateLabels() {
        scoreLabel.setText("Score: " + score);
        levelLabel.setText("Level: " + level);
        linesLabel.setText("Lines: " + linesCleared);
    }
    
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw board
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    drawBlock(g2d, j * BLOCK_SIZE, i * BLOCK_SIZE, board[i][j]);
                }
            }
            
            // Draw current piece
            if (!gameOver) {
                int[][] shape = PIECES[currentPiece][currentRotation];
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (shape[i][j] != 0) {
                            drawBlock(g2d, 
                                (currentX + j) * BLOCK_SIZE, 
                                (currentY + i) * BLOCK_SIZE, 
                                shape[i][j]);
                        }
                    }
                }
            }
            
            // Draw grid
            g2d.setColor(new Color(40, 40, 70));
            for (int i = 0; i <= BOARD_HEIGHT; i++) {
                g2d.drawLine(0, i * BLOCK_SIZE, BOARD_WIDTH * BLOCK_SIZE, i * BLOCK_SIZE);
            }
            for (int j = 0; j <= BOARD_WIDTH; j++) {
                g2d.drawLine(j * BLOCK_SIZE, 0, j * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE);
            }
        }
        
        private void drawBlock(Graphics2D g2d, int x, int y, int colorIndex) {
            g2d.setColor(COLORS[colorIndex]);
            g2d.fillRect(x + 1, y + 1, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
            
            if (colorIndex != 0) {
                g2d.setColor(COLORS[colorIndex].brighter());
                g2d.fillRect(x + 2, y + 2, BLOCK_SIZE - 4, BLOCK_SIZE - 4);
                
                g2d.setColor(COLORS[colorIndex].darker());
                g2d.drawRect(x + 1, y + 1, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TetrisGame game = new TetrisGame();
            game.setVisible(true);
        });
    }
}

