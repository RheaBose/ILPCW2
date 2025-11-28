package uk.ac.ed.acp.cw2.controller;

import com.fasterxml.jackson.databind.JsonSerializer;

import lombok.RequiredArgsConstructor;
import uk.ac.ed.acp.cw2.dto.DistanceRequest;
import uk.ac.ed.acp.cw2.dto.IsCloseToRequest;
import uk.ac.ed.acp.cw2.dto.LngLat;
import uk.ac.ed.acp.cw2.dto.NextPositionRequest;
import uk.ac.ed.acp.cw2.dto.isRegionRequest;
import uk.ac.ed.acp.cw2.service.GeometryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.time.Instant;
import java.util.Map;

/**
 * Controller class that handles various HTTP endpoints for the application.
 * Provides functionality for serving the index page, retrieving a static UUID,
 * and managing key-value pairs through POST requests.
 */
@RestController()
@RequestMapping("/api/v1")
public class ServiceController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);
    
    
    private final GeometryService  geometryService;

    @Autowired
    public ServiceController(GeometryService geometryService) {
        this.geometryService = geometryService;  
    }

    @Value("${ilp.service.url}")
    public URL serviceUrl;


    @GetMapping("/")
    public String index() {
        return "<html><body>" +
                "<h1>Welcome from ILP</h1>" +
                "<h4>ILP-REST-Service-URL:</h4> <a href=\"" + serviceUrl + "\" target=\"_blank\"> " + serviceUrl+ " </a>" +
                "</body></html>";
    }

    @GetMapping("/uid")
    public String uid() {
        return "s2555085";
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<Double> distanceTo(@RequestBody DistanceRequest request) {
        if (!request.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        LngLat pos1 = request.getPosition1();
        LngLat pos2 = request.getPosition2();
        double deltaLat = pos2.getLat() - pos1.getLat();
        double deltaLng = pos2.getLng() - pos1.getLng();
        double distance = Math.sqrt(deltaLat * deltaLat + deltaLng * deltaLng);
        return ResponseEntity.ok(distance);
       
}

    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@RequestBody IsCloseToRequest request) {
        if (!request.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
       boolean isClose = geometryService.isCloseTo(request.getPosition1(), request.getPosition2());
       return ResponseEntity.ok(isClose);
        
}

    @PostMapping("/nextPosition")
    public ResponseEntity<LngLat> nextPosition(@RequestBody NextPositionRequest request){
        if(!request.isValid()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        LngLat nextPos = geometryService.calculateNewPosition(request.getStart(), request.getAngle());
        return ResponseEntity.ok(nextPos);

}
    
    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody isRegionRequest request) {
        if (!request.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean inside = geometryService.isInRegion(request.getPosition(), request.getRegion());
        return ResponseEntity.ok(inside);
}
    }
     




