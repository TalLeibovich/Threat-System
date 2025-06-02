import java.util.*;

public class Facility {
    private String facilityId;
    private String name;
    private int capacity;
    private List<Citizen> detainedCitizens;

    // Constructor
    public Facility(String facilityId, String name, int capacity) {
        setFacilityId(facilityId);
        setName(name);
        setCapacity(capacity);
        this.detainedCitizens = new ArrayList<>();
    }

    // Add a citizen to the facility
    public boolean addCitizen(Citizen citizen) {
        if (isFull()) {
            Citizen lowestThreatCitizen = getLowestThreatCitizen();
            if (lowestThreatCitizen != null && lowestThreatCitizen.getThreatLevel() < citizen.getThreatLevel()) {
                detainedCitizens.remove(lowestThreatCitizen);
                lowestThreatCitizen.setInDetention(false); // Release the citizen
                System.out.println("Citizen removed: " + lowestThreatCitizen.getName() + ", Threat Level: " + lowestThreatCitizen.getThreatLevel());
                System.out.println("Citizen added: " + citizen.getName() + ", Threat Level: " + citizen.getThreatLevel());
                detainedCitizens.add(citizen);
                citizen.setInDetention(true);
                return true;
            }
            return false; // No replacement occurred
        } else {
            detainedCitizens.add(citizen);
            citizen.setInDetention(true);
            return true;
        }
    }

    // Check if the facility has available space
    public boolean hasAvailableSpace() {
        return detainedCitizens.size() < capacity;
    }

    // Check if the facility is full
    public boolean isFull() {
        return detainedCitizens.size() >= capacity;
    }

    // Get the citizen with the lowest threat level
    public Citizen getLowestThreatCitizen() {
        return detainedCitizens.stream()
                .min(Comparator.comparingDouble(Citizen::getThreatLevel))
                .orElse(null);
    }

    // Print all detained citizens
    public void printAllDetainedCitizens() {
        for (Citizen citizen : detainedCitizens) {
            System.out.println(citizen);
        }
    }

    // Getters and setters
    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        if (facilityId == null || facilityId.trim().isEmpty()) {
            System.out.println("שגיאה: מזהה מתקן לא יכול להיות ריק. חזור שנית.");
            return;
        }
        this.facilityId = facilityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("שגיאה: שם מתקן לא יכול להיות ריק. חזור שנית.");
            return;
        }
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity < 0) {
            System.out.println("שגיאה: קיבולת מתקן חייבת להיות לפחות 0. חזור שנית.");
            return;
        }
        this.capacity = capacity;
    }

    public int getCurrentOccupancy() {
        return detainedCitizens.size();
    }

    public List<Citizen> getDetainedCitizens() {
        return new ArrayList<>(detainedCitizens); // Return a copy to prevent external modifications
    }

    public void setDetainedCitizens(List<Citizen> detainedCitizens) {
        if (detainedCitizens == null) {
            System.out.println("שגיאה: רשימת אזרחים לא יכולה להיות ריקה. חזור שנית.");
            return;
        }
        this.detainedCitizens = new ArrayList<>(detainedCitizens); // Ensure the list is modifiable
    }

    // Return all detained citizens sorted by their IDs
    public List<Citizen> getDetainedCitizensSortedById() {
        return detainedCitizens.stream()
                .sorted(Comparator.comparing(Citizen::getId))
                .toList();
    }

    public boolean removeCitizen(Citizen citizen) {
        boolean removed = detainedCitizens.remove(citizen); // Remove citizen from the list
        if (removed) {
            citizen.setInDetention(false); // Update the citizen's detention status
            System.out.println("Citizen " + citizen.getName() + " has been released from facility " + name + ".");
        } else {
            System.out.println("Citizen " + citizen.getName() + " was not found in facility " + name + ".");
        }
        return removed;
    }

    public void refreshFacilityData() {
        // Refresh the list by sorting citizens by ID
        detainedCitizens = new ArrayList<>(getDetainedCitizensSortedById());

        // Update current occupancy
        int currentOccupancy = getCurrentOccupancy();

        // Calculate the occupancy rate
        double occupancyRate = (double) currentOccupancy / capacity * 100;

        // Print updated data
        System.out.println("Facility data refreshed for facility: " + name);
        System.out.println("Current occupancy: " + currentOccupancy + "/" + capacity);
        System.out.printf("Occupancy rate: %.2f%%\n", occupancyRate);

        // Refresh citizens (e.g., update detention status if needed)
        for (Citizen citizen : detainedCitizens) {
            citizen.setInDetention(true); // Ensure all citizens are marked as detained
        }
    }
}
