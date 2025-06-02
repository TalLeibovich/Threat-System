import java.time.LocalDate;
import java.time.Period;

public class Citizen {
    private String name;
    private LocalDate birthDate;
    private String origin;
    private int economicPercentile; // Scale: 1-10
    private int governmentSupportLevel; // Scale: -1 to 10
    private int publicImpactScore; // Scale: 1-10
    private boolean isParliamentMember;
    private double threatLevel; // Scale: 1-1000
    private boolean isInDetention; // Whether the citizen is in detention
    private String id; // Unique identifier for the citizen

    public Citizen(String name, LocalDate birthDate, String origin, int economicPercentile, int governmentSupportLevel,
                   int publicImpactScore, boolean isParliamentMember, String id) {
        if (birthDate == null) {
            throw new IllegalArgumentException("שגיאה: תאריך לידה לא יכול להיות null.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("שגיאה: שם האזרח לא יכול להיות ריק.");
        }
        if (origin == null || !("A".equalsIgnoreCase(origin) || "B".equalsIgnoreCase(origin) || "C".equalsIgnoreCase(origin))) {
            throw new IllegalArgumentException("שגיאה: מוצא חייב להיות A, B או C בלבד.");
        }
        if (economicPercentile < 1 || economicPercentile > 10) {
            throw new IllegalArgumentException("שגיאה: אחוזון כלכלי חייב להיות בין 1 ל-10.");
        }
        if (governmentSupportLevel < -1 || governmentSupportLevel > 10) {
            throw new IllegalArgumentException("שגיאה: רמת תמיכה בשלטון חייבת להיות בין -1 ל-10.");
        }
        if (publicImpactScore < 1 || publicImpactScore > 10) {
            throw new IllegalArgumentException("שגיאה: מדד השפעה ציבורית חייב להיות בין 1 ל-10.");
        }
        if (id == null || !id.matches("\\d+")) {
            throw new IllegalArgumentException("שגיאה: מזהה חייב להיות מורכב מספרות בלבד.");
        }

        this.name = name;
        this.birthDate = birthDate;
        this.origin = origin;
        this.economicPercentile = economicPercentile;
        this.governmentSupportLevel = governmentSupportLevel;
        this.publicImpactScore = publicImpactScore;
        this.isParliamentMember = isParliamentMember;
        this.id = id;
        this.isInDetention = false;
        
        // Finally calculate the threat level
        this.threatLevel = calculateThreatLevel();
    }

    // Method to calculate threat level
    public double calculateThreatLevel() {
        double baseThreat =  (10-governmentSupportLevel) * 20 + (publicImpactScore *20);
        int age = calculateAge();
        if (age >= 20 && age <= 50) {
            baseThreat += 100; // More active age group increases threat
        }
        if ((age > 50 && age <= 75)||(age >= 15 && age <20)) {
            baseThreat += 50; // More active age group increases threat
        }
        if ("B".equalsIgnoreCase(origin)) {
            baseThreat += 40; // Moderate threat
        } else if ("C".equalsIgnoreCase(origin)) {
            baseThreat += 80; // High threat
        }
        baseThreat += economicPercentile * 20;

        if (isParliamentMember) {
            baseThreat *= 1.25; // Increase by 25% if they are a parliament member
        }
        
        return Math.min(1000, Math.max(1, baseThreat)); // Ensure it's within 1-1000
    }

    // Update threat level after changes
    public void updateThreatLevel() {
        this.threatLevel = calculateThreatLevel();
    }

    // Set whether the citizen is in detention
    public void setInDetention(boolean isInDetention) {
        this.isInDetention = isInDetention;
    }

    // Getter for detention status
    public boolean isInDetention() {
        return isInDetention;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("שגיאה: שם האזרח לא יכול להיות ריק. חזור שנית.");
            return;
        }
        this.name = name;
        updateThreatLevel();
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            System.out.println("שגיאה: תאריך לידה לא תקין. חזור שנית.");
            return;
        }
        this.birthDate = birthDate;
        updateThreatLevel();
    }

    public int calculateAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        if (origin == null || !("A".equalsIgnoreCase(origin) || "B".equalsIgnoreCase(origin) || "C".equalsIgnoreCase(origin))) {
            System.out.println("שגיאה: מוצא חייב להיות A, B או C בלבד. חזור שנית.");
            return;
        }
        this.origin = origin;
        updateThreatLevel();
    }

    public int getEconomicPercentile() {
        return economicPercentile;
    }

    public void setEconomicPercentile(int economicPercentile) {
        if (economicPercentile < 1 || economicPercentile > 10) {
            System.out.println("שגיאה: אחוזון כלכלי חייב להיות בין 1 ל-10. חזור שנית.");
            return;
        }
        this.economicPercentile = economicPercentile;
        updateThreatLevel();
    }

    public int getGovernmentSupportLevel() {
        return governmentSupportLevel;
    }

    public void setGovernmentSupportLevel(int governmentSupportLevel) {
        if (governmentSupportLevel < -1 || governmentSupportLevel > 10) {
            System.out.println("שגיאה: רמת תמיכה בשלטון חייבת להיות בין -1 ל-10. חזור שנית.");
            return;
        }
        this.governmentSupportLevel = governmentSupportLevel;
        updateThreatLevel();
    }

    public int getPublicImpactScore() {
        return publicImpactScore;
    }

    public void setPublicImpactScore(int publicImpactScore) {
        if (publicImpactScore < 1 || publicImpactScore > 10) {
            System.out.println("שגיאה: מדד השפעה ציבורית חייב להיות בין 1 ל-10. חזור שנית.");
            return;
        }
        this.publicImpactScore = publicImpactScore;
        updateThreatLevel();
    }

    public boolean isParliamentMember() {
        return isParliamentMember;
    }

    public void setParliamentMember(boolean parliamentMember) {
        isParliamentMember = parliamentMember;
        updateThreatLevel();
    }

    public double getThreatLevel() {
        return threatLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || !id.matches("\\d+")) {
            System.out.println("שגיאה: מזהה חייב להיות מורכב מספרות בלבד. חזור שנית.");
            return;
        }
        this.id = id;
    }

    @Override
    public String toString() {
        return "Citizen{" +
                "name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", age=" + calculateAge() +
                ", origin='" + origin + '\'' +
                ", economicPercentile=" + economicPercentile +
                ", governmentSupportLevel=" + governmentSupportLevel +
                ", publicImpactScore=" + publicImpactScore +
                ", isParliamentMember=" + isParliamentMember +
                ", threatLevel=" + threatLevel +
                ", isInDetention=" + isInDetention +
                ", id='" + id + '\'' +
                '}';
    }

    // Update threat level to the minimum value
    public void updateThreatLevelToMinimum() {
        this.threatLevel = 1.0;
    }

    // Set the threat level to the maximum value
    public void setThreatLevelToMax() {
        this.threatLevel = 1000.0;
    }

    // New method to set threat level manually
    public void setThreatLevelManually(double threatLevel) {
        if (threatLevel < 1 || threatLevel > 1000) {
            System.out.println("שגיאה: מדד האיום חייב להיות בין 1 ל-1000.");
            return;
        }
        this.threatLevel = threatLevel;
    }
 // "Report" methods for the manager to change economicPercentile, governmentSupportLevel, and publicImpactScore
    public void reportEconomicPercentile(int newEconomicPercentile) {
        if (newEconomicPercentile < 1 || newEconomicPercentile > 10) {
            System.out.println("שגיאה: אחוזון כלכלי חייב להיות בין 1 ל-10.");
            return;
        }
        this.economicPercentile = newEconomicPercentile;
        updateThreatLevel(); // Automatically update threat level
    }

    public void reportGovernmentSupportLevel(int newGovernmentSupportLevel) {
        if (newGovernmentSupportLevel < -1 || newGovernmentSupportLevel > 10) {
            System.out.println("שגיאה: רמת תמיכה בשלטון חייבת להיות בין -1 ל-10.");
            return;
        }
        this.governmentSupportLevel = newGovernmentSupportLevel;
        updateThreatLevel(); // Automatically update threat level
    }

    public void reportPublicImpactScore(int newPublicImpactScore) {
        if (newPublicImpactScore < 1 || newPublicImpactScore > 10) {
            System.out.println("שגיאה: מדד השפעה ציבורית חייב להיות בין 1 ל-10.");
            return;
        }
        this.publicImpactScore = newPublicImpactScore;
        updateThreatLevel(); // Automatically update threat level
    }

}
