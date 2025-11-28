package uk.ac.ed.acp.cw2.dto;

public class MedDispatchRec {

  private int id;
  private String date;
  private String time;
  private LngLat delivery;
  private Requirements requirements;
  public MedDispatchRec() {
  }
  public MedDispatchRec(int id, String date, String time, Requirements requirements) {
    this.id = id;
    this.date = date;
    this.time = time;
    this.requirements = requirements;
  }
  public int getId() {
    return id;  
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getDate() {
    return date;
  }
  public void setDate(String date) {
    this.date = date;
  }
  public String getTime() {
    return time;
  }
  public void setTime(String time) {
    this.time = time;
  }
  public LngLat getDelivery() {
        return delivery;
    }
  public void setDelivery(LngLat delivery) {
        this.delivery = delivery;
    }
  public Requirements getRequirements() {
    return requirements;
  }
  public void setRequirements(Requirements requirements) {
    this.requirements = requirements;
  }

  public boolean isValid(){
    if (id <= 0 || requirements == null ) {
      return false;
    }
    if (delivery != null && !delivery.isValid()) {
            return false;
        }
    return requirements.isValid();
  }

  @Override
  public String toString() {
    return "MedDispatchRec{" +
      "id=" + id +
      ", date='" + date + '\'' +
      ", time='" + time + '\'' +
      ", requirements=" + requirements +
      '}';
  }
  
}
