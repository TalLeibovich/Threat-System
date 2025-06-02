import java.io.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class CitizenFacilityAssignment {
    private Citizen citizen;
    private Facility facility;
    private LocalDateTime assignedDate;
    private LocalDateTime releaseDate;

    public CitizenFacilityAssignment(Citizen citizen, Facility facility, LocalDateTime assignedDate) {
        this.citizen = citizen;
        this.facility = facility;
        this.assignedDate = assignedDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        if (releaseDate.isBefore(assignedDate)) {
            throw new IllegalArgumentException("Release date cannot be before assigned date.");
        }
        this.releaseDate = releaseDate;
    }

    public void exportAssignmentData(String fileName) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write("Citizen Name: " + citizen.getName() + ", Citizen ID: " + citizen.getId() +
                         ", Facility: " + facility.getName() + 
                         ", Assigned: " + assignedDate.format(formatter) + 
                         ", Released: " + (releaseDate != null ? releaseDate.format(formatter) : "Still Detained") + "\n");
        }
    }

    public long calculateDetentionDuration() {
        LocalDateTime endDate = (releaseDate != null) ? releaseDate : LocalDateTime.now();
        return Duration.between(assignedDate, endDate).toDays();
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public Facility getFacility() {
        return facility;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public String getCitizenName() {
        return citizen.getName();
    }

    public String getFacilityName() {
        return facility.getName();
    }

    @Override
    public String toString() {
        return "CitizenFacilityAssignment{" +
                "citizen=" + citizen +
                ", facility=" + facility +
                ", assignedDate=" + assignedDate +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
