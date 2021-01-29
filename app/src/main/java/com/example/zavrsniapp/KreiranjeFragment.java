package com.example.zavrsniapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zavrsniapp.Adapteri.GrupaViewAdapter;
import com.example.zavrsniapp.Adapteri.PlaniranjeViewAdapter;
import com.example.zavrsniapp.Objects.GrupaModel;
import com.example.zavrsniapp.Objects.P1GrupaModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.Inflater;


public class KreiranjeFragment extends Fragment {

    private ImageView imgNazad;
    SharedPref sharedpref;
    private TabLayout mTabs;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private View mTjedni, mMjesecni, mGodisnji;
    TextInputLayout razdobljePicker;
    int tabId;
    ImageButton imgBtnPrihod, imgBtnTrosak;
    RelativeLayout timeSelectors, planComands;
    Button btnOdbaci, btnZakljuci;
    TextView txtValutaPlan, txtIznosTotal;


    NumberPicker nWeeks, nMonths, nYears;
    TextView btnUredu, txtOdabir;
    ImageButton imgBtnx;


    TextView txtNaslov;
    TextInputLayout inpNaziv, inpIznos, mDat;
    RadioGroup inpSvojstvo;
    RadioButton radioTrosak, radioPrihod;
    Button btnOdustani, btnDodaj;
    Spinner spinnerGrupe;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;


    TextView txtBtnOK;
    CalendarView calendarView;


    RecyclerView planiranjeRecycler;
    ImageButton gExtend;

    private PlaniranjeViewAdapter adapter1;

