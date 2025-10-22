package scientificcalc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * ScientificCalculator
 * A fully functional scientific calculator implemented using Java Swing.
 */
public class ScientificCalc implements ActionListener {

    // --- GUI Components ---
    private JFrame frame;
    private JTextField display;
    private JPanel buttonPanel;

    // --- Calculator State Variables ---
    private String displayValue = "0";
    private double currentResult = 0.0;
    private String lastOperation = "";
    private boolean isNewNumber = true; // True if the display is showing a result or waiting for a new number
    private boolean justCalculated = false; // True if '=' was just pressed
    
    // Formatter for precision. Use E notation for very large/small numbers.
    private static final DecimalFormat df = new DecimalFormat("0.############E0"); 
    private static final DecimalFormat dfStandard = new DecimalFormat("#.############");

    // --- Button Labels for Layout (6 rows, 8 columns) ---
    private final String[] combinedButtons = {
        // Row 1
        "sin", "cos", "tan", "log", "%", "sqrt", "x^2", "1/x",
        // Row 2
        "asin", "acos", "atan", "ln", "C", "+/-", "BkSp", "/",
        // Row 3
        "deg", "rad", "PI", "e", "7", "8", "9", "*",
        // Row 4
        "x^y", "!", "(", ")", "4", "5", "6", "-",
        // Row 5
        "", "", "", "", "1", "2", "3", "+",
        // Row 6 (Removed spanning '0' for clean GridLayout)
        "", "", "", "", "0", ".", "=", "" // Last slot is empty for 6x8
    };

    // Constructor to initialize the calculator GUI
    public ScientificCalc() {
        frame = new JFrame("Scientific Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(5, 5));

        // 1. Display Area
        display = new JTextField("0");
        display.setFont(new Font("Inter", Font.BOLD, 30));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        display.setBackground(new Color(245, 245, 245));

        // 2. Button Panel setup
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 8, 5, 5));

        // Create and style buttons
        for (String label : combinedButtons) {
            if (label == null || label.isEmpty()) {
                buttonPanel.add(new JLabel("")); // Filler space
                continue;
            }

            JButton button = new JButton(label);
            button.setFont(new Font("Inter", Font.BOLD, 18));
            button.addActionListener(this);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Apply specific styles
            if (label.matches("[0-9]")) {
                button.setBackground(new Color(240, 240, 240));
                button.setForeground(new Color(30, 30, 30));
            } else if (label.equals("=") || label.matches("[+\\-*/]")) {
                button.setBackground(new Color(59, 130, 246)); // Blue operators
                button.setForeground(Color.WHITE);
            } else if (label.equals("C") || label.equals("BkSp")) {
                button.setBackground(new Color(239, 68, 68)); // Red clear/delete
                button.setForeground(Color.WHITE);
            } else {
                button.setBackground(new Color(229, 231, 235)); // Gray scientific/utility
                button.setForeground(new Color(30, 30, 30));
            }
            button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            buttonPanel.add(button);
        }

        // 3. Assemble Frame
        frame.add(display, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // Final frame setup
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Entry point for the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScientificCalc::new);
    }

    /**
     * Handles all button click events.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.matches("[0-9]")) {
            handleNumber(command);
        } else if (command.equals(".")) {
            handleDecimal();
        } else if (command.equals("C")) {
            clear();
        } else if (command.equals("BkSp")) {
            backspace();
        } else if (command.equals("=")) {
            calculate();
        } else if (command.matches("[+\\-*/]|x\\^y")) { // Arithmetic and Power
            handleArithmetic(command);
        } else if (command.matches("sin|cos|tan|log|ln|sqrt|x\\^2|1/x|%|\\+/\\-|asin|acos|atan|PI|e|!|deg|rad")) {
            handleScientific(command);
        }
        // Parenthesis buttons '(' and ')' are ignored as full expression parsing is not implemented.
    }

    private void handleNumber(String digit) {
        if (isNewNumber || displayValue.equals("Error")) {
            displayValue = digit;
            isNewNumber = false;
            justCalculated = false;
        } else {
            // Prevent leading zero unless it's a decimal "0.X"
            if (displayValue.equals("0") && !digit.equals("0")) {
                 displayValue = digit;
            } else if (!displayValue.equals("0") || displayValue.contains(".")) {
                displayValue += digit;
            }
        }
        updateDisplay();
    }

    private void handleDecimal() {
        if (isNewNumber || displayValue.equals("Error")) {
            displayValue = "0.";
            isNewNumber = false;
            justCalculated = false;
        } else if (!displayValue.contains(".")) {
            displayValue += ".";
        }
        updateDisplay();
    }

    private void clear() {
        displayValue = "0";
        currentResult = 0.0;
        lastOperation = "";
        isNewNumber = true;
        justCalculated = false;
        updateDisplay();
    }

    private void backspace() {
        if (displayValue.equals("Error")) {
            clear();
            return;
        }
        if (displayValue.length() > 1 && !isNewNumber) {
            displayValue = displayValue.substring(0, displayValue.length() - 1);
        } else {
            displayValue = "0";
            isNewNumber = true;
        }
        updateDisplay();
    }

    private void handleArithmetic(String op) {
        try {
            double value = Double.parseDouble(displayValue);

            // If a previous operation exists and we haven't just calculated, perform it
            if (!lastOperation.isEmpty() && !isNewNumber && !justCalculated) {
                currentResult = performOperation(currentResult, value, lastOperation);
            } else if (isNewNumber) {
                 // If a new number is displayed (e.g., after an operation or initial 0),
                 // update current result only if it wasn't a result from a previous calc
                 currentResult = Double.parseDouble(formatResult(currentResult));
            } else {
                // First number in a sequence (e.g., '5' then '+')
                currentResult = value;
            }

            lastOperation = op;
            isNewNumber = true;
            justCalculated = false;
            displayValue = formatResult(currentResult);
            updateDisplay();

        } catch (Exception ex) {
            display.setText("Error");
            isNewNumber = true;
            lastOperation = "";
        }
    }

    private void calculate() {
        if (lastOperation.isEmpty()) {
            // If no operation is pending, do nothing or re-display current number
            isNewNumber = true;
            justCalculated = true;
            return;
        }

        try {
            // The value to use as the second operand
            double value = Double.parseDouble(displayValue);
            
            // Perform the final operation
            currentResult = performOperation(currentResult, value, lastOperation);
            
            displayValue = formatResult(currentResult);
            lastOperation = ""; // Clear pending operation
            isNewNumber = true;
            justCalculated = true;
            updateDisplay();
            
        } catch (Exception ex) {
            display.setText("Error");
            isNewNumber = true;
            lastOperation = "";
            justCalculated = false;
        }
    }

    private double performOperation(double num1, double num2, String op) throws ArithmeticException {
        switch (op) {
            case "+": return num1 + num2;
            case "-": return num1 - num2;
            case "*": return num1 * num2;
            case "/":
                if (num2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return num1 / num2;
            case "x^y": return Math.pow(num1, num2);
            default:
                 // Should not happen if handleArithmetic is correct
                 throw new IllegalArgumentException("Unknown operation: " + op); 
        }
    }

    private void handleScientific(String command) {
        try {
            // Get value from display, but if it's a constant (PI, e), ignore current display
            double value = command.matches("PI|e") ? 0.0 : Double.parseDouble(displayValue);
            double result = value;
            
            // If an arithmetic operation is pending, clear it before unary function
            lastOperation = ""; 

            switch (command) {
                case "sin": result = Math.sin(Math.toRadians(value)); break;
                case "cos": result = Math.cos(Math.toRadians(value)); break;
                case "tan": result = Math.tan(Math.toRadians(value)); break;
                case "asin": result = Math.toDegrees(Math.asin(value)); break;
                case "acos": result = Math.toDegrees(Math.acos(value)); break;
                case "atan": result = Math.toDegrees(Math.atan(value)); break;
                case "log": 
                    if (value <= 0) throw new ArithmeticException("Invalid input for log");
                    result = Math.log10(value); 
                    break;
                case "ln": 
                    if (value <= 0) throw new ArithmeticException("Invalid input for ln");
                    result = Math.log(value); 
                    break;
                case "sqrt": 
                    if (value < 0) throw new ArithmeticException("Cannot take sqrt of negative");
                    result = Math.sqrt(value); 
                    break;
                case "x^2": result = value * value; break;
                case "1/x": 
                    if (value == 0) throw new ArithmeticException("Cannot divide by zero");
                    result = 1.0 / value; 
                    break;
                case "%": result = value / 100.0; break;
                case "+/-": result = -value; break;
                case "PI": result = Math.PI; break;
                case "e": result = Math.E; break;
                case "!": 
                    // Factorial: check for non-integer or negative
                    if (value != (long) value || value < 0) {
                        throw new ArithmeticException("Invalid input for !");
                    }
                    result = factorial((long) value);
                    break;
                case "deg": result = Math.toDegrees(value); break;
                case "rad": result = Math.toRadians(value); break;
            }

            displayValue = formatResult(result);
            isNewNumber = true;
            justCalculated = true;
            updateDisplay();

        } catch (Exception ex) {
            display.setText("Error");
            isNewNumber = true;
            lastOperation = "";
            justCalculated = false;
        }
    }

    /**
     * Calculates the factorial of a number (iterative). Checks for overflow using Double.
     * @param n The non-negative integer number to calculate factorial of.
     * @return The factorial result or Double.POSITIVE_INFINITY on overflow.
     */
    private double factorial(long n) {
        if (n == 0) return 1.0;
        
        double result = 1.0;
        for (long i = 1; i <= n; i++) {
            // Check for potential overflow based on max double value before next multiplication
            if (result > Double.MAX_VALUE / i) { 
                return Double.POSITIVE_INFINITY;
            }
            result *= i;
        }
        return result;
    }

    /**
     * Formats the double result to a clean string, using standard format 
     * or scientific notation for extremes.
     */
    private String formatResult(double result) {
        if (Double.isInfinite(result)) {
            return "Error (Overflow/Div by Zero)";
        }
        if (Double.isNaN(result)) {
            return "Error (NaN)";
        }
        
        // Use standard format unless the number is too large/small
        String formatted = dfStandard.format(result);

        // Check for scientific notation necessity (simplistic check)
        if (Math.abs(result) >= 1e12 || (Math.abs(result) > 0 && Math.abs(result) < 1e-8)) {
            formatted = df.format(result);
        }

        return formatted;
    }

    private void updateDisplay() {
        display.setText(displayValue);
    }
}