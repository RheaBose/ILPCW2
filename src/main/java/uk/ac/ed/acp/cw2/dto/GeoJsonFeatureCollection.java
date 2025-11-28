package uk.ac.ed.acp.cw2.dto;

import java.util.List;

/**
 * GeoJSON FeatureCollection - top level response.
 * Contains all delivery paths as features.
 */
public class GeoJsonFeatureCollection {
    
    private String type;  // Always "FeatureCollection"
    private List<GeoJsonFeature> features;
    
    public GeoJsonFeatureCollection() {
        this.type = "FeatureCollection";
    }
    
    public GeoJsonFeatureCollection(List<GeoJsonFeature> features) {
        this.type = "FeatureCollection";
        this.features = features;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public List<GeoJsonFeature> getFeatures() {
        return features;
    }
    
    public void setFeatures(List<GeoJsonFeature> features) {
        this.features = features;
    }
}
