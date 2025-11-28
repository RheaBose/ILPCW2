package uk.ac.ed.acp.cw2.dto;

import java.util.List;

public class RestrictedArea {
    private String name;
    private int id;
    private List<LngLat> vertices; 
    
    public RestrictedArea() {
    }
    
    public RestrictedArea(String name, int id, List<LngLat> vertices) {
        this.name = name;
        this.id = id;
        this.vertices = vertices;
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
    
    public List<LngLat> getVertices() {
        return vertices;
    }
    
    public void setVertices(List<LngLat> vertices) {
        this.vertices = vertices;
    }
    
   
    public boolean contains(LngLat position) {
        Region region = new Region(name, vertices);
        return region.isValid() && isPointInRegion(position, region);
    }
    
    // Copy isPointInRegion logic here or reference GeometryService
    private boolean isPointInRegion(LngLat point, Region region) {
        
        return false;  
    }
    
    @Override
    public String toString() {
        return "RestrictedArea{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", vertices=" + vertices +
                '}';
    }
}