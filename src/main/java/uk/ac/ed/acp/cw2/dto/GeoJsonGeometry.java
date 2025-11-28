package uk.ac.ed.acp.cw2.dto;

import java.util.List;

/**
 * GeoJSON Geometry object.
 * Represents a LineString (path) in GeoJSON format.
 */
public class GeoJsonGeometry {
    
    private String type;  // Always "LineString"
    private List<double[]> coordinates;  // Array of [lng, lat] pairs
    
    public GeoJsonGeometry() {
        this.type = "LineString";
    }
    
    public GeoJsonGeometry(List<double[]> coordinates) {
        this.type = "LineString";
        this.coordinates = coordinates;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public List<double[]> getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(List<double[]> coordinates) {
        this.coordinates = coordinates;
    }
}
