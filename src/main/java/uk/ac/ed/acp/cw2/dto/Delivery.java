package uk.ac.ed.acp.cw2.dto;

import java.util.List;

/**
 * Represents a single delivery within a drone's path.
 * Contains the delivery ID and the flight path to reach it.
 */
public class Delivery {
    
    private int deliveryId;           // MedDispatchRec ID
    private List<LngLat> flightPath;  // Positions from previous point to this delivery
    
    public Delivery() {
    }
    
    public Delivery(int deliveryId, List<LngLat> flightPath) {
        this.deliveryId = deliveryId;
        this.flightPath = flightPath;
    }
    
    public int getDeliveryId() {
        return deliveryId;
    }
    
    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }
    
    public List<LngLat> getFlightPath() {
        return flightPath;
    }
    
    public void setFlightPath(List<LngLat> flightPath) {
        this.flightPath = flightPath;
    }
    
    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryId=" + deliveryId +
                ", flightPath=" + (flightPath != null ? flightPath.size() + " positions" : "null") +
                '}';
    }
}