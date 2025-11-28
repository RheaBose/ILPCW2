package uk.ac.ed.acp.cw2.dto;

public class IsCloseToRequest {

    private LngLat position1;
    private LngLat position2;

    public IsCloseToRequest(){

    }
    public IsCloseToRequest(LngLat position1, LngLat position2) {
        this.position1 = position1;
        this.position2 = position2;
    }

    public LngLat getPosition1() {
        return position1;
    }
    public void setPosition1(LngLat position1) {
        this.position1 = position1;
    }
    public LngLat getPosition2() {
        return position2;
    }
    public void setPosition2(LngLat position2) {
        this.position2 = position2;
    }

    public boolean isValid() {
        return position1 != null && position1.isValid() && position2 != null && position2.isValid();
    }
    
}
