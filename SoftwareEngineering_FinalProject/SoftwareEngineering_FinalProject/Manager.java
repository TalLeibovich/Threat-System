import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.io.IOException;

public class Manager {
    private String username;
    private byte[] hashedPassword; // Hashed password
    private ThreatManagementSystem threatManagementSystem;

    // Constructor
    public Manager(String username, String password, ThreatManagementSystem threatManagementSystem) {
        this.username = username;
        validatePassword(password);
        this.hashedPassword = hashPassword(password);
        this.threatManagementSystem = threatManagementSystem;
    }

    // Hash the password
    private byte[] hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error initializing password hashing", e);
        }
    }

    // Validate password complexity
    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        if (!password.matches(".*[0-9].*") || !password.matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("Password must contain both letters and numbers.");
        }
    }

    // Authenticate the manager
    public boolean verifyCredentials(String username, String password) {
        return this.username.equals(username) && Arrays.equals(this.hashedPassword, hashPassword(password));
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (verifyCredentials(this.username, oldPassword)) {
            validatePassword(newPassword);
            this.hashedPassword = hashPassword(newPassword);
            System.out.println("Password updated successfully.");

            // שמירת מנהל לאחר שינוי סיסמה
            DataHandler.saveManagerToFile(this, "manager.txt");

            return true;
        } else {
            System.out.println("Error: Old password is incorrect.");
            return false;
        }
    }

    // Add a citizen through the manager
    public void addCitizen(Citizen citizen) {
        if (threatManagementSystem.findCitizenById(citizen.getId()) != null) {
            System.out.println("Error: Citizen with ID " + citizen.getId() + " already exists in the system.");
            return;
        }
        threatManagementSystem.addCitizen(citizen);
        System.out.println("Citizen " + citizen.getName() + " added by manager.");
    }

    // Remove a citizen through the manager
    public void removeCitizen(Citizen citizen) {
        threatManagementSystem.removeCitizen(citizen);
        System.out.println("Citizen " + citizen.getName() + " removed by manager.");
    }

    // Add a facility through the manager
    public void addFacility(Facility facility) {
        threatManagementSystem.addFacility(facility);
        System.out.println("Facility " + facility.getName() + " added by manager.");
    }

    // Print all citizens in descending order of threat level
    public void printAllCitizens() {
        System.out.println("Citizens by threat level:");
        threatManagementSystem.printAllCitizensInThreatLevelOrder();
    }

    // Print all facilities
    public void printAllFacilities() {
        System.out.println("Facilities:");
        threatManagementSystem.printAllFacilities();
    }

    // Export citizen history
    public void exportCitizenHistory(String fileName) {
        try {
            threatManagementSystem.exportCitizenHistory(fileName);
            System.out.println("Citizen history exported to " + fileName);
        } catch (IOException e) {
            System.out.println("Error exporting citizen history: " + e.getMessage());
        }
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ThreatManagementSystem getThreatManagementSystem() {
        return threatManagementSystem;
    }

    public void setThreatManagementSystem(ThreatManagementSystem threatManagementSystem) {
        this.threatManagementSystem = threatManagementSystem;
    }
    public void updatePublicImpactScore(Citizen citizen, int newPublicImpactScore) {
        if (newPublicImpactScore < 1 || newPublicImpactScore > 10) {
            System.out.println("Error: Public impact score must be between 1 and 10.");
            return;
        }
        citizen.setPublicImpactScore(newPublicImpactScore);
        citizen.updateThreatLevel(); // עדכון מדד האיום לאחר שינוי ההשפעה הציבורית
        System.out.println("Public impact score for Citizen " + citizen.getName() + " (ID: " + citizen.getId() + ") updated to: " + newPublicImpactScore);
        threatManagementSystem.manageDetention(); // עדכון מערכת הכליאה
    }
    public void updateEconomicPercentile(Citizen citizen, int newEconomicPercentile) {
        if (newEconomicPercentile < 1 || newEconomicPercentile > 10) {
            System.out.println("Error: Economic percentile must be between 1 and 10.");
            return;
        }
        citizen.setEconomicPercentile(newEconomicPercentile);
        citizen.updateThreatLevel(); // עדכון מדד האיום לאחר שינוי האחוזון הכלכלי
        System.out.println("Economic percentile for Citizen " + citizen.getName() + " (ID: " + citizen.getId() + ") updated to: " + newEconomicPercentile);
        threatManagementSystem.manageDetention(); // עדכון מערכת הכליאה
    }
    public void updateGovernmentSupportLevel(Citizen citizen, int newSupportLevel) {
        if (newSupportLevel < -1 || newSupportLevel > 10) {
            System.out.println("Error: Government support level must be between -1 and 10.");
            return;
        }

        citizen.setGovernmentSupportLevel(newSupportLevel); // עדכון רמת התמיכה בממשלה
        citizen.updateThreatLevel(); // עדכון מדד האיום לאחר שינוי התמיכה בממשלה
        System.out.println("Government support level for Citizen " + citizen.getName() + " (ID: " + citizen.getId() + ") updated to: " + newSupportLevel);

        // הפעלת manageDetention לעדכון מצב הכלואים
        if (threatManagementSystem != null) {
            threatManagementSystem.manageDetention();
        }
    }
 // Method to report economicPercentile change for a Citizen
    public void reportEconomicPercentile(Citizen citizen, int newEconomicPercentile) {
        citizen.reportEconomicPercentile(newEconomicPercentile);
    }

    // Method to report governmentSupportLevel change for a Citizen
    public void reportGovernmentSupportLevel(Citizen citizen, int newGovernmentSupportLevel) {
        citizen.reportGovernmentSupportLevel(newGovernmentSupportLevel);
    }

    // Method to report publicImpactScore change for a Citizen
    public void reportPublicImpactScore(Citizen citizen, int newPublicImpactScore) {
        citizen.reportPublicImpactScore(newPublicImpactScore);
    }

    public byte[] getHashedPassword() {
        return this.hashedPassword;
    }
}
