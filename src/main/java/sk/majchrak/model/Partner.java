package sk.majchrak.model;

import java.util.List;

public class Partner {

    private Integer id;
    private String ico;
    private String name;
    private String state;
    private List<KonecnyUzivatelVyhod> konecnyUzivatelVyhodList;


    public Partner() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
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

    public List<KonecnyUzivatelVyhod> getKonecnyUzivatelVyhodList() {
        return konecnyUzivatelVyhodList;
    }

    public void setKonecnyUzivatelVyhodList(List<KonecnyUzivatelVyhod> konecnyUzivatelVyhodList) {
        this.konecnyUzivatelVyhodList = konecnyUzivatelVyhodList;
    }

    @Override
    public String toString() {
        return "Partner{" +
                "id=" + id +
                ", ico='" + ico + '\'' +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", konecnyUzivatelVyhodList=" + konecnyUzivatelVyhodList +
                '}';
    }
}
