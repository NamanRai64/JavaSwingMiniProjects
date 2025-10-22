package airlinereservationsystem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Flight {
    private String flightNumber;
    private String departure;
    private String arrival;
    private int totalSeats;
    private int availableSeats;
    private double price;

    public Flight(String num, String dep, String arr, int seats, double price) {
        this.flightNumber = num;
        this.departure = dep;
        this.arrival = arr;
        this.totalSeats = seats;
        this.availableSeats = seats;
        this.price = price;
    }
    
    // --- Getters ---
    public String getFlightNumber() { return flightNumber; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public int getAvailableSeats() { return availableSeats; }
    public double getPrice() { return price; }

    // --- Core Logic ---
    public boolean bookSeats(int count) {
        if (availableSeats >= count) {
            availableSeats -= count;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return flightNumber; // Used for simple identification if needed
    }
}


public class AirlineReservationSystem implements ActionListener {

    // --- Styling Constants ---
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210); // Deep Blue
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Off-white/light gray
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80); // Green
    private static final Color WARNING_COLOR = new Color(239, 83, 80); // Red

    // --- Data Store & GUI Components ---
    private List<Flight> allFlights = new ArrayList<>();
    private JTable flightTable;
    private DefaultTableModel tableModel;
    
    private JFrame frame;
    private JTextField nameField, seatsField;
    private JTextField cardNumberField, expiryField, cvvField;
    private JButton bookButton, confirmPaymentButton;
    private JTextArea outputArea;
    
    // Panel to switch between Booking and Payment forms
    private JPanel dynamicFormPanel; 
    private CardLayout cardLayout = new CardLayout();
    
    // Variables to hold pending booking details
    private Flight pendingFlight;
    private String pendingName;
    private int pendingSeats;
    private double pendingCost;

    public AirlineReservationSystem() {
        initializeData();
        createAndShowGUI();
    }

    /**
     * Initializes dummy flight data. (Unchanged)
     */
    private void initializeData() {
        allFlights.add(new Flight("UA101", "NYC", "LAX", 150, 450.00));
        allFlights.add(new Flight("DL405", "ATL", "MIA", 100, 280.50));
        allFlights.add(new Flight("AA777", "CHI", "DAL", 80, 320.00));
        allFlights.add(new Flight("WN999", "SEA", "PHX", 200, 199.99));
    }

    /**
     * Sets up the main JFrame and components. (Modified to use new panel structure)
     */
    private void createAndShowGUI() {
        frame = new JFrame("Airline Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 700);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.setLayout(new BorderLayout());

        // --- 1. Header Panel (NORTH) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel headerLabel = new JLabel("FlyHigh Airlines Booking ✈️");
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        frame.add(headerPanel, BorderLayout.NORTH);

        // --- 2. Main Content Panel (CENTER) ---
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // --- LEFT: Flight Table ---
        contentPanel.add(createFlightPanel());

        // --- RIGHT: Booking/Payment Forms & Output ---
        contentPanel.add(createBookingPanel());

        frame.add(contentPanel, BorderLayout.CENTER);

        // --- 3. Finalization ---
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    // --- UI Creation Methods (Modified) ---

    private JPanel createFlightPanel() {
        // ... (Flight Table creation remains the same) ...
        String[] columnNames = {"Flight No.", "From", "To", "Seats", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class;
                if (columnIndex == 4) return Double.class;
                return String.class;
            }
        };

        flightTable = new JTable(tableModel);
        flightTable.setFont(LABEL_FONT);
        flightTable.setRowHeight(25);
        flightTable.setFillsViewportHeight(true);
        flightTable.getTableHeader().setFont(BUTTON_FONT.deriveFont(Font.PLAIN));
        flightTable.setSelectionBackground(PRIMARY_COLOR.darker());
        flightTable.setSelectionForeground(Color.WHITE);

        updateFlightTableData();

        JScrollPane tableScrollPane = new JScrollPane(flightTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true), 
            "Available Flights", 
            0, 0, 
            BUTTON_FONT.deriveFont(Font.BOLD), PRIMARY_COLOR
        ));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Creates the dynamic panel containing the Booking and Payment forms.
     */
    private JPanel createBookingPanel() {
        JPanel containerPanel = new JPanel(new BorderLayout(0, 15));
        
        // --- Dynamic Form Panel (Uses CardLayout) ---
        dynamicFormPanel = new JPanel(cardLayout);
        dynamicFormPanel.add(createBookingForm(), "BOOKING");
        dynamicFormPanel.add(createPaymentForm(), "PAYMENT");
        
        containerPanel.add(dynamicFormPanel, BorderLayout.NORTH);

        // --- Output Area (BOTTOM) ---
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setFont(LABEL_FONT);
        outputArea.setText("Welcome! Select a flight and enter details to book.\n");
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Transaction Log"));
        
        containerPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        // Start on the booking form
        cardLayout.show(dynamicFormPanel, "BOOKING");
        
        return containerPanel;
    }

    /**
     * Creates the initial Booking Input Form.
     */
    private JPanel createBookingForm() {
        JPanel inputForm = new JPanel(new GridBagLayout());
        inputForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "1. Passenger & Seats"));
        inputForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        nameField = new JTextField(20);
        seatsField = new JTextField(5);
        
        int row = 0;
        row = addField(inputForm, gbc, "Passenger Name:", nameField, row);
        row = addField(inputForm, gbc, "Number of Seats:", seatsField, row);
        
        bookButton = new JButton("Proceed to Payment");
        bookButton.setFont(BUTTON_FONT);
        bookButton.setBackground(PRIMARY_COLOR);
        bookButton.setForeground(Color.WHITE);
        bookButton.addActionListener(this);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.ipady = 10;
        inputForm.add(bookButton, gbc);
        
        return inputForm;
    }

    /**
     * Creates the Payment Input Form.
     */
    private JPanel createPaymentForm() {
        JPanel paymentForm = new JPanel(new GridBagLayout());
        paymentForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "2. Payment Details"));
        paymentForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        cardNumberField = new JTextField(16);
        expiryField = new JTextField(5);
        cvvField = new JTextField(3);
        
        int row = 0;
        row = addField(paymentForm, gbc, "Card Number:", cardNumberField, row);
        
        // Expiry and CVV on the same row
        JLabel expLabel = new JLabel("Expiry (MM/YY):");
        JLabel cvvLabel = new JLabel("CVV:");
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3; paymentForm.add(expLabel, gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.3; paymentForm.add(expiryField, gbc);
        gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.1; paymentForm.add(cvvLabel, gbc);
        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.3; paymentForm.add(cvvField, gbc);
        row++;
        
        confirmPaymentButton = new JButton("Pay & Complete Booking");
        confirmPaymentButton.setFont(BUTTON_FONT);
        confirmPaymentButton.setBackground(SUCCESS_COLOR.darker());
        confirmPaymentButton.setForeground(Color.WHITE);
        confirmPaymentButton.addActionListener(this);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.ipady = 10;
        paymentForm.add(confirmPaymentButton, gbc);
        
        return paymentForm;
    }
    
    private int addField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT.deriveFont(Font.BOLD));
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3; gbc.gridwidth = 1; 
        panel.add(label, gbc);
        
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7; gbc.gridwidth = 1;
        panel.add(field, gbc);
        
        return row + 1;
    }


    private void updateFlightTableData() {
        // ... (Unchanged) ...
        tableModel.setRowCount(0);
        for (Flight f : allFlights) {
            tableModel.addRow(new Object[]{
                f.getFlightNumber(),
                f.getDeparture(),
                f.getArrival(),
                f.getAvailableSeats(),
                f.getPrice()
            });
        }
    }

    // --- Action Handling (Modified) ---

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bookButton) {
            handleInitialBookingRequest();
        } else if (e.getSource() == confirmPaymentButton) {
            handlePaymentConfirmation();
        }
    }

    /**
     * Step 1: Validates passenger input and transitions to the payment screen.
     */
    private void handleInitialBookingRequest() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a flight from the table.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Flight flight = allFlights.get(selectedRow);
        String name = nameField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter passenger name.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int seats;
        try {
            seats = Integer.parseInt(seatsField.getText().trim());
            if (seats <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number of seats (1 or more).", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (flight.getAvailableSeats() < seats) {
            String errMsg = String.format("❌ ERROR: Only %d seats available on %s.", flight.getAvailableSeats(), flight.getFlightNumber());
            outputArea.append("\n" + errMsg);
            JOptionPane.showMessageDialog(frame, errMsg, "Booking Failed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Store pending details
        pendingFlight = flight;
        pendingName = name;
        pendingSeats = seats;
        pendingCost = seats * flight.getPrice();
        
        // Update payment button text with cost
        confirmPaymentButton.setText(String.format("Pay $%.2f & Complete Booking", pendingCost));
        
        // Show Payment Form
        cardLayout.show(dynamicFormPanel, "PAYMENT");
        outputArea.append(String.format("\n➡️ Pending Booking: %s needs %d seats. Proceed to payment.", flight.getFlightNumber(), seats));
    }
    
    /**
     * Step 2: Validates payment details and finalizes the reservation.
     */
    private void handlePaymentConfirmation() {
        // Simple validation (not robust, but demonstrates the flow)
        String card = cardNumberField.getText().trim();
        String expiry = expiryField.getText().trim();
        String cvv = cvvField.getText().trim();
        
        if (card.length() < 16 || expiry.isEmpty() || cvv.length() < 3) {
            JOptionPane.showMessageDialog(frame, "Please enter valid payment details.", "Payment Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // --- Dummy Payment Processing ---
        
        // 1. Finalize the reservation (i.e., actually deduct the seats)
        if (pendingFlight.bookSeats(pendingSeats)) {
             // 2. Success Feedback
             String msg = String.format("✅ SUCCESS: %s booked %d seats on %s. Total: $%.2f paid via card ending in %s.",
                                            pendingName, pendingSeats, pendingFlight.getFlightNumber(), pendingCost, card.substring(card.length() - 4));
             outputArea.append("\n" + msg);
             JOptionPane.showMessageDialog(frame, msg + "\n\nReservation Complete!", "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
             
             // 3. Update UI and State
             int selectedRow = allFlights.indexOf(pendingFlight);
             if (selectedRow != -1) {
                 tableModel.setValueAt(pendingFlight.getAvailableSeats(), selectedRow, 3);
             }

        } else {
             // This should ideally not happen after the initial check, but included for robustness.
             String errMsg = "❌ FAILED: Seat availability changed. Please retry.";
             outputArea.append("\n" + errMsg);
             JOptionPane.showMessageDialog(frame, errMsg, "Booking Failed", JOptionPane.WARNING_MESSAGE);
        }

        // 4. Reset Forms and State
        nameField.setText("");
        seatsField.setText("");
        cardNumberField.setText("");
        expiryField.setText("");
        cvvField.setText("");
        pendingFlight = null;
        
        // Show Booking Form
        cardLayout.show(dynamicFormPanel, "BOOKING");
    }

    /**
     * Main method to run the application. (Unchanged)
     */
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fallback to default look and feel
        }
        SwingUtilities.invokeLater(AirlineReservationSystem::new);
    }
}