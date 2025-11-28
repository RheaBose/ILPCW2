package uk.ac.ed.acp.cw2.dto;

public class LngLat {
    private double lng;
    private double lat;

    public LngLat() {
    }
    public LngLat(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public double getLat() {
        return lat; 
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    public boolean isValid() {
        
        if (Double.isNaN(lat) || Double.isNaN(lng)) {
            return false;
        }
        
        
        if (Double.isInfinite(lat) || Double.isInfinite(lng)) {
            return false;
        }
        
        
        if (lat < -90.0 || lat > 90.0) {
            return false;
        }
        
        
        if (lng < -180.0 || lng > 180.0) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "LngLat{" +
                "lng=" + lng +
                ", lat=" + lat +
                '}';    
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false; 
        LngLat lngLat = (LngLat) o;
        return Double.compare(lngLat.lng, lng) == 0 && Double.compare(lngLat.lat, lat) == 0;
    }
    
}
