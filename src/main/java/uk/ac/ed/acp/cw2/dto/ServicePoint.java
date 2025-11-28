package uk.ac.ed.acp.cw2.dto;

public class ServicePoint {
    private String name;
    private int id;
    private LngLat location;  // ← Use your existing LngLat!
    
    public ServicePoint() {
    }
    
    public ServicePoint(String name, int id, LngLat location) {
        this.name = name;
        this.id = id;
        this.location = location;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LngLat getLocation() {
        return location;
    }
    
    public void setLocation(LngLat location) {
        this.location = location;
    }
    
    @Override
    public String toString() {
        return "ServicePoint{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", location=" + location +
                '}';
    }
}
