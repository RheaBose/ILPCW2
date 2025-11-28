package uk.ac.ed.acp.cw2.service;
import uk.ac.ed.acp.cw2.dto.Drone;
import uk.ac.ed.acp.cw2.dto.ServicePointDrones;
import uk.ac.ed.acp.cw2.dto.DroneAvailability;
import uk.ac.ed.acp.cw2.dto.Availability;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import uk.ac.ed.acp.cw2.dto.ServicePoint;
import uk.ac.ed.acp.cw2.dto.RestrictedArea;

import java.util.List;
import java.util.Map;

@Service
public class ILPRestClient {

  private final RestTemplate restTemplate;
  private final String ilpEndpoint;
  @Autowired
  public ILPRestClient(RestTemplate restTemplate, String ilpEndpoint) {
    this.restTemplate = restTemplate;
    this.ilpEndpoint = ilpEndpoint;
  }
  public List<Map<String,Object>> fetchAllDrones(){
    String url = ilpEndpoint + "api/v1/drones";
    try{
      ResponseEntity<List<Map<String,Object>>> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Map<String,Object>>>(){}
      );
      System.out.println("Fetched drones: " + response.getBody());
      return response.getBody();
    } catch (HttpClientErrorException e){
      System.err.println("Error fetching drones: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
      return List.of();
    }
  }
  public List<Drone> getAllDrones(){
    String url = ilpEndpoint + "drones";
    try {
      ResponseEntity<Drone[]> response = restTemplate.getForEntity(url, Drone[].class);
      return Arrays.asList(response.getBody());
    } catch (Exception e) {
      System.err.println("Error fetching drones: " + e.getMessage());
      throw new RuntimeException("Failed to fetch drones from ILP service", e);
    }
  }

  public Drone getDroneById(int id) {
    System.out.println("Fetching drone with ID: " + id);
    
    try {
       
        List<Drone> allDrones = getAllDrones();
        
        System.out.println("Fetched " + allDrones.size() + " drones, searching for ID " + id);
        
        
        Drone found = allDrones.stream()
                .filter(drone -> drone.getId() == id)
                .findFirst()
                .orElse(null);
        
        if (found != null) {
            System.out.println("Found drone: " + found.getName());
        } else {
            System.out.println("Drone with ID " + id + " not found");
        }
        
        return found;
      } catch (Exception e) {
        System.err.println("Error fetching drone by ID: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
public List<ServicePointDrones> getDronesForServicePoints() {
    String url = ilpEndpoint + "drones-for-service-points";
    System.out.println("Calling external API: " + url);
    
    try {
        ResponseEntity<ServicePointDrones[]> response = restTemplate.getForEntity(
            url,
            ServicePointDrones[].class
        );
        
        ServicePointDrones[] result = response.getBody();
        System.out.println("Received availability for " + 
            (result != null ? result.length : 0) + " service points");
        
        return result != null ? Arrays.asList(result) : List.of();
        
    } catch (Exception e) {
        System.err.println("Error fetching drone availability: " + e.getMessage());
        e.printStackTrace();
        return List.of();
    }

    
}


public List<ServicePoint> getServicePoints() {
    String url = ilpEndpoint + "service-points";
    System.out.println("Calling external API: " + url);
    
    try {
        ResponseEntity<ServicePoint[]> response = restTemplate.getForEntity(
            url,
            ServicePoint[].class
        );
        
        ServicePoint[] result = response.getBody();
        System.out.println("Received " + (result != null ? result.length : 0) + " service points");
        
        return result != null ? Arrays.asList(result) : List.of();
        
    } catch (Exception e) {
        System.err.println("Error fetching service points: " + e.getMessage());
        return List.of();
    }
}


public List<RestrictedArea> getRestrictedAreas() {
    String url = ilpEndpoint + "restricted-areas";
    System.out.println("Calling external API: " + url);
    
    try {
        ResponseEntity<RestrictedArea[]> response = restTemplate.getForEntity(
            url,
            RestrictedArea[].class
        );
        
        RestrictedArea[] result = response.getBody();
        System.out.println("Received " + (result != null ? result.length : 0) + " restricted areas");
        
        return result != null ? Arrays.asList(result) : List.of();
        
    } catch (Exception e) {
        System.err.println("Error fetching restricted areas: " + e.getMessage());
        return List.of();
    }
}



  
}
