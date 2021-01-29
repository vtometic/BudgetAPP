package com.example.zavrsniapp.Objects;

public class GrupaModel {

    private String nazivGrupe, opisGrupe, svojstvoGrupe;

    private GrupaModel() {

    }

    private GrupaModel(String nazivGrupe, String opisGrupe, String svojstvoGrupe) {
        this.nazivGrupe = nazivGrupe;
        this.opisGrupe = opisGrupe;
        this.svojstvoGrupe = svojstvoGrupe;
    }


    public String getNazivGrupe() {
        return nazivGrupe;
    }

    public void setNazivGrupe(String nazivGrupe) {
        this.nazivGrupe = nazivGrupe;
    }

    public String getOpisGrupe() {
        return opisGrupe;
    }

    public void setOpisGrupe(String opisGrupe) {
        this.opisGrupe = opisGrupe;
    }

    public String getSvojstvoGrupe() {
        return svojstvoGrupe;
    }

    public void setSvojstvoGrupe(String svojstvoGrupe) {
        this.svojstvoGrupe = svojstvoGrupe;
    }
}
