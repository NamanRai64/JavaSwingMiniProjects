package numbermerge;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class NumberMerge extends JFrame {
    private static final int SIZE = 4;
    private static final int TILE_SIZE = 100;
    private static final int TILE_MARGIN = 10;
    
    private int[][] board = new int[SIZE][SIZE];
    private JPanel gamePanel;
    private JLabel scoreLabel;
    private int score = 0;
    private Random random = new Random();
    
    private static final Color[] TILE_COLORS = {
        new Color(255, 107, 107), // 2 - Vibrant Red
        new Color(255, 159, 64),  // 4 - Orange
        new Color(255, 205, 86),  // 8 - Yellow
        new Color(132, 255, 103), // 16 - Lime Green
        new Color(75, 207, 250),  // 32 - Cyan
        new Color(106, 137, 255), // 64 - Blue
        new Color(179, 136, 255), // 128 - Purple
        new Color(255, 107, 237), // 256 - Magenta
        new Color(255, 71, 133),  // 512 - Hot Pink
        new Color(255, 184, 77),  // 1024 - Gold
        new Color(255, 102, 196)  // 2048+ - Bright Pink
    };
    
    public NumberMerge() {
        setTitle("Number Merge Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(30, 30, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        scoreLabel.setForeground(new Color(255, 215, 0));
        topPanel.add(scoreLabel);
        
        JButton resetButton = new JButton("New Game");
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.setBackground(new Color(255, 107, 107));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetGame());
        topPanel.add(resetButton);
        
        add(topPanel, BorderLayout.NORTH);
        
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        gamePanel.setPreferredSize(new Dimension(
            SIZE * TILE_SIZE + (SIZE + 1) * TILE_MARGIN,
            SIZE * TILE_SIZE + (SIZE + 1) * TILE_MARGIN
        ));
        gamePanel.setBackground(new Color(45, 45, 75));
        gamePanel.setFocusable(true);
        
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean moved = false;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        moved = moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        moved = moveRight();
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        moved = moveUp();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        moved = moveDown();
                        break;
                }
                if (moved) {
                    addRandomTile();
                    gamePanel.repaint();
                    checkGameOver();
                }
            }
        });
        
        add(gamePanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        
        resetGame();
    }
    
    private void resetGame() {
        board = new int[SIZE][SIZE];
        score = 0;
        scoreLabel.setText("Score: 0");
        addRandomTile();
        addRandomTile();
        gamePanel.repaint();
        gamePanel.requestFocus();
    }
    
    private void addRandomTile() {
        int emptyCells = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) emptyCells++;
            }
        }
        
        if (emptyCells == 0) return;
        
        int target = random.nextInt(emptyCells);
        int count = 0;
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    if (count == target) {
                        board[i][j] = random.nextDouble() < 0.9 ? 2 : 4;
                        return;
                    }
                    count++;
                }
            }
        }
    }
    
    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int x = TILE_MARGIN + j * (TILE_SIZE + TILE_MARGIN);
                int y = TILE_MARGIN + i * (TILE_SIZE + TILE_MARGIN);
                
                int value = board[i][j];
                Color bgColor = value == 0 ? new Color(60, 60, 90) : getTileColor(value);
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 15, 15);
                
                if (value != 0) {
                    g2d.setColor(Color.WHITE);
                    Font font = value < 100 ? new Font("Arial", Font.BOLD, 48) :
                               value < 1000 ? new Font("Arial", Font.BOLD, 36) :
                               new Font("Arial", Font.BOLD, 30);
                    g2d.setFont(font);
                    
                    String text = String.valueOf(value);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = x + (TILE_SIZE - fm.stringWidth(text)) / 2;
                    int textY = y + (TILE_SIZE + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(text, textX, textY);
                }
            }
        }
    }
    
    private Color getTileColor(int value) {
        int index = (int)(Math.log(value) / Math.log(2)) - 1;
        return TILE_COLORS[Math.min(index, TILE_COLORS.length - 1)];
    }
    
    private boolean moveLeft() {
        boolean moved = false;
        for (int i = 0; i < SIZE; i++) {
            int[] row = new int[SIZE];
            int pos = 0;
            boolean merged = false;
            
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != 0) {
                    if (pos > 0 && row[pos - 1] == board[i][j] && !merged) {
                        row[pos - 1] *= 2;
                        score += row[pos - 1];
                        merged = true;
                        moved = true;
                    } else {
                        row[pos] = board[i][j];
                        merged = false;
                        if (pos != j) moved = true;
                        pos++;
                    }
                }
            }
            board[i] = row;
        }
        if (moved) scoreLabel.setText("Score: " + score);
        return moved;
    }
    
    private boolean moveRight() {
        boolean moved = false;
        for (int i = 0; i < SIZE; i++) {
            int[] row = new int[SIZE];
            int pos = SIZE - 1;
            boolean merged = false;
            
            for (int j = SIZE - 1; j >= 0; j--) {
                if (board[i][j] != 0) {
                    if (pos < SIZE - 1 && row[pos + 1] == board[i][j] && !merged) {
                        row[pos + 1] *= 2;
                        score += row[pos + 1];
                        merged = true;
                        moved = true;
                    } else {
                        row[pos] = board[i][j];
                        merged = false;
                        if (pos != j) moved = true;
                        pos--;
                    }
                }
            }
            board[i] = row;
        }
        if (moved) scoreLabel.setText("Score: " + score);
        return moved;
    }
    
    private boolean moveUp() {
        boolean moved = false;
        for (int j = 0; j < SIZE; j++) {
            int[] col = new int[SIZE];
            int pos = 0;
            boolean merged = false;
            
            for (int i = 0; i < SIZE; i++) {
                if (board[i][j] != 0) {
                    if (pos > 0 && col[pos - 1] == board[i][j] && !merged) {
                        col[pos - 1] *= 2;
                        score += col[pos - 1];
                        merged = true;
                        moved = true;
                    } else {
                        col[pos] = board[i][j];
                        merged = false;
                        if (pos != i) moved = true;
                        pos++;
                    }
                }
            }
            for (int i = 0; i < SIZE; i++) {
                board[i][j] = col[i];
            }
        }
        if (moved) scoreLabel.setText("Score: " + score);
        return moved;
    }
    
    private boolean moveDown() {
        boolean moved = false;
        for (int j = 0; j < SIZE; j++) {
            int[] col = new int[SIZE];
            int pos = SIZE - 1;
            boolean merged = false;
            
            for (int i = SIZE - 1; i >= 0; i--) {
                if (board[i][j] != 0) {
                    if (pos < SIZE - 1 && col[pos + 1] == board[i][j] && !merged) {
                        col[pos + 1] *= 2;
                        score += col[pos + 1];
                        merged = true;
                        moved = true;
                    } else {
                        col[pos] = board[i][j];
                        merged = false;
                        if (pos != i) moved = true;
                        pos--;
                    }
                }
            }
            for (int i = 0; i < SIZE; i++) {
                board[i][j] = col[i];
            }
        }
        if (moved) scoreLabel.setText("Score: " + score);
        return moved;
    }
    
    private void checkGameOver() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) return;
                if (j < SIZE - 1 && board[i][j] == board[i][j + 1]) return;
                if (i < SIZE - 1 && board[i][j] == board[i + 1][j]) return;
            }
        }
        
        JOptionPane.showMessageDialog(this, 
            "Game Over! Final Score: " + score, 
            "Game Over", 
            JOptionPane.INFORMATION_MESSAGE);
        resetGame();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NumberMerge game = new NumberMerge();
            game.setVisible(true);
        });
    }
}