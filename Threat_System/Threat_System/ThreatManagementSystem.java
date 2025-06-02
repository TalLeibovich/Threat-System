
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ThreatManagementSystem {
    private List<Citizen> citizens;
    private List<Facility> facilities;
    private Manager manager;
    private President president;
    private int citizenCount; // קאונטר לאזרחים
    private int totalCapacity;  // משתנה חדש לקיבולת כוללת
    public ThreatManagementSystem(Manager manager) {
        this.citizens = new ArrayList<>();
        this.facilities = new ArrayList<>();
        this.manager = manager;
        this.citizenCount = 0;
        this.totalCapacity = 0;
    }

    // Start periodic threat level updates

    // Refresh threat levels for all citizens
    public void refreshThreatLevels() {
        for (Citizen citizen : citizens) {
            citizen.updateThreatLevel();
        }
    }

    public double getStaticThreshold() {
        return 500.0; // Static threat level threshold example
    }

    // Assign a citizen to the best available facility
    public void assignCitizenToFacility(Citizen citizen) {
        for (Facility facility : facilities) {
            if (facility.addCitizen(citizen)) {
                System.out.println("Citizen " + citizen.getName() + " assigned to facility " + facility.getName());
                return;
            }
        }
        System.out.println("No available space for citizen " + citizen.getName());
    }

    // Export the history of all citizens to a file
    public void exportCitizenHistory(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write("Citizen History Export - " + LocalDateTime.now() + "\n");
            for (Citizen citizen : citizens) {
                writer.write("Citizen Name: " + citizen.getName() + ", Citizen ID: " + citizen.getId() + ", Threat Level: " + citizen.getThreatLevel() + ", In Detention: " + citizen.isInDetention() + "\n");
            }
        }
    }

    // Add a new citizen to the system
    public void addCitizen(Citizen citizen) {
        citizens.add(citizen);
        refreshThreatLevels();
        manageDetention(); // ביצוע איוס מחדש של הכלואים
        citizenCount++; // עדכון קאונטר של אזרחים
        System.out.println("Citizen " + citizen.getName() + " (ID: " + citizen.getId() + ") added to the system.");
        DataHandler.saveCitizensToFile(citizens, "citizen.txt");
    }



   public void removeCitizen(Citizen citizen) {
    if (citizens.remove(citizen)) {
        citizen.setInDetention(false);
        System.out.println("Citizen " + citizen.getName() + " removed from the system.");
        manageDetention(); // Reassess detention after removal
        citizenCount--; // עדכון קאונטר של אזרחים
        DataHandler.saveCitizensToFile(citizens, "citizen.txt");
    } else {
        System.out.println("Citizen " + citizen.getName() + " does not exist in the system.");
    }
}
   public void addFacility(Facility facility) {
	    facilities.add(facility);
	    manageDetention(); // עדכון הכלואים מחדש
	    totalCapacity = getTotalCapacity(); // חישוב מחדש של הקיבולת במקום להסתמך על משתנה
	    System.out.println("Facility " + facility.getName() + " added. New total capacity: " + totalCapacity);
	    DataHandler.saveFacilitiesToFile(facilities, "facilities.txt");
	}

   
   public void removeFacility(Facility facility) {
	    if (!facilities.remove(facility)) {
	        System.out.println("Facility " + facility.getName() + " does not exist in the system.");
	        DataHandler.saveFacilitiesToFile(facilities, "facilities.txt");
	        return;
	        
	    }

	    System.out.println("Facility " + facility.getName() + " removed.");

	    // שמירה מחדש לאחר מחיקת מתקן
	    DataHandler.saveFacilitiesToFile(facilities, "facilities.txt");

	    // שחרור אזרחים שנכלאו במתקן
	    List<Citizen> detainedCitizens = facility.getDetainedCitizens();
	    for (Citizen citizen : detainedCitizens) {
	        citizen.setInDetention(false);
	        assignCitizenToFacility(citizen); 
	    }

	    totalCapacity = getTotalCapacity(); // חישוב מחדש של הקיבולת
	    System.out.println("New total facility capacity: " + totalCapacity);
	    manageDetention();
	}



   
    // Print all citizens in descending order of threat level
    public void printAllCitizensInThreatLevelOrder() {
        citizens.stream()
                .sorted(Comparator.comparingDouble(Citizen::getThreatLevel).reversed())
                .forEach(citizen -> System.out.println("Name: " + citizen.getName() + ", ID: " + citizen.getId() + ", Threat Level: " + citizen.getThreatLevel()));
    }

 // Print all facilities with detailed information
    public void printAllFacilities() {
        facilities.forEach(facility -> {
            double occupancyRate = ((double) facility.getCurrentOccupancy() / facility.getCapacity()) * 100;
            System.out.printf(
                "Facility Name: %s (ID: %s) | Capacity: %d | Occupancy: %d/%d (%.2f%%)%n",
                facility.getName(),
                facility.getFacilityId(),
                facility.getCapacity(),
                facility.getCurrentOccupancy(),
                facility.getCapacity(),
                occupancyRate
            );
        });
    }


    // Get a citizen by name
    public Citizen getCitizenByName(String name) {
        for (Citizen citizen : citizens) {
            if (citizen.getName().equals(name)) {
                return citizen;
            }
        }
        return null;
    }

    // Get a facility by name
    public Facility getFacilityByName(String name) {
        for (Facility facility : facilities) {
            if (facility.getName().equals(name)) {
                return facility;
            }
        }
        return null;
    }

    // Search for a citizen by ID
    public Citizen findCitizenById(String id) {
        if (id == null || !id.matches("\\d+")) {
            System.out.println("\u05E9\u05D2\u05D9\u05D0\u05D4: \u05DE\u05D6\u05D4\u05D4 \u05D7\u05D9\u05D9\u05D1 \u05DC\u05D4\u05D9\u05D5\u05EA \u05DE\u05D5\u05E8\u05DB\u05D1 \u05DE\u05DE\u05E1\u05E4\u05E8\u05D9\u05DD \u05D1\u05DC\u05D1\u05D3. \u05D7\u05D6\u05D5\u05E8 \u05E9\u05E0\u05D9\u05EA.");
            return null;
        }
        return citizens.stream()
                .filter(citizen -> id.equals(citizen.getId()))
                .findFirst()
                .orElse(null);
    }

    // Getter and setters
    public List<Citizen> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<Citizen> citizens) {
        this.citizens = citizens;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
//////////////////////////// פונקציה החשובה בקוד
public void manageDetention() {
    System.out.println("🔄 Running manageDetention...");
    totalCapacity = getTotalCapacity(); // חישוב קיבולת כוללת

    List<Citizen> sortedCitizens = getSortedCitizensByThreat();
    double dynamicThreshold = areFacilitiesFull() ? getDynamicThresholdForAllFacilities() : getStaticThreshold();

    System.out.println("Total citizens in system: " + citizens.size());
    System.out.println("Total facilities in system: " + facilities.size());
    System.out.println("Dynamic threshold: " + dynamicThreshold);

    for (Citizen citizen : sortedCitizens) {
        System.out.println("🧐 Checking citizen: " + citizen.getName() + " | Threat Level: " + citizen.getThreatLevel());

        if (citizen.isInDetention()) {
            System.out.println("⚠ Citizen " + citizen.getName() + " is already in detention. Skipping.");
            continue;
        }

        if (citizen.getThreatLevel() < dynamicThreshold) {
            System.out.println("❌ Citizen " + citizen.getName() + " does not meet threat threshold.");
            break;
        }

        boolean assigned = tryAssignCitizenToFacility(citizen);
        if (assigned) {
            System.out.println("✅ Citizen " + citizen.getName() + " assigned successfully.");
        } else {
            System.out.println("❌ Citizen " + citizen.getName() + " could not be assigned.");
        }
    }
}

   public double getDynamicThresholdForAllFacilities() {
	    return facilities.stream()
	            .flatMap(facility -> facility.getDetainedCitizens().stream()) // שילוב כל הכלואים מכל המתקנים
	            .mapToDouble(Citizen::getThreatLevel) // המרת רמת האיום ל-double
	            .min() // מציאת הערך המינימלי
	            .orElse(getStaticThreshold()); // אם אין כלואים, חזור לרף הסטטי
	}

    private List<Citizen> getSortedCitizensByThreat() {
        return citizens.stream()
                .sorted(Comparator.comparingDouble(Citizen::getThreatLevel).reversed())
                .collect(Collectors.toList());
    }

 

    private boolean tryAssignCitizenToFacility(Citizen citizen) {
        // ניסיון להוסיף למתקן עם אחוז התפוסה הנמוך ביותר
        Facility facility = getFacilityWithLowestOccupancy();
        if (facility != null && facility.addCitizen(citizen)) {
            citizen.setInDetention(true);
            facility.refreshFacilityData();
            System.out.println("Citizen " + citizen.getName() + " assigned to facility " + facility.getName());
            return true;
        }

        // אם המתקנים מלאים, ניסיון להחליף אזרח עם רמת איום נמוכה יותר
        return replaceCitizenInFacility(citizen);
    }


    private boolean replaceCitizenInFacility(Citizen newCitizen) {
    for (Facility facility : facilities) {
        Citizen citizenToReplace = facility.getDetainedCitizens().stream()
                .min(Comparator.comparingDouble(Citizen::getThreatLevel))
                .orElse(null);

        if (citizenToReplace != null && newCitizen.getThreatLevel() > citizenToReplace.getThreatLevel()) {
            facility.removeCitizen(citizenToReplace); // הסרת האזרח הקיים
            citizenToReplace.setInDetention(false); // עדכון סטטוס מעצר

            boolean added = facility.addCitizen(newCitizen); // הוספת האזרח החדש
            if (added) {
                newCitizen.setInDetention(true); // עדכון סטטוס מעצר
                facility.refreshFacilityData(); // רענון נתוני המתקן
                System.out.println("Replaced citizen " + citizenToReplace.getName() +
                        " with citizen " + newCitizen.getName() + " in facility " + facility.getName());
                return true;
            }
        }
    }
    return false; // לא ניתן להחליף
}

    // Check if all facilities are full
    public boolean areFacilitiesFull() {
        return facilities.stream().allMatch(f -> f.getCurrentOccupancy() == f.getCapacity());
    }

    // Check if any facility is not full
    public boolean areFacilitiesNotFull() {
        return facilities.stream().anyMatch(f -> f.getCurrentOccupancy() < f.getCapacity());
    }

    // Get the dynamic threshold based on facility's current occupancy
    public double getDynamicThreshold(Facility facility) {
        return facility.getDetainedCitizens().stream()
                .min(Comparator.comparingDouble(Citizen::getThreatLevel))
                .map(Citizen::getThreatLevel)
                .orElse(0.0);
    }

    // Get facility with the lowest occupancy
    public Facility getFacilityWithLowestOccupancy() {
        return facilities.stream()
                .min(Comparator.comparingDouble(f -> (double) f.getCurrentOccupancy() / f.getCapacity()))
                .orElse(null);
    }

	public void setPresident(President president) {
		this.president = president;
	}
    
	public President getPresident() {
		return president;
	}

	public int getTotalCapacity() {
	    int total = facilities.stream().mapToInt(Facility::getCapacity).sum();
	    return total;
	}


	    // שיטה לקבלת מספר האזרחים
	    public int getCitizenCount() {
	        return citizenCount;
	    }
}
