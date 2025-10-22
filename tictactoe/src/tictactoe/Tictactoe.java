
package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class Tictactoe implements ActionListener {

    // --- GUI Components ---
    private JFrame frame;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton[] buttons;

    // --- Game State Variables ---
    private String currentPlayer = "X";
    private int movesCount = 0;
    private boolean gameOver = false;
    private static final int BOARD_SIZE = 9;

    /**
     * Constructor: Initializes the game GUI.
     */
    public Tictactoe() {
        // 1. Frame Setup
        frame = new JFrame("Tic-Tac-Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); // Center the window

        // 2. Status Label (Top)
        statusLabel = new JLabel("Player X's turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statusLabel.setForeground(new Color(25, 118, 210)); // Blue
        frame.add(statusLabel, BorderLayout.NORTH);

        // 3. Board Panel (Center)
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3, 5, 5)); // 3x3 grid with gaps
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(boardPanel, BorderLayout.CENTER);

        // 4. Initialize Buttons
        buttons = new JButton[BOARD_SIZE];
        initializeButtons();

        // 5. Reset Button (Bottom)
        JButton resetButton = new JButton("Reset Game");
        resetButton.setFont(new Font("Arial", Font.BOLD, 18));
        resetButton.setBackground(new Color(239, 83, 80)); // Reddish tone
        resetButton.setForeground(Color.WHITE);
        resetButton.addActionListener(e -> resetGame());
        frame.add(resetButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Creates and styles the 9 game buttons.
     */
    private void initializeButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Comic Sans MS", Font.BOLD, 60));
            buttons[i].setFocusPainted(false);
            buttons[i].setBackground(new Color(240, 240, 240)); // Light Gray
            buttons[i].addActionListener(this);
            boardPanel.add(buttons[i]);
        }
    }

    /**
     * Handles all button clicks (the 9 grid cells).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        JButton clickedButton = (JButton) e.getSource();

        // Only allow moves on empty cells
        if (clickedButton.getText().equals("")) {
            clickedButton.setText(currentPlayer);
            movesCount++;
            
            // Set color based on player
            if (currentPlayer.equals("X")) {
                clickedButton.setForeground(new Color(40, 53, 147)); // Dark Blue for X
            } else {
                clickedButton.setForeground(new Color(211, 47, 47)); // Dark Red for O
            }

            // 1. Check for win/draw
            if (checkWin()) {
                statusLabel.setText("🎉 Player " + currentPlayer + " wins! 🎉");
                highlightWinningLine();
                gameOver = true;
            } else if (movesCount == BOARD_SIZE) {
                statusLabel.setText("🤝 Game is a Draw! 🤝");
                gameOver = true;
            } else {
                // 2. Switch player
                currentPlayer = currentPlayer.equals("X") ? "O" : "X";
                statusLabel.setText("Player " + currentPlayer + "'s turn");
            }
        }
    }

    /**
     * Checks all 8 possible winning combinations (rows, columns, diagonals).
     * @return true if the current player has won, false otherwise.
     */
    private boolean checkWin() {
        int[][] winCombinations = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
            {0, 4, 8}, {2, 4, 6}             // Diagonals
        };

        String currentText;
        for (int[] combo : winCombinations) {
            currentText = buttons[combo[0]].getText();
            // Check if the starting cell is not empty AND all three match
            if (!currentText.isEmpty() &&
                currentText.equals(buttons[combo[1]].getText()) &&
                currentText.equals(buttons[combo[2]].getText())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Highlights the buttons that form the winning line.
     */
    private void highlightWinningLine() {
        int[][] winCombinations = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
            {0, 4, 8}, {2, 4, 6}             // Diagonals
        };

        String winner = currentPlayer;
        Color highlightColor = new Color(76, 175, 80); // Green
        
        for (int[] combo : winCombinations) {
            if (buttons[combo[0]].getText().equals(winner) &&
                buttons[combo[1]].getText().equals(winner) &&
                buttons[combo[2]].getText().equals(winner)) {
                
                // Highlight the winning buttons
                for (int index : combo) {
                    buttons[index].setBackground(highlightColor);
                    buttons[index].setForeground(Color.WHITE); 
                }
                return;
            }
        }
    }

    /**
     * Resets the game state and board.
     */
    private void resetGame() {
        currentPlayer = "X";
        movesCount = 0;
        gameOver = false;
        statusLabel.setText("Player X's turn");
        statusLabel.setForeground(new Color(25, 118, 210)); // Blue
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            buttons[i].setText("");
            buttons[i].setBackground(new Color(240, 240, 240)); // Reset background
            buttons[i].setForeground(Color.BLACK); // Default foreground (will be set on next move)
        }
    }

    /**
     * Main method to start the game on the Event Dispatch Thread (EDT).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Tictactoe::new);
    }
}
