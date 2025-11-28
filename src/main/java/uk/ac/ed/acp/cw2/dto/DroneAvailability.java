package uk.ac.ed.acp.cw2.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a drone's availability schedule.
 * Contains drone ID and list of availability time slots.
 */
public class DroneAvailability {
    
    private String id;  // Drone ID (as string in API)
    private List<Availability> availability;
    
    public DroneAvailability() {
    }
    
    public DroneAvailability(String id, List<Availability> availability) {
        this.id = id;
        this.availability = availability;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public List<Availability> getAvailability() {
        return availability;
    }
    
    public void setAvailability(List<Availability> availability) {
        this.availability = availability;
    }
    
    /**
     * Checks if this drone is available on a specific date and time.
     * @param date Date string "YYYY-MM-DD"
     * @param time Time string "HH:mm" or "HH:mm:ss" (can be null)
     * @return true if drone is available
     */
    public boolean isAvailableOn(String date, String time) {
        if (availability == null || availability.isEmpty()) {
            return false;  // No availability info = not available
        }
        
        try {
            // Parse date to get day of week
            LocalDate localDate = LocalDate.parse(date);
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            String dayName = dayOfWeek.toString();  // "MONDAY", "TUESDAY", etc.
            
            // If no time specified, just check if available any time that day
            if (time == null || time.isEmpty()) {
                return availability.stream()
                        .anyMatch(slot -> slot.getDayOfWeek().equalsIgnoreCase(dayName));
            }
            
            // Check if any availability slot covers this day and time
            return availability.stream()
                    .anyMatch(slot -> slot.coversDateTime(dayName, time));
            
        } catch (Exception e) {
            System.err.println("Error checking availability for date " + date + ": " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "DroneAvailability{" +
                "id='" + id + '\'' +
                ", availability=" + availability +
                '}';
    }
}
