package uk.ac.ed.acp.cw2.dto;

import java.util.List;

/**
 * Represents drones available at a specific service point.
 * Maps service point to its available drones with schedules.
 */
public class ServicePointDrones {
    
    private int servicePointId;
    private List<DroneAvailability> drones;
    
    public ServicePointDrones() {
    }
    
    public ServicePointDrones(int servicePointId, List<DroneAvailability> drones) {
        this.servicePointId = servicePointId;
        this.drones = drones;
    }
    
    public int getServicePointId() {
        return servicePointId;
    }
    
    public void setServicePointId(int servicePointId) {
        this.servicePointId = servicePointId;
    }
    
    public List<DroneAvailability> getDrones() {
        return drones;
    }
    
    public void setDrones(List<DroneAvailability> drones) {
        this.drones = drones;
    }
    
    @Override
    public String toString() {
        return "ServicePointDrones{" +
                "servicePointId=" + servicePointId +
                ", drones=" + drones +
                '}';
    }
}
