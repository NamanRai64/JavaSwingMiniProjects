/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package librarymanagementsystem;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class LibraryManagementSystem extends JFrame {
    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private JTable bookTable, memberTable, transactionTable;
    private DefaultTableModel bookModel, memberModel, transactionModel;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Modern color scheme
    private final Color PRIMARY = new Color(99, 102, 241);
    private final Color SECONDARY = new Color(236, 72, 153);
    private final Color BACKGROUND = new Color(249, 250, 251);
    private final Color CARD_BG = Color.WHITE;
    private final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private final Color TEXT_SECONDARY = new Color(107, 114, 128);
    
    public LibraryManagementSystem() {
        setTitle("Library Management System");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeSampleData();
        createUI();
        
        setVisible(true);
    }
    
    private void initializeSampleData() {
        books.add(new Book("B001", "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", true));
        books.add(new Book("B002", "To Kill a Mockingbird", "Harper Lee", "Fiction", true));
        books.add(new Book("B003", "1984", "George Orwell", "Dystopian", false));
        books.add(new Book("B004", "Pride and Prejudice", "Jane Austen", "Romance", true));
        books.add(new Book("B005", "The Catcher in the Rye", "J.D. Salinger", "Fiction", true));
        
        members.add(new Member("M001", "Alice Johnson", "alice@email.com", "555-0101"));
        members.add(new Member("M002", "Bob Smith", "bob@email.com", "555-0102"));
        members.add(new Member("M003", "Carol White", "carol@email.com", "555-0103"));
    }
    
    private void createUI() {
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(0, 0));
        
        add(createSidebar(), BorderLayout.WEST);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(createBooksPanel(), "Books");
        mainPanel.add(createMembersPanel(), "Members");
        mainPanel.add(createTransactionsPanel(), "Transactions");
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(30, 41, 59));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel title = new JLabel("📚 Library System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 30, 0));
        sidebar.add(title);
        
        sidebar.add(createMenuButton("🏠 Dashboard", "Dashboard"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("📖 Books", "Books"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("👥 Members", "Members"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("🔄 Transactions", "Transactions"));
        
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private JButton createMenuButton(String text, String panel) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(30, 41, 59));
        btn.setBorder(new EmptyBorder(15, 20, 15, 20));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(230, 50));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(51, 65, 85));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(30, 41, 59));
            }
        });
        
        btn.addActionListener(e -> cardLayout.show(mainPanel, panel));
        
        return btn;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(BACKGROUND);
        
        panel.add(createStatCard("Total Books", String.valueOf(books.size()), "📚", PRIMARY));
        panel.add(createStatCard("Available Books", String.valueOf(books.stream().filter(b -> b.isAvailable()).count()), "✅", new Color(16, 185, 129)));
        panel.add(createStatCard("Total Members", String.valueOf(members.size()), "👥", SECONDARY));
        panel.add(createStatCard("Active Loans", String.valueOf(transactions.stream().filter(t -> t.getReturnDate() == null).count()), "🔄", new Color(245, 158, 11)));
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, String icon, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        iconLabel.setForeground(accentColor);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_SECONDARY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(TEXT_PRIMARY);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BACKGROUND);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        
        JLabel titleLabel = new JLabel("Book Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        header.add(titleLabel, BorderLayout.WEST);
        
        JButton addBtn = createStyledButton("+ Add Book", PRIMARY);
        addBtn.addActionListener(e -> showAddBookDialog());
        header.add(addBtn, BorderLayout.EAST);
        
        panel.add(header, BorderLayout.NORTH);
        
        String[] columns = {"Book ID", "Title", "Author", "Category", "Status"};
        bookModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookTable = createStyledTable(bookModel);
        updateBookTable();
        
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(new LineBorder(new Color(229, 231, 235), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BACKGROUND);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        
        JLabel titleLabel = new JLabel("Member Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        header.add(titleLabel, BorderLayout.WEST);
        
        JButton addBtn = createStyledButton("+ Add Member", SECONDARY);
        addBtn.addActionListener(e -> showAddMemberDialog());
        header.add(addBtn, BorderLayout.EAST);
        
        panel.add(header, BorderLayout.NORTH);
        
        String[] columns = {"Member ID", "Name", "Email", "Phone"};
        memberModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        memberTable = createStyledTable(memberModel);
        updateMemberTable();
        
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(new LineBorder(new Color(229, 231, 235), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BACKGROUND);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        
        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        header.add(titleLabel, BorderLayout.WEST);
        
        JButton issueBtn = createStyledButton("Issue Book", PRIMARY);
        issueBtn.addActionListener(e -> showIssueBookDialog());
        
        JButton returnBtn = createStyledButton("Return Book", new Color(16, 185, 129));
        returnBtn.addActionListener(e -> showReturnBookDialog());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BACKGROUND);
        btnPanel.add(issueBtn);
        btnPanel.add(returnBtn);
        header.add(btnPanel, BorderLayout.EAST);
        
        panel.add(header, BorderLayout.NORTH);
        
        String[] columns = {"Transaction ID", "Book", "Member", "Issue Date", "Return Date"};
        transactionModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        transactionTable = createStyledTable(transactionModel);
        updateTransactionTable();
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(new LineBorder(new Color(229, 231, 235), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(CARD_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(new MatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        
        return table;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }
    
    private void showAddBookDialog() {
        JDialog dialog = createDialog("Add New Book");
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField categoryField = new JTextField();
        
        panel.add(new JLabel("Book ID:"));
        panel.add(idField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        
        JButton saveBtn = createStyledButton("Save Book", PRIMARY);
        saveBtn.addActionListener(e -> {
            if (!idField.getText().isEmpty() && !titleField.getText().isEmpty()) {
                books.add(new Book(idField.getText(), titleField.getText(), 
                    authorField.getText(), categoryField.getText(), true));
                updateBookTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Book added successfully!");
            }
        });
        
        panel.add(new JLabel(""));
        panel.add(saveBtn);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showAddMemberDialog() {
        JDialog dialog = createDialog("Add New Member");
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        
        panel.add(new JLabel("Member ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        
        JButton saveBtn = createStyledButton("Save Member", SECONDARY);
        saveBtn.addActionListener(e -> {
            if (!idField.getText().isEmpty() && !nameField.getText().isEmpty()) {
                members.add(new Member(idField.getText(), nameField.getText(), 
                    emailField.getText(), phoneField.getText()));
                updateMemberTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Member added successfully!");
            }
        });
        
        panel.add(new JLabel(""));
        panel.add(saveBtn);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showIssueBookDialog() {
        JDialog dialog = createDialog("Issue Book");
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JComboBox<String> bookCombo = new JComboBox<>();
        for (Book book : books) {
            if (book.isAvailable()) {
                bookCombo.addItem(book.getId() + " - " + book.getTitle());
            }
        }
        
        JComboBox<String> memberCombo = new JComboBox<>();
        for (Member member : members) {
            memberCombo.addItem(member.getId() + " - " + member.getName());
        }
        
        panel.add(new JLabel("Select Book:"));
        panel.add(bookCombo);
        panel.add(new JLabel("Select Member:"));
        panel.add(memberCombo);
        
        JButton issueBtn = createStyledButton("Issue Book", PRIMARY);
        issueBtn.addActionListener(e -> {
            if (bookCombo.getSelectedItem() != null && memberCombo.getSelectedItem() != null) {
                String bookId = bookCombo.getSelectedItem().toString().split(" - ")[0];
                String memberId = memberCombo.getSelectedItem().toString().split(" - ")[0];
                
                Book book = books.stream().filter(b -> b.getId().equals(bookId)).findFirst().orElse(null);
                if (book != null) {
                    book.setAvailable(false);
                    transactions.add(new Transaction("T" + (transactions.size() + 1), bookId, memberId, new Date(), null));
                    updateBookTable();
                    updateTransactionTable();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Book issued successfully!");
                }
            }
        });
        
        panel.add(new JLabel(""));
        panel.add(issueBtn);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showReturnBookDialog() {
        JDialog dialog = createDialog("Return Book");
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JComboBox<String> transactionCombo = new JComboBox<>();
        for (Transaction t : transactions) {
            if (t.getReturnDate() == null) {
                Book book = books.stream().filter(b -> b.getId().equals(t.getBookId())).findFirst().orElse(null);
                if (book != null) {
                    transactionCombo.addItem(t.getId() + " - " + book.getTitle());
                }
            }
        }
        
        panel.add(new JLabel("Select Transaction:"));
        panel.add(transactionCombo);
        
        JButton returnBtn = createStyledButton("Return Book", new Color(16, 185, 129));
        returnBtn.addActionListener(e -> {
            if (transactionCombo.getSelectedItem() != null) {
                String transId = transactionCombo.getSelectedItem().toString().split(" - ")[0];
                Transaction trans = transactions.stream().filter(t -> t.getId().equals(transId)).findFirst().orElse(null);
                
                if (trans != null) {
                    trans.setReturnDate(new Date());
                    Book book = books.stream().filter(b -> b.getId().equals(trans.getBookId())).findFirst().orElse(null);
                    if (book != null) {
                        book.setAvailable(true);
                    }
                    updateBookTable();
                    updateTransactionTable();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Book returned successfully!");
                }
            }
        });
        
        panel.add(new JLabel(""));
        panel.add(returnBtn);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private JDialog createDialog(String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        return dialog;
    }
    
    private void updateBookTable() {
        bookModel.setRowCount(0);
        for (Book book : books) {
            bookModel.addRow(new Object[]{
                book.getId(), book.getTitle(), book.getAuthor(), 
                book.getCategory(), book.isAvailable() ? "Available" : "Issued"
            });
        }
    }
    
    private void updateMemberTable() {
        memberModel.setRowCount(0);
        for (Member member : members) {
            memberModel.addRow(new Object[]{
                member.getId(), member.getName(), member.getEmail(), member.getPhone()
            });
        }
    }
    
    private void updateTransactionTable() {
        transactionModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Transaction t : transactions) {
            Book book = books.stream().filter(b -> b.getId().equals(t.getBookId())).findFirst().orElse(null);
            Member member = members.stream().filter(m -> m.getId().equals(t.getMemberId())).findFirst().orElse(null);
            
            transactionModel.addRow(new Object[]{
                t.getId(),
                book != null ? book.getTitle() : t.getBookId(),
                member != null ? member.getName() : t.getMemberId(),
                sdf.format(t.getIssueDate()),
                t.getReturnDate() != null ? sdf.format(t.getReturnDate()) : "Not Returned"
            });
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LibraryManagementSystem();
        });
    }
}

class Book {
    private String id, title, author, category;
    private boolean available;
    
    public Book(String id, String title, String author, String category, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.available = available;
    }
    
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

class Member {
    private String id, name, email, phone;
    
    public Member(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}

class Transaction {
    private String id, bookId, memberId;
    private Date issueDate, returnDate;
    
    public Transaction(String id, String bookId, String memberId, Date issueDate, Date returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }
    
    public String getId() { return id; }
    public String getBookId() { return bookId; }
    public String getMemberId() { return memberId; }
    public Date getIssueDate() { return issueDate; }
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
}
