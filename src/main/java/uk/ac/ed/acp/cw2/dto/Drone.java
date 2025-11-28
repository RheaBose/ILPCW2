package uk.ac.ed.acp.cw2.dto;

public class Drone {
  private int id;
  private String name;
  private DroneCapability capability;

  public Drone() {

  }
  public Drone(int id, String name, DroneCapability capability) {
    this.id = id;
    this.name = name;
    this.capability = capability; 
  }
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public DroneCapability getCapability() {
    return capability;
  }
  public void setCapability(DroneCapability capability) {
    this.capability = capability;
  }
  public boolean hasCooling() {
    return capability != null && capability.isCooling();
  }
  public boolean hasHeating() {
    return capability != null && capability.isHeating();
  }

  @Override
  public String toString() {
    return "Drone{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", capability=" + capability +
      '}';
  }
  
  
}
