package com.example.zavrsniapp.Objects;

public class IzvjestajPlanModel {


    private String nazivPlana, identifikatorRazdoblja, idPlana;

    private IzvjestajPlanModel() {

    }


    private IzvjestajPlanModel(String nazivPlana, String identifikatorRazdoblja) {
        this.nazivPlana = nazivPlana;
        this.identifikatorRazdoblja = identifikatorRazdoblja;
        this.idPlana = idPlana;
    }

    public String getIdPlana() {
        return idPlana;
    }

    public void setIdPlana(String idPlana) {
        this.idPlana = idPlana;
    }

    public String getNazivPlana() {
        return nazivPlana;
    }

    public void setNazivPlana(String nazivPlana) {
        this.nazivPlana = nazivPlana;
    }


    public String getIdentifikatorRazdoblja() {
        return identifikatorRazdoblja;
    }

    public void setIdentifikatorRazdoblja(String identifikatorRazdoblja) {
        this.identifikatorRazdoblja = identifikatorRazdoblja;
    }
}

