package uk.ac.ed.acp.cw2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.dto.LngLat;
import uk.ac.ed.acp.cw2.dto.RestrictedArea;
import uk.ac.ed.acp.cw2.dto.Region;

import java.util.*;

@Service
public class PathfindingService {
    
    private static final double MOVE_DISTANCE = 0.00015;
    private static final double[] VALID_ANGLES = {0, 45, 90, 135, 180, 225, 270, 315}; // Degrees
    
    private final GeometryService geometryService;
    
    @Autowired
    public PathfindingService(GeometryService geometryService) {
        this.geometryService = geometryService;
    }
    
    /**
     * Finds a path from start to goal avoiding restricted areas.
     * Uses A* pathfinding algorithm.
     * 
     * @param start Starting position
     * @param goal Target position
     * @param restrictedAreas No-fly zones to avoid
     * @return List of positions from start to goal (including both)
     */
    public List<LngLat> findPath(LngLat start, LngLat goal, List<RestrictedArea> restrictedAreas) {
        System.out.println("Finding path from " + start + " to " + goal);
        
        
        if (geometryService.isCloseTo(start, goal)) {
            List<LngLat> path = new ArrayList<>();
            path.add(start);
            path.add(start);  // Hover = same position twice
            return path;
        }
        
        
        return aStarSearch(start, goal, restrictedAreas);
    }
    
    private List<LngLat> aStarSearch(LngLat start, LngLat goal, List<RestrictedArea> restrictedAreas) {
        
       
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Map<String, Node> allNodes = new HashMap<>();
        
       
        Node startNode = new Node(start);
        startNode.gScore = 0;
        startNode.fScore = heuristic(start, goal);
        
        openSet.add(startNode);
        allNodes.put(positionKey(start), startNode);
        
        int iterations = 0;
        int maxIterations = 10000;  
        
        while (!openSet.isEmpty() && iterations < maxIterations) {
            iterations++;
            
            Node current = openSet.poll();
            
           
            if (geometryService.isCloseTo(current.position, goal)) {
                System.out.println("Path found in " + iterations + " iterations");
                return reconstructPath(current, goal);
            }
            
            current.closed = true;
            
      
            for (double angle : VALID_ANGLES) {
                LngLat neighbor = geometryService.calculateNewPosition(current.position, angle);
                
                
                if (isInAnyRestrictedArea(neighbor, restrictedAreas)) {
                    continue;
                }
                
                String key = positionKey(neighbor);
                Node neighborNode = allNodes.get(key);
                
                if (neighborNode == null) {
                    neighborNode = new Node(neighbor);
                    allNodes.put(key, neighborNode);
                }
                
                if (neighborNode.closed) {
                    continue;
                }
                
                
                double tentativeGScore = current.gScore + MOVE_DISTANCE;
                
                if (tentativeGScore < neighborNode.gScore) {
                
                    neighborNode.parent = current;
                    neighborNode.gScore = tentativeGScore;
                    neighborNode.fScore = tentativeGScore + heuristic(neighbor, goal);
                    
                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }
            }
        }
        
        
        System.err.println("No path found after " + iterations + " iterations - using direct path");
        return createDirectPath(start, goal);
    }
    
    
    private double heuristic(LngLat from, LngLat to) {
        return geometryService.calculateDistance(from, to);
    }
    
    
    private List<LngLat> reconstructPath(Node goalNode, LngLat actualGoal) {
        List<LngLat> path = new ArrayList<>();
        Node current = goalNode;
        
        while (current != null) {
            path.add(current.position);
            current = current.parent;
        }
        
        Collections.reverse(path);
        
        
        if(!path.isEmpty()){
            path.set(path.size() - 1, actualGoal); // Ensure exact goal position
            path.add(actualGoal); // Hover at goal
        }  
        
        return path;
    }
    
    
    private List<LngLat> createDirectPath(LngLat start, LngLat goal) {
        List<LngLat> path = new ArrayList<>();
        path.add(start);
        
        LngLat current = start;
        int maxSteps = 1000;
        int steps = 0;
        
        while (!geometryService.isCloseTo(current, goal) && steps < maxSteps) {
            
            double angle = findBestAngle(current, goal);
            current = geometryService.calculateNewPosition(current, angle);
            path.add(current);
            steps++;
        }
        
        
        if(path.isEmpty()){
            path.set(path.size() - 1, goal); // Ensure exact goal position
            path.add(goal); // Hover at goal
        }
        
        return path;
    }
  
    private double findBestAngle(LngLat from, LngLat to) {
        double dx = to.getLng() - from.getLng();
        double dy = to.getLat() - from.getLat();
        
        
        double angleRadians = Math.atan2(dy, dx);
        double angleDegrees = Math.toDegrees(angleRadians);
        
        
        if (angleDegrees < 0) {
            angleDegrees += 360;
        }
        
        
        return snapToValidAngle(angleDegrees);
    }
    
   
    private double snapToValidAngle(double angle) {
        double nearest = VALID_ANGLES[0];
        double minDiff = Math.abs(angle - nearest);
        
        for (double validAngle : VALID_ANGLES) {
            double diff = Math.abs(angle - validAngle);
            if (diff < minDiff) {
                minDiff = diff;
                nearest = validAngle;
            }
        }
        
        return nearest;
    }
    
   
    private boolean isInAnyRestrictedArea(LngLat position, List<RestrictedArea> restrictedAreas) {
        for (RestrictedArea area : restrictedAreas) {
            Region region = new Region(area.getName(), area.getVertices());
            if (geometryService.isInRegion(position, region)) {
                return true;
            }
        }
        return false;
    }
    
   
    private String positionKey(LngLat pos) {
        return String.format("%.6f,%.6f", pos.getLng(), pos.getLat());
    }
   
    private static class Node {
        LngLat position;
        Node parent;
        double gScore = Double.POSITIVE_INFINITY;  // Cost from start
        double fScore = Double.POSITIVE_INFINITY;  // gScore + heuristic
        boolean closed = false;
        
        Node(LngLat position) {
            this.position = position;
        }
    }
}
