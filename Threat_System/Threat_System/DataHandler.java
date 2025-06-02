import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataHandler {

    private static final Logger logger = Logger.getLogger(DataHandler.class.getName());
    private static final String DEFAULT_DATA_FOLDER = "Data";
    private static final String DATA_FOLDER = System.getProperty("data.folder", DEFAULT_DATA_FOLDER);

    static {
        // Ensure the "Data" folder exists
        try {
            Files.createDirectories(Paths.get(DATA_FOLDER));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create data folder: " + DATA_FOLDER, e);
        }
    }

    public static void saveCitizensToFile(List<Citizen> citizens, String filename) {
        String fullPath = DATA_FOLDER + File.separator + filename;

        System.out.println("Saving citizens to: " + fullPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
            for (Citizen citizen : citizens) {
                String citizenData = String.format("%s,%s,%s,%d,%d,%d,%b,%s,%b",
                        citizen.getName(),
                        citizen.getBirthDate(),
                        citizen.getOrigin(),
                        citizen.getEconomicPercentile(),
                        citizen.getGovernmentSupportLevel(),
                        citizen.getPublicImpactScore(),
                        citizen.isParliamentMember(),
                        citizen.getId(),
                        citizen.isInDetention());

                writer.write(citizenData);
                writer.newLine();
                System.out.println("✅ Citizen saved: " + citizen.getName() + " (ID: " + citizen.getId() + ")");
            }
            System.out.println("✅ Citizens saved successfully: " + citizens.size());

        } catch (IOException e) {
            System.out.println("❌ Error saving citizens to " + fullPath);
            e.printStackTrace();
        }
    }

public static List<Citizen> loadCitizensFromFile(ThreatManagementSystem system, String filename) {
    String fullPath = DATA_FOLDER + File.separator + filename;
    List<Citizen> citizens = new ArrayList<>();

    System.out.println("Loading citizens from: " + fullPath);

    try (BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("Reading line: " + line);
            Citizen citizen = citizenFromFileString(line);
            if (citizen != null) {
                citizen.setInDetention(false); // 👈 אפס את הסטטוס של כל האזרחים!
                citizens.add(citizen);
                system.addCitizen(citizen);
                System.out.println("✅ Citizen added: " + citizen.getName() + " | Detention Reset: " + citizen.isInDetention());
            } else {
                System.out.println("⚠ Skipping invalid citizen entry: " + line);
            }
        }
        System.out.println("✅ Citizens loaded successfully: " + citizens.size());
    } catch (IOException e) {
        System.out.println("❌ Error loading citizens from " + fullPath);
        e.printStackTrace();
    }

    return citizens;
}


    public static void saveFacilitiesToFile(List<Facility> facilities, String filename) {
        String fullPath = DATA_FOLDER + File.separator + filename;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath, false))) { // false = כתיבה מחדש
            for (Facility facility : facilities) {
                writer.write(facilityToFileString(facility));
                writer.newLine();
            }
            logger.info("Facilities saved successfully to " + fullPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving facilities to " + fullPath, e);
        }
    }

    public static List<Facility> loadFacilitiesFromFile(ThreatManagementSystem system, String filename) {
        String fullPath = DATA_FOLDER + File.separator + filename;
        List<Facility> facilities = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Facility facility = facilityFromFileString(line, system);
                    facilities.add(facility);
                } catch (IllegalArgumentException e) {
                    logger.log(Level.WARNING, "Invalid facility data: " + line, e);
                }
            }
            logger.info("Facilities loaded successfully from " + fullPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading facilities from " + fullPath, e);
        }
        return facilities;
    }
    public static void saveCitizenHistoryToFile(String history, String filename) {
        String fullPath = DATA_FOLDER + File.separator + filename;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
            writer.write(history);
            logger.info("Citizen history saved successfully to " + fullPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving citizen history to " + fullPath, e);
        }
    }

    private static String citizenToFileString(Citizen citizen) {
        return String.format("%s,%s,%s,%d,%d,%d,%s,%s,%b", //%b for boolean
                citizen.getName(), citizen.getBirthDate(), citizen.getOrigin(),
                citizen.getEconomicPercentile(), citizen.getGovernmentSupportLevel(),
                citizen.getPublicImpactScore(), citizen.isInDetention(), citizen.getId(),
                citizen.calculateThreatLevel());
    }



    private static Citizen citizenFromFileString(String data) {
        try {
            System.out.println("Parsing citizen data: " + data);
            String[] parts = data.split(",");

            if (parts.length != 9) {  // בדיקה שהשורה מכילה בדיוק 9 חלקים
                System.out.println("❌ Invalid citizen data format: " + data);
                return null;
            }

            String name = parts[0].trim();
            LocalDate birthDate = LocalDate.parse(parts[1].trim());  // תאריך לידה
            String origin = parts[2].trim();
            int economicPercentile = Integer.parseInt(parts[3].trim());
            int governmentSupportLevel = Integer.parseInt(parts[4].trim());
            int publicImpactScore = Integer.parseInt(parts[5].trim());
            boolean isParliamentMember = Boolean.parseBoolean(parts[6].trim());
            String id = parts[7].trim();
            boolean isInDetention = Boolean.parseBoolean(parts[8].trim());

            if (!id.matches("\\d+")) { // בדיקה שה-ID מורכב ממספרים בלבד
                System.out.println("❌ Error: ID must be numeric! Invalid ID: " + id);
                return null;
            }

            Citizen citizen = new Citizen(name, birthDate, origin, economicPercentile, governmentSupportLevel,
                    publicImpactScore, isParliamentMember, id);
            
            citizen.setInDetention(isInDetention); // עדכון סטטוס מעצר
            System.out.println("✅ Citizen created: " + citizen.getName() + " (ID: " + id + ", Detained: " + isInDetention + ")");
            return citizen;

        } catch (Exception e) {
            System.out.println("❌ Error parsing citizen: " + data);
            e.printStackTrace();
            return null;
        }
    }



    private static String facilityToFileString(Facility facility) {
        // Existing code ... (modify to include detained citizens)
        List<Citizen> detainedCitizens = facility.getDetainedCitizens();
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s,%s,%d", facility.getFacilityId(), facility.getName(), facility.getCapacity()));
        builder.append(","); // separator
        for (Citizen citizen : detainedCitizens) {
            builder.append(citizen.getId()); // Assuming citizen ID is unique identifier
            builder.append(",");
        }
        return builder.toString();
    }

    private static Facility facilityFromFileString(String data, ThreatManagementSystem system) {
        String[] parts = data.split(",");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid facility data format: " + data);
        }

        Facility facility = new Facility(parts[0], parts[1], Integer.parseInt(parts[2]));

        // Extract detained citizen IDs
        if (parts.length > 3) {
            List<Citizen> detainedCitizens = new ArrayList<>();
            for (int i = 3; i < parts.length; i++) {
                String citizenId = parts[i];
                Citizen citizen = system.findCitizenById(citizenId); // Assuming system can find citizen by ID
                if (citizen != null) {
                    detainedCitizens.add(citizen);
                } else {
                    logger.warning("Citizen with ID " + citizenId + " not found while loading facility");
                }
            }
            facility.setDetainedCitizens(detainedCitizens);
        }

        return facility;
    }

    
    
    public static void saveManagerToFile(Manager manager, String filename) {
        String fullPath = DATA_FOLDER + File.separator + filename;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
             FileChannel channel = new RandomAccessFile(fullPath, "rw").getChannel();
             FileLock lock = channel.lock()) {
            writer.write(managerToFileString(manager));
            writer.newLine();
            logger.info("Manager saved successfully to " + fullPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving manager to " + fullPath, e);
        }
    }

    public static Manager loadManagerFromFile(ThreatManagementSystem system, String filename) {
        String fullPath = DATA_FOLDER + File.separator + filename;
        try(BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
            String line = reader.readLine();
            if (line != null) {
                Manager manager = managerFromFileString(line, system);
                logger.info("Manager loaded successfully: " + manager.getUsername());
                return manager;
            } else {
                throw new IllegalArgumentException("File is empty or contains no valid data");
            }
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid manager data format", e);
            throw e; // או טפל בשגיאה בהתאם לצורך
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading the manager file", e);
        }

        return null;
    }

    public static void savePresidentToFile(President president, String filename) {
        String fullPath = DATA_FOLDER + File.separator + filename;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
             FileChannel channel = new RandomAccessFile(fullPath, "rw").getChannel();
             FileLock lock = channel.lock()) {
            writer.write(presidentToFileString(president));
            writer.newLine();
            logger.info("President saved successfully to " + fullPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving president to " + fullPath, e);
        }
    }

    
    public static President loadPresidentFromFile(ThreatManagementSystem system, String filename) {
        String fullPath = DATA_FOLDER + File.separator + filename;
        try(BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
            String line = reader.readLine();
            if (line != null) {
            	President president = presidentFromFileString(line, system);
                logger.info("President loaded successfully: " + president.getUsername());
                return president;
            } else {
                throw new IllegalArgumentException("File is empty or contains no valid data");
            }
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid President data format", e);
            throw e; // או טפל בשגיאה בהתאם לצורך
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading the President file", e);
        }
        return null;
    }     

    private static String managerToFileString(Manager manager) {
        return String.format("%s,%s", manager.getUsername(), manager.getHashedPassword());
    }

    private static Manager managerFromFileString(String data, ThreatManagementSystem system) {
        String[] parts = data.split(",", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid manager data format: " + data);
        }
        return new Manager(parts[0], parts[1], system);
    }

    private static String presidentToFileString(President president) {
        return String.format("%s,%s,%s", president.getUsername(), president.getHashedPassword(), president.getKey());
    }

    private static President presidentFromFileString(String data, ThreatManagementSystem system) {
        String[] parts = data.split(",", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid president data format: " + data);
        }
        return new President(parts[0], parts[1], parts[2], system);
    }
}