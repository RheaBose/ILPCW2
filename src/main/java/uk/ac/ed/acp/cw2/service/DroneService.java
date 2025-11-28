package uk.ac.ed.acp.cw2.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import uk.ac.ed.acp.cw2.dto.Drone;
import uk.ac.ed.acp.cw2.dto.QueryAttribute;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import uk.ac.ed.acp.cw2.dto.MedDispatchRec;
import uk.ac.ed.acp.cw2.dto.Requirements;
import uk.ac.ed.acp.cw2.dto.ServicePointDrones;
import uk.ac.ed.acp.cw2.dto.DroneAvailability;

@Service
public class DroneService {
  private final ILPRestClient ilpRestClient;

  @Autowired
  public DroneService(ILPRestClient ilpRestClient) {
    this.ilpRestClient = ilpRestClient;
  }

  public List<Drone> getAllDrones() {
    return ilpRestClient.getAllDrones();
  }

  public Drone getDroneById(int id) {
    return ilpRestClient.getDroneById(id);
  }

  public List<Integer> getDronesWithCooling(boolean hasCooling){
    List<Drone> allDrones = getAllDrones();
    return allDrones.stream()
      .filter(drone -> drone.hasCooling() == hasCooling)
      .map(Drone::getId)
      .collect(Collectors.toList());
  }
  public List<Integer> getDronesWithHeating(boolean hasHeating){
    List<Drone> allDrones = getAllDrones();
    return allDrones.stream()
      .filter(drone -> drone.hasHeating() == hasHeating)
      .map(Drone::getId)
      .collect(Collectors.toList());
  }
  public List<Integer> queryByAttribute(String attributeName, String attributeValue){
    System.out.println("Querying drones by attribute: " + attributeName + " = " + attributeValue);
    List<Drone> allDrones = getAllDrones();
    return allDrones.stream()
      .filter(drone -> matchesAttribute(drone, attributeName,"=", attributeValue))
      .map(Drone::getId)
      .collect(Collectors.toList());
  }
  public List<Integer> queryByMultipleAttributes(List<QueryAttribute> queryAttributes){
    System.out.println("Querying drones by multiple attributes: " + queryAttributes);
    for(QueryAttribute qa : queryAttributes){
      if(!qa.isValid()){
        throw new IllegalArgumentException("Invalid QueryAttribute: " + qa);
      }
    }
    List<Drone> allDrones = getAllDrones();
    return allDrones.stream()
      .filter(drone -> {
        for(QueryAttribute qa : queryAttributes){
          if(!matchesAttribute(drone, qa.getAttribute(), qa.getOperator(), qa.getValue().toString())){
            return false;
          }
        }
        return true;
      })
      .map(Drone::getId)
      .collect(Collectors.toList());
        
  }

  private boolean matchesAttribute(Drone drone, String attributeName, String operator, String value){
    try{
      Object droneValue = getAttributeValue(drone, attributeName);
      if(droneValue == null){
        System.err.println("Could not find attribute " + attributeName);
        return false;
      }
      return compareValues(droneValue, operator, value);
    } catch (Exception e){
      System.err.println("Error matching attribute " + attributeName + ": " + e.getMessage());
      return false;
    }
  }

  private Object getAttributeValue(Drone drone, String attributePath){
    try{
      String[] parts = attributePath.split("\\.");
      Object current = drone;

      for(String part : parts){
        if(current == null) return null;
      
        Field field = current.getClass().getDeclaredField(part);
        field.setAccessible(true);
        current = field.get(current);
      }
      return current;
    } catch (Exception e){
      System.err.println("Error getting attribute value for path " + attributePath + ": " + e.getMessage() );
      return null;
    } 
  }

  
private boolean compareValues(Object droneValue, String operator, String targetValue) {
    
    System.out.println("Comparing: " + droneValue + " (type: " + droneValue.getClass().getSimpleName() + 
                      ") " + operator + " '" + targetValue + "'");
    
    
    if (droneValue instanceof Boolean) {
        Boolean droneBoolean = (Boolean) droneValue;
        boolean targetBoolean = Boolean.parseBoolean(targetValue.toLowerCase());
        
        System.out.println("Boolean comparison: " + droneBoolean + " " + operator + " " + targetBoolean);
        
        if (operator.equals("=")) {
            return droneBoolean == targetBoolean;
        } else if (operator.equals("!=")) {
            return droneBoolean != targetBoolean;
        } else {
            System.err.println("Invalid operator for boolean: " + operator);
            return false;
        }
    }
    
    
    if (droneValue instanceof Number) {
        double droneNumber = ((Number) droneValue).doubleValue();
        double targetNumber;
        
        try {
            targetNumber = Double.parseDouble(targetValue);
        } catch (NumberFormatException e) {
            System.err.println("Cannot parse number: " + targetValue);
            return false;
        }
        
        System.out.println("Number comparison: " + droneNumber + " " + operator + " " + targetNumber);
        
        if (operator.equals("=")) {
            return Math.abs(droneNumber - targetNumber) < 0.0001;
        } else if (operator.equals("!=")) {
            return Math.abs(droneNumber - targetNumber) >= 0.0001;
        } else if (operator.equals("<")) {
            return droneNumber < targetNumber;
        } else if (operator.equals(">")) {
            return droneNumber > targetNumber;
        } else {
            System.err.println("Invalid operator: " + operator);
            return false;
        }
    }
    
    
    if (droneValue instanceof String) {
        String droneString = (String) droneValue;
        
        System.out.println("String comparison: '" + droneString + "' " + operator + " '" + targetValue + "'");
        
        if (operator.equals("=")) {
            return droneString.equals(targetValue);
        } else if (operator.equals("!=")) {
            return !droneString.equals(targetValue);
        } else {
            System.err.println("Invalid operator for string: " + operator);
            return false;
        }
    }
    
    System.err.println("Unsupported type for comparison: " + droneValue.getClass().getName());
    return false;
}

/**
 * Queries which drones can fulfill ALL dispatch requirements.
 * Uses AND logic - drone must be able to handle every dispatch.
 * 
 * @param dispatches List of medicine dispatch records
 * @return List of drone IDs that can fulfill all requirements
 */
public List<Integer> queryAvailableDrones(List<MedDispatchRec> dispatches) {
    System.out.println("Querying available drones for " + dispatches.size() + " dispatches");
    
    // Validate input
    if (dispatches == null || dispatches.isEmpty()) {
        System.out.println("No dispatches provided - returning empty list");
        return List.of();
    }
    
    // Validate all dispatch records
    for (MedDispatchRec dispatch : dispatches) {
        if (dispatch == null || !dispatch.isValid()) {
            System.err.println("Invalid dispatch record: " + dispatch);
            return List.of();
        }
    }
    
    try {
        // Fetch all drones and their availability
        List<Drone> allDrones = getAllDrones();
        List<ServicePointDrones> servicePoints = ilpRestClient.getDronesForServicePoints();
        
        // Build a map of drone availability for quick lookup
        Map<String, DroneAvailability> availabilityMap = buildAvailabilityMap(servicePoints);
        
        System.out.println("Checking " + allDrones.size() + " drones against " + dispatches.size() + " dispatches");
        
        // Filter drones that can fulfill ALL dispatches
        List<Integer> availableDroneIds = allDrones.stream()
                .filter(drone -> canFulfillAllDispatches(drone, dispatches, availabilityMap))
                .map(Drone::getId)
                .collect(Collectors.toList());
        
        System.out.println("Found " + availableDroneIds.size() + " drones that can fulfill all dispatches");
        
        return availableDroneIds;
        
    } catch (Exception e) {
        System.err.println("Error querying available drones: " + e.getMessage());
        e.printStackTrace();
        return List.of();
    }
}

/**
 * Builds a map of drone ID to availability for quick lookup.
 */
private Map<String, DroneAvailability> buildAvailabilityMap(List<ServicePointDrones> servicePoints) {
    Map<String, DroneAvailability> map = new HashMap<>();
    
    for (ServicePointDrones sp : servicePoints) {
        for (DroneAvailability da : sp.getDrones()) {
            map.put(da.getId(), da);
        }
    }
    
    System.out.println("Built availability map with " + map.size() + " drones");
    return map;
}

/**
 * Checks if a drone can fulfill ALL dispatch requirements (AND logic).
 */
private boolean canFulfillAllDispatches(Drone drone, List<MedDispatchRec> dispatches, 
                                       Map<String, DroneAvailability> availabilityMap) {
    
    String droneIdStr = String.valueOf(drone.getId());
    DroneAvailability availability = availabilityMap.get(droneIdStr);
    
    // Check each dispatch
    for (MedDispatchRec dispatch : dispatches) {
        if (!canFulfillDispatch(drone, dispatch, availability)) {
            // Failed one dispatch - exclude this drone
            return false;
        }
    }
    
    // Passed all dispatches!
    return true;
}

/**
 * Checks if a drone can fulfill a single dispatch requirement.
 * Checks: capacity, temperature, availability, and cost.
 */
private boolean canFulfillDispatch(Drone drone, MedDispatchRec dispatch, 
                                  DroneAvailability availability) {
    
    Requirements req = dispatch.getRequirements();
    
    if (req == null) {
        System.err.println("Dispatch " + dispatch.getId() + " has no requirements");
        return false;
    }
    
    // Check 1: Capacity
    if (!meetsCapacityRequirement(drone, req)) {
        return false;
    }
    
    // Check 2: Temperature (cooling/heating)
    if (!meetsTemperatureRequirement(drone, req)) {
        return false;
    }
    
    // Check 3: Availability (date/time)
    if (!meetsAvailabilityRequirement(availability, dispatch)) {
        return false;
    }
    
    // Check 4: Cost constraint (if specified)
    if (!meetsCostConstraint(drone, req)) {
        return false;
    }
    
    return true;
}

/**
 * Checks if drone has sufficient capacity.
 */
private boolean meetsCapacityRequirement(Drone drone, Requirements req) {
    if (drone.getCapability() == null) {
        return false;
    }
    
    double droneCapacity = drone.getCapability().getCapacity();
    double requiredCapacity = req.getCapacity();
    
    boolean meets = droneCapacity >= requiredCapacity;
    
    if (!meets) {
        System.out.println("Drone " + drone.getId() + " capacity " + droneCapacity + 
                          " < required " + requiredCapacity);
    }
    
    return meets;
}

/**
 * Checks if drone meets temperature requirements.
 */
private boolean meetsTemperatureRequirement(Drone drone, Requirements req) {
    if (drone.getCapability() == null) {
        return false;
    }
    
    // If cooling required, drone must have cooling
    if (req.isCoolingRequired() && !drone.getCapability().isCooling()) {
        System.out.println("Drone " + drone.getId() + " lacks required cooling");
        return false;
    }
    
    // If heating required, drone must have heating
    if (req.isHeatingRequired() && !drone.getCapability().isHeating()) {
        System.out.println("Drone " + drone.getId() + " lacks required heating");
        return false;
    }
    
    return true;
}


private boolean meetsAvailabilityRequirement(DroneAvailability availability, 
                                            MedDispatchRec dispatch) {
    
    
    if (availability == null) {
        System.out.println("No availability info for drone - assuming available");
        return true;
    }
    
    String date = dispatch.getDate();
    String time = dispatch.getTime();
    
    // If no date specified, can't check availability
    if (date == null || date.isEmpty()) {
        System.out.println("No date specified in dispatch - assuming available");
        return true;
    }
    
    boolean available = availability.isAvailableOn(date, time);
    
    if (!available) {
        System.out.println("Drone " + availability.getId() + " not available on " + 
                          date + (time != null ? " at " + time : ""));
    }
    
    return available;
}

/**
 * Checks if drone meets cost constraint (if specified).
 * Note: This is a simplified check. Full implementation would need
 * to calculate actual delivery cost including distance.
 */
private boolean meetsCostConstraint(Drone drone, Requirements req) {
    // If no max cost specified, any cost is acceptable
    if (!req.hasMaxCostConstraint()) {
        return true;
    }
    
    if (drone.getCapability() == null) {
        return false;
    }
    
    double maxCost = req.getMaxCost();
    
    
    double minPossibleCost = drone.getCapability().getCostInitial() + 
                            drone.getCapability().getCostFinal();
    
    boolean meets = minPossibleCost <= maxCost;
    
    if (!meets) {
        System.out.println("Drone " + drone.getId() + " minimum cost " + minPossibleCost + 
                          " exceeds max " + maxCost);
    }
    
    return meets;
}


  
}
