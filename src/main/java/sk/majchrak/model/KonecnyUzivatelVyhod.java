package sk.majchrak.model;

public class KonecnyUzivatelVyhod {

    private String name;
    private String state;

    public KonecnyUzivatelVyhod() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "KonecnyUzivatelVyhod{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
