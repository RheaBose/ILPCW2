package uk.ac.ed.acp.cw2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.dto.*;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class AdvancedDeliveryPlanner {
    
    private final PathfindingService pathfindingService;
    private final GeometryService geometryService;
    
    @Autowired
    public AdvancedDeliveryPlanner(PathfindingService pathfindingService,
                                  GeometryService geometryService) {
        this.pathfindingService = pathfindingService;
        this.geometryService = geometryService;
    }
    
    
    public List<DronePath> planOptimizedRoutes(
            List<Drone> availableDrones,
            List<MedDispatchRec> dispatches,
            List<ServicePoint> servicePoints,
            List<ServicePointDrones> availability,
            List<RestrictedArea> restrictedAreas) {
        
        System.out.println("=== Advanced Route Planning ===");
        System.out.println("Available drones: " + availableDrones.size());
        System.out.println("Dispatches: " + dispatches.size());
        
        // Build drone assignments
        List<DroneAssignment> assignments = new ArrayList<>();
        
        for (Drone drone : availableDrones) {
            ServicePoint servicePoint = findServicePointForDrone(
                drone, servicePoints, dispatches
            );
            
            if (servicePoint != null) {
                assignments.add(new DroneAssignment(drone, servicePoint));
            }
        }
        
        if (assignments.isEmpty()) {
            System.err.println("No valid drone assignments found");
            return new ArrayList<>();
        }
        
        
        List<DronePath> result = distributeDeliveries(
            assignments, dispatches, restrictedAreas
        );
        
        System.out.println("Planned routes for " + result.size() + " drone(s)");
        return result;
    }
    
    
    private List<DronePath> distributeDeliveries(
            List<DroneAssignment> assignments,
            List<MedDispatchRec> dispatches,
            List<RestrictedArea> restrictedAreas) {
        
        
        assignments.sort((a, b) -> {
            double efficiencyA = a.drone.getCapability().getCapacity() / 
                               (a.drone.getCapability().getCostPerMove() + 0.01);
            double efficiencyB = b.drone.getCapability().getCapacity() / 
                               (b.drone.getCapability().getCostPerMove() + 0.01);
            return Double.compare(efficiencyB, efficiencyA);
        });
        
        // Optimize delivery order (nearest-neighbor heuristic)
        List<MedDispatchRec> optimizedOrder = optimizeDeliveryOrder(
            dispatches, assignments.get(0).servicePoint.getLocation()
        );
        
        List<DronePath> dronePaths = new ArrayList<>();
        List<MedDispatchRec> remainingDeliveries = new ArrayList<>(optimizedOrder);
        
        // Try to assign deliveries to drones
        for (DroneAssignment assignment : assignments) {
            if (remainingDeliveries.isEmpty()) {
                break;
            }
            
            // Plan route for this drone with remaining deliveries
            DroneRouteResult result = planDroneRoute(
                assignment, remainingDeliveries, restrictedAreas
            );
            
            if (!result.completedDeliveries.isEmpty()) {
                dronePaths.add(result.dronePath);
                remainingDeliveries.removeAll(result.completedDeliveries);
                
                System.out.println("Drone " + assignment.drone.getId() + 
                                 " assigned " + result.completedDeliveries.size() + 
                                 " deliveries (" + result.totalMoves + " moves)");
            }
        }
        
        if (!remainingDeliveries.isEmpty()) {
            System.err.println("WARNING: Could not assign " + 
                             remainingDeliveries.size() + " deliveries");
        }
        
        return dronePaths;
    }
    
   
    private DroneRouteResult planDroneRoute(
            DroneAssignment assignment,
            List<MedDispatchRec> deliveries,
            List<RestrictedArea> restrictedAreas) {
        
        Drone drone = assignment.drone;
        ServicePoint servicePoint = assignment.servicePoint;
        int maxMoves = drone.getCapability().getMaxMoves();
        
        List<Delivery> completedDeliveries = new ArrayList<>();
        List<MedDispatchRec> completedDispatches = new ArrayList<>();
        LngLat currentPosition = servicePoint.getLocation();
        int totalMoves = 0;
        
        // Try to add deliveries while under move limit
        for (MedDispatchRec dispatch : deliveries) {
            LngLat deliveryLocation = dispatch.getDelivery();
            
            if (deliveryLocation == null || !deliveryLocation.isValid()) {
                continue;
            }
            
            
            int estimatedMoves = estimateMovesForDelivery(
                currentPosition, deliveryLocation, servicePoint.getLocation()
            );
            
            
            if (totalMoves + estimatedMoves > maxMoves) {
                System.out.println("  Skipping delivery " + dispatch.getId() + 
                                 " - would exceed move limit");
                break;  // Can't fit any more deliveries
            }
            
            
            List<LngLat> flightPath = pathfindingService.findPath(
                currentPosition, deliveryLocation, restrictedAreas
            );
            
            completedDeliveries.add(new Delivery(dispatch.getId(), flightPath));
            completedDispatches.add(dispatch);
            
            currentPosition = flightPath.get(flightPath.size() - 1);
           int moves = flightPath.size() - 1;
           totalMoves += moves;
        }
        
        
        if (!completedDeliveries.isEmpty()) {
          List<LngLat> returnPath = pathfindingService.findPath(
        currentPosition, 
        servicePoint.getLocation(), 
        restrictedAreas
    );
    
    
    Delivery returnDelivery = new Delivery(0, returnPath);
    completedDeliveries.add(returnDelivery);
            
            totalMoves += returnPath.size() - 1;
        }
        
        DronePath dronePath = new DronePath(drone.getId(), completedDeliveries);
        return new DroneRouteResult(dronePath, completedDispatches, totalMoves);
    }
    
   
    private List<MedDispatchRec> optimizeDeliveryOrder(
            List<MedDispatchRec> dispatches, 
            LngLat startLocation) {
        
        List<MedDispatchRec> optimized = new ArrayList<>();
        List<MedDispatchRec> remaining = new ArrayList<>(dispatches);
        LngLat currentLocation = startLocation;
        
        while (!remaining.isEmpty()) {
            // Find nearest delivery
            MedDispatchRec nearest = null;
            double minDistance = Double.MAX_VALUE;
            
            for (MedDispatchRec dispatch : remaining) {
                if (dispatch.getDelivery() == null) continue;
                
                double distance = geometryService.calculateDistance(
                    currentLocation, dispatch.getDelivery()
                );
                
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = dispatch;
                }
            }
            
            if (nearest == null) {
                
                break;
            }
            
            optimized.add(nearest);
            remaining.remove(nearest);
            currentLocation = nearest.getDelivery();
        }
        
        System.out.println("Optimized delivery order: " + 
                         optimized.stream()
                                 .map(MedDispatchRec::getId)
                                 .collect(Collectors.toList()));
        
        return optimized;
    }
    
   
    private int estimateMovesForDelivery(LngLat from, LngLat to, LngLat servicePoint) {
        // Straight-line distance estimate (will be more in practice with obstacles)
        double distanceToDelivery = geometryService.calculateDistance(from, to);
        double distanceToReturn = geometryService.calculateDistance(to, servicePoint);
        double totalDistance = distanceToDelivery + distanceToReturn;
        
        // Each move is 0.00015 degrees
        int estimatedMoves = (int) Math.ceil(totalDistance / 0.00015);
        
        // Add buffer for obstacle avoidance (50% extra)
        return (int) (estimatedMoves * 1.5);
    }
    
  
private ServicePoint findServicePointForDrone(
        Drone drone,
        List<ServicePoint> servicePoints,
        List<MedDispatchRec> deliveries) {
    
    if (servicePoints.isEmpty() || deliveries.isEmpty()) {
        return servicePoints.isEmpty() ? null : servicePoints.get(0);
    }
    
    
    double avgLng = 0;
    double avgLat = 0;
    int validCount = 0;
    
    for (MedDispatchRec delivery : deliveries) {
        if (delivery.getDelivery() != null && delivery.getDelivery().isValid()) {
            avgLng += delivery.getDelivery().getLng();
            avgLat += delivery.getDelivery().getLat();
            validCount++;
        }
    }
    
    if (validCount == 0) {
        return servicePoints.get(0);  
    }
    
    avgLng /= validCount;
    avgLat /= validCount;
    LngLat deliveryCentroid = new LngLat(avgLng, avgLat);
    
    System.out.println("Delivery centroid: " + deliveryCentroid);
    
    
    ServicePoint closest = servicePoints.get(0);
    double minDistance = geometryService.calculateDistance(
        closest.getLocation(), 
        deliveryCentroid
    );
    
    for (ServicePoint sp : servicePoints) {
        double distance = geometryService.calculateDistance(
            sp.getLocation(), 
            deliveryCentroid
        );
        
        if (distance < minDistance) {
            minDistance = distance;
            closest = sp;
        }
    }
    
    System.out.println("Selected service point: " + closest.getName() + 
                      " at " + closest.getLocation() + 
                      " (distance: " + minDistance + ")");
    
    return closest;
}
    
    
    private static class DroneAssignment {
        Drone drone;
        ServicePoint servicePoint;
        
        DroneAssignment(Drone drone, ServicePoint servicePoint) {
            this.drone = drone;
            this.servicePoint = servicePoint;
        }
    }
    
    
    private static class DroneRouteResult {
        DronePath dronePath;
        List<MedDispatchRec> completedDeliveries;
        int totalMoves;
        
        DroneRouteResult(DronePath dronePath, 
                        List<MedDispatchRec> completedDeliveries,
                        int totalMoves) {
            this.dronePath = dronePath;
            this.completedDeliveries = completedDeliveries;
            this.totalMoves = totalMoves;
        }
    }
}