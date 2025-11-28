package uk.ac.ed.acp.cw2.dto;

import java.util.List;

/**
 * Response structure for calcDeliveryPath endpoint.
 * Contains total cost, total moves, and paths for all drones.
 */
public class DeliveryPathResponse {
    
    private double totalCost;
    private int totalMoves;
    private List<DronePath> dronePaths;
    
    public DeliveryPathResponse() {
    }
    
    public DeliveryPathResponse(double totalCost, int totalMoves, List<DronePath> dronePaths) {
        this.totalCost = totalCost;
        this.totalMoves = totalMoves;
        this.dronePaths = dronePaths;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    public int getTotalMoves() {
        return totalMoves;
    }
    
    public void setTotalMoves(int totalMoves) {
        this.totalMoves = totalMoves;
    }
    
    public List<DronePath> getDronePaths() {
        return dronePaths;
    }
    
    public void setDronePaths(List<DronePath> dronePaths) {
        this.dronePaths = dronePaths;
    }
    
    @Override
    public String toString() {
        return "DeliveryPathResponse{" +
                "totalCost=" + totalCost +
                ", totalMoves=" + totalMoves +
                ", dronePaths=" + (dronePaths != null ? dronePaths.size() : 0) +
                '}';
    }
}