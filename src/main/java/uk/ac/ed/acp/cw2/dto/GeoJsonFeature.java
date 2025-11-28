package uk.ac.ed.acp.cw2.dto;

/**
 * GeoJSON Feature object.
 * Represents a single delivery path with metadata.
 */
public class GeoJsonFeature {
    
    private String type;  // Always "Feature"
    private GeoJsonProperties properties;
    private GeoJsonGeometry geometry;
    
    public GeoJsonFeature() {
        this.type = "Feature";
    }
    
    public GeoJsonFeature(GeoJsonProperties properties, GeoJsonGeometry geometry) {
        this.type = "Feature";
        this.properties = properties;
        this.geometry = geometry;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public GeoJsonProperties getProperties() {
        return properties;
    }
    
    public void setProperties(GeoJsonProperties properties) {
        this.properties = properties;
    }
    
    public GeoJsonGeometry getGeometry() {
        return geometry;
    }
    
    public void setGeometry(GeoJsonGeometry geometry) {
        this.geometry = geometry;
    }
}
