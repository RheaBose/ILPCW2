package uk.ac.ed.acp.cw2.dto;


import java.time.LocalTime;

/**
 * Represents a time slot when a drone is available.
 * Part of drone availability scheduling.
 */
public class Availability {
    
    private String dayOfWeek;  // e.g., "MONDAY", "TUESDAY"
    private String from;       // Start time: "00:00:00"
    private String until;      // End time: "23:59:59"
    
    public Availability() {
    }
    
    public Availability(String dayOfWeek, String from, String until) {
        this.dayOfWeek = dayOfWeek;
        this.from = from;
        this.until = until;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getUntil() {
        return until;
    }
    
    public void setUntil(String until) {
        this.until = until;
    }
    
    /**
     * Checks if this availability slot covers a specific day and time.
     * @param dayOfWeek Day to check (e.g., "MONDAY")
     * @param time Time to check (e.g., "14:30" or "14:30:00")
     * @return true if available
     */
    public boolean coversDateTime(String dayOfWeek, String time) {
        // Check day matches
        if (!this.dayOfWeek.equalsIgnoreCase(dayOfWeek)) {
            return false;
        }
        
        try {
            // Parse times
            LocalTime checkTime = parseTime(time);
            LocalTime fromTime = parseTime(this.from);
            LocalTime untilTime = parseTime(this.until);
            
            // Check if time is within range (inclusive)
            return !checkTime.isBefore(fromTime) && !checkTime.isAfter(untilTime);
            
        } catch (Exception e) {
            System.err.println("Error parsing times: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Parses time string to LocalTime.
     * Handles both "HH:mm" and "HH:mm:ss" formats.
     */
    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return LocalTime.MIDNIGHT;
        }
        
        // Add seconds if not present
        if (timeStr.length() == 5) {  // "HH:mm"
            timeStr = timeStr + ":00";
        }
        
        return LocalTime.parse(timeStr);
    }
    
    @Override
    public String toString() {
        return "Availability{" +
                "dayOfWeek='" + dayOfWeek + '\'' +
                ", from='" + from + '\'' +
                ", until='" + until + '\'' +
                '}';
    }
}