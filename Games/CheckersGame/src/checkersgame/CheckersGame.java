/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package checkersgame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CheckersGame extends JFrame {
    private static final int BOARD_SIZE = 8;
    private static final int CELL_SIZE = 80;
    private static final int PIECE_SIZE = 60;
    
    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private GamePanel gamePanel;
    private JLabel statusLabel;
    private int currentPlayer = 1; // 1 = Red, 2 = Black
    private int selectedRow = -1;
    private int selectedCol = -1;
    private ArrayList<Move> validMoves = new ArrayList<>();
    private boolean mustJump = false;
    
    private static final int EMPTY = 0;
    private static final int RED_PIECE = 1;
    private static final int RED_KING = 2;
    private static final int BLACK_PIECE = 3;
    private static final int BLACK_KING = 4;
    
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color RED_COLOR = new Color(255, 71, 87);
    private static final Color BLACK_COLOR = new Color(50, 50, 70);
    private static final Color KING_GOLD = new Color(255, 215, 0);
    private static final Color SELECTED_COLOR = new Color(144, 238, 144);
    private static final Color VALID_MOVE_COLOR = new Color(100, 200, 100);
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 60);
    
    class Move {
        int fromRow, fromCol, toRow, toCol;
        boolean isJump;
        int jumpedRow, jumpedCol;
        
        Move(int fromRow, int fromCol, int toRow, int toCol) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
            this.isJump = false;
        }
        
        Move(int fromRow, int fromCol, int toRow, int toCol, int jumpedRow, int jumpedCol) {
            this(fromRow, fromCol, toRow, toCol);
            this.isJump = true;
            this.jumpedRow = jumpedRow;
            this.jumpedCol = jumpedCol;
        }
    }
    
    public CheckersGame() {
        setTitle("Checkers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        
        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        statusLabel = new JLabel("Red's Turn");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 28));
        statusLabel.setForeground(RED_COLOR);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(statusLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Game panel
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(
            BOARD_SIZE * CELL_SIZE + 40,
            BOARD_SIZE * CELL_SIZE + 40
        ));
        gamePanel.setBackground(BACKGROUND_COLOR);
        
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = (e.getY() - 20) / CELL_SIZE;
                int col = (e.getX() - 20) / CELL_SIZE;
                
                if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                    handleClick(row, col);
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
        
        initializeBoard();
    }
    
    private void initializeBoard() {
        // Place black pieces (top)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    board[row][col] = BLACK_PIECE;
                }
            }
        }
        
        // Place red pieces (bottom)
        for (int row = 5; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    board[row][col] = RED_PIECE;
                }
            }
        }
    }
    
    private void newGame() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
        currentPlayer = 1;
        selectedRow = -1;
        selectedCol = -1;
        validMoves.clear();
        mustJump = false;
        updateStatus();
        gamePanel.repaint();
    }
    
    private void handleClick(int row, int col) {
        if (selectedRow == -1) {
            // Select a piece
            if (isPieceOfCurrentPlayer(row, col)) {
                selectedRow = row;
                selectedCol = col;
                calculateValidMoves(row, col);
                gamePanel.repaint();
            }
        } else {
            // Try to move
            Move move = findMove(selectedRow, selectedCol, row, col);
            if (move != null) {
                executeMove(move);
                
                // Check for additional jumps
                if (move.isJump && canJumpFrom(row, col)) {
                    selectedRow = row;
                    selectedCol = col;
                    calculateValidMoves(row, col);
                    mustJump = true;
                    gamePanel.repaint();
                } else {
                    selectedRow = -1;
                    selectedCol = -1;
                    validMoves.clear();
                    mustJump = false;
                    switchPlayer();
                    checkGameOver();
                }
            } else {
                // Deselect or select another piece
                selectedRow = -1;
                selectedCol = -1;
                validMoves.clear();
                if (isPieceOfCurrentPlayer(row, col)) {
                    selectedRow = row;
                    selectedCol = col;
                    calculateValidMoves(row, col);
                }
                gamePanel.repaint();
            }
        }
    }
    
    private boolean isPieceOfCurrentPlayer(int row, int col) {
        int piece = board[row][col];
        if (currentPlayer == 1) {
            return piece == RED_PIECE || piece == RED_KING;
        } else {
            return piece == BLACK_PIECE || piece == BLACK_KING;
        }
    }
    
    private void calculateValidMoves(int row, int col) {
        validMoves.clear();
        
        // First check if there are any jumps available for any piece
        boolean jumpAvailable = hasJumpsAvailable();
        
        if (jumpAvailable) {
            // Only calculate jump moves
            calculateJumpMoves(row, col);
        } else {
            // Calculate regular moves
            calculateRegularMoves(row, col);
        }
    }
    
    private boolean hasJumpsAvailable() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (isPieceOfCurrentPlayer(row, col) && canJumpFrom(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean canJumpFrom(int row, int col) {
        int piece = board[row][col];
        boolean isKing = (piece == RED_KING || piece == BLACK_KING);
        
        int[][] directions = isKing ? 
            new int[][]{{-1,-1}, {-1,1}, {1,-1}, {1,1}} :
            (currentPlayer == 1 ? new int[][]{{-1,-1}, {-1,1}} : new int[][]{{1,-1}, {1,1}});
        
        for (int[] dir : directions) {
            int jumpRow = row + dir[0] * 2;
            int jumpCol = col + dir[1] * 2;
            int midRow = row + dir[0];
            int midCol = col + dir[1];
            
            if (isValidJump(row, col, jumpRow, jumpCol, midRow, midCol)) {
                return true;
            }
        }
        return false;
    }
    
    private void calculateJumpMoves(int row, int col) {
        int piece = board[row][col];
        boolean isKing = (piece == RED_KING || piece == BLACK_KING);
        
        int[][] directions = isKing ? 
            new int[][]{{-1,-1}, {-1,1}, {1,-1}, {1,1}} :
            (currentPlayer == 1 ? new int[][]{{-1,-1}, {-1,1}} : new int[][]{{1,-1}, {1,1}});
        
        for (int[] dir : directions) {
            int jumpRow = row + dir[0] * 2;
            int jumpCol = col + dir[1] * 2;
            int midRow = row + dir[0];
            int midCol = col + dir[1];
            
            if (isValidJump(row, col, jumpRow, jumpCol, midRow, midCol)) {
                validMoves.add(new Move(row, col, jumpRow, jumpCol, midRow, midCol));
            }
        }
    }
    
    private boolean isValidJump(int fromRow, int fromCol, int toRow, int toCol, int midRow, int midCol) {
        if (toRow < 0 || toRow >= BOARD_SIZE || toCol < 0 || toCol >= BOARD_SIZE) {
            return false;
        }
        
        if (board[toRow][toCol] != EMPTY) {
            return false;
        }
        
        int midPiece = board[midRow][midCol];
        if (currentPlayer == 1) {
            return midPiece == BLACK_PIECE || midPiece == BLACK_KING;
        } else {
            return midPiece == RED_PIECE || midPiece == RED_KING;
        }
    }
    
    private void calculateRegularMoves(int row, int col) {
        int piece = board[row][col];
        boolean isKing = (piece == RED_KING || piece == BLACK_KING);
        
        int[][] directions = isKing ? 
            new int[][]{{-1,-1}, {-1,1}, {1,-1}, {1,1}} :
            (currentPlayer == 1 ? new int[][]{{-1,-1}, {-1,1}} : new int[][]{{1,-1}, {1,1}});
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE) {
                if (board[newRow][newCol] == EMPTY) {
                    validMoves.add(new Move(row, col, newRow, newCol));
                }
            }
        }
    }
    
    private Move findMove(int fromRow, int fromCol, int toRow, int toCol) {
        for (Move move : validMoves) {
            if (move.toRow == toRow && move.toCol == toCol) {
                return move;
            }
        }
        return null;
    }
    
    private void executeMove(Move move) {
        int piece = board[move.fromRow][move.fromCol];
        board[move.fromRow][move.fromCol] = EMPTY;
        board[move.toRow][move.toCol] = piece;
        
        // Remove jumped piece
        if (move.isJump) {
            board[move.jumpedRow][move.jumpedCol] = EMPTY;
        }
        
        // Promote to king
        if (piece == RED_PIECE && move.toRow == 0) {
            board[move.toRow][move.toCol] = RED_KING;
        } else if (piece == BLACK_PIECE && move.toRow == BOARD_SIZE - 1) {
            board[move.toRow][move.toCol] = BLACK_KING;
        }
        
        gamePanel.repaint();
    }
    
    private void switchPlayer() {
        currentPlayer = 3 - currentPlayer;
        updateStatus();
    }
    
    private void updateStatus() {
        if (currentPlayer == 1) {
            statusLabel.setText("Red's Turn");
            statusLabel.setForeground(RED_COLOR);
        } else {
            statusLabel.setText("Black's Turn");
            statusLabel.setForeground(BLACK_COLOR);
        }
    }
    
    private void checkGameOver() {
        int redCount = 0, blackCount = 0;
        boolean currentPlayerHasMoves = false;
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int piece = board[row][col];
                if (piece == RED_PIECE || piece == RED_KING) redCount++;
                if (piece == BLACK_PIECE || piece == BLACK_KING) blackCount++;
                
                if (isPieceOfCurrentPlayer(row, col)) {
                    calculateValidMoves(row, col);
                    if (!validMoves.isEmpty()) {
                        currentPlayerHasMoves = true;
                    }
                }
            }
        }
        
        validMoves.clear();
        
        if (redCount == 0) {
            showGameOver("Black Wins!");
        } else if (blackCount == 0) {
            showGameOver("Red Wins!");
        } else if (!currentPlayerHasMoves) {
            String winner = currentPlayer == 1 ? "Black" : "Red";
            showGameOver(winner + " Wins!");
        }
    }
    
    private void showGameOver(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(KING_GOLD);
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
    
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int offsetX = 20;
            int offsetY = 20;
            
            // Draw board
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    int x = offsetX + col * CELL_SIZE;
                    int y = offsetY + row * CELL_SIZE;
                    
                    // Draw square
                    if ((row + col) % 2 == 0) {
                        g2d.setColor(LIGHT_SQUARE);
                    } else {
                        g2d.setColor(DARK_SQUARE);
                    }
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    
                    // Highlight selected square
                    if (row == selectedRow && col == selectedCol) {
                        g2d.setColor(SELECTED_COLOR);
                        g2d.setStroke(new BasicStroke(4));
                        g2d.drawRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                    }
                }
            }
            
            // Draw valid move indicators
            for (Move move : validMoves) {
                int x = offsetX + move.toCol * CELL_SIZE;
                int y = offsetY + move.toRow * CELL_SIZE;
                g2d.setColor(VALID_MOVE_COLOR);
                g2d.fillOval(x + CELL_SIZE / 2 - 10, y + CELL_SIZE / 2 - 10, 20, 20);
            }
            
            // Draw pieces
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if (board[row][col] != EMPTY) {
                        int x = offsetX + col * CELL_SIZE + (CELL_SIZE - PIECE_SIZE) / 2;
                        int y = offsetY + row * CELL_SIZE + (CELL_SIZE - PIECE_SIZE) / 2;
                        drawPiece(g2d, x, y, board[row][col]);
                    }
                }
            }
        }
        
        private void drawPiece(Graphics2D g2d, int x, int y, int piece) {
            Color color;
            boolean isKing = false;
            
            switch (piece) {
                case RED_PIECE:
                    color = RED_COLOR;
                    break;
                case RED_KING:
                    color = RED_COLOR;
                    isKing = true;
                    break;
                case BLACK_PIECE:
                    color = BLACK_COLOR;
                    break;
                case BLACK_KING:
                    color = BLACK_COLOR;
                    isKing = true;
                    break;
                default:
                    return;
            }
            
            // Shadow
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.fillOval(x + 3, y + 3, PIECE_SIZE, PIECE_SIZE);
            
            // Main piece
            GradientPaint gradient = new GradientPaint(
                x + PIECE_SIZE / 4, y + PIECE_SIZE / 4,
                color.brighter(),
                x + PIECE_SIZE * 3 / 4, y + PIECE_SIZE * 3 / 4,
                color.darker()
            );
            g2d.setPaint(gradient);
            g2d.fillOval(x, y, PIECE_SIZE, PIECE_SIZE);
            
            // Border
            g2d.setColor(color.darker().darker());
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(x, y, PIECE_SIZE, PIECE_SIZE);
            
            // King crown
            if (isKing) {
                g2d.setColor(KING_GOLD);
                g2d.setFont(new Font("Arial", Font.BOLD, 32));
                String crown = "♔";
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (PIECE_SIZE - fm.stringWidth(crown)) / 2;
                int textY = y + (PIECE_SIZE + fm.getAscent() - fm.getDescent()) / 2;
                
                // Crown shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(crown, textX + 1, textY + 1);
                
                // Crown
                g2d.setColor(KING_GOLD);
                g2d.drawString(crown, textX, textY);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CheckersGame game = new CheckersGame();
            game.setVisible(true);
        });
    }
}

