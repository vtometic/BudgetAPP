package com.example.zavrsniapp.Adapteri;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zavrsniapp.R;
import com.example.zavrsniapp.SharedPref;

import java.util.ArrayList;

public class PlanoviViewAdapter extends RecyclerView.Adapter<PlanoviViewAdapter.Viewholder> {

    SharedPref sharedpref;
    Button btnPotvrdi;
    private ArrayList<String> nazivi;
    private ArrayList<String> oznake;
    private Context mContext;
    int selectedPosition;

    public PlanoviViewAdapter(Context mContext, ArrayList<String> nazivi, ArrayList<String> oznake) {
        this.nazivi = nazivi;
        this.oznake = oznake;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_item, parent, false);
        Viewholder holder = new Viewholder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        int color = holder.valItem.getResources().getColor(R.color.cardcolor);
        int colorA = holder.valItem.getResources().getColor(R.color.colorPrimaryDark);
        sharedpref = new SharedPref(mContext);
        holder.valItem.setBackgroundColor(color);
        holder.valNaziv.setText(nazivi.get(position));
        holder.valOznaka.setText(oznake.get(position));
        selectedPosition = sharedpref.loadPlanPosition();
        if (selectedPosition == position) {
            holder.valItem.setBackgroundColor(colorA);
        } else
            holder.valItem.setBackgroundColor(color);

        holder.valItem.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();

            sharedpref.setPlanID(holder.valOznaka.getText().toString());
            sharedpref.setPlanPosition(selectedPosition);
        });

    }

    @Override
    public int getItemCount() {
        return nazivi.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        RelativeLayout valItem;
        TextView valNaziv, valOznaka;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            valNaziv = itemView.findViewById(R.id.valNaziv);
            valOznaka = itemView.findViewById(R.id.valOznaka);
            valItem = itemView.findViewById(R.id.valItem);
        }
    }

}