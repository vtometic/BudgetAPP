package com.example.zavrsniapp;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.zavrsniapp.Adapteri.ValutaViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class PostavkeFragment extends Fragment {

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    private ImageButton imgCurrencyPicker;
    private ArrayList<String> mNazivi = new ArrayList<>();
    private ArrayList<String> mOznake = new ArrayList<>();
    private RecyclerView currencyRecycler;
    private ArrayList<String> NotificationDates;


    SharedPref sharedpref;

    public static PostavkeFragment newInstance() {

        return new PostavkeFragment();
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
        View v = inflater.inflate(R.layout.fragment_postavke, container, false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        SwitchMaterial darkmode = v.findViewById(R.id.switch_dark_mode);
        if (sharedpref.loadNightModeState()) {
            darkmode.setChecked(true);
        }
        darkmode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sharedpref.setNightModeState(true);
                refresh();
            } else {
                sharedpref.setNightModeState(false);
                refresh();
            }
        });
        SwitchMaterial notifications = v.findViewById(R.id.switch_notifications);
        if (sharedpref.loadNotifications()) {
            notifications.setChecked(true);
        }
        notifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sharedpref.setNotifications(true);
                getPlans();
            } else {
                sharedpref.setNotifications(false);
                refresh();
            }
        });

        imgCurrencyPicker = v.findViewById(R.id.currencyPicker);
        initValute();
        initRecycler(v);
        RelativeLayout showCurrency = v.findViewById(R.id.currency);
        showCurrency.setOnClickListener(v12 -> {
            float deg = (imgCurrencyPicker.getRotation() == 90F) ? 0F : 90F;
            imgCurrencyPicker.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
            if (currencyRecycler.getVisibility() == View.GONE) {
                currencyRecycler.setVisibility(View.VISIBLE);
            } else {
                currencyRecycler.setVisibility(View.GONE);
            }

        });

        ImageView imgNazad = v.findViewById(R.id.natrag);
        imgNazad.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        return v;
    }

    private void refresh() {

        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, PostavkeFragment.newInstance());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void initValute() {

        mNazivi.add("Hrvatska kuna");
        mOznake.add("kn");

        mNazivi.add("Euro");
        mOznake.add("€");

        mNazivi.add("Američki dolar");
        mOznake.add("$");

        mNazivi.add("Kanadski dolar");
        mOznake.add("KD");

        mNazivi.add("Australijski dolar");
        mOznake.add("AUD");

        mNazivi.add("Češka kruna");
        mOznake.add("CZK");

        mNazivi.add("Funta");
        mOznake.add("£");

        mNazivi.add("Japanski jen");
        mOznake.add("¥");

    }


    private void initRecycler(View v) {
        currencyRecycler = v.findViewById(R.id.currencyRecycler);
        ValutaViewAdapter adapter = new ValutaViewAdapter(getActivity(), mNazivi, mOznake);
        currencyRecycler.setAdapter(adapter);
        currencyRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getPlans() {
        CollectionReference collectionReference = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                sharedpref.setMinDate("31.12.2100");
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        getGroups(document.getId());
                    }
                }
            }
        });

    }

    private void getGroups(String planID) {
        Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi").document(planID).collection("grupeNaPlanu")
                .whereEqualTo("p1Svojstvo", "trosak");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        getData(document.getId(), planID);
                    }
                }
            }
        });
    }

    private void getData(String groupID, String planID) {
        Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi").document(planID)
                .collection("grupeNaPlanu").document(groupID).collection("stavke");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    NotificationDates = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String datum = document.getString("datumStavke");
                        NotificationDates.add(datum);
                        validateDate(NotificationDates);

                    }
                }
            }
        });
    }

    private void validateDate(ArrayList<String> nDatum) {

        String minDate = sharedpref.loadMinDate();
        String nextDate;
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date md, nd, td;
        if (nDatum.size() > 0) {

            for (int i = 0; i < nDatum.size(); i++) {
                nextDate = nDatum.get(i);
                try {
                    Calendar calendar2 = Calendar.getInstance();
                    md = sdf.parse(minDate);
                    nd = sdf.parse(nextDate);
                    td = sdf.parse(getDateTimeFromTimeStamp(System.currentTimeMillis(), "dd.MM.yyyy"));

                    if (md.after(nd)) {

                        minDate = nextDate;
                        calendar2.setTime(md);
                        sharedpref.setMinDate(dateFormat.format(calendar2.getTime()));
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            try {
                calendar1.setTime(sdf.parse(sharedpref.loadMinDate()));
                sharedpref.setDateInMilis(calendar1.getTimeInMillis());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDateTimeFromTimeStamp(Long time, String mDateFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateTime = new Date(time);
        return dateFormat.format(dateTime);
    }
}
