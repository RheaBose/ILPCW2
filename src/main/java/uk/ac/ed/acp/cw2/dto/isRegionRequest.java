package uk.ac.ed.acp.cw2.dto;

public class isRegionRequest {

    private LngLat position;
    private Region region;

    public isRegionRequest() {

    }
    public isRegionRequest(LngLat position, Region region) {
        this.position = position;
        this.region = region;
    }
    public LngLat getPosition() {
        return position;
    }
    public void setPosition(LngLat position) {
        this.position = position;
    }
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
    public boolean isValid() {
        if (position == null || !position.isValid()) {
            return false;
        }
        if (region == null || !region.isValid()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "isRegionRequest{" +
                "position=" + position +
                ", region=" + region +
                '}';    
    }


    
}
