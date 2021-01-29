package com.example.zavrsniapp.Adapteri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zavrsniapp.Objects.P2StavkaModel;
import com.example.zavrsniapp.R;
import com.example.zavrsniapp.SharedPref;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class P2StavkaViewAdapter extends FirestoreRecyclerAdapter<P2StavkaModel, P2StavkaViewAdapter.StavkaHolder> {
    private OnItemClickListener listener;

    public P2StavkaViewAdapter(@NonNull FirestoreRecyclerOptions<P2StavkaModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StavkaHolder holder, int position, @NonNull P2StavkaModel model) {

        SharedPref sharedPref = new SharedPref(holder.gNaziv.getContext());
        holder.gNaziv.setText(model.getNazivStavke());
        holder.gIznos.setText(formatIznos(Double.parseDouble(model.getIznosStavke())));
        holder.gValuta.setText(sharedPref.loadCurrency());
    }

    @NonNull
    @Override
    public StavkaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_p2, parent, false);

        return new StavkaHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();


    }

    public class StavkaHolder extends RecyclerView.ViewHolder {
        TextView gNaziv, gIznos, gValuta;


        public StavkaHolder(@NonNull View itemView) {
            super(itemView);
            gNaziv = itemView.findViewById(R.id.p2Naziv);
            gIznos = itemView.findViewById(R.id.p2Iznos);
            gValuta = itemView.findViewById(R.id.p2Oznaka);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);

                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    private String formatIznos(double iznos) {
        Double rounded = new BigDecimal(iznos).setScale(2, RoundingMode.HALF_UP).doubleValue();
        DecimalFormat df = new DecimalFormat("################.00");
        df.setDecimalSeparatorAlwaysShown(true);
        String rez = String.valueOf(df.format(rounded));
        rez = rez.replace(",", ".");
        return rez;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}