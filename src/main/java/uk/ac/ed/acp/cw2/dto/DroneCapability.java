package uk.ac.ed.acp.cw2.dto;

public class DroneCapability {
  private double capacity;
  private boolean heating;
  private boolean cooling;
  private int maxMoves;
  private double costPerMove;
  private double costInitial;
  private double costFinal;

  public DroneCapability() {

  }
  public DroneCapability(double capacity, boolean heating, boolean cooling, int maxMoves, double costPerMove, double costInitial, double costFinal) {
    this.capacity = capacity;
    this.heating = heating;
    this.cooling = cooling;
    this.maxMoves = maxMoves;
    this.costPerMove = costPerMove;
    this.costInitial = costInitial;
    this.costFinal = costFinal;
  }

  public double getCapacity() {
    return capacity;
  }
  public void setCapacity(double capacity) {
    this.capacity = capacity;
  }
  public boolean isHeating() {
    return heating;
  }
  public void setHeating(boolean heating) {
    this.heating = heating; 
  }
  public boolean isCooling() {
    return cooling;
  }
  public void setCooling(boolean cooling) {
    this.cooling = cooling;
  }
  public int getMaxMoves() {
    return maxMoves;
  }
  public void setMaxMoves(int maxMoves) {
    this.maxMoves = maxMoves;
  }
  public double getCostPerMove() {
    return costPerMove;
  }
  public void setCostPerMove(double costPerMove) {
    this.costPerMove = costPerMove;
  }
  public double getCostInitial() {
    return costInitial;
  }
  public void setCostInitial(double costInitial) {
    this.costInitial = costInitial;
  }
  public double getCostFinal() {
    return costFinal;
  }
  public void setCostFinal(double costFinal) {
    this.costFinal = costFinal;
  }

  @Override
  public String toString() {
    return "DroneCapability{" +
            "capacity=" + capacity +
            ", heating=" + heating +
            ", cooling=" + cooling +
            ", maxMoves=" + maxMoves +
            ", costPerMove=" + costPerMove +
            ", costInitial=" + costInitial +
            ", costFinal=" + costFinal +
            '}';  
  }

  
}
