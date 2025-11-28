## Implementation Summary

### Completed Features 
-  Group 2: Static queries 
-  Group 3: Dynamic queries 
-  Group 4: Availability queries 
-  Group 5: Simple calcDeliveryPath 
-  Group 5: Complex calcDeliveryPath 
-  Group 6: calcDeliveryPathAsGeoJSON 

### Key Implementation Details

**Pathfinding:** A* algorithm with obstacle avoidance
**Multi-drone planning:** Efficiency-based drone selection with nearest-neighbor optimization
**Move counting:** Correct implementation (positions - 1)
**Service point selection:** Closest to delivery centroid
**Return path:** Separate delivery with ID 0

### Running the Application

**Local:**
```bash
mvn clean package -DskipTests
java -jar target/acp-cw2-1.0.0.jar
```

**Docker:**
```bash
docker build -t ilp-service .
docker run -p 8080:8080 ilp-service
```

### API Endpoints

- `GET /api/v1/dronesWithCooling/{hasCooling}`
- `GET /api/v1/droneDetails/{droneId}`
- `POST /api/v1/query` - Returns DronePath
- `GET /api/v1/queryAsPath/{dispatchId}`
- `POST /api/v1/queryAvailableDrones`
- `POST /api/v1/calcDeliveryPath`
- `POST /api/v1/calcDeliveryPathAsGeoJson`

### External Dependencies

- Spring Boot 3.3.5
- Java 21
- Maven 3.9+

