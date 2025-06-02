public class CitizenToFacilityRatioThread extends Thread {
    private final ThreatManagementSystem system;
    private final int ratioThreshold = 10; // לכל 10 אזרחים, מתקן אחד

    public CitizenToFacilityRatioThread(ThreatManagementSystem system) {
        this.system = system;
    }

    @Override
    public void run() {
        try {
            // המתנה של 10 שניות לפני תחילת הריצה
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("ההמתנה לפני תחילת הריצה נקטעה.");
            return;
        }

        while (true) {
            try {
                int totalCitizens = system.getCitizenCount();
                int totalFacilities = system.getTotalCapacity();

                // המתנה עד שיהיו מתקנים במערכת
                if (totalFacilities == 0) {
                    System.out.println("Waiting for facilities to load...");
                    Thread.sleep(5000); // לחכות 5 שניות ולבדוק שוב
                    continue;
                }

                // הדפסת מצב נוכחי לאבחון
                System.out.println("Citizen count: " + totalCitizens);
                System.out.println("Total facility capacity: " + totalFacilities);

                double ratio = (totalFacilities == 0) ? Double.MAX_VALUE : (double) totalCitizens / totalFacilities;

                if (ratio > ratioThreshold) {
                    int requiredFacilities = (int) Math.ceil((double) totalCitizens / ratioThreshold);
                    int deficit = requiredFacilities - totalFacilities;
                    System.out.println("Facilities needed: " + requiredFacilities + ", Deficit: " + deficit);
                }

                // המתנה של 3 דקות (180,000 מילישניות) לפני העדכון הבא
                Thread.sleep(180000);
            } catch (InterruptedException e) {
                System.out.println("התרד להערכת היחס נקטע.");
                break;
            }
        }
    }
}
