package uk.ac.ed.acp.cw2.dto;

public class NextPositionRequest {
    
    private LngLat start;
    private double angle;

    public NextPositionRequest() {

    }   
    public NextPositionRequest(LngLat start, double angle) {
        this.start = start;
        this.angle = angle;
    }

    public LngLat getStart() {
        return start;
    }
    public void setStart(LngLat start) {
        this.start = start;
    }
    public double getAngle() {
        return angle;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public boolean isValid() {
        if (start == null || !start.isValid()) {
        return false;
    }
    
    
    if (Double.isNaN(angle) || Double.isInfinite(angle)) {
        return false;
    }
    
    return true;
    }   

    @Override
    public String toString() {
        return "NextPositionRequest{" +
                "start=" + start +
                ", angle=" + angle +
                '}';
    }
}
