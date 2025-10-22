/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package snakegame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JFrame {
    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = 30;
    private static final int INITIAL_DELAY = 150;
    
    private GamePanel gamePanel;
    private Timer timer;
    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    
    private ArrayList<Point> snake = new ArrayList<>();
    private Point food;
    private Point specialFood;
    private int specialFoodTimer = 0;
    private int direction = KeyEvent.VK_RIGHT;
    private int nextDirection = KeyEvent.VK_RIGHT;
    private int score = 0;
    private int highScore = 0;
    private boolean gameOver = false;
    private boolean paused = false;
    private Random random = new Random();
    
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 40);
    private static final Color GRID_COLOR = new Color(30, 30, 50);
    private static final Color SNAKE_HEAD_COLOR = new Color(0, 255, 150);
    private static final Color SNAKE_BODY_COLOR = new Color(50, 200, 100);
    private static final Color FOOD_COLOR = new Color(255, 71, 87);
    private static final Color SPECIAL_FOOD_COLOR = new Color(255, 215, 0);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    
    public SnakeGame() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        
        // Top panel
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        topPanel.setBackground(new Color(30, 30, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(SNAKE_HEAD_COLOR);
        scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        highScoreLabel = new JLabel("High Score: 0");
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        highScoreLabel.setForeground(SPECIAL_FOOD_COLOR);
        highScoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        topPanel.add(scoreLabel);
        topPanel.add(highScoreLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Game panel
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(
            GRID_SIZE * CELL_SIZE,
            GRID_SIZE * CELL_SIZE
        ));
        gamePanel.setBackground(BACKGROUND_COLOR);
        gamePanel.setFocusable(true);
        
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                
                if (key == KeyEvent.VK_SPACE) {
                    if (gameOver) {
                        newGame();
                    } else {
                        togglePause();
                    }
                    return;
                }
                
                if (paused || gameOver) return;
                
                // Prevent 180-degree turns
                if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && direction != KeyEvent.VK_RIGHT) {
                    nextDirection = KeyEvent.VK_LEFT;
                } else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && direction != KeyEvent.VK_LEFT) {
                    nextDirection = KeyEvent.VK_RIGHT;
                } else if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && direction != KeyEvent.VK_DOWN) {
                    nextDirection = KeyEvent.VK_UP;
                } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && direction != KeyEvent.VK_UP) {
                    nextDirection = KeyEvent.VK_DOWN;
                }
            }
        });
        
        add(gamePanel, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(30, 30, 60));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton newGameButton = new JButton("New Game (Space)");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 14));
        newGameButton.setBackground(new Color(106, 137, 255));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusPainted(false);
        newGameButton.setBorderPainted(false);
        newGameButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newGameButton.addActionListener(e -> {
            newGame();
            gamePanel.requestFocus();
        });
        
        JButton pauseButton = new JButton("Pause (Space)");
        pauseButton.setFont(new Font("Arial", Font.BOLD, 14));
        pauseButton.setBackground(new Color(255, 107, 107));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFocusPainted(false);
        pauseButton.setBorderPainted(false);
        pauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pauseButton.addActionListener(e -> {
            togglePause();
            gamePanel.requestFocus();
        });
        
        bottomPanel.add(newGameButton);
        bottomPanel.add(pauseButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        
        timer = new Timer(INITIAL_DELAY, e -> {
            if (!paused && !gameOver) {
                gameLoop();
            }
        });
        
        newGame();
    }
    
    private void newGame() {
        snake.clear();
        snake.add(new Point(GRID_SIZE / 2, GRID_SIZE / 2));
        snake.add(new Point(GRID_SIZE / 2 - 1, GRID_SIZE / 2));
        snake.add(new Point(GRID_SIZE / 2 - 2, GRID_SIZE / 2));
        
        direction = KeyEvent.VK_RIGHT;
        nextDirection = KeyEvent.VK_RIGHT;
        score = 0;
        gameOver = false;
        paused = false;
        specialFoodTimer = 0;
        specialFood = null;
        
        spawnFood();
        updateLabels();
        
        timer.stop();
        timer.setDelay(INITIAL_DELAY);
        timer.start();
        
        gamePanel.requestFocus();
        gamePanel.repaint();
    }
    
    private void togglePause() {
        if (!gameOver) {
            paused = !paused;
            gamePanel.repaint();
        }
    }
    
    private void gameLoop() {
        direction = nextDirection;
        moveSnake();
        checkCollisions();
        updateSpecialFood();
        gamePanel.repaint();
    }
    
    private void moveSnake() {
        Point head = snake.get(0);
        Point newHead = new Point(head);
        
        switch (direction) {
            case KeyEvent.VK_LEFT:
                newHead.x--;
                break;
            case KeyEvent.VK_RIGHT:
                newHead.x++;
                break;
            case KeyEvent.VK_UP:
                newHead.y--;
                break;
            case KeyEvent.VK_DOWN:
                newHead.y++;
                break;
        }
        
        snake.add(0, newHead);
        
        // Check if food eaten
        boolean ateFood = false;
        if (newHead.equals(food)) {
            score += 10;
            ateFood = true;
            spawnFood();
            
            // Speed up slightly
            int newDelay = Math.max(50, timer.getDelay() - 2);
            timer.setDelay(newDelay);
            
            // Spawn special food occasionally
            if (score % 50 == 0) {
                spawnSpecialFood();
            }
        } else if (specialFood != null && newHead.equals(specialFood)) {
            score += 50;
            ateFood = true;
            specialFood = null;
            specialFoodTimer = 0;
        }
        
        if (!ateFood) {
            snake.remove(snake.size() - 1);
        }
        
        updateLabels();
    }
    
    private void checkCollisions() {
        Point head = snake.get(0);
        
        // Wall collision
        if (head.x < 0 || head.x >= GRID_SIZE || head.y < 0 || head.y >= GRID_SIZE) {
            endGame();
            return;
        }
        
        // Self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                endGame();
                return;
            }
        }
    }
    
    private void endGame() {
        gameOver = true;
        timer.stop();
        
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
        }
        
        gamePanel.repaint();
    }
    
    private void spawnFood() {
        do {
            food = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        } while (snake.contains(food) || (specialFood != null && food.equals(specialFood)));
    }
    
    private void spawnSpecialFood() {
        do {
            specialFood = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        } while (snake.contains(specialFood) || specialFood.equals(food));
        specialFoodTimer = 50; // Lasts for 50 ticks
    }
    
    private void updateSpecialFood() {
        if (specialFood != null) {
            specialFoodTimer--;
            if (specialFoodTimer <= 0) {
                specialFood = null;
            }
        }
    }
    
    private void updateLabels() {
        scoreLabel.setText("Score: " + score);
    }
    
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw grid
            g2d.setColor(GRID_COLOR);
            for (int i = 0; i <= GRID_SIZE; i++) {
                g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE);
                g2d.drawLine(0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE, i * CELL_SIZE);
            }
            
            if (!gameOver && !paused) {
                // Draw food
                drawFood(g2d, food, FOOD_COLOR);
                
                // Draw special food
                if (specialFood != null) {
                    drawFood(g2d, specialFood, SPECIAL_FOOD_COLOR);
                    
                    // Draw timer bar
                    g2d.setColor(SPECIAL_FOOD_COLOR);
                    int barWidth = (specialFoodTimer * CELL_SIZE) / 50;
                    g2d.fillRect(specialFood.x * CELL_SIZE + (CELL_SIZE - barWidth) / 2,
                               specialFood.y * CELL_SIZE - 5, barWidth, 3);
                }
                
                // Draw snake
                for (int i = 0; i < snake.size(); i++) {
                    Point p = snake.get(i);
                    if (i == 0) {
                        drawSnakeSegment(g2d, p, SNAKE_HEAD_COLOR, true);
                    } else {
                        float ratio = (float)(snake.size() - i) / snake.size();
                        Color color = blendColors(SNAKE_BODY_COLOR, SNAKE_BODY_COLOR.darker(), ratio);
                        drawSnakeSegment(g2d, p, color, false);
                    }
                }
            }
            
            // Draw game over or pause screen
            if (gameOver) {
                drawOverlay(g2d, "GAME OVER", "Press SPACE to restart");
            } else if (paused) {
                drawOverlay(g2d, "PAUSED", "Press SPACE to continue");
            }
        }
        
        private void drawSnakeSegment(Graphics2D g2d, Point p, Color color, boolean isHead) {
            int x = p.x * CELL_SIZE;
            int y = p.y * CELL_SIZE;
            
            // Shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRoundRect(x + 3, y + 3, CELL_SIZE - 6, CELL_SIZE - 6, 12, 12);
            
            // Main body
            GradientPaint gradient = new GradientPaint(
                x, y, color.brighter(),
                x + CELL_SIZE, y + CELL_SIZE, color.darker()
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4, 10, 10);
            
            // Border
            g2d.setColor(color.darker().darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4, 10, 10);
            
            // Eyes for head
            if (isHead) {
                g2d.setColor(Color.WHITE);
                int eyeSize = 5;
                int eyeOffset = 8;
                
                if (direction == KeyEvent.VK_RIGHT) {
                    g2d.fillOval(x + CELL_SIZE - eyeOffset - eyeSize, y + eyeOffset, eyeSize, eyeSize);
                    g2d.fillOval(x + CELL_SIZE - eyeOffset - eyeSize, y + CELL_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                } else if (direction == KeyEvent.VK_LEFT) {
                    g2d.fillOval(x + eyeOffset, y + eyeOffset, eyeSize, eyeSize);
                    g2d.fillOval(x + eyeOffset, y + CELL_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                } else if (direction == KeyEvent.VK_UP) {
                    g2d.fillOval(x + eyeOffset, y + eyeOffset, eyeSize, eyeSize);
                    g2d.fillOval(x + CELL_SIZE - eyeOffset - eyeSize, y + eyeOffset, eyeSize, eyeSize);
                } else if (direction == KeyEvent.VK_DOWN) {
                    g2d.fillOval(x + eyeOffset, y + CELL_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                    g2d.fillOval(x + CELL_SIZE - eyeOffset - eyeSize, y + CELL_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                }
                
                // Pupils
                g2d.setColor(Color.BLACK);
                if (direction == KeyEvent.VK_RIGHT) {
                    g2d.fillOval(x + CELL_SIZE - eyeOffset - eyeSize + 2, y + eyeOffset + 1, 2, 2);
                    g2d.fillOval(x + CELL_SIZE - eyeOffset - eyeSize + 2, y + CELL_SIZE - eyeOffset - eyeSize + 1, 2, 2);
                } else if (direction == KeyEvent.VK_LEFT) {
                    g2d.fillOval(x + eyeOffset + 1, y + eyeOffset + 1, 2, 2);
                    g2d.fillOval(x + eyeOffset + 1, y + CELL_SIZE - eyeOffset - eyeSize + 1, 2, 2);
                } else if (direction == KeyEvent.VK_UP) {
                    g2d.fillOval(x + eyeOffset + 1, y + eyeOffset + 1, 2, 2);
                    g2d.fillOval(x + CELL_SIZE - eyeOffset - eyeSize + 1, y + eyeOffset + 1, 2, 2);
                } else if (direction == KeyEvent.VK_DOWN) {
                    g2d.fillOval(x + eyeOffset + 1, y + CELL_SIZE - eyeOffset - eyeSize + 2, 2, 2);
                    g2d.fillOval(x + CELL_SIZE - eyeOffset - eyeSize + 1, y + CELL_SIZE - eyeOffset - eyeSize + 2, 2, 2);
                }
            }
        }
        
        private void drawFood(Graphics2D g2d, Point p, Color color) {
            int x = p.x * CELL_SIZE;
            int y = p.y * CELL_SIZE;
            int size = CELL_SIZE - 8;
            
            // Shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(x + 7, y + 7, size, size);
            
            // Main food
            RadialGradientPaint gradient = new RadialGradientPaint(
                x + CELL_SIZE / 2, y + CELL_SIZE / 2, size / 2,
                new float[]{0f, 1f},
                new Color[]{color.brighter(), color.darker()}
            );
            g2d.setPaint(gradient);
            g2d.fillOval(x + 4, y + 4, size, size);
            
            // Highlight
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.fillOval(x + 8, y + 8, size / 3, size / 3);
        }
        
        private Color blendColors(Color c1, Color c2, float ratio) {
            int r = (int)(c1.getRed() * ratio + c2.getRed() * (1 - ratio));
            int g = (int)(c1.getGreen() * ratio + c2.getGreen() * (1 - ratio));
            int b = (int)(c1.getBlue() * ratio + c2.getBlue() * (1 - ratio));
            return new Color(r, g, b);
        }
        
        private void drawOverlay(Graphics2D g2d, String title, String subtitle) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm1 = g2d.getFontMetrics();
            int titleX = (getWidth() - fm1.stringWidth(title)) / 2;
            int titleY = getHeight() / 2 - 30;
            g2d.drawString(title, titleX, titleY);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            FontMetrics fm2 = g2d.getFontMetrics();
            int subtitleX = (getWidth() - fm2.stringWidth(subtitle)) / 2;
            int subtitleY = getHeight() / 2 + 20;
            g2d.drawString(subtitle, subtitleX, subtitleY);
            
            if (gameOver) {
                String scoreText = "Final Score: " + score;
                g2d.setColor(SNAKE_HEAD_COLOR);
                int scoreX = (getWidth() - fm2.stringWidth(scoreText)) / 2;
                int scoreY = getHeight() / 2 + 50;
                g2d.drawString(scoreText, scoreX, scoreY);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame game = new SnakeGame();
            game.setVisible(true);
        });
    }
}