package uk.ac.ed.acp.cw2.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.micrometer.core.ipc.http.HttpSender.Response;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import uk.ac.ed.acp.cw2.dto.DeliveryPathResponse;
import uk.ac.ed.acp.cw2.dto.Drone;
import uk.ac.ed.acp.cw2.dto.GeoJsonFeatureCollection;
import uk.ac.ed.acp.cw2.dto.LngLat;
import uk.ac.ed.acp.cw2.dto.QueryAttribute;
import uk.ac.ed.acp.cw2.dto.MedDispatchRec;
import uk.ac.ed.acp.cw2.service.DroneService;
import uk.ac.ed.acp.cw2.service.GeoJsonConverter;
import uk.ac.ed.acp.cw2.service.GeometryService;

import java.util.stream.Collectors;
import uk.ac.ed.acp.cw2.dto.DeliveryPathResponse;
import uk.ac.ed.acp.cw2.service.DeliveryService;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class DroneController {
  private final DroneService droneService;
  private final DeliveryService deliveryService;
  private final GeometryService geometryService;
  private final GeoJsonConverter geoJsonConverter;

@Autowired
public DroneController(DroneService droneService, DeliveryService deliveryService, GeometryService geometryService, GeoJsonConverter geoJsonConverter) {
    this.droneService = droneService;
    this.deliveryService = deliveryService;
    this.geometryService = geometryService;
    this.geoJsonConverter = geoJsonConverter;
}

  @GetMapping("/dronesWithCooling/{state}")
  public ResponseEntity<List<Integer>> dronesWithCooling(@PathVariable boolean state) {
    try{
      List<Integer> droneIds = droneService.getDronesWithCooling(state);
      return ResponseEntity.ok(droneIds);
    } catch (Exception e){
      System.err.println("Error fetching drones with cooling state " + state + ": " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.ok(List.of());
    }
  }
  @GetMapping("/droneDetails/{id}")
public ResponseEntity<Drone> droneDetails(@PathVariable int id) {
    try {
        System.out.println("Fetching drone with ID: " + id);
        
        
        Drone drone = droneService.getDroneById(id);
        
        System.out.println("Result: " + (drone != null ? "Found" : "Not Found"));
        
        
        if (drone == null) {
            System.out.println("Returning 404 - drone not found");
            return ResponseEntity.notFound().build();  
        }
        
        System.out.println("Returning 200 - drone found");
        return ResponseEntity.ok(drone);
        
    } catch (Exception e) {
        System.err.println("Exception occurred: " + e.getMessage());
        e.printStackTrace();
        
        return ResponseEntity.notFound().build();  
    }
}
  @GetMapping("/queryAsPath/{attributeName}/{attributeValue}")
  public ResponseEntity<List<Integer>> queryAsPath(@PathVariable String attributeName, @PathVariable String attributeValue){
    try{
      List<Integer> droneIds = droneService.queryByAttribute(attributeName, attributeValue);
      return ResponseEntity.ok(droneIds);
    } catch (Exception e){
      System.err.println("Error querying drones by attribute " + attributeName + ": " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.ok(List.of());
    }
  }
  @PostMapping("/query")
  public ResponseEntity<List<Integer>> query(@RequestBody List<QueryAttribute> queryAttributes){
    try{
      if(queryAttributes == null || queryAttributes.isEmpty()){
        System.out.println("POST /query with" + queryAttributes.size() + "conditions");
        List<Drone> allDrones = droneService.getAllDrones();
        List<Integer> allDroneIds = allDrones.stream()
          .map(Drone::getId)
          .collect(Collectors.toList());
        return ResponseEntity.ok(allDroneIds);
      }
      List<Integer> droneIds = droneService.queryByMultipleAttributes(queryAttributes);
      System.out.println("Found " + droneIds.size() + " drones matching query conditions");
      return ResponseEntity.ok(droneIds);
    } catch (Exception e){
      System.err.println("Error querying drones by multiple attributes: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.ok(List.of());
    }
  }
  @PostMapping("queryAvailableDrones")
  public ResponseEntity<List<Integer>> queryAvailableDrones(@RequestBody List<MedDispatchRec> dispatches){
    try{
      if(dispatches == null){
        System.out.println("POST /queryAvailableDrones with null dispatch record");
        return ResponseEntity.ok(List.of());
      }
       List<Integer> availableDroneIds = droneService.queryAvailableDrones(dispatches);
       System.out.println("Returning" + availableDroneIds.size() + " available drones for dispatch records");
       return ResponseEntity.ok(availableDroneIds);
    } catch (Exception e){
      System.err.println("Error querying available drones for dispatch records: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.ok(List.of());
    }
  }

  /**
 * POST /api/v1/calcDeliveryPath
 * 
 * Calculates optimal delivery paths for given dispatches.
 * Returns complete flight plan with costs and moves.
 * 
 * Request body: Array of MedDispatchRec
 * Response: DeliveryPathResponse with drone paths
 * 
 * @param dispatches List of delivery requests
 * @return 200 OK with delivery path response
 */

@PostMapping("/calcDeliveryPath")
public ResponseEntity<DeliveryPathResponse> calcDeliveryPath(@RequestBody List<MedDispatchRec> dispatches) {
    
    try {
        if (dispatches == null || dispatches.isEmpty()) {
            return ResponseEntity.ok(new DeliveryPathResponse(0.0, 0, new ArrayList<>()));
        }
        
        // Determine if we need complex logic
        boolean useAdvanced = shouldUseAdvancedLogic(dispatches);
        
        System.out.println("Using " + (useAdvanced ? "ADVANCED" : "SIMPLE") + " logic");
        
        // Calculate delivery path
        DeliveryPathResponse response = deliveryService.calculateDeliveryPath(
            dispatches, useAdvanced
        );
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.ok(new DeliveryPathResponse(0.0, 0, new ArrayList<>()));
    }
}

/**
 * Determines if advanced multi-drone logic is needed.
 */
private boolean shouldUseAdvancedLogic(List<MedDispatchRec> dispatches) {
    // Use advanced logic if:
    // - More than 3 deliveries (might need multiple drones)
    // - Deliveries spread far apart
    // - High capacity requirements
    
    if (dispatches.size() > 3) {
        return true;
    }
    
    // Check if deliveries are spread far apart
    if (dispatches.size() >= 2) {
        double maxDistance = 0;
        for (int i = 0; i < dispatches.size() - 1; i++) {
            for (int j = i + 1; j < dispatches.size(); j++) {
                LngLat loc1 = dispatches.get(i).getDelivery();
                LngLat loc2 = dispatches.get(j).getDelivery();
                
                if (loc1 != null && loc2 != null) {
                    double dist = geometryService.calculateDistance(loc1, loc2);
                    maxDistance = Math.max(maxDistance, dist);
                }
            }
        }
        
        // If deliveries are > 0.01 degrees apart (~1km), use advanced
        if (maxDistance > 0.01) {
            return true;
        }
    }
    
    return false;  // Use simple logic
}
@PostMapping("/calcDeliveryPathAsGeoJson")
public ResponseEntity<GeoJsonFeatureCollection> calcDeliveryPathAsGeoJson(
        @RequestBody List<MedDispatchRec> dispatches) {
    
    try {
        System.out.println("POST /calcDeliveryPathAsGeoJson with " + 
                          (dispatches != null ? dispatches.size() : 0) + " dispatches");
        
        // Validate input
        if (dispatches == null || dispatches.isEmpty()) {
            return ResponseEntity.ok(new GeoJsonFeatureCollection(new ArrayList<>()));
        }
        
        // Calculate delivery path (reuse existing logic)
        boolean useAdvanced = shouldUseAdvancedLogic(dispatches);
        DeliveryPathResponse pathResponse = deliveryService.calculateDeliveryPath(
            dispatches, useAdvanced
        );
        
        // Convert to GeoJSON
        GeoJsonFeatureCollection geoJson = geoJsonConverter.convertToGeoJson(pathResponse);
        
        System.out.println("Returning GeoJSON with " + 
                          geoJson.getFeatures().size() + " features");
        
        // Always return 200 OK
        return ResponseEntity.ok(geoJson);
        
    } catch (Exception e) {
        System.err.println("Error in calcDeliveryPathAsGeoJson: " + e.getMessage());
        e.printStackTrace();
        
        // Return empty GeoJSON with 200
        return ResponseEntity.ok(new GeoJsonFeatureCollection(new ArrayList<>()));
    }
}

  

  
}
