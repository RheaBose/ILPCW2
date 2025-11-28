package uk.ac.ed.acp.cw2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.dto.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for planning drone delivery routes.
 * Coordinates pathfinding, drone selection, and cost calculation.
 */
@Service
public class DeliveryService {
    
    private final ILPRestClient ilpRestClient;
    private final PathfindingService pathfindingService;
    private final GeometryService geometryService;
    private final DroneService droneService;
    
    @Autowired
    public DeliveryService(ILPRestClient ilpRestClient, 
                          PathfindingService pathfindingService,
                          GeometryService geometryService,
                          DroneService droneService) {
        this.ilpRestClient = ilpRestClient;
        this.pathfindingService = pathfindingService;
        this.geometryService = geometryService;
        this.droneService = droneService;
    }
    @Autowired
    private AdvancedDeliveryPlanner advancedPlanner;
    
    /**
     * Calculates delivery paths for given dispatch records.
     * This is the SIMPLE case - assumes all deliveries can be done by one drone.
     * 
     * @param dispatches List of delivery requests
     * @return Complete delivery path with costs
     */
    public DeliveryPathResponse calculateDeliveryPath(List<MedDispatchRec> dispatches) {
        System.out.println("=== Calculating delivery path for " + dispatches.size() + " dispatches ===");
        
        // Validate input
        if (dispatches == null || dispatches.isEmpty()) {
            return createEmptyResponse();
        }
        
        try {
            // Fetch all necessary data
            List<Drone> allDrones = droneService.getAllDrones();
            List<ServicePoint> servicePoints = ilpRestClient.getServicePoints();
            List<RestrictedArea> restrictedAreas = ilpRestClient.getRestrictedAreas();
            List<ServicePointDrones> availability = ilpRestClient.getDronesForServicePoints();
            
            System.out.println("Found " + allDrones.size() + " drones, " + 
                             servicePoints.size() + " service points, " +
                             restrictedAreas.size() + " restricted areas");
            
            // Find available drones that can fulfill all dispatches
            List<Integer> availableDroneIds = droneService.queryAvailableDrones(dispatches);
            
            if (availableDroneIds.isEmpty()) {
                System.err.println("No drones available for these dispatches");
                return createEmptyResponse();
            }
            
            System.out.println("Found " + availableDroneIds.size() + " available drones: " + availableDroneIds);
            
            // Select best drone (first available for now - can optimize later)
            int selectedDroneId = availableDroneIds.get(0);
            Drone selectedDrone = allDrones.stream()
                    .filter(d -> d.getId() == selectedDroneId)
                    .findFirst()
                    .orElse(null);
            
            if (selectedDrone == null) {
                System.err.println("Selected drone not found");
                return createEmptyResponse();
            }
            
            System.out.println("Selected drone: " + selectedDrone.getName() + " (ID: " + selectedDroneId + ")");
            
            // Find which service point has this drone
            ServicePoint servicePoint = findServicePointForDrone(selectedDroneId, servicePoints, availability);
            
            if (servicePoint == null) {
                System.err.println("No service point found for drone " + selectedDroneId);
                return createEmptyResponse();
            }
            
            System.out.println("Starting from: " + servicePoint.getName());
            
            // Plan the route
            DronePath dronePath = planRoute(selectedDrone, servicePoint, dispatches, restrictedAreas);
            
           
            int totalMoves = countTotalMoves(dronePath);
            double totalCost = calculateTotalCost(selectedDrone, totalMoves, dispatches.size());
            
            System.out.println("Total moves: " + totalMoves + ", Total cost: " + totalCost);
            
            
            DeliveryPathResponse response = new DeliveryPathResponse();
            response.setTotalCost(totalCost);
            response.setTotalMoves(totalMoves);
            response.setDronePaths(Arrays.asList(dronePath));
            
            return response;
            
        } catch (Exception e) {
            System.err.println("Error calculating delivery path: " + e.getMessage());
            e.printStackTrace();
            return createEmptyResponse();
        }
    }
    
    
private DronePath planRoute(Drone drone, ServicePoint servicePoint, 
                           List<MedDispatchRec> dispatches, 
                           List<RestrictedArea> restrictedAreas) {
    
    List<Delivery> deliveries = new ArrayList<>();
    LngLat currentPosition = servicePoint.getLocation();
    
    System.out.println("Planning route from service point: " + currentPosition);
    
    // For each dispatch, plan path from current position to delivery location
    for (MedDispatchRec dispatch : dispatches) {
        
        LngLat deliveryLocation = dispatch.getDelivery();
        
        if (deliveryLocation == null) {
            System.err.println("WARNING: Dispatch " + dispatch.getId() + 
                              " has no delivery location, generating test location");
            deliveryLocation = createDummyDeliveryLocation(currentPosition, dispatch.getId());
        }
        
        if (!deliveryLocation.isValid()) {
            System.err.println("ERROR: Invalid delivery location for dispatch " + dispatch.getId());
            continue;
        }
        
        System.out.println("Planning delivery " + dispatch.getId() + " to " + deliveryLocation);
        
        // Find path from current position to delivery location
        List<LngLat> flightPath = pathfindingService.findPath(
            currentPosition, 
            deliveryLocation, 
            restrictedAreas
        );
        
        System.out.println("  Path has " + flightPath.size() + " positions = " + 
                          (flightPath.size() - 1) + " moves");
        
        // Create delivery object
        Delivery delivery = new Delivery(dispatch.getId(), flightPath);
        deliveries.add(delivery);
        
        // Update current position (last position in flight path, which is the hover position)
        currentPosition = flightPath.get(flightPath.size() - 1);
    }
    
    // Return to service point as SEPARATE delivery with ID 0
    if (!deliveries.isEmpty()) {
        System.out.println("Planning return to service point");
        List<LngLat> returnPath = pathfindingService.findPath(
            currentPosition, 
            servicePoint.getLocation(), 
            restrictedAreas
        );
        
        System.out.println("  Return path has " + returnPath.size() + " positions = " + 
                          (returnPath.size() - 1) + " moves");
        
        // Create return delivery with ID 0
        Delivery returnDelivery = new Delivery(0, returnPath);
        deliveries.add(returnDelivery);
    }
    
    // Create drone path
    DronePath dronePath = new DronePath(drone.getId(), deliveries);
    return dronePath;
}
    
   
    private ServicePoint findServicePointForDrone(int droneId, 
                                                  List<ServicePoint> servicePoints,
                                                  List<ServicePointDrones> availability) {
        
        String droneIdStr = String.valueOf(droneId);
        
       
        for (ServicePointDrones spd : availability) {
            for (DroneAvailability da : spd.getDrones()) {
                if (da.getId().equals(droneIdStr)) {
                    
                    int servicePointId = spd.getServicePointId();
                    return servicePoints.stream()
                            .filter(sp -> sp.getId() == servicePointId)
                            .findFirst()
                            .orElse(null);
                }
            }
        }
        
        
        return servicePoints.isEmpty() ? null : servicePoints.get(0);
    }
    
    
    private int countTotalMoves(DronePath dronePath) {
        int total = 0;
        for (Delivery delivery : dronePath.getDeliveries()) {
        int positions = delivery.getFlightPath().size();
        if (positions > 0) {
            total += positions - 1;  
        }
    }
        return total;
    }
    
    
    private double calculateTotalCost(Drone drone, int totalMoves, int numDeliveries) {
        DroneCapability capability = drone.getCapability();
        
        // Base costs distributed across deliveries
        double baseCostPerDelivery = (capability.getCostInitial() + capability.getCostFinal());
        double totalBaseCost = baseCostPerDelivery * numDeliveries;
        
        // Move costs
        double moveCost = totalMoves * capability.getCostPerMove();
        
        return totalBaseCost + moveCost;
    }
    
    
    private DeliveryPathResponse createEmptyResponse() {
        return new DeliveryPathResponse(0.0, 0, new ArrayList<>());
    }
    
    
    private LngLat createDummyDeliveryLocation(LngLat servicePoint, int dispatchId) {
    
    
    double[] testLocations = {
        -3.188, 55.945,  // Near George Square
        -3.192, 55.943,  // Near Bristo Square
        -3.184, 55.947,  // Near Old College
        -3.189, 55.946,  // Central area
        -3.186, 55.944   // Near service point
    };
    
    int index = (dispatchId % 5) * 2;
    return new LngLat(testLocations[index], testLocations[index + 1]);
}
public DeliveryPathResponse calculateDeliveryPath(List<MedDispatchRec> dispatches, boolean useAdvanced) {
    if (!useAdvanced) {
        // Use existing simple logic
        return calculateDeliveryPath(dispatches);
    }
    
    System.out.println("=== Using ADVANCED delivery path calculation ===");
    
    if (dispatches == null || dispatches.isEmpty()) {
        return createEmptyResponse();
    }
    
    try {
        // Fetch all data
        List<Drone> allDrones = droneService.getAllDrones();
        List<ServicePoint> servicePoints = ilpRestClient.getServicePoints();
        List<RestrictedArea> restrictedAreas = ilpRestClient.getRestrictedAreas();
        List<ServicePointDrones> availability = ilpRestClient.getDronesForServicePoints();
        
        // Find available drones
        List<Integer> availableDroneIds = droneService.queryAvailableDrones(dispatches);
        
        if (availableDroneIds.isEmpty()) {
            System.err.println("No drones available");
            return createEmptyResponse();
        }
        
        // Get drone objects
        List<Drone> availableDrones = allDrones.stream()
                .filter(d -> availableDroneIds.contains(d.getId()))
                .collect(Collectors.toList());
        
        System.out.println("Planning with " + availableDrones.size() + " available drones");
        
        // Use advanced planner
        List<DronePath> dronePaths = advancedPlanner.planOptimizedRoutes(
            availableDrones, dispatches, servicePoints, availability, restrictedAreas
        );
        
        if (dronePaths.isEmpty()) {
            return createEmptyResponse();
        }
        
        // Calculate totals
        int totalMoves = 0;
        double totalCost = 0.0;
        
        for (DronePath dronePath : dronePaths) {
            int moves = countTotalMoves(dronePath);
            totalMoves += moves;
            
            // Find drone for cost calculation
            Drone drone = availableDrones.stream()
                    .filter(d -> d.getId() == dronePath.getDroneId())
                    .findFirst()
                    .orElse(null);
            
            if (drone != null) {
                double cost = calculateTotalCost(
                    drone, moves, dronePath.getDeliveries().size()
                );
                totalCost += cost;
            }
        }
        
        System.out.println("Complex plan: " + dronePaths.size() + " drones, " + 
                         totalMoves + " moves, $" + totalCost);
        
        DeliveryPathResponse response = new DeliveryPathResponse();
        response.setTotalCost(totalCost);
        response.setTotalMoves(totalMoves);
        response.setDronePaths(dronePaths);
        
        return response;
        
    } catch (Exception e) {
        System.err.println("Error in advanced planning: " + e.getMessage());
        e.printStackTrace();
        return createEmptyResponse();
    }
}


}