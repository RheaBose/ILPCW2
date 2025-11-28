package uk.ac.ed.acp.cw2.dto;

public class QueryAttribute {
    private String attribute;
    private String operator;
    private Object value;

    public QueryAttribute() {
    }
    public QueryAttribute(String attribute, String operator, Object value) {
        this.attribute = attribute;
        this.operator = operator;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isValid(){
        if (attribute == null || attribute.isEmpty()) return false;
        if (operator == null || operator.isEmpty()) return false;
        if (value == null) return false;
        return operator.equals("=") || operator.equals("!=") || operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals(">=");
    }
    
    @Override
    public String toString() {
        return "QueryAttribute{" +
                "attribute='" + attribute + '\'' +
                ", operator='" + operator + '\'' +
                ", value=" + value +
                '}';
    }
}
