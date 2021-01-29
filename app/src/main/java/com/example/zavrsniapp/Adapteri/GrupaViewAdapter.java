package com.example.zavrsniapp.Adapteri;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zavrsniapp.Objects.GrupaModel;
import com.example.zavrsniapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class GrupaViewAdapter extends FirestoreRecyclerAdapter<GrupaModel, GrupaViewAdapter.GrupaHolder> {
    private OnItemClickListener listener;

    public GrupaViewAdapter(@NonNull FirestoreRecyclerOptions<GrupaModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GrupaHolder holder, int position, @NonNull GrupaModel model) {
        holder.gNaziv.setText(model.getNazivGrupe());
        holder.gOpis.setText(model.getOpisGrupe());
        holder.gSvojstvo.setText(model.getSvojstvoGrupe());

    }

    @NonNull
    @Override
    public GrupaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grupa, parent, false);
        return new GrupaHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();


    }

    public class GrupaHolder extends RecyclerView.ViewHolder {
        TextView gNaziv, gOpis, gSvojstvo;

        public GrupaHolder(@NonNull View itemView) {
            super(itemView);
            gNaziv = itemView.findViewById(R.id.nazivGrupe);
            gOpis = itemView.findViewById(R.id.opisGrupe);
            gSvojstvo = itemView.findViewById(R.id.svojstvoGrupe);

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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
