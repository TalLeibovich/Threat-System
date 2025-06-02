import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;


public class MainGUI extends JFrame {

    // הגדרת המשתנים הקבועים
    private static final String CITIZENS_FILE = "citizens.txt";
    private static final String FACILITIES_FILE = "facilities.txt";
    private static final String MANAGER_FILE = "manager.txt";
    private static final String PRESIDENT_FILE = "president.txt";

    private final ThreatManagementSystem system;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final JTextArea outputArea = new JTextArea(20, 60);

    public MainGUI() {
        this.system = new ThreatManagementSystem(null);
        loadData(); // ✅ קודם נטען את הנתונים
        system.manageDetention(); // ✅ עכשיו אפשר להפעיל את זה, אחרי שהנתונים נטענו
        setupWindow();
        createPanels();
        startRatioThread();
        setVisible(true);
    }

    private void setupWindow() {
        setTitle("Threat Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });
    }

    private void createPanels() {
        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createManagerPanel(), "manager");
        mainPanel.add(createPresidentPanel(), "president");
        cardLayout.show(mainPanel, "login");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);
        gbc.gridy++;
        panel.add(passwordField, gbc);

        JButton managerLogin = new JButton("Manager Login");
        managerLogin.addActionListener(e -> handleManagerLogin(usernameField.getText(), new String(passwordField.getPassword())));

        JButton presidentLogin = new JButton("President Login");
        presidentLogin.addActionListener(e -> handlePresidentLogin(usernameField.getText(), new String(passwordField.getPassword())));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(managerLogin, gbc);
        gbc.gridy++;
        panel.add(presidentLogin, gbc);

        return panel;
    }

    private JPanel createManagerPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 5, 5)); // שיניתי ל-8 שורות כי הוספתי אופציה
        addButton(panel, "View All Citizens", e -> printCitizens());
        addButton(panel, "Add New Citizen", e -> addCitizenDialog());
        addButton(panel, "Update Support Level", e -> updateSupportLevelDialog());
        addButton(panel, "Update Public Impact", e -> updatePublicImpactDialog());
        addButton(panel, "Update Economic Percentile", e -> updateEconomicDialog());
        addButton(panel, "View Facilities", e -> printFacilities());
        addButton(panel, "View Detained Citizens", e -> viewDetainedCitizensDialog()); // הוספתי את האופציה החדשה
        addButton(panel, "Logout", e -> cardLayout.show(mainPanel, "login"));

        return panel;
    }

    private JPanel createPresidentPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 5, 5)); // שיניתי ל-7 שורות כי הוספתי אופציה
        addButton(panel, "View All Citizens", e -> printCitizens());
        addButton(panel, "Grant Pardon", e -> grantPardonDialog());
        addButton(panel, "Emergency Detention", e -> emergencyDetentionDialog());
        addButton(panel, "View Facilities", e -> printFacilities());
        addButton(panel, "View Detained Citizens", e -> viewDetainedCitizensDialog()); // הוספתי את האופציה החדשה
        addButton(panel, "Export History", e -> exportHistoryDialog());
        addButton(panel, "Logout", e -> cardLayout.show(mainPanel, "login"));

        return panel;
    }
    private void addButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        panel.add(button);
    }

    private void handleManagerLogin(String username, String password) {
        Manager manager = system.getManager();
        if (manager != null && manager.verifyCredentials(username, password)) {
            JOptionPane.showMessageDialog(this, "Manager login successful!");
            cardLayout.show(mainPanel, "manager");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }

    private void handlePresidentLogin(String username, String password) {
        President president = system.getPresident();
        String key = JOptionPane.showInputDialog("Enter special key:");
        if (president != null && president.verifyCredentials(username, password, key)) {
            JOptionPane.showMessageDialog(this, "President login successful!");
            cardLayout.show(mainPanel, "president");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }

    private void printCitizens() {
        outputArea.setText("");
        system.getCitizens().forEach(c -> outputArea.append(c.toString() + "\n"));
    }

    private void printFacilities() {
        outputArea.setText("🔍 Facilities List:\n");

        List<Facility> facilities = system.getFacilities();
        if (facilities.isEmpty()) {
            outputArea.append("❌ No facilities available.\n");
            return;
        }

        outputArea.append(String.format("%-10s | %-35s | %-10s | %-10s | %-10s\n", 
                "ID", "Name", "Capacity", "Occupied", "Occupancy %"));
        outputArea.append("----------------------------------------------------------------------------------\n");

        for (Facility facility : facilities) {
            double occupancyRate = (facility.getCurrentOccupancy() / (double) facility.getCapacity()) * 100;
            outputArea.append(String.format("%-10s | %-35s | %-10d | %-10d | %-9.2f%%\n",
                    facility.getFacilityId(),
                    facility.getName(),
                    facility.getCapacity(),
                    facility.getCurrentOccupancy(),
                    occupancyRate));
        }

        System.out.println("✅ Displayed " + facilities.size() + " facilities.");
    }

private void addCitizenDialog() {
    JPanel panel = new JPanel(new GridLayout(0, 2));
    JTextField nameField = new JTextField();
    JTextField birthField = new JTextField();
    JTextField originField = new JTextField();
    JTextField economicField = new JTextField();
    JTextField supportField = new JTextField();
    JTextField impactField = new JTextField();
    JTextField idField = new JTextField();

    panel.add(new JLabel("Name:"));
    panel.add(nameField);
    panel.add(new JLabel("Birthdate (YYYY-MM-DD):"));
    panel.add(birthField);
    panel.add(new JLabel("Origin (A/B/C):"));
    panel.add(originField);
    panel.add(new JLabel("Economic Percentile (1-10):"));
    panel.add(economicField);
    panel.add(new JLabel("Support Level (-1-10):"));
    panel.add(supportField);
    panel.add(new JLabel("Public Impact (1-10):"));
    panel.add(impactField);
    panel.add(new JLabel("ID:"));
    panel.add(idField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Citizen",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        try {
            Citizen citizen = new Citizen(
                    nameField.getText(),
                    LocalDate.parse(birthField.getText()),
                    originField.getText(),
                    Integer.parseInt(economicField.getText()),
                    Integer.parseInt(supportField.getText()),
                    Integer.parseInt(impactField.getText()),
                    false, // 👈 האזרח לא חבר בפרלמנט כברירת מחדל
                    idField.getText()
            );

            citizen.setInDetention(false); // 👈 ודא שכל אזרח חדש לא נכנס ישר לכליאה!

            system.addCitizen(citizen);
            system.manageDetention(); // 👈 הפעל שיבוץ מחדש אחרי הוספת אזרח

            outputArea.append("✅ Citizen added successfully: " + citizen.getName() + "\n");

        } catch (DateTimeParseException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }
}

    private void updateSupportLevelDialog() {
        String id = JOptionPane.showInputDialog("Enter citizen ID:");
        Citizen citizen = system.findCitizenById(id);
        if (citizen != null) {
            String input = JOptionPane.showInputDialog("Enter new support level (-1-10):");
            try {
                int level = Integer.parseInt(input);
                system.getManager().updateGovernmentSupportLevel(citizen, level);
                outputArea.append("Support level updated!\n");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Citizen not found!");
        }
    }

    private void updatePublicImpactDialog() {
        String id = JOptionPane.showInputDialog("Enter citizen ID:");
        Citizen citizen = system.findCitizenById(id);
        if (citizen != null) {
            String input = JOptionPane.showInputDialog("Enter new public impact score (1-10):");
            try {
                int score = Integer.parseInt(input);
                system.getManager().updatePublicImpactScore(citizen, score);
                outputArea.append("Public impact score updated!\n");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Citizen not found!");
        }
    }

    private void updateEconomicDialog() {
        String id = JOptionPane.showInputDialog("Enter citizen ID:");
        Citizen citizen = system.findCitizenById(id);
        if (citizen != null) {
            String input = JOptionPane.showInputDialog("Enter new economic percentile (1-10):");
            try {
                int percentile = Integer.parseInt(input);
                system.getManager().updateEconomicPercentile(citizen, percentile);
                outputArea.append("Economic percentile updated!\n");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Citizen not found!");
        }
    }

    private void grantPardonDialog() {
        String id = JOptionPane.showInputDialog("Enter citizen ID to pardon:");
        Citizen citizen = system.findCitizenById(id);
        if (citizen != null) {
            system.getPresident().grantPardon(citizen);
            outputArea.append("Citizen pardoned!\n");
        } else {
            JOptionPane.showMessageDialog(this, "Citizen not found!");
        }
    }

    private void emergencyDetentionDialog() {
        String id = JOptionPane.showInputDialog("Enter citizen ID for emergency detention:");
        Citizen citizen = system.findCitizenById(id);
        if (citizen != null) {
            system.getPresident().emergencyDetention(citizen);
            outputArea.append("Citizen detained!\n");
        } else {
            JOptionPane.showMessageDialog(this, "Citizen not found!");
        }
    }

    private void exportHistoryDialog() {
        String filename = JOptionPane.showInputDialog("Enter filename to export history:");
        if (filename != null && !filename.isEmpty()) {
            try {
                system.exportCitizenHistory(filename);
                outputArea.append("History exported successfully to " + filename + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting history: " + ex.getMessage());
            }
        }
    }

    private void saveData() {
    	DataHandler.saveCitizensToFile(new ArrayList<>(system.getCitizens()), CITIZENS_FILE);
        DataHandler.saveFacilitiesToFile(system.getFacilities(), FACILITIES_FILE);
        DataHandler.saveManagerToFile(system.getManager(), MANAGER_FILE);
        DataHandler.savePresidentToFile(system.getPresident(), PRESIDENT_FILE);
    }

    private void loadData() {
        system.setCitizens(DataHandler.loadCitizensFromFile(system, CITIZENS_FILE));
        system.setFacilities(DataHandler.loadFacilitiesFromFile(system, FACILITIES_FILE));
        system.setManager(DataHandler.loadManagerFromFile(system, MANAGER_FILE));
        system.setPresident(DataHandler.loadPresidentFromFile(system, PRESIDENT_FILE));

        // בדיקות דיאגנוסטיות
        System.out.println("Citizens loaded: " + system.getCitizens().size());
        System.out.println("Facilities loaded: " + system.getFacilities().size());
    }


    private void startRatioThread() {
        CitizenToFacilityRatioThread ratioThread = new CitizenToFacilityRatioThread(system);
        ratioThread.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI());
    }
    private void viewDetainedCitizensDialog() {
        outputArea.setText("Available Facilities:\n");
        List<Facility> facilities = system.getFacilities();
        
        if (facilities.isEmpty()) {
            outputArea.append("No facilities available.\n");
            return;
        }
        
        // Display facilities with numbered list
        for (int i = 0; i < facilities.size(); i++) {
            Facility facility = facilities.get(i);
            outputArea.append(String.format("%d. %s (Capacity: %d, Occupied: %d)\n", 
                    i + 1, facility.getName(), facility.getCapacity(), facility.getCurrentOccupancy()));
        }

        // Prompt user to choose a facility
        String input = JOptionPane.showInputDialog("Enter the number of the facility to view detained citizens:");
        try {
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice <= facilities.size()) {
                Facility selectedFacility = facilities.get(choice - 1);
                List<Citizen> detainedCitizens = selectedFacility.getDetainedCitizens();
                
                outputArea.append("\nDetained Citizens in " + selectedFacility.getName() + ":\n");
                if (detainedCitizens.isEmpty()) {
                    outputArea.append("No detained citizens in this facility.\n");
                } else {
                    for (Citizen citizen : detainedCitizens) {
                        outputArea.append(citizen.toString() + "\n");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid facility number!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
        }
    }

}