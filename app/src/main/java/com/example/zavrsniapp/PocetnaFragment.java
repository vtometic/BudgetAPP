package com.example.zavrsniapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Objects;
import java.util.TimeZone;


public class PocetnaFragment extends Fragment {


    CardView cardProfile, cardSettings, cardPlans, cardReports, cardGroups, cardNewic;
    SharedPref sharedpref;
    private FirebaseFirestore fStore;
    private ArrayList<String> NotificationDates;
    private PocetnaFragment mViewModel;

    public static PocetnaFragment newInstance() {

        return new PocetnaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        sharedpref = new SharedPref(getActivity());
        if (sharedpref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        fStore = FirebaseFirestore.getInstance();
        View v = inflater.inflate(R.layout.fragment_pocetna, container, false);


        cardProfile = v.findViewById(R.id.profile);
        cardProfile.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, ProfilFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        cardSettings = v.findViewById(R.id.settings);
        cardSettings.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, PostavkeFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        cardPlans = v.findViewById(R.id.plan);
        cardPlans.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, KreiranjeFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        cardGroups = v.findViewById(R.id.group);
        cardGroups.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, DodajFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        cardReports = v.findViewById(R.id.report);
        cardReports.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, IzvjestajiFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        cardNewic = v.findViewById(R.id.newic);
        cardNewic.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, NewicFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


        if (sharedpref.loadNotifications()) {
            sendNotifications();
            Intent intent = new Intent(getActivity(), BroadcastReminder.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            getPlans();
            long timesAtClick = System.currentTimeMillis();
            long TenSecs = (1000 * 10);
            alarmManager.set(AlarmManager.RTC_WAKEUP, timesAtClick + TenSecs, pendingIntent);
        }
        ;


        return v;
    }

    public void sendNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder";
            String descr = "Channel for Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyUser", name, importance);
            channel.setDescription(descr);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

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