    public static KreiranjeFragment newInstance() {

        return new KreiranjeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedpref = new SharedPref(getActivity());
        if (sharedpref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }

        View v = inflater.inflate(R.layout.fragment_kreiranje, container, false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mTabs = v.findViewById(R.id.dateTabsPlans);
        mTjedni = v.findViewById(R.id.tjedni);
        mMjesecni = v.findViewById(R.id.mjesecni);
        mGodisnji = v.findViewById(R.id.godisnji);
        razdobljePicker = v.findViewById(R.id.datePickerPlan);

        planComands = v.findViewById(R.id.planComands);
        timeSelectors = v.findViewById(R.id.timeSelectors);
        if (sharedpref.loadPlanID().equals("prazno")) {
            sharedpref.setZgVisibility(true);
        } else {
            sharedpref.setZgVisibility(false);
        }
        if (sharedpref.loadZgVisibility()) {
            timeSelectors.setVisibility(View.VISIBLE);
            planComands.setVisibility(View.GONE);
        } else {
            timeSelectors.setVisibility(View.GONE);
            planComands.setVisibility(View.VISIBLE);
        }
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabId = mTabs.getSelectedTabPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabId = -1;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tabId = mTabs.getSelectedTabPosition();

            }
        });
        razdobljePicker.getEditText().setOnClickListener(v12 -> {

            if (tabId == 0) {
                getWeekPicker(inflater, v);
            }
            if (tabId == 1) {
                getMonthPicker(inflater, v);

            }
            if (tabId == 2) {
                getYearPicker(inflater, v);

            }
        });
        final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
        final View mView = inflater.inflate(R.layout.dialog_add_pit, null);
        mBulider.setView(mView);
        final AlertDialog dialog = mBulider.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initRecycler(v, inflater);

        imgBtnPrihod = v.findViewById(R.id.addPrihod);
        imgBtnPrihod.setOnClickListener(v13 -> addPrihod(inflater, mView, dialog));
        imgBtnTrosak = v.findViewById(R.id.addTrosak);
        imgBtnTrosak.setOnClickListener(v14 -> addTrosak(inflater, mView, dialog));
        txtValutaPlan = v.findViewById(R.id.valutaPlan);
        txtValutaPlan.setText(sharedpref.loadCurrency());
        btnOdbaci = v.findViewById(R.id.OdbaciPlan);
        btnOdbaci.setOnClickListener(v15 -> {
            deletePlan();

        });
        btnZakljuci = v.findViewById(R.id.zakljuciPlan);
        btnZakljuci.setOnClickListener(v16 -> {
            sharedpref.setPlanID("prazno");
            timeSelectors.setVisibility(View.VISIBLE);
            planComands.setVisibility(View.GONE);
            sharedpref.setZgVisibility(true);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, KreiranjeFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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

    public String getFirstAndLastDayWeek(int week, int month, int year, int id) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String ym = "01." + month + "." + year;
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(sdf.parse(ym));
        long tim = calendar.getTimeInMillis();
        long wim = 604800000;
        wim = wim * (week - 1);
        tim = tim + wim;
        calendar.setTimeInMillis(tim);

        Date date1 = sdf.parse(sdf.format(calendar.getTime()));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        int year1 = cal.get(Calendar.YEAR);
        cal.set(Calendar.DAY_OF_WEEK, cal.MONDAY);
        String firstWkDay = String.valueOf(sdf.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_WEEK, 6);
        String lastWkDay = String.valueOf(sdf.format(cal.getTime()));
        if (id == 0)
            return firstWkDay;
        else
            return lastWkDay;
    }

    private void getWeekPicker(LayoutInflater inflater, View view) {
        final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
        final View mView = inflater.inflate(R.layout.dialog_razdoblje_picker, null);
        mBulider.setView(mView);
        final AlertDialog dialog = mBulider.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        txtOdabir = mView.findViewById(R.id.txtOdabir);


        nWeeks = mView.findViewById(R.id.weekPicker);
        nWeeks.setMinValue(1);
        nWeeks.setMaxValue(6);

        nMonths = mView.findViewById(R.id.monthPicker);
        nMonths.setMinValue(1);
        nMonths.setMaxValue(12);

        nYears = mView.findViewById(R.id.yearPicker);
        nYears.setMinValue(year);
        nYears.setMaxValue(year + 10);

        int a = nWeeks.getValue();
        int b = nMonths.getValue();
        int c = nYears.getValue();

        txtOdabir.setText("Plan za " + a + ". tjedan u " + b + ". mjesecu " + c + " godine");

        nWeeks.setOnValueChangedListener((picker, oldVal, newVal) -> {
            int m = nMonths.getValue();
            int y = nYears.getValue();
            txtOdabir.setText("Plan za " + newVal + ". tjedan u " + m + ". mjesecu " + y + " godine");
        });
        nMonths.setOnValueChangedListener((picker, oldVal, newVal) -> {
            int w = nWeeks.getValue();
            int y = nYears.getValue();
            txtOdabir.setText("Plan za " + w + ". tjedan u " + newVal + ". mjesecu " + y + " godine");
        });
        nYears.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int w = nWeeks.getValue();
                int m = nMonths.getValue();
                txtOdabir.setText("Plan za " + w + ". tjedan u " + m + ". mjesecu " + newVal + " godine");
            }
        });

        imgBtnx = mView.findViewById(R.id.zatvoridialog);
        imgBtnx.setOnClickListener(v -> dialog.dismiss());

        btnUredu = mView.findViewById(R.id.btnUredu);
        btnUredu.setOnClickListener(v -> {
            int week = nWeeks.getValue();
            int month = nMonths.getValue();
            int year1 = nYears.getValue();

            sharedpref.setSpYear(year1);
            sharedpref.setSpMonth(month);
            sharedpref.setSpWeek(week);
            razdobljePicker.getEditText().setText(txtOdabir.getText());
            try {

                sharedpref.setPocetniDatum(getFirstAndLastDayWeek(week, month, year1, 0));
                sharedpref.setZavrsniDatum(getFirstAndLastDayWeek(week, month, year1, 1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Map<String, Object> plan = new HashMap<>();
            plan.put("nazivPlana", razdobljePicker.getEditText().getText().toString());
            plan.put("razdobljeWeek", sharedpref.loadSpWeek());
            plan.put("razdobljeMonth", sharedpref.loadSpMonth());
            plan.put("razdobljeYear", sharedpref.loadSpYear());
            plan.put("pocetniDatum", sharedpref.loadPocetniDatum());
            plan.put("zavrsniDatum", sharedpref.loadZavrsniDatum());
            plan.put("identifikatorRazdoblja", "tjedni");
            DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("planovi").document();
            sharedpref.setPlanID(dr.getId());
            dr.set(plan)
                    .addOnFailureListener(e -> {
                        String error = e.getMessage();
                        Toast.makeText(getActivity(), "Greška: " + error, Toast.LENGTH_SHORT).show();
                    });
            dr.update("idPlana", dr.getId());
            planComands = view.findViewById(R.id.planComands);
            planComands.setVisibility(View.VISIBLE);
            timeSelectors = view.findViewById(R.id.timeSelectors);
            timeSelectors.setVisibility(View.GONE);

            sharedpref.setZgVisibility(false);
            dialog.dismiss();
        });

    }

    public String getFirstAndLastDayMonth(int month, int year, int id) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String firstDay = "01." + month + "." + year;
        firstDay = sdf.format(sdf.parse(firstDay));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(firstDay));
        int last = calendar.getActualMaximum(Calendar.DATE);
        String lastDay = last + "." + month + "." + year;
        lastDay = sdf.format(sdf.parse(lastDay));
        if (id == 0)
            return firstDay;
        else
            return lastDay;
    }

    private void getMonthPicker(LayoutInflater inflater, View view) {

        final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
        final View mView = inflater.inflate(R.layout.dialog_razdoblje_picker, null);
        mBulider.setView(mView);
        final AlertDialog dialog = mBulider.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        txtOdabir = mView.findViewById(R.id.txtOdabir);

        nWeeks = mView.findViewById(R.id.weekPicker);
        nWeeks.setEnabled(false);
        nWeeks.setVisibility(View.GONE);

        nMonths = mView.findViewById(R.id.monthPicker);
        nMonths.setMinValue(1);
        nMonths.setMaxValue(12);

        nYears = mView.findViewById(R.id.yearPicker);
        nYears.setMinValue(year);
        nYears.setMaxValue(year + 10);

        int b = nMonths.getValue();
        int c = nYears.getValue();

        txtOdabir.setText("Plan za " + b + ". mjesec u " + c + " godini");

        nMonths.setOnValueChangedListener((picker, oldVal, newVal) -> {
            int y = nYears.getValue();
            txtOdabir.setText("Plan za " + newVal + ". mjesec u " + y + " godini");
        });
        nYears.setOnValueChangedListener((picker, oldVal, newVal) -> {
            int m = nMonths.getValue();
            txtOdabir.setText("Plan za " + m + ". mjesec u " + newVal + " godini");
        });

        imgBtnx = mView.findViewById(R.id.zatvoridialog);
        imgBtnx.setOnClickListener(v -> dialog.dismiss());

        btnUredu = mView.findViewById(R.id.btnUredu);
        btnUredu.setOnClickListener(v -> {
            int month = nMonths.getValue();
            int year1 = nYears.getValue();
            sharedpref.setSpYear(year1);
            sharedpref.setSpMonth(month);
            sharedpref.setSpWeek(-1);
            razdobljePicker.getEditText().setText(txtOdabir.getText());

            try {
                sharedpref.setPocetniDatum(getFirstAndLastDayMonth(month, year1, 0));
                sharedpref.setZavrsniDatum(getFirstAndLastDayMonth(month, year1, 1));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Map<String, Object> plan = new HashMap<>();
            plan.put("nazivPlana", razdobljePicker.getEditText().getText().toString());
            plan.put("razdobljeMonth", sharedpref.loadSpMonth());
            plan.put("razdobljeYear", sharedpref.loadSpYear());
            plan.put("pocetniDatum", sharedpref.loadPocetniDatum());
            plan.put("zavrsniDatum", sharedpref.loadZavrsniDatum());
            plan.put("identifikatorRazdoblja", "mjesecni");

            DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("planovi").document();
            sharedpref.setPlanID(dr.getId());
            dr.set(plan).addOnFailureListener(e -> {
                String error = e.getMessage();
                Toast.makeText(getActivity(), "Greška: " + error, Toast.LENGTH_SHORT).show();
            });
            dr.update("idPlana", dr.getId());
            planComands = view.findViewById(R.id.planComands);
            planComands.setVisibility(View.VISIBLE);
            timeSelectors = view.findViewById(R.id.timeSelectors);
            timeSelectors.setVisibility(View.GONE);
            sharedpref.setZgVisibility(false);
            dialog.dismiss();
        });

    }

    private void getYearPicker(LayoutInflater inflater, View view) {
        final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
        final View mView = inflater.inflate(R.layout.dialog_razdoblje_picker, null);
        mBulider.setView(mView);
        final AlertDialog dialog = mBulider.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        txtOdabir = mView.findViewById(R.id.txtOdabir);

        nWeeks = mView.findViewById(R.id.weekPicker);
        nWeeks.setEnabled(false);
        nWeeks.setVisibility(View.GONE);

        nMonths = mView.findViewById(R.id.monthPicker);
        nMonths.setEnabled(false);
        nMonths.setVisibility(View.GONE);

        nYears = mView.findViewById(R.id.yearPicker);
        nYears.setMinValue(year);
        nYears.setMaxValue(year + 10);

        imgBtnx = mView.findViewById(R.id.zatvoridialog);
        imgBtnx.setOnClickListener(v -> dialog.dismiss());

        int c = nYears.getValue();
        txtOdabir.setText("Plan za " + c + " godinu");

        nYears.setOnValueChangedListener((picker, oldVal, newVal) -> {
            txtOdabir.setText("Plan za " + newVal + " godinu");
        });

        btnUredu = mView.findViewById(R.id.btnUredu);
        btnUredu.setOnClickListener(v -> {
            int year1 = nYears.getValue();
            sharedpref.setSpYear(year1);
            sharedpref.setSpMonth(-1);
            sharedpref.setSpWeek(-1);
            razdobljePicker.getEditText().setText(txtOdabir.getText());
            String firstDay = "01.01." + year1;
            String lastDay = "31.12." + year1;
            Map<String, Object> plan = new HashMap<>();
            plan.put("nazivPlana", razdobljePicker.getEditText().getText().toString());
            plan.put("razdobljeYear", sharedpref.loadSpYear());
            plan.put("pocetniDatum", firstDay);
            plan.put("zavrsniDatum", lastDay);
            plan.put("identifikatorRazdoblja", "godisnji");

            DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("planovi").document();
            sharedpref.setPlanID(dr.getId());
            dr.set(plan).addOnFailureListener(e -> {
                String error = e.getMessage();
                Toast.makeText(getActivity(), "Greška: " + error, Toast.LENGTH_SHORT).show();
            });
            dr.update("idPlana", dr.getId());
            planComands = view.findViewById(R.id.planComands);
            planComands.setVisibility(View.VISIBLE);
            timeSelectors = view.findViewById(R.id.timeSelectors);
            timeSelectors.setVisibility(View.GONE);
            sharedpref.setZgVisibility(false);
            dialog.dismiss();

        });

    }

    private void addPrihod(LayoutInflater inflater, View mView, AlertDialog dialog) {
        if (sharedpref.loadPlanID().equals("prazno")) {
            Snackbar.make((View) imgNazad.getParent(), "Prvo odaberite razdoblje i kreirajte plan ", Snackbar.LENGTH_SHORT).show();
        } else {
            dialog.show();
            txtNaslov = mView.findViewById(R.id.adpNaslov);
            txtNaslov.setText("Dodaj novi prihod");
            inpNaziv = mView.findViewById(R.id.txtNazivItem);
            inpIznos = mView.findViewById(R.id.txtIznos);
            inpIznos.setSuffixText(sharedpref.loadCurrency());
            inpIznos.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    formatIznos(Double.valueOf(s.toString()));
                }
            });
            mDat = mView.findViewById(R.id.datum);
            mDat.getEditText().setOnClickListener(v -> setDate(inflater));
            inpSvojstvo = mView.findViewById(R.id.adpOpcije);
            inpSvojstvo.setEnabled(false);

            radioPrihod = mView.findViewById(R.id.Prihod);
            radioPrihod.setChecked(true);

            radioTrosak = mView.findViewById(R.id.Trošak);
            radioTrosak.setEnabled(false);

            spinnerGrupe = mView.findViewById(R.id.spinnerGrupe);
            spinnerDataList = new ArrayList<>();
            adapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.item_spinner,
                    spinnerDataList);

            spinnerGrupe.setAdapter(adapter);
            dohvatiGrupe("Prihod");

            spinnerGrupe.getSelectedItem();

            btnDodaj = mView.findViewById(R.id.adpDodaj);
            btnDodaj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!validateIznos(mView) | !validateNaziv(mView) | !validateDat(mView)) {
                        return;
                    }
                    DocumentReference dr1 = fStore.collection("korisnici").document(sharedpref.loadUserID())
                            .collection("planovi").document(sharedpref.loadPlanID())
                            .collection("grupeNaPlanu").document(spinnerGrupe.getSelectedItem().toString());
                    dr1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String iznos = String.valueOf(task.getResult().get("p1Iznos"));
                            if (iznos.equals("null")) {
                                String s = "prihod";
                                String i = inpIznos.getEditText().getText().toString();
                                saveGroupToBase(spinnerGrupe.getSelectedItem().toString(),
                                        inpNaziv.getEditText().getText().toString(),
                                        inpIznos.getEditText().getText().toString(),
                                        s, i,
                                        mDat.getEditText().getText().toString()
                                );
                            } else {
                                String s = "prihod";
                                iznos = String.valueOf(task.getResult().get("p1Iznos"));
                                float total = Float.valueOf(iznos);
                                total = total + Float.valueOf(String.valueOf(inpIznos.getEditText().getText()));
                                saveGroupToBase(spinnerGrupe.getSelectedItem().toString(),
                                        inpNaziv.getEditText().getText().toString(),
                                        inpIznos.getEditText().getText().toString(),
                                        s,
                                        String.valueOf(total),
                                        mDat.getEditText().getText().toString());
                            }
                        }
                    });
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, KreiranjeFragment.newInstance());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    dialog.dismiss();
                }
            });

            btnOdustani = mView.findViewById(R.id.adpOdustani);
            btnOdustani.setOnClickListener(v -> dialog.dismiss());
        }

    }

    private void addTrosak(LayoutInflater inflater, View mView, AlertDialog dialog) {
        if (sharedpref.loadPlanID().equals("prazno")) {
            Snackbar.make((View) imgNazad.getParent(), "Prvo odaberite razdoblje i kreirajte plan ", Snackbar.LENGTH_SHORT).show();
        } else {
            dialog.show();
            txtNaslov = mView.findViewById(R.id.adpNaslov);
            txtNaslov.setText("Dodaj novi Trošak");

            inpNaziv = mView.findViewById(R.id.txtNazivItem);

            inpIznos = mView.findViewById(R.id.txtIznos);
            inpIznos.setSuffixText(sharedpref.loadCurrency());

            inpSvojstvo = mView.findViewById(R.id.adpOpcije);
            inpSvojstvo.setEnabled(false);

            radioTrosak = mView.findViewById(R.id.Trošak);
            radioTrosak.setChecked(true);

            radioPrihod = mView.findViewById(R.id.Prihod);
            radioPrihod.setEnabled(false);

            mDat = mView.findViewById(R.id.datum);
            mDat.getEditText().setOnClickListener(v -> setDate(inflater));

            spinnerGrupe = mView.findViewById(R.id.spinnerGrupe);
            spinnerDataList = new ArrayList<>();
            adapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.item_spinner,
                    spinnerDataList);

            spinnerGrupe.setAdapter(adapter);
            dohvatiGrupe("Trošak");

            spinnerGrupe.getSelectedItem();


            btnDodaj = mView.findViewById(R.id.adpDodaj);
            btnDodaj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!validateIznos(mView) | !validateNaziv(mView) | !validateDat(mView)) {
                        return;
                    }
                    DocumentReference dr1 = fStore.collection("korisnici").document(sharedpref.loadUserID())
                            .collection("planovi").document(sharedpref.loadPlanID())
                            .collection("grupeNaPlanu").document(spinnerGrupe.getSelectedItem().toString());
                    dr1.get().addOnCompleteListener(task -> {
                        String iznos = String.valueOf(task.getResult().get("p1Iznos"));
                        if (iznos.equals("null")) {
                            String s = "trosak";
                            String i = inpIznos.getEditText().getText().toString();
                            saveGroupToBase(spinnerGrupe.getSelectedItem().toString(),
                                    inpNaziv.getEditText().getText().toString(),
                                    inpIznos.getEditText().getText().toString(),
                                    s, i,
                                    mDat.getEditText().getText().toString()
                            );
                        } else {
                            iznos = String.valueOf(task.getResult().get("p1Iznos"));
                            double total = Double.parseDouble(iznos);
                            total = total + Double.parseDouble(inpIznos.getEditText().getText().toString());

                            String s = "trosak";
                            saveGroupToBase(spinnerGrupe.getSelectedItem().toString(),
                                    inpNaziv.getEditText().getText().toString(),
                                    inpIznos.getEditText().getText().toString(),
                                    s, String.valueOf(total),
                                    mDat.getEditText().getText().toString()
                            );
                        }
                    });
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, KreiranjeFragment.newInstance());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    dialog.dismiss();
                }

            });
            btnOdustani = mView.findViewById(R.id.adpOdustani);
            btnOdustani.setOnClickListener(v -> dialog.dismiss());
        }
    }

    public void setDate(LayoutInflater inflater) {

        final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
        final View mView = inflater.inflate(R.layout.dialog_date_picker, null);
        mBulider.setView(mView);
        final AlertDialog dialog = mBulider.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        calendarView = mView.findViewById(R.id.calendarDialog);
        final Calendar calendar = Calendar.getInstance();
        calendarView.setMinDate(calendar.getTimeInMillis());
        calendarView.setDate(calendar.getTimeInMillis());
        calendarView.setOnDateChangeListener((view, y, m, d) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.YEAR, y);
            calendar1.set(Calendar.MONTH, m);
            calendar1.set(Calendar.DATE, d);
            String dateString = String.valueOf(DateFormat.format("dd.MM.yyyy", calendar1));
            mDat.getEditText().setText(dateString);
        });
        txtBtnOK = mView.findViewById(R.id.dialogOK);
        txtBtnOK.setOnClickListener(v -> dialog.dismiss());
    }

    private boolean validateDat(View v) {
        mDat = v.findViewById(R.id.datum);
        String pickeddat = mDat.getEditText().getText().toString().trim();
        if (pickeddat.isEmpty()) {
            mDat.setError("Odaberite datum");
            return false;
        } else {
            mDat.setError(null);
            return true;
        }
    }

    private void dohvatiGrupe(String svojstvo) {
        Query colRef = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("grupe").whereEqualTo("svojstvoGrupe", svojstvo);
        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    spinnerDataList.add(document.get("nazivGrupe").toString());

                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "Greška", Toast.LENGTH_SHORT).show());
    }

    private void initRecycler(View v, LayoutInflater inflater) {
        Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi").document(sharedpref.loadPlanID()).collection("grupeNaPlanu");
        FirestoreRecyclerOptions<P1GrupaModel> options = new FirestoreRecyclerOptions.Builder<P1GrupaModel>()
                .setQuery(query, P1GrupaModel.class).build();
        adapter1 = new PlaniranjeViewAdapter(options);
        planiranjeRecycler = v.findViewById(R.id.planRecycler);
        planiranjeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        planiranjeRecycler.setAdapter(adapter1);
        gExtend = v.findViewById(R.id.p1Expand);
        txtIznosTotal = v.findViewById(R.id.iznosPlan);
        calculateTotal(query, v);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter1.deleteItem(viewHolder.getAdapterPosition(), viewHolder);

            }
        }).attachToRecyclerView(planiranjeRecycler);

        adapter1.setOnItemClickListener((documentSnapshot, position) -> {
            P1GrupaModel grupa = documentSnapshot.toObject(P1GrupaModel.class);

        });
    }

    private void saveGroupToBase(String nazivGrupe, String nazivStavke, String iznosStavke, String svojstvoGrupe, String iznos, String datumStavke) {

        Map<String, Object> grupa = new HashMap<>();
        grupa.put("p1Naziv", nazivGrupe);
        grupa.put("p1Svojstvo", svojstvoGrupe);
        grupa.put("p1Iznos", iznos);

        DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi").document(sharedpref.loadPlanID())
                .collection("grupeNaPlanu").document(nazivGrupe);
        dr.set(grupa);
        saveDataToBase(dr.getId(), nazivStavke, formatIznos(Double.valueOf(iznosStavke)), datumStavke, svojstvoGrupe);
    }

    private void saveDataToBase(String idGrupe, String nazivStavke, String iznosStavke, String datumStavke, String svojstvoStavke) {

        Map<String, Object> stavka = new HashMap<>();
        stavka.put("nazivStavke", nazivStavke);
        stavka.put("iznosStavke", iznosStavke);
        stavka.put("datumStavke", datumStavke);
        stavka.put("svojstvoStavke", svojstvoStavke);
        stavka.put("idGrupe", idGrupe);

        DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi").document(sharedpref.loadPlanID())
                .collection("grupeNaPlanu").document(idGrupe)
                .collection("stavke").document();

        dr.set(stavka);

    }

    private void calculateTotal(Query query, View v) {
        txtIznosTotal = v.findViewById(R.id.iznosPlan);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    double total = Double.parseDouble(txtIznosTotal.getText().toString().trim());
                    double iznosStavke;
                    String svojstvo = String.valueOf(document.get("p1Svojstvo"));
                    if (svojstvo.equals("prihod")) {
                        iznosStavke = Double.parseDouble(String.valueOf(document.get("p1Iznos")));
                        total = total + iznosStavke;
                        txtIznosTotal.setText(formatIznos(total));
                    } else {
                        iznosStavke = Float.valueOf(String.valueOf(document.get("p1Iznos")));
                        total = total - iznosStavke;
                        txtIznosTotal.setText(formatIznos(total));
                    }
                }
            }
        });
    }

    private boolean validateNaziv(View v) {
        inpNaziv = v.findViewById(R.id.txtNazivItem);
        String naziv = inpNaziv.getEditText().getText().toString();
        if (naziv.isEmpty()) {
            inpNaziv.setError("Unesite naziv stavke ");
            return false;
        } else {
            inpNaziv.setError(null);
            return true;
        }
    }

    private boolean validateIznos(View v) {
        inpIznos = v.findViewById(R.id.txtIznos);
        String iznos = inpIznos.getEditText().getText().toString();
        if (iznos.isEmpty()) {
            inpIznos.setError("Unesite iznos stavke");
            return false;
        } else {
            inpIznos.setError(null);
            formatIznos(Double.parseDouble(inpIznos.getEditText().getText().toString()));
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

    private void deletePlan() {
        SharedPref sharedpref = new SharedPref(getActivity());

        CollectionReference deleteGroup = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi").document(sharedpref.loadPlanID())
                .collection("grupeNaPlanu");
        deleteGroup.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    deleteStavke(document.getId());
                    DocumentReference dr1 = fStore.collection("korisnici").document(sharedpref.loadUserID())
                            .collection("planovi").document(sharedpref.loadPlanID())
                            .collection("grupeNaPlanu").document(document.getId());
                    dr1.delete();
                }
                DocumentReference deletePlan = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("planovi").document(sharedpref.loadPlanID());
                deletePlan.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.container, KreiranjeFragment.newInstance());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        sharedpref.setPlanID("prazno");
                        timeSelectors.setVisibility(View.VISIBLE);
                        planComands.setVisibility(View.GONE);
                        sharedpref.setZgVisibility(true);
                    }
                });
            }
        });
    }

    private void deleteStavke(String groupID) {
        CollectionReference cl = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi").document(sharedpref.loadPlanID())
                .collection("grupeNaPlanu").document(groupID).collection("stavke");
        cl.get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                for (QueryDocumentSnapshot document1 : task1.getResult()) {
                    sharedpref.setStavkaID((document1.getId()));
                    DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                            .collection("planovi").document(sharedpref.loadPlanID())
                            .collection("grupeNaPlanu").document(groupID).collection("stavke").document(sharedpref.loadStavkaID());
                    dr.delete();
                }

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter1.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter1.stopListening();
    }


}