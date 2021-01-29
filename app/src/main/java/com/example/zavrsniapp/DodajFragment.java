package com.example.zavrsniapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zavrsniapp.Adapteri.GrupaViewAdapter;
import com.example.zavrsniapp.Objects.GrupaModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DodajFragment extends Fragment {

    SharedPref sharedpref;
    private MaterialButton btnOdustani, btnKreiraj;
    private TextInputLayout mNaziv, mOpis;
    private RadioGroup radioGroup;
    private RelativeLayout relativeLayout;

    private FirebaseFirestore fStore;


    private GrupaViewAdapter adapter;

    public static DodajFragment newInstance() {

        return new DodajFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedpref = new SharedPref(Objects.requireNonNull(getActivity()));
        if (sharedpref.loadNightModeState()) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }

        View v = inflater.inflate(R.layout.fragment_dodaj, container, false);

        fStore = FirebaseFirestore.getInstance();

        FloatingActionButton fbtnNova = v.findViewById(R.id.fbtnDodaj);
        fbtnNova.setOnClickListener(v12 -> {
            final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
            final View mView = inflater.inflate(R.layout.dialog_add_group, null);
            mBulider.setView(mView);
            final AlertDialog dialog = mBulider.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            relativeLayout = v.findViewById(R.id.group_podatci);
            mOpis = mView.findViewById(R.id.txtOpisGrupe);
            mNaziv = mView.findViewById(R.id.txtNazivGrupe);
            radioGroup = mView.findViewById(R.id.opcije);
            btnOdustani = mView.findViewById(R.id.adgOdustani);
            btnOdustani.setOnClickListener(v13 -> dialog.dismiss());
            btnKreiraj = mView.findViewById(R.id.adgKreiraj);
            btnKreiraj.setOnClickListener(v14 -> {
                final String svojstvo =
                        ((RadioButton) mView.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                saveToBase(Objects.requireNonNull(mNaziv.getEditText()).getText().toString(), Objects.requireNonNull(mOpis.getEditText()).getText().toString(), svojstvo, dialog);
            });
        });

        ImageView imgNazad = v.findViewById(R.id.natrag);
        imgNazad.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        initRecycler(v, inflater);

        return v;
    }

    private void initRecycler(View v, LayoutInflater inflater) {
        Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("grupe");
        FirestoreRecyclerOptions<GrupaModel> options = new FirestoreRecyclerOptions.Builder<GrupaModel>()
                .setQuery(query, GrupaModel.class).build();
        adapter = new GrupaViewAdapter(options);
        RecyclerView groupRecycler = v.findViewById(R.id.recyclerGroups);
        groupRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupRecycler.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(groupRecycler);

        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            GrupaModel grupa = documentSnapshot.toObject(GrupaModel.class);
            final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
            final View mView = inflater.inflate(R.layout.dialog_add_group, null);
            mBulider.setView(mView);
            final AlertDialog dialog = mBulider.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            TextView adgNaslov = mView.findViewById(R.id.adgNaslov);
            adgNaslov.setText("Uredi podatke");
            mOpis = mView.findViewById(R.id.txtOpisGrupe);
            assert grupa != null;
            Objects.requireNonNull(mOpis.getEditText()).setText(grupa.getOpisGrupe());
            mNaziv = mView.findViewById(R.id.txtNazivGrupe);
            Objects.requireNonNull(mNaziv.getEditText()).setText(grupa.getNazivGrupe());
            radioGroup = mView.findViewById(R.id.opcije);
            RadioButton Prihod = mView.findViewById(R.id.Prihod);
            RadioButton Trosak = mView.findViewById(R.id.Trošak);
            if (Prihod.getText().equals(grupa.getSvojstvoGrupe())) {
                Prihod.setChecked(true);
                Trosak.setChecked(false);
            } else {
                Trosak.setChecked(true);
                Prihod.setChecked(false);
            }
            btnOdustani = mView.findViewById(R.id.adgOdustani);
            btnOdustani.setOnClickListener(v13 -> dialog.dismiss());
            btnKreiraj = mView.findViewById(R.id.adgKreiraj);
            btnKreiraj.setText("Spremi");
            btnKreiraj.setOnClickListener(v14 -> {
                final String svojstvo =
                        ((RadioButton) mView.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                updateBase(mNaziv.getEditText().getText().toString()
                        , mOpis.getEditText().getText().toString()
                        , svojstvo
                        , documentSnapshot.getId()
                        , dialog);

            });
        });

    }

    private void saveToBase(String naziv, String opis, String svojstvo, AlertDialog dialog) {
        if (validateNaziv() | validateOpis()) {
            return;
        }
        Map<String, Object> grupa = new HashMap<>();
        grupa.put("nazivGrupe", naziv);
        grupa.put("opisGrupe", opis);
        grupa.put("svojstvoGrupe", svojstvo);
        DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("grupe").document();
        dr.set(grupa).addOnSuccessListener(aVoid ->
                Snackbar.make((View) relativeLayout.getParent(), "Grupa unesena", Snackbar.LENGTH_LONG).show())
                .addOnFailureListener(e -> {
                    String error = e.getMessage();
                    Snackbar.make((View) relativeLayout.getParent(), "Greška: " + error, Snackbar.LENGTH_LONG).show();
                });
        dialog.dismiss();
    }

    private void updateBase(String naziv, String opis, String svojstvo, String docid, AlertDialog dialog) {
        if (validateNaziv() | validateOpis()) {
            return;
        }
        DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("grupe").document(docid);
        dr.update("nazivGrupe", naziv);
        dr.update("opisGrupe", opis);
        dr.update("svojstvoGrupe", svojstvo);
        dialog.dismiss();
    }

    private boolean validateNaziv() {
        String naziv = Objects.requireNonNull(mNaziv.getEditText()).getText().toString();
        if (naziv.isEmpty()) {
            mNaziv.setError("Unesite naziv grupe");
            return true;
        } else
            mNaziv.setError(null);
        return false;

    }

    private boolean validateOpis() {
        String opis = Objects.requireNonNull(mOpis.getEditText()).getText().toString();
        if (opis.length() > mOpis.getCounterMaxLength()) {
            mOpis.setError("Predugačak opis");
            return true;
        } else
            mOpis.setError(null);
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}