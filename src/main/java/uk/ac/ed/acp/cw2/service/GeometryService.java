package uk.ac.ed.acp.cw2.service;

import java.util.List;

import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.dto.LngLat;
import uk.ac.ed.acp.cw2.dto.Region;

@Service
public class GeometryService {

    private static final double MOVE_DISTANCE = 0.00015;
    private static final double CLOSE_THRESHOLD = 0.00015;

    public double calculateDistance(LngLat position1, LngLat position2) {
        double deltaLat = position2.getLat() - position1.getLat();
        double deltaLng = position2.getLng() - position1.getLng();
        return Math.sqrt(deltaLat * deltaLat + deltaLng * deltaLng);
    }

    public boolean isCloseTo(LngLat position1, LngLat position2) {
        return calculateDistance(position1, position2) < CLOSE_THRESHOLD;
    }

    public LngLat calculateNewPosition(LngLat start, double angle) {
        double angleRad = Math.toRadians(angle);
        double newLat = start.getLat() + MOVE_DISTANCE * Math.sin(angleRad);
        double newLng = start.getLng() + MOVE_DISTANCE * Math.cos(angleRad);
        return new LngLat(newLng, newLat);
    }

    public boolean isInRegion(LngLat position, Region region) {
        List<LngLat> vertices = region.getVertices();
        return isPointInPolygon(position, vertices);
    }

    private boolean isPointInPolygon(LngLat point, List<LngLat> vertices) {
        int intersectCount = 0;
        for (int i = 0; i < vertices.size() - 1; i++) {
            LngLat v1 = vertices.get(i);
            LngLat v2 = vertices.get(i + 1);
            if (isPointOnLineSegment(point, v1, v2)) {
                return true;
            }
            if (v1.getLng() > point.getLng() != v2.getLng() > point.getLng()) {
                double slope = (v2.getLat() - v1.getLat()) / (v2.getLng() - v1.getLng());
                double intersectLat = v1.getLat() + slope * (point.getLng() - v1.getLng());
                if (intersectLat > point.getLat()) {
                    intersectCount++;
                }
            }
        }
        return (intersectCount % 2) == 1;
    }
    private boolean isPointOnLineSegment(LngLat point, LngLat v1, LngLat v2) {
        double epsilon = 1e-9;
        double minLat = Math.min(v1.getLat(), v2.getLat());
        double maxLat = Math.max(v1.getLat(), v2.getLat());
        double minLng = Math.min(v1.getLng(), v2.getLng());
        double maxLng = Math.max(v1.getLng(), v2.getLng());
        if (point.getLat() < minLat - epsilon || point.getLat() > maxLat + epsilon ||
            point.getLng() < minLng - epsilon || point.getLng() > maxLng + epsilon) {
            return false;
        }
        double crossProduct = (point.getLat() - v1.getLat()) * (v2.getLng() - v1.getLng()) -
                              (point.getLng() - v1.getLng()) * (v2.getLat() - v1.getLat());
        return Math.abs(crossProduct) < epsilon;
    }
    
}
