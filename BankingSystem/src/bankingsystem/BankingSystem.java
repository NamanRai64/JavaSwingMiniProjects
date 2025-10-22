package bankingsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class Account {
    private final String accountNumber;
    private final String accountHolder;
    private double balance;

    public Account(String accountNumber, String accountHolder, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialDeposit;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }

    // Core Transactions
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
}

public class BankingSystem {

    // --- Data Store ---
    private Map<String, Account> accounts = new HashMap<>();

    // --- GUI Components ---
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JTextArea outputArea;
    
    // 💡 ENHANCEMENT 3: Dedicated label for immediate transaction feedback
    private JLabel transactionStatusLabel; 

    // Common fields for transactions
    private JTextField transAccNumField, transAmountField;

    // Account Creation fields
    private JTextField createHolderField, createInitialDepositField;

    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 24);
    private static final Color PRIMARY_COLOR = new Color(0, 150, 136); // Teal/Cyan
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113); // Brighter Green
    private static final Color ERROR_COLOR = new Color(231, 76, 60); // Red

    public BankingSystem() {
        accounts.put("1001", new Account("1001", "Alice Smith", 500.00));
        accounts.put("1002", new Account("1002", "Bob Johnson", 1200.50));
        
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("SecureBank - Digital Banking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 10, 15, 10));
        JLabel titleLabel = new JLabel("SecureBank - Digital Transactions");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane for main functionality
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        // 💡 ENHANCEMENT: Clearer tab titles/icons
        tabbedPane.addTab("👤 New Account", createAccountPanel());
        tabbedPane.addTab("💰 Deposit/Withdraw", createTransactionPanel());
        tabbedPane.addTab("🔍 Check Balance", createBalancePanel());
        
        frame.add(tabbedPane, BorderLayout.CENTER);

        // Output Log
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setText("System Log:\n");
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Full Transaction History"));
        frame.add(scrollPane, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    // --- Tab 1: Account Creation ---

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        createHolderField = new JTextField(20);
        createInitialDepositField = new JTextField(10);
        
        JButton createButton = createStyledButton("Create Account", e -> handleAccountCreation());

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Account Holder Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(createHolderField, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Initial Deposit ($):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(createInitialDepositField, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.ipady = 10;
        gbc.insets = new Insets(30, 10, 10, 10); // extra top padding for button
        panel.add(createButton, gbc);
        
        return panel;
    }

    private void handleAccountCreation() {
        String holder = createHolderField.getText().trim();
        double initialDeposit = 0.0;

        if (holder.isEmpty()) {
            logMessage("❌ Error: Account holder name is required.", true);
            return;
        }

        try {
            initialDeposit = Double.parseDouble(createInitialDepositField.getText().trim());
            if (initialDeposit < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            logMessage("❌ Error: Invalid initial deposit amount. Must be a non-negative number.", true);
            return;
        }

        String newAccNum = generateUniqueAccountNumber();
        Account newAccount = new Account(newAccNum, holder, initialDeposit);
        accounts.put(newAccNum, newAccount);

        logMessage("✅ Account created! Number: " + newAccNum + ", Holder: " + holder + ", Balance: $" + String.format("%.2f", initialDeposit), false);
        
        createHolderField.setText("");
        createInitialDepositField.setText("");
    }

    private String generateUniqueAccountNumber() {
        Random rand = new Random();
        String num;
        do {
            num = String.format("%04d", rand.nextInt(10000));
        } while (accounts.containsKey(num));
        return num;
    }

    // --- Tab 2: Transactions (Deposit/Withdrawal) ---

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 💡 ENHANCEMENT: Use fixed width for inputs
        transAccNumField = new JTextField(15); 
        transAmountField = new JTextField(15);
        
        JButton depositButton = createStyledButton("Deposit", e -> handleTransaction("DEPOSIT"));
        JButton withdrawButton = createStyledButton("Withdraw", e -> handleTransaction("WITHDRAW"));
        withdrawButton.setBackground(ERROR_COLOR); 

        // 💡 ENHANCEMENT 3: Initialize the feedback label
        transactionStatusLabel = new JLabel("Enter details and select transaction type.", SwingConstants.CENTER);
        transactionStatusLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        transactionStatusLabel.setForeground(Color.GRAY);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Account Number (e.g., 1001):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(transAccNumField, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Amount ($):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(transAmountField, gbc);

        // Buttons row
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = row; gbc.ipady = 10;
        panel.add(depositButton, gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(withdrawButton, gbc);
        
        // Status label row
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.ipady = 0;
        panel.add(transactionStatusLabel, gbc);

        return panel;
    }

    private void handleTransaction(String type) {
        String accNum = transAccNumField.getText().trim();
        double amount;
        
        Account account = accounts.get(accNum);
        if (account == null) {
            setTransactionStatus("❌ Account number " + accNum + " not found.", true);
            logMessage("❌ Error: Account number " + accNum + " not found.", true);
            return;
        }

        try {
            amount = Double.parseDouble(transAmountField.getText().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            setTransactionStatus("❌ Invalid amount. Please enter a positive number.", true);
            logMessage("❌ Error: Invalid transaction amount.", true);
            return;
        }
        
        // Clear amount field after successful validation/retrieval
        transAmountField.setText(""); 

        if (type.equals("DEPOSIT")) {
            account.deposit(amount);
            String msg = "✅ Deposit of $" + String.format("%.2f", amount) + " successful. New Balance: $" + String.format("%.2f", account.getBalance());
            setTransactionStatus(msg, false);
            logMessage("✅ Deposit of $" + String.format("%.2f", amount) + " successful for " + accNum, false);
        } else if (type.equals("WITHDRAW")) {
            if (account.withdraw(amount)) {
                String msg = "✅ Withdrawal of $" + String.format("%.2f", amount) + " successful. New Balance: $" + String.format("%.2f", account.getBalance());
                setTransactionStatus(msg, false);
                logMessage("✅ Withdrawal of $" + String.format("%.2f", amount) + " successful for " + accNum, false);
            } else {
                setTransactionStatus("❌ Withdrawal failed: Insufficient funds ($" + String.format("%.2f", account.getBalance()) + ").", true);
                logMessage("❌ Withdrawal failed for " + accNum + ": Insufficient funds.", true);
            }
        }
        
        // Update the balance display in the Balance Check tab if it's currently open or visible
        updateBalanceDisplay(accNum); 
    }
    
    /** 💡 ENHANCEMENT: Setter for the immediate status label. */
    private void setTransactionStatus(String message, boolean isError) {
        transactionStatusLabel.setText(message);
        transactionStatusLabel.setForeground(isError ? ERROR_COLOR : SUCCESS_COLOR);
    }

    // --- Tab 3: Balance Check ---

    private JPanel createBalancePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField checkAccNumField = new JTextField(15);
        
        // 💡 ENHANCEMENT: Clearer initial balance display message
        JLabel balanceDisplayLabel = new JLabel("Enter Account Number and Click Check.", SwingConstants.CENTER); 
        balanceDisplayLabel.setFont(HEADER_FONT.deriveFont(Font.BOLD, 22f));
        balanceDisplayLabel.setForeground(PRIMARY_COLOR.darker());

        JButton checkButton = createStyledButton("Check Balance", e -> {
            String accNum = checkAccNumField.getText().trim();
            updateBalanceDisplay(accNum, balanceDisplayLabel);
        });
        
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(checkAccNumField, gbc);

        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.ipady = 10;
        panel.add(checkButton, gbc);
        row++;
        
        gbc.insets = new Insets(30, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.ipady = 30;
        panel.add(balanceDisplayLabel, gbc);

        return panel;
    }
    
    // --- Utility Methods ---

    /** Updates the balance display label in the Balance Check tab. */
    private void updateBalanceDisplay(String accNum, JLabel label) {
        Account account = accounts.get(accNum);
        if (account != null) {
            label.setText("Balance: $" + String.format("%.2f", account.getBalance()));
            label.setForeground(SUCCESS_COLOR.darker());
        } else {
            label.setText("Account Not Found: " + accNum);
            label.setForeground(ERROR_COLOR);
        }
    }
    
    /** Placeholder to ensure the Balance Check tab shows the latest balance if open. */
    private void updateBalanceDisplay(String accNum) {
        // Find the Balance Check tab panel components
        try {
            Component balancePanel = tabbedPane.getComponentAt(2);
            if (balancePanel instanceof JPanel) {
                 Component[] components = ((JPanel) balancePanel).getComponents();
                 for (Component comp : components) {
                     if (comp instanceof JLabel) {
                         JLabel label = (JLabel) comp;
                         // Identify the large balance display label
                         if (label.getText().contains("Balance") || label.getText().contains("check")) {
                             updateBalanceDisplay(accNum, label);
                             break;
                         }
                     }
                 }
            }
        } catch (IndexOutOfBoundsException e) {
            // Handle case where tab components haven't fully initialized (shouldn't happen with invokeLater)
        }
    }
    
    /** Logs a message to the JTextArea. */
    private void logMessage(String message, boolean isError) {
        String status = isError ? "[ERROR] " : "[INFO] ";
        outputArea.append(status + message + "\n");
        // Scroll to the bottom
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    /** Creates a consistently styled button. */
    private JButton createStyledButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addActionListener(action);
        return button;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Fallback
        }
        SwingUtilities.invokeLater(BankingSystem::new);
    }
}