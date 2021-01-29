package com.example.zavrsniapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class NewicFragment extends Fragment {

    ImageView imgNazad;
    CalendarView calendarView;
    SharedPref sharedpref;
    TextInputLayout dayPickerNewic;
    TextView txtBtnOK;


    TextView btnUredu, txtOdabir;
    ImageButton imgBtnx;
    MaterialButton btnDodaj;

    TextInputLayout nazivNewic, iznosNewic, datumNewic;
    RelativeLayout groupPicker;
    Spinner spinnerGrupeNewic;
    ArrayAdapter<String> adapterGrupe;
    ArrayList<String> spinnerGrupeList;

    private RelativeLayout relativeLayout;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;


    public static NewicFragment newInstance() {

        return new NewicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedpref = new SharedPref(getActivity());
        if (sharedpref.loadNightModeState()) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }

        View v = inflater.inflate(R.layout.fragment_newic, container, false);

        relativeLayout = v.findViewById(R.id.podatciNewic);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        dayPickerNewic = v.findViewById(R.id.datePickerNewic);
        nazivNewic = v.findViewById(R.id.txtNazivNewic);
        iznosNewic = v.findViewById(R.id.txtIznosNewic);
        dayPickerNewic.getEditText().setOnClickListener(v13 -> setDateDayPick(inflater));

        groupPicker = v.findViewById(R.id.spinnerLayoutGrupa);
        spinnerGrupeNewic = v.findViewById(R.id.spinnerGrupeNewic);
        spinnerGrupeNewic.setGravity(7);
        spinnerGrupeList = new ArrayList<>();
        adapterGrupe = new ArrayAdapter<String>(getActivity(),
                R.layout.item_spinner,
                spinnerGrupeList);
        spinnerGrupeNewic.setAdapter(adapterGrupe);
        dohvatiGrupe();
        spinnerGrupeNewic.getSelectedItem();
        spinnerGrupeNewic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dohvatiSvojstvo(spinnerGrupeNewic.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnDodaj = v.findViewById(R.id.dodajNewic);
        btnDodaj.setOnClickListener(v12 -> {
            dohvatiSvojstvo(spinnerGrupeNewic.getSelectedItem().toString());
            if (!validateIznos() | !validateNaziv() | !validateDay()) {
                return;
            }

            Toast.makeText(getActivity(), spinnerGrupeNewic.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            Map<String, Object> stavka = new HashMap<>();
            stavka.put("nazivStavke", nazivNewic.getEditText().getText().toString());
            stavka.put("iznosStavke", iznosNewic.getEditText().getText().toString());
            stavka.put("datumStavke", dayPickerNewic.getEditText().getText().toString());
            stavka.put("idGrupe", spinnerGrupeNewic.getSelectedItem().toString());
            stavka.put("svojstvoStavke", sharedpref.loadSvojstvo());
            DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("stavke").document();
            dr.set(stavka).addOnSuccessListener(aVoid ->
                    Snackbar.make((View) container.getParent(), "Stavka unesena", Snackbar.LENGTH_LONG).show());
            refreshFragment();
        });

        imgNazad = v.findViewById(R.id.natrag);
        imgNazad.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        return v;
    }

    private void setDateDayPick(LayoutInflater inflater) {
        final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
        final View mView = inflater.inflate(R.layout.dialog_date_picker, null);
        mBulider.setView(mView);
        final AlertDialog dialog = mBulider.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        calendarView = mView.findViewById(R.id.calendarDialog);
        final Calendar calendar = Calendar.getInstance();
        calendarView.setDate(calendar.getTimeInMillis());
        calendarView.setOnDateChangeListener((view, y, m, d) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.YEAR, y);
            calendar1.set(Calendar.MONTH, m);
            calendar1.set(Calendar.DATE, d);
            String dateString = String.valueOf(DateFormat.format("dd.MM.yyyy", calendar1));
            dayPickerNewic.getEditText().setText(dateString);
        });
        txtBtnOK = mView.findViewById(R.id.dialogOK);
        txtBtnOK.setOnClickListener(v -> dialog.dismiss());

    }


    private void dohvatiGrupe() {
        Query colRef = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("grupe");
        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    spinnerGrupeList.add(document.get("nazivGrupe").toString());

                }
                adapterGrupe.notifyDataSetChanged();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "Greška", Toast.LENGTH_SHORT).show());
    }

    private String dohvatiSvojstvo(String nazivGrupe) {
        Query colRef = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("grupe").whereEqualTo("nazivGrupe", nazivGrupe);
        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.get("nazivGrupe").equals(nazivGrupe)) {
                        String svojstvo = document.get("svojstvoGrupe").toString();
                        sharedpref.setSvojstvo(svojstvo);
                    }
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "Greška", Toast.LENGTH_SHORT).show());

        return sharedpref.loadSvojstvo();
    }

    private boolean validateNaziv() {
        String naziv = nazivNewic.getEditText().getText().toString();
        if (naziv.isEmpty()) {
            nazivNewic.setError("Unesite naziv stavke ");
            return false;
        } else {
            nazivNewic.setError(null);
            return true;
        }
    }

    private boolean validateIznos() {
        String iznos = iznosNewic.getEditText().getText().toString();
        if (iznos.isEmpty()) {
            iznosNewic.setError("Unesite iznos stavke");
            return false;
        } else {
            iznosNewic.setError(null);
            formatIznos(Double.parseDouble(iznosNewic.getEditText().getText().toString()));
            return true;
        }
    }

    private Boolean validateDay() {
        String pickeddat = dayPickerNewic.getEditText().getText().toString().trim();
        if (pickeddat.isEmpty()) {
            dayPickerNewic.setError("Odaberite datum");
            return false;
        } else {
            dayPickerNewic.setError(null);
            return true;
        }
    }

    private String formatIznos(double iznos) {
        sharedpref = new SharedPref(getActivity());
        Double rounded = new BigDecimal(iznos).setScale(2, RoundingMode.HALF_UP).doubleValue();
        DecimalFormat df = new DecimalFormat("################.00");
        String rez = String.valueOf(df.format(rounded));
        rez = rez.replace(",", ".");
        return rez;
    }


    private void refreshFragment() {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, NewicFragment.newInstance());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
