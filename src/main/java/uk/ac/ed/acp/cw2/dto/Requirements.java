package uk.ac.ed.acp.cw2.dto;

public class Requirements {
  private double capacity;
  private Boolean heating;
  private Boolean cooling;
  private Double maxCost;
  public Requirements() {

  }
  public Requirements(double capacity, Boolean heating, Boolean cooling, Double maxCost) {
    this.capacity = capacity;
    this.heating = heating;
    this.cooling = cooling;
    this.maxCost = maxCost;
  }
  public double getCapacity() {
    return capacity;
  }
  public void setCapacity(double capacity) {
    this.capacity = capacity;
  }
  public Boolean getHeating() {
    return heating;
  }
  public void setHeating(Boolean heating) {
    this.heating = heating;
  }
  public boolean isHeatingRequired() {
    return heating != null && heating;
  }
  public boolean isCoolingRequired() {
    return cooling != null && cooling;
  }
  public Boolean getCooling() {
    return cooling;
  }
  public void setCooling(Boolean cooling) {
    this.cooling = cooling;
  }
  public boolean hasMaxCost() {
    return maxCost != null;
  }
  public void setMaxCost(Double maxCost) {
    this.maxCost = maxCost;
  }
  public Double getMaxCost() {
    return maxCost;
  }
  public boolean hasMaxCostConstraint() {
    return maxCost != null;
}

  public boolean isValid() {
    if (capacity < 0) {
      return false;
    }
    if (isCoolingRequired() && isHeatingRequired()) {
      return false;
    }
    if (hasMaxCost() && maxCost < 0) {
      return false;
    }
    return true;
  }
  @Override
  public String toString() {
    return "Requirements{" +
      "capacity=" + capacity +
      ", heating=" + heating +
      ", cooling=" + cooling +
      ", maxCost=" + maxCost +
      '}';
  }

  
}
