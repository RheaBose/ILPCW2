package uk.ac.ed.acp.cw2.dto;

import java.util.List;

public class Region {

    private String name;
    private List<LngLat> vertices;
    
    public Region() {

    }
    public Region(String name, List<LngLat> vertices) {
        this.name = name;
        this.vertices = vertices;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<LngLat> getVertices() {
        return vertices;
    }
    public void setVertices(List<LngLat> vertices) {
        this.vertices = vertices;
    }
    public boolean isValid() {
        if (name == null || name.isEmpty()) {
            return false;
        }
        if (vertices == null || vertices.size() < 3) {
            return false;
        }

        for (LngLat vertex : vertices) {
            if (vertex == null || !vertex.isValid()) {
                return false;
            }
        }

        LngLat first = vertices.get(0);
        LngLat last = vertices.get(vertices.size() - 1);

        double epsilon = 1e-9;
        boolean lngMatch = Math.abs(first.getLng() - last.getLng()) < epsilon;
        boolean latMatch = Math.abs(first.getLat() - last.getLat()) < epsilon;

        return lngMatch && latMatch;
    }


    @Override
    public String toString() {
        return "Region{" +
                "name='" + name + '\'' +
                ", vertices=" + vertices +
                '}';
    }


}
