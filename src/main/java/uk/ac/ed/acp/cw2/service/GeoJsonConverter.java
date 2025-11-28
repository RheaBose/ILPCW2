package uk.ac.ed.acp.cw2.service;

import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.dto.*;

import java.util.ArrayList;
import java.util.List;


@Service
public class GeoJsonConverter {
    
    /**
     * Converts a DeliveryPathResponse to GeoJSON FeatureCollection.
     * 
     * @param pathResponse The delivery path response
     * @return GeoJSON FeatureCollection with all paths
     */
    public GeoJsonFeatureCollection convertToGeoJson(DeliveryPathResponse pathResponse) {
        
        List<GeoJsonFeature> features = new ArrayList<>();
        
        if (pathResponse == null || pathResponse.getDronePaths() == null) {
            return new GeoJsonFeatureCollection(features);
        }
        
        
        for (DronePath dronePath : pathResponse.getDronePaths()) {
            int droneId = dronePath.getDroneId();
            
            if (dronePath.getDeliveries() == null) {
                continue;
            }
            
            
            for (Delivery delivery : dronePath.getDeliveries()) {
                
                
                GeoJsonProperties properties = new GeoJsonProperties(
                    droneId,
                    delivery.getDeliveryId()
                );
                
               
                List<double[]> coordinates = new ArrayList<>();
                
                if (delivery.getFlightPath() != null) {
                    for (LngLat position : delivery.getFlightPath()) {
                        // GeoJSON format: [longitude, latitude]
                        coordinates.add(new double[]{
                            position.getLng(),
                            position.getLat()
                        });
                    }
                }
                
                
                GeoJsonGeometry geometry = new GeoJsonGeometry(coordinates);
                
             
                GeoJsonFeature feature = new GeoJsonFeature(properties, geometry);
                features.add(feature);
            }
        }
        
        return new GeoJsonFeatureCollection(features);
    }
}
