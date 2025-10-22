
package chessgame;import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ChessGame extends JFrame {

    // --- GUI & Constants ---
    private final int BOARD_SIZE = 8;
    private final JButton[][] boardSquares = new JButton[BOARD_SIZE][BOARD_SIZE];
    private final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private final Color DARK_SQUARE = new Color(181, 136, 99);
    private final Color HIGHLIGHT_COLOR = new Color(100, 255, 100); 
    private final Font PIECE_FONT = new Font("SansSerif", Font.BOLD, 48);
    
    // --- Game State ---
    private boolean isWhiteTurn = true; 
    private boolean isGameOver = false; // NEW: Game state flag
    private JButton selectedSquare = null;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private JLabel statusLabel;
    
    // Map for Unicode Chess Pieces
    private final Map<String, String> pieceSymbols = createPieceSymbols();

    public ChessGame() {
        setTitle("Java Swing Chess - King Capture Game Over");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel chessBoardPanel = initializeBoard();
        mainPanel.add(chessBoardPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("White to move.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setMinimumSize(new Dimension(650, 650));
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // --- Initializers (Unchanged) ---
    private JPanel initializeBoard() {
        JPanel board = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton square = new JButton();
                Color squareColor = ((row + col) % 2 == 0) ? LIGHT_SQUARE : DARK_SQUARE;
                square.setBackground(squareColor);
                square.setOpaque(true);
                square.setBorderPainted(false);
                square.setFocusPainted(false);
                square.setMargin(new Insets(0, 0, 0, 0));
                
                square.putClientProperty("row", row);
                square.putClientProperty("col", col);
                square.putClientProperty("baseColor", squareColor);

                square.addActionListener(new SquareClickListener());

                boardSquares[row][col] = square;
                board.add(square);
            }
        }
        setupInitialPieces();
        return board;
    }
    
    private void setupInitialPieces() {
        String[] backRow = {"Rook", "Knight", "Bishop", "Queen", "King", "Bishop", "Knight", "Rook"};
        
        for (int col = 0; col < BOARD_SIZE; col++) {
            // Black Pieces (Row 0 & 1)
            setPiece(0, col, pieceSymbols.get("Black" + backRow[col]), Color.BLACK, "Black", backRow[col]);
            setPiece(1, col, pieceSymbols.get("BlackPawn"), Color.BLACK, "Black", "Pawn");
            
            // White Pieces (Row 6 & 7)
            setPiece(6, col, pieceSymbols.get("WhitePawn"), Color.WHITE, "White", "Pawn");
            setPiece(7, col, pieceSymbols.get("White" + backRow[col]), Color.WHITE, "White", backRow[col]);
        }
    }
    
    private void setPiece(int r, int c, String symbol, Color color, String player, String type) {
        JButton square = boardSquares[r][c];
        square.setText(symbol);
        square.setFont(PIECE_FONT);
        square.setForeground(color);
        square.putClientProperty("owner", player); 
        square.putClientProperty("type", type);
    }
    
    private Map<String, String> createPieceSymbols() {
        Map<String, String> map = new HashMap<>();
        map.put("WhiteKing", "\u2654"); map.put("WhiteQueen", "\u2655");
        map.put("WhiteRook", "\u2656"); map.put("WhiteBishop", "\u2657");
        map.put("WhiteKnight", "\u2658"); map.put("WhitePawn", "\u2659");
        map.put("BlackKing", "\u265A"); map.put("BlackQueen", "\u265B");
        map.put("BlackRook", "\u265C"); map.put("BlackBishop", "\u265D");
        map.put("BlackKnight", "\u265E"); map.put("BlackPawn", "\u265F");
        return map;
    }

    // --- Core Game Logic ---

    private void handleSquareClick(int row, int col) {
        if (isGameOver) {
            JOptionPane.showMessageDialog(this, "The game is over! Start a new game.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JButton clickedSquare = boardSquares[row][col];
        String pieceOwner = (String) clickedSquare.getClientProperty("owner");
        String currentPlayer = isWhiteTurn ? "White" : "Black";
        
        if (selectedSquare == null) {
            // --- First Click (Selecting a Piece) ---
            if (pieceOwner != null && pieceOwner.equals(currentPlayer)) {
                selectedSquare = clickedSquare;
                selectedRow = row;
                selectedCol = col;
                selectedSquare.setBackground(HIGHLIGHT_COLOR);
            }
            
        } else {
            // --- Second Click (Moving to Destination) ---
            int destRow = row;
            int destCol = col;
            
            if (selectedSquare == clickedSquare) {
                resetSelection();
                return;
            }
            
            // Prevent capturing own piece
            if (pieceOwner != null && pieceOwner.equals(currentPlayer)) {
                resetSelection();
                handleSquareClick(row, col);
                return;
            }
            
            if (isMoveLegal(selectedRow, selectedCol, destRow, destCol)) {
                
                String capturedPieceType = (String) clickedSquare.getClientProperty("type"); // Check what is being captured

                // Execute the move (Transfer all properties)
                clickedSquare.setText(selectedSquare.getText());
                clickedSquare.setFont(selectedSquare.getFont());
                clickedSquare.setForeground(selectedSquare.getForeground());
                clickedSquare.putClientProperty("owner", selectedSquare.getClientProperty("owner"));
                clickedSquare.putClientProperty("type", selectedSquare.getClientProperty("type"));

                // Clear the old square
                selectedSquare.setText("");
                selectedSquare.putClientProperty("owner", null);
                selectedSquare.putClientProperty("type", null);
                
                // --- NEW: Check for Game Over Condition ---
                if ("King".equals(capturedPieceType)) {
                    isGameOver = true;
                    String winner = isWhiteTurn ? "White" : "Black";
                    statusLabel.setText("GAME OVER! " + winner + " wins by King capture!");
                    JOptionPane.showMessageDialog(this, winner + " wins! The opposing King has been captured.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Only switch the turn if the game is not over
                    isWhiteTurn = !isWhiteTurn;
                    statusLabel.setText(isWhiteTurn ? "White to move." : "Black to move.");
                }

            } else {
                statusLabel.setText("ILLEGAL MOVE! " + currentPlayer + " to move.");
            }
            
            // Reset state
            resetSelection();
        }
    }
    
    private void resetSelection() {
        if (selectedSquare != null) {
            Color baseColor = (Color) selectedSquare.getClientProperty("baseColor");
            selectedSquare.setBackground(baseColor);
        }
        selectedSquare = null;
        selectedRow = -1;
        selectedCol = -1;
        if (!statusLabel.getText().startsWith("ILLEGAL") && !isGameOver) {
             statusLabel.setText(isWhiteTurn ? "White to move." : "Black to move.");
        }
    }

    // ------------------------------------------------------------------------------------------------
    // --- Move Validation Logic (Simplified from previous step) ---
    // ------------------------------------------------------------------------------------------------

    private boolean isMoveLegal(int startRow, int startCol, int endRow, int endCol) {
        String pieceType = (String) boardSquares[startRow][startCol].getClientProperty("type");

        if (pieceType == null) return false;

        switch (pieceType) {
            case "Rook":
                return isValidRookMove(startRow, startCol, endRow, endCol);
            case "Pawn":
                return isValidPawnMove(startRow, startCol, endRow, endCol);
            case "King":
            case "Queen":
            case "Knight":
            case "Bishop":
                // TEMPORARY: Allow all moves for other pieces (for quick testing of King capture)
                return true; 
            default:
                return false;
        }
    }
    
    private boolean isTargetEmpty(int r, int c) {
        return boardSquares[r][c].getClientProperty("owner") == null;
    }
    
    private boolean isHorizontalOrVertical(int sr, int sc, int er, int ec) {
        return (sr == er && sc != ec) || (sr != er && sc == ec);
    }
    
    private boolean isValidRookMove(int sr, int sc, int er, int ec) {
        if (!isHorizontalOrVertical(sr, sc, er, ec)) {
            return false;
        }
        // Path clearance logic is omitted
        return true;
    }

    private boolean isValidPawnMove(int sr, int sc, int er, int ec) {
        String owner = (String) boardSquares[sr][sc].getClientProperty("owner");
        int direction = owner.equals("White") ? -1 : 1;
        
        int rowChange = er - sr;
        int colChange = Math.abs(ec - sc);

        // Forward 1
        if (colChange == 0 && rowChange == direction) {
            return isTargetEmpty(er, ec);
        }
        // Forward 2
        if (colChange == 0 && rowChange == 2 * direction) {
            int startRow = owner.equals("White") ? 6 : 1;
            return sr == startRow && isTargetEmpty(er, ec) && isTargetEmpty(sr + direction, sc);
        }
        
        // Capture
        if (colChange == 1 && rowChange == direction) {
            String targetOwner = (String) boardSquares[er][ec].getClientProperty("owner");
            return targetOwner != null && !targetOwner.equals(owner);
        }
        
        return false;
    }
    // ------------------------------------------------------------------------------------------------

    private class SquareClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clickedSquare = (JButton) e.getSource();
            int row = (int) clickedSquare.getClientProperty("row");
            int col = (int) clickedSquare.getClientProperty("col");
            handleSquareClick(row, col);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGame::new);
    }
} 