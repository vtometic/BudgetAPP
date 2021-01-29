package com.example.zavrsniapp.Objects;

public class P2StavkaModel {

    private String nazivStavke, iznosStavke, valutaStavke;

    private P2StavkaModel() {

    }

    private P2StavkaModel(String nazivStavke, String iznosStavke, String valutaStavke) {
        this.nazivStavke = nazivStavke;
        this.iznosStavke = iznosStavke;
        this.valutaStavke = valutaStavke;
    }

    public String getNazivStavke() {
        return nazivStavke;
    }

    public void setNazivStavke(String nazivStavke) {
        this.nazivStavke = nazivStavke;
    }

    public String getIznosStavke() {
        return iznosStavke;
    }

    public void setIznosStavke(String iznosStavke) {
        this.iznosStavke = iznosStavke;
    }

    public String getValutaStavke() {
        return valutaStavke;
    }

    public void setValutaStavke(String valutaStavke) {
        this.valutaStavke = valutaStavke;
    }
}
