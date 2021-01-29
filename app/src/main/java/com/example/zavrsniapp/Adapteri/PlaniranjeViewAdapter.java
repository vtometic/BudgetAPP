package com.example.zavrsniapp.Adapteri;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;

import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zavrsniapp.KreiranjeFragment;

import com.example.zavrsniapp.Objects.P1GrupaModel;
import com.example.zavrsniapp.Objects.P2StavkaModel;

import com.example.zavrsniapp.R;
import com.example.zavrsniapp.SharedPref;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class PlaniranjeViewAdapter extends FirestoreRecyclerAdapter<P1GrupaModel, PlaniranjeViewAdapter.PlaniranjeHolder> {
    private OnItemClickListener listener;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    Context mContext;
    private P2StavkaViewAdapter adapter2;

    public static KreiranjeFragment newInstance() {

        return new KreiranjeFragment();
    }

    public PlaniranjeViewAdapter(@NonNull FirestoreRecyclerOptions<P1GrupaModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PlaniranjeHolder holder, int position, @NonNull P1GrupaModel model) {

        SharedPref sharedPref = new SharedPref(holder.gRecycler.getContext());
        holder.gNaziv.setText(model.getP1Naziv());
        holder.gValuta.setText(sharedPref.loadCurrency());
        holder.gRecycler.findViewById(R.id.p1Recycler);
        model.setP1Recycler(holder.gRecycler.findViewById(R.id.p1Recycler));

        String idGrupe = holder.gNaziv.getText().toString();
        Query query = fStore.collection("korisnici").document(sharedPref.loadUserID())
                .collection("planovi").document(sharedPref.loadPlanID()).collection("grupeNaPlanu")
                .document(idGrupe).collection("stavke");

        FirestoreRecyclerOptions<P2StavkaModel> options = new FirestoreRecyclerOptions.Builder<P2StavkaModel>()
                .setQuery(query, P2StavkaModel.class).build();


        adapter2 = new P2StavkaViewAdapter(options);
        holder.gRecycler.setLayoutManager(new LinearLayoutManager(holder.gRecycler.getContext()));
        holder.gRecycler.setAdapter(adapter2);
        adapter2.startListening();
        getSum(query, holder, idGrupe, sharedPref, adapter2);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter2.deleteItem(viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(holder.gRecycler);
        adapter2.setOnItemClickListener((documentSnapshot, position1) -> {
            P2StavkaModel stavka = documentSnapshot.toObject(P2StavkaModel.class);
        });


    }


    @NonNull
    @Override
    public PlaniranjeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_p1, parent, false);
        mContext = parent.getContext();
        return new PlaniranjeHolder(v);
    }

    public void deleteItem(int position, RecyclerView.ViewHolder viewHolder) {
        SharedPref sharedpref = new SharedPref(viewHolder.itemView.getContext());
        String ng = getSnapshots().getSnapshot(position).getId();
        sharedpref.setGroupID(ng);
        CollectionReference cl = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi").document(sharedpref.loadPlanID())
                .collection("grupeNaPlanu").document(sharedpref.loadGroupID()).collection("stavke");
        cl.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    sharedpref.setStavkaID((document.getId()));
                    DocumentReference dr1 = fStore.collection("korisnici").document(sharedpref.loadUserID())
                            .collection("planovi").document(sharedpref.loadPlanID())
                            .collection("grupeNaPlanu").document(sharedpref.loadGroupID()).collection("stavke").document(sharedpref.loadStavkaID());
                    dr1.delete();
                }
            }
        });
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public class PlaniranjeHolder extends RecyclerView.ViewHolder {
        TextView gNaziv, gTotal, gValuta;
        ImageButton gExpand;
        RecyclerView gRecycler;

        public PlaniranjeHolder(@NonNull View itemView) {
            super(itemView);
            gNaziv = itemView.findViewById(R.id.p1Grupa);
            gTotal = itemView.findViewById(R.id.p1GrupaUkupno);
            gValuta = itemView.findViewById(R.id.p1GrupaUkupnoOznaka);
            gExpand = itemView.findViewById(R.id.p1Expand);
            gRecycler = itemView.findViewById(R.id.p1Recycler);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    float deg = (gExpand.getRotation() == 90F) ? 0F : 90F;
                    gExpand.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                    if (gRecycler.getVisibility() == View.GONE) {
                        gRecycler.setVisibility(View.VISIBLE);
                    } else {
                        gRecycler.setVisibility(View.GONE);
                    }

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

    private void getSum(Query query, PlaniranjeHolder holder, String idGrupe, SharedPref sharedPref, FirestoreRecyclerAdapter adapter) {
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    double total = Double.parseDouble(holder.gTotal.getText().toString());
                    double iznosStavke;
                    iznosStavke = Double.parseDouble(String.valueOf(document.get("iznosStavke")));
                    total = total + iznosStavke;
                    holder.gTotal.setText(formatIznos(total));
                }
            }
        });
    }

    private String formatIznos(double iznos) {
        Double rounded = new BigDecimal(iznos).setScale(2, RoundingMode.HALF_UP).doubleValue();
        DecimalFormat df = new DecimalFormat("################.00");
        String rez = String.valueOf(df.format(rounded));
        rez = rez.replace(",", ".");
        return rez;
    }
}
