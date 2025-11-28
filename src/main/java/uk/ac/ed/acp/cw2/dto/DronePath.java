package uk.ac.ed.acp.cw2.dto;

import java.util.List;

/**
 * Represents a single drone's complete delivery path.
 * Contains the drone ID and all deliveries it makes.
 */
public class DronePath {
    
    private int droneId;
    private List<Delivery> deliveries;
    
    public DronePath() {
    }
    
    public DronePath(int droneId, List<Delivery> deliveries) {
        this.droneId = droneId;
        this.deliveries = deliveries;
    }
    
    public int getDroneId() {
        return droneId;
    }
    
    public void setDroneId(int droneId) {
        this.droneId = droneId;
    }
    
    public List<Delivery> getDeliveries() {
        return deliveries;
    }
    
    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }
    
    @Override
    public String toString() {
        return "DronePath{" +
                "droneId=" + droneId +
                ", deliveries=" + (deliveries != null ? deliveries.size() : 0) +
                '}';
    }
}
