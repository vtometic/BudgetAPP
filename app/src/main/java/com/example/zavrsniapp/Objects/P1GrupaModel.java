package com.example.zavrsniapp.Objects;

import android.widget.ImageButton;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.zavrsniapp.R;

public class P1GrupaModel {

    private String p1Naziv, p1Total, p1Valuta;
    private ImageButton p1Expand;
    private RecyclerView p1Recycler;

    private P1GrupaModel() {

    }

    private P1GrupaModel(String nazivGrupe, String p1Total, String p1Valuta, ImageButton p1Expand, RecyclerView p1Recycler) {
        this.p1Naziv = nazivGrupe;
        this.p1Total = p1Total;
        this.p1Valuta = p1Valuta;
        this.p1Expand = p1Expand;
        this.p1Recycler = p1Recycler;
    }

    public String getP1Naziv() {
        return p1Naziv;
    }

    public void setP1Naziv(String p1Naziv) {
        this.p1Naziv = p1Naziv;
    }

    public String getP1Total() {
        return p1Total;
    }

    public void setP1Total(String p1Total) {
        this.p1Total = p1Total;
    }

    public String getP1Valuta() {
        return p1Valuta;
    }

    public void setP1Valuta(String p1Valuta) {
        this.p1Valuta = p1Valuta;
    }

    public ImageButton getP1Expand() {
        return p1Expand;
    }

    public void setP1Expand(ImageButton p1Expand) {
        this.p1Expand = p1Expand;
    }

    public RecyclerView getP1Recycler() {
        return p1Recycler;
    }

    public void setP1Recycler(RecyclerView p1Recycler) {
        this.p1Recycler = p1Recycler;
    }
}
