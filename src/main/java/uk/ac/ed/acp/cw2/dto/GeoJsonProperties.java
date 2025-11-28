package uk.ac.ed.acp.cw2.dto;

/**
 * Properties for a GeoJSON Feature.
 * Contains metadata about the delivery path.
 */
public class GeoJsonProperties {
    
    private int droneId;
    private int deliveryId;
    
    public GeoJsonProperties() {
    }
    
    public GeoJsonProperties(int droneId, int deliveryId) {
        this.droneId = droneId;
        this.deliveryId = deliveryId;
    }
    
    public int getDroneId() {
        return droneId;
    }
    
    public void setDroneId(int droneId) {
        this.droneId = droneId;
    }
    
    public int getDeliveryId() {
        return deliveryId;
    }
    
    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }
}
