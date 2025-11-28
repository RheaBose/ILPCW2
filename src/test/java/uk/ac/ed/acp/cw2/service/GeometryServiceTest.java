package uk.ac.ed.acp.cw2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ed.acp.cw2.dto.LngLat;
import uk.ac.ed.acp.cw2.dto.Region;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeometryServiceTest {
    private GeometryService geometryService;

    @BeforeEach
    public void setUp() {
        geometryService = new GeometryService();
    }

    @Test
    void testCalculateDistance_SamePoint_ReturnsZero() {
        LngLat pos1 = new LngLat(-3.192473, 55.946233);
        LngLat pos2 = new LngLat(-3.192473, 55.946233);
        double distance = geometryService.calculateDistance(pos1, pos2);
        assertEquals(0.0, distance, 1e-9, "Distance between the same points should be zero");
    }

    @Test
    void testCalculateDistance_KnownPoints_ReturnsCorrectDistance() {
        LngLat pos1 = new LngLat(-3.192473, 55.946233);
        LngLat pos2 = new LngLat(-3.192473, 55.942617);
        double distance = geometryService.calculateDistance(pos1, pos2);
        assertEquals(0.003616, distance, 1e-9, "Calculated distance is incorrect");
    }

    @Test
    void testCalculateDistance_HorizontalMovement_ReturnsCorrectDistance() {
        LngLat pos1 = new LngLat(0,0);
        LngLat pos2 = new LngLat(1.0, 0);
        double distance = geometryService.calculateDistance(pos1, pos2);
        assertEquals(1.0, distance, 1e-9, "Calculated horizontal distance is incorrect");
    }
    
    @Test
    void testCalculateDistance_VerticalMovement_ReturnsCorrectDistance() {
        LngLat pos1 = new LngLat(0,0);
        LngLat pos2 = new LngLat(0, 1.0);
        double distance = geometryService.calculateDistance(pos1, pos2);
        assertEquals(1.0, distance, 1e-9, "Calculated vertical distance is incorrect");
    }

    @Test
    void  testCalculateDistance_DiagonalMovement_ReturnsCorrectDistance() {
        LngLat pos1 = new LngLat(0,0);
        LngLat pos2 = new LngLat(3.0, 4.0);
        double distance = geometryService.calculateDistance(pos1, pos2);
        assertEquals(5.0, distance, 1e-9, "Calculated diagonal distance is incorrect");
    }

    @Test
    void testIsCloseTo_VeryClosePoints_ReturnsTrue() {
        LngLat pos1 = new LngLat(0.0,0.0);
        LngLat pos2 = new LngLat(0.00001, 0.00001);
        assertTrue(geometryService.isCloseTo(pos1, pos2), "Points should be considered close");
    }

    @Test
    void testIsCloseTo_DistantPoints_ReturnsFalse() {
        LngLat pos1 = new LngLat(0.0,0.0);
        LngLat pos2 = new LngLat(1.0,1.0);
        assertFalse(geometryService.isCloseTo(pos1, pos2), "Points should not be considered close");
    }

    @Test
     void testIsCloseTo_SamePoint_ReturnsTrue(){
        LngLat pos1 = new LngLat(1.0,1.0);
        LngLat pos2 = new LngLat(1.0,1.0);
        assertTrue(geometryService.isCloseTo(pos1, pos2), "Same points should be considered close");
     }


     @Test
     void testIsCloseTo_AtThreshold_ReturnsFalse(){
        LngLat pos1 = new LngLat(0.0,0.0);
        LngLat pos2 = new LngLat(0.00015,0.0);
        assertFalse(geometryService.isCloseTo(pos1, pos2), "Points at threshold should not be considered close");
     }


     @Test
     void testCalculateNextPosition_East_MovesCorrectly(){
        LngLat start = new LngLat(0.0, 0.0);
        double angle = 0.0;

        LngLat result = geometryService.calculateNewPosition(start, angle);

        assertEquals(0.00015, result.getLng(), 0.00001, "Longitude should increase by 0.00015");
        assertEquals(0.0, result.getLat(), 0.00001, "Latitude should remain the same");
     }

     @Test
     void testCalculateNextPosition_North_MovesCorrectly(){
        LngLat start = new LngLat(0.0, 0.0);
        double angle = 90.0;

        LngLat result = geometryService.calculateNewPosition(start, angle);

        assertEquals(0.0, result.getLng(), 0.00001, "Longitude should remain the same");
        assertEquals(0.00015, result.getLat(), 0.00001, "Latitude should increase by 0.00015");
     }

     @Test
     void testCalculateNextPosition_West_MovesCorrectly() {
        LngLat start = new LngLat(0.0, 0.0);
        double angle = 180.0;

        LngLat result = geometryService.calculateNewPosition(start, angle);

        assertEquals(-0.00015, result.getLng(), 0.00001, "Longitude should decrease by 0.00015");
        assertEquals(0.0, result.getLat(), 0.00001, "Latitude should remain the same");
     }

     @Test
     void testCalculateNextPosition_South_MovesCorrectly(){
        LngLat start = new LngLat(0.0, 0.0);
        double angle = 270.0;

        LngLat result = geometryService.calculateNewPosition(start, angle);

        assertEquals(0.0, result.getLng(), 0.00001, "Longitude should remain the same");
        assertEquals(-0.00015, result.getLat(), 0.00001, "Latitude should decrease by 0.00015");
     }

     @Test
     void testCalculateNextPosition_Northeast_MovesCorrectly(){
        LngLat start = new LngLat(0.0, 0.0);
        double angle = 45.0;

        LngLat result = geometryService.calculateNewPosition(start, angle);

        double expectedMove = 0.00015 / Math.sqrt(2);
        assertEquals(expectedMove, result.getLng(), 0.00001, "Longitude should increase correctly");
        assertEquals(expectedMove, result.getLat(), 0.00001, "Latitude should increase correctly");
    }


    // ==================== isInRegion() Tests ====================

    private Region createSquareRegion() {
        List<LngLat> vertices = new ArrayList<>();
        vertices.add(new LngLat(0.0, 0.0));
        vertices.add(new LngLat(1.0, 0.0));
        vertices.add(new LngLat(1.0, 1.0));
        vertices.add(new LngLat(0.0, 1.0));
        vertices.add(new LngLat(0.0, 0.0)); // Closing the polygon
        return new Region("test-squares", vertices);
    }

    @Test
    void testIsInRegion_PointInside_ReturnsTrue() {
        Region region = createSquareRegion();
        LngLat pointInside = new LngLat(0.5, 0.5);
        assertTrue(geometryService.isInRegion(pointInside, region), "Point inside the region should return true");
    }

    @Test
    void testIsInRegion_PointOutside_ReturnsFalse() {
        Region region = createSquareRegion();
        LngLat pointOutside = new LngLat(2.0,2.0);
        assertFalse(geometryService.isInRegion(pointOutside, region), "Point outside the region should return false");
    }

    @Test
    void testIsInRegion_PointOnEdge_ReturnsTrue() {
        Region region = createSquareRegion();
        LngLat pointOnEdge = new LngLat(0.5, 0.0);
        assertTrue(geometryService.isInRegion(pointOnEdge, region), "Point on the edge should return true");
    }

    @Test
    void testIsInRegion_PointOnVertex_ReturnsTrue() {
        Region region = createSquareRegion();
        LngLat pointOnVertex = new LngLat(0.0, 0.0);
        assertTrue(geometryService.isInRegion(pointOnVertex, region), "Point on the vertex should return true");
    }

    @Test
    void testIsInRegion_AssignmentExample(){
        List<LngLat> vertices = new ArrayList<>();
        vertices.add(new LngLat(-3.192473, 55.946233));
        vertices.add(new LngLat(-3.192473, 55.942617));
        vertices.add(new LngLat(-3.184319, 55.942617));
        vertices.add(new LngLat(-3.184319, 55.946233));
        vertices.add(new LngLat(-3.192473, 55.946233));

        Region region = new Region("assignment-region", vertices);
        LngLat pointInside = new LngLat(-3.188000, 55.944000);
        LngLat pointOutside = new LngLat(1.234, 1.222);
        assertTrue(geometryService.isInRegion(pointInside, region), "Point should be inside the assignment region");
        assertFalse(geometryService.isInRegion(pointOutside, region), "Point should be outside the assignment region");
    }




}

