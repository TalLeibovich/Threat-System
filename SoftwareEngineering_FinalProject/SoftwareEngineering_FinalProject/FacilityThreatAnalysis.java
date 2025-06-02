import java.util.List;

class FacilityThreatAnalysis {
    private Facility facility;
    private double averageThreatLevel;
    private double occupancyRate;

    public FacilityThreatAnalysis(Facility facility) {
        this.facility = facility;
        calculateMetrics();
    }

    // Calculate metrics for the facility
    private void calculateMetrics() {
        this.averageThreatLevel = facility.getDetainedCitizens().stream()
                .mapToDouble(Citizen::getThreatLevel)
                .average()
                .orElse(0.0);
        this.occupancyRate = (double) facility.getCurrentOccupancy() / facility.getCapacity() * 100;
    }

    // Refresh metrics if facility data changes
    public void refreshMetrics() {
        calculateMetrics();
    }

    // Get average threat level
    public double getAverageThreatLevel() {
        return averageThreatLevel;
    }

    // Get occupancy rate
    public double getOccupancyRate() {
        return occupancyRate;
    }

    // Calculate overall occupancy rate for multiple facilities
    public static double calculateOverallOccupancyRate(List<Facility> facilities) {
        int totalCapacity = facilities.stream().mapToInt(Facility::getCapacity).sum();
        int totalOccupancy = facilities.stream().mapToInt(Facility::getCurrentOccupancy).sum();
        return totalCapacity == 0 ? 0.0 : ((double) totalOccupancy / totalCapacity) * 100;
    }

    // Get the ID of the facility with the lowest occupancy rate
    public static String getFacilityIdWithLowestOccupancyRate(List<FacilityThreatAnalysis> analyses) {
        return analyses.stream()
                .min((analysis1, analysis2) -> Double.compare(analysis1.getOccupancyRate(), analysis2.getOccupancyRate()))
                .map(analysis -> analysis.facility.getFacilityId())
                .orElseThrow(() -> new IllegalArgumentException("No facilities available for analysis."));
    }

    // Get a list of facilities with occupancy above a certain threshold
    public static List<Facility> getFacilitiesAboveOccupancyRate(List<Facility> facilities, double threshold) {
        return facilities.stream()
                .filter(facility -> (double) facility.getCurrentOccupancy() / facility.getCapacity() * 100 > threshold)
                .toList();
    }

    @Override
    public String toString() {
        return "FacilityThreatAnalysis{" +
                "facility=" + facility +
                ", averageThreatLevel=" + averageThreatLevel +
                ", occupancyRate=" + occupancyRate + '%' +
                '}';
    }
    public static Facility getFacilityWithLowestOccupancyRate(List<FacilityThreatAnalysis> analyses) {
        return analyses.stream()
                .min((analysis1, analysis2) -> Double.compare(analysis1.getOccupancyRate(), analysis2.getOccupancyRate()))
                .map(analysis -> analysis.facility)
                .orElseThrow(() -> new IllegalArgumentException("No facilities available for analysis."));
    }

}
