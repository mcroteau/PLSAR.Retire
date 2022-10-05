package oceanblue.model;

public class VariablePosition {
    Integer position;
    String value;

    public VariablePosition(Integer position, String value){
        this.position = position;
        this.value = value;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
