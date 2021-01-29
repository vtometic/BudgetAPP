package com.example.zavrsniapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.PieRadarChartBase;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static java.lang.Thread.sleep;

public class IzvjestajiFragment extends Fragment {
    ImageView imgNazad;
    SharedPref sharedpref;

    private TabLayout mTabs;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    int tabId;
    Button btnGeneriraj;
    RelativeLayout bodyLayout;

    private TextInputLayout mStart, mEnd;
    TextView txtBtnOK;
    CalendarView calendarView;

    int[] dColors = new int[]{
            Color.rgb(0, 255, 255),
            Color.rgb(0, 0, 0),
            Color.rgb(0, 0, 255),
            Color.rgb(128, 128, 128),
            Color.rgb(0, 128, 0),
            Color.rgb(0, 255, 0),
            Color.rgb(128, 0, 0),
            Color.rgb(0, 0, 128),
            Color.rgb(128, 0, 0),
            Color.rgb(128, 128, 0),
            Color.rgb(255, 165, 0),
            Color.rgb(128, 0, 128),
            Color.rgb(255, 0, 0),
            Color.rgb(0, 128, 128),
            Color.rgb(255, 255, 0)};
    int[] lColors = new int[]{
            Color.rgb(0, 255, 255),
            Color.rgb(192, 192, 192),
            Color.rgb(0, 0, 255),
            Color.rgb(128, 128, 128),
            Color.rgb(0, 128, 0),
            Color.rgb(0, 255, 0),
            Color.rgb(128, 0, 0),
            Color.rgb(0, 0, 128),
            Color.rgb(128, 0, 0),
            Color.rgb(128, 128, 0),
            Color.rgb(255, 165, 0),
            Color.rgb(128, 0, 128),
            Color.rgb(255, 0, 0),
            Color.rgb(0, 128, 128),
            Color.rgb(255, 255, 0)};

    ImageView imgClose;
    TextView naslovOverall;
    TextView overallCmdPie, overallCmdBar, overallCmdRadar;
    ArrayList<PieEntry> prihodiStavke;
    ArrayList<PieEntry> troskoviStavke;
    PieChart oPieChart;
    BarChart oBarChart;
    RadarChart oRadarChart;

    ArrayList<String> grupe = new ArrayList<>();
    ArrayList<RadarEntry> ostvareni = new ArrayList<>();
    ArrayList<RadarEntry> planirani = new ArrayList<>();

    TextView poDatumimaNaslov;
    TextView btnLineChart, btnGroupedBar;
    LineChart lineChart;
    BarChart groupedBarChart;
    List<List<String>> lista = new ArrayList<>();
    List<List<String>> planiraneStavke = new ArrayList<>();


    public static IzvjestajiFragment newInstance() {

        return new IzvjestajiFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        prihodiStavke = new ArrayList<>();
        troskoviStavke = new ArrayList<>();
        sharedpref = new SharedPref(getActivity());

        if (sharedpref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        View v = inflater.inflate(R.layout.fragment_izvjestaji, container, false);

        imgNazad = v.findViewById(R.id.natrag);
        imgNazad.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mTabs = v.findViewById(R.id.repTabs);
        mTabs.getTabAt(sharedpref.loadRepID()).select();
        bodyLayout = v.findViewById(R.id.repBody);

        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                sharedpref.setRepID(mTabs.getSelectedTabPosition());
                refreshFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                sharedpref.setRepID(mTabs.getSelectedTabPosition());
                refreshFragment();
            }
        });

        mStart = v.findViewById(R.id.intPickStart);
        mStart.getEditText().setOnClickListener(v12 -> {
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
                mStart.getEditText().setText(dateString);
            });
            txtBtnOK = mView.findViewById(R.id.dialogOK);
            txtBtnOK.setOnClickListener(v1 -> dialog.dismiss());
        });
        mEnd = v.findViewById(R.id.intPickEnd);
        mEnd.getEditText().setOnClickListener(v13 -> {
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
                mEnd.getEditText().setText(dateString);
            });
            txtBtnOK = mView.findViewById(R.id.dialogOK);
            txtBtnOK.setOnClickListener(v1 -> dialog.dismiss());

        });

        btnGeneriraj = v.findViewById(R.id.generirajIzvjestaj);
        btnGeneriraj.setOnClickListener(v14 -> {
            if (!validateStart(v) | !validateEnd(v)) {
                return;
            }
            String start = mStart.getEditText().getText().toString().trim();
            String end = mEnd.getEditText().getText().toString().trim();

            final AlertDialog.Builder mBulider = new AlertDialog.Builder(getActivity());
            final View mView = inflater.inflate(R.layout.fragment_izvjestaji_rezultat, null);
            mBulider.setView(mView);
            final AlertDialog dialog = mBulider.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            if (sharedpref.loadRepID() == 0) {

                generateOverallChartWindow(mView, "prihod", start, end);
                try {
                    generateDatesChartWindow(mView, "prihod", start, end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (sharedpref.loadRepID() == 1) {
                generateOverallChartWindow(mView, "trosak", start, end);
                try {
                    generateDatesChartWindow(mView, "trosak", start, end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (sharedpref.loadRepID() == 2) {
                generateOverallChartWindow(mView, "comp", start, end);
                try {
                    generateDatesChartWindow(mView, "comp", start, end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            imgClose = mView.findViewById(R.id.zatvori);
            imgClose.setOnClickListener(v15 -> dialog.cancel());


        });

        return v;
    }

    public void generateDatesChartWindow(View v, String generatorID, String startDate, String endDate) throws ParseException {
        poDatumimaNaslov = v.findViewById(R.id.datesNaslov);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        @ColorInt int color = typedValue.data;
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        @ColorInt int color1 = typedValue.data;

        Boolean isYearly = getDifference(startDate, endDate);


        if (generatorID.equals("prihod")) {
            poDatumimaNaslov.setText("Pregled razlike planiranih i ostvarenih prihoda po datumima u razdoblju od " + startDate + " do " + endDate + "");
            getDatesData(v, generatorID, 1, startDate, endDate, isYearly);

            btnLineChart = v.findViewById(R.id.lineChartCmd);
            btnLineChart.setOnClickListener(v1 -> {
                poDatumimaNaslov.setText("Pregled razlike planiranih i ostvarenih prihoda po datumima u razdoblju od " + startDate + " do " + endDate + "");
                btnLineChart.setTextColor(color);
                btnGroupedBar.setTextColor(color1);
                getDatesData(v, generatorID, 1, startDate, endDate, isYearly);
            });
            btnGroupedBar = v.findViewById(R.id.groupedBarChartCmd);
            btnGroupedBar.setOnClickListener(v12 -> {
                poDatumimaNaslov.setText("Pregled planiranih i ostvarenih prihoda po datumima u razdoblju od " + startDate + " do " + endDate + "");
                btnLineChart.setTextColor(color1);
                btnGroupedBar.setTextColor(color);
                getDatesData(v, generatorID, 2, startDate, endDate, isYearly);
            });


        }
        if (generatorID.equals("trosak")) {
            poDatumimaNaslov.setText("Pregled razlike planiranih i ostvarenih troškova po datumima u razdoblju od " + startDate + " do " + endDate + "");
            getDatesData(v, generatorID, 1, startDate, endDate, isYearly);

            btnLineChart = v.findViewById(R.id.lineChartCmd);
            btnLineChart.setOnClickListener(v1 -> {
                poDatumimaNaslov.setText("Pregled razlike planiranih i ostvarenih troškova po datumima u razdoblju od " + startDate + " do " + endDate + "");
                btnLineChart.setTextColor(color);
                btnGroupedBar.setTextColor(color1);
                getDatesData(v, generatorID, 1, startDate, endDate, isYearly);
            });
            btnGroupedBar = v.findViewById(R.id.groupedBarChartCmd);
            btnGroupedBar.setOnClickListener(v12 -> {
                poDatumimaNaslov.setText("Pregled planiranih i ostvarenih troškova po datumima u razdoblju od " + startDate + " do " + endDate + "");
                btnLineChart.setTextColor(color1);
                btnGroupedBar.setTextColor(color);
                getDatesData(v, generatorID, 2, startDate, endDate, isYearly);
            });


        }
        if (generatorID.equals("comp")) {
            poDatumimaNaslov.setText("Pregled razlike nastalih prihoda i troškova po datumima u razdoblju od " + startDate + " do " + endDate + "");
            getDatesData(v, generatorID, 1, startDate, endDate, isYearly);

            btnLineChart = v.findViewById(R.id.lineChartCmd);
            btnLineChart.setOnClickListener(v1 -> {
                poDatumimaNaslov.setText("Pregled razlike nastalih prihoda i troškova po datumima u razdoblju od " + startDate + " do " + endDate + "");
                btnLineChart.setTextColor(color);
                btnGroupedBar.setTextColor(color1);
                getDatesData(v, generatorID, 1, startDate, endDate, isYearly);
            });
            btnGroupedBar = v.findViewById(R.id.groupedBarChartCmd);
            btnGroupedBar.setOnClickListener(v12 -> {
                poDatumimaNaslov.setText("Pregled nastalih prihoda i troškova po datumima u razdoblju od " + startDate + " do " + endDate + "");
                btnLineChart.setTextColor(color1);
                btnGroupedBar.setTextColor(color);
                getDatesData(v, generatorID, 2, startDate, endDate, isYearly);
            });


        }

    }

    public void generateOverallChartWindow(View v, String generatorID, String startDate, String endDate) {
        naslovOverall = v.findViewById(R.id.overallNaslov);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        @ColorInt int color = typedValue.data;
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        @ColorInt int color1 = typedValue.data;

        if (generatorID.equals("prihod")) {
            naslovOverall.setText("Pregled planiranih i ostvarenih prihoda u razdoblju od " + startDate + " do " + endDate + "");
            getData(v, generatorID, 1, startDate, endDate);

            overallCmdPie = v.findViewById(R.id.pieOverall);
            overallCmdPie.setOnClickListener(v1 -> {
                overallCmdPie.setTextColor(color);
                overallCmdBar.setTextColor(color1);
                overallCmdRadar.setTextColor(color1);
                getData(v, generatorID, 1, startDate, endDate);
            });
            overallCmdBar = v.findViewById(R.id.barOverall);
            overallCmdBar.setOnClickListener(v12 -> {
                overallCmdPie.setTextColor(color1);
                overallCmdBar.setTextColor(color);
                overallCmdRadar.setTextColor(color1);
                getData(v, generatorID, 2, startDate, endDate);
            });
            overallCmdRadar = v.findViewById(R.id.radarOverall);
            overallCmdRadar.setOnClickListener(v13 -> {
                overallCmdPie.setTextColor(color1);
                overallCmdBar.setTextColor(color1);
                overallCmdRadar.setTextColor(color);
                getData(v, generatorID, 3, startDate, endDate);

            });

        }
        if (generatorID.equals("trosak")) {
            naslovOverall.setText("Pregled planiranih i nastalih troškova u razdoblju od " + startDate + " do " + endDate + "");
            getData(v, generatorID, 1, startDate, endDate);

            overallCmdPie = v.findViewById(R.id.pieOverall);
            overallCmdPie.setOnClickListener(v1 -> {
                overallCmdPie.setTextColor(color);
                overallCmdBar.setTextColor(color1);
                overallCmdRadar.setTextColor(color1);
                getData(v, generatorID, 1, startDate, endDate);
            });
            overallCmdBar = v.findViewById(R.id.barOverall);
            overallCmdBar.setOnClickListener(v12 -> {
                overallCmdPie.setTextColor(color1);
                overallCmdBar.setTextColor(color);
                overallCmdRadar.setTextColor(color1);
                getData(v, generatorID, 2, startDate, endDate);
            });
            overallCmdRadar = v.findViewById(R.id.radarOverall);
            overallCmdRadar.setOnClickListener(v13 -> {
                overallCmdPie.setTextColor(color1);
                overallCmdBar.setTextColor(color1);
                overallCmdRadar.setTextColor(color);
                getData(v, generatorID, 3, startDate, endDate);

            });
        }
        if (generatorID.equals("comp")) {
            naslovOverall.setText("Pregled ostvarenih prihoda i troškova u razdoblju od " + startDate + " do " + endDate + "");
            getData(v, generatorID, 1, startDate, endDate);

            overallCmdPie = v.findViewById(R.id.pieOverall);
            overallCmdPie.setOnClickListener(v1 -> {
                overallCmdPie.setTextColor(color);
                overallCmdBar.setTextColor(color1);
                overallCmdRadar.setTextColor(color1);
                getData(v, generatorID, 1, startDate, endDate);
            });
            overallCmdBar = v.findViewById(R.id.barOverall);
            overallCmdBar.setOnClickListener(v12 -> {
                overallCmdPie.setTextColor(color1);
                overallCmdBar.setTextColor(color);
                overallCmdRadar.setTextColor(color1);
                getData(v, generatorID, 2, startDate, endDate);
            });
            overallCmdRadar = v.findViewById(R.id.radarOverall);
            overallCmdRadar.setVisibility(View.GONE);

        }
    }

    private boolean validateDatOd(String startDate, String endDate, String targetDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        Date dStart = sdf.parse(startDate);
        Date dEnd = sdf.parse(endDate);
        Date tDate = sdf.parse(targetDate);
        if (tDate.after(dStart) && tDate.before(dEnd)) {
            return true;
        } else {
            return false;
        }

    }

    public void getDatesData(View v, String generatorID, int idGrafa, String startDate, String endDate, Boolean isYearly) {
        lineChart = v.findViewById(R.id.lineChart);
        groupedBarChart = v.findViewById(R.id.groupedBarChart);

        if (idGrafa == 1) {
            lineChart.setVisibility(View.VISIBLE);
            groupedBarChart.setVisibility(View.GONE);


            if (generatorID.equals("prihod")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Prihod");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<List<String>> ostvareneStavke = new ArrayList<List<String>>();
                        int y = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String iznosStavke, nazivStavke;
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    nazivStavke = String.valueOf(document.get("idGrupe"));
                                    iznosStavke = String.valueOf(document.get("iznosStavke"));
                                    ostvareneStavke.add(new ArrayList<String>());
                                    ostvareneStavke.get(y).add(nazivStavke);
                                    ostvareneStavke.get(y).add(iznosStavke);
                                    ostvareneStavke.get(y).add(targetDate);
                                    y++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        getDatesPlanData(ostvareneStavke, startDate, endDate, generatorID, isYearly);
                    }
                });
            }
            if (generatorID.equals("trosak")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Trošak");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<List<String>> ostvareneStavke = new ArrayList<List<String>>();
                        int y = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String iznosStavke, nazivStavke;
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    nazivStavke = String.valueOf(document.get("idGrupe"));
                                    iznosStavke = String.valueOf(document.get("iznosStavke"));
                                    ostvareneStavke.add(new ArrayList<String>());
                                    ostvareneStavke.get(y).add(nazivStavke);
                                    ostvareneStavke.get(y).add(iznosStavke);
                                    ostvareneStavke.get(y).add(targetDate);
                                    y++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        getDatesPlanData(ostvareneStavke, startDate, endDate, generatorID, isYearly);
                    }
                });
            }
            if (generatorID.equals("comp")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Trošak");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<List<String>> ostvareneStavke = new ArrayList<List<String>>();
                        int y = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String iznosStavke, nazivStavke;
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    nazivStavke = String.valueOf(document.get("idGrupe"));
                                    iznosStavke = String.valueOf(document.get("iznosStavke"));
                                    ostvareneStavke.add(new ArrayList<String>());
                                    ostvareneStavke.get(y).add(nazivStavke);
                                    ostvareneStavke.get(y).add(iznosStavke);
                                    ostvareneStavke.get(y).add(targetDate);
                                    y++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        getDatesPlanData(ostvareneStavke, startDate, endDate, generatorID, isYearly);
                    }
                });
            }

        }
        if (idGrafa == 2) {
            lineChart.setVisibility(View.GONE);
            groupedBarChart.setVisibility(View.VISIBLE);
        }

    }

    public void getDatesPlanData(List<List<String>> ostvareneStavke, String startDate, String endDate, String generatorID, Boolean isYearly) {
        if (generatorID.equals("prihod")) {
            Query queryPlan = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("planovi");
            queryPlan.get().addOnCompleteListener(task12 -> {
                if (task12.isSuccessful()) {
                    for (QueryDocumentSnapshot document1 : task12.getResult()) {
                        if (startDate.equals(document1.get("pocetniDatum")) && endDate.equals(document1.get("zavrsniDatum"))) {
                            Query stavkePlana = fStore.collection("korisnici").document(sharedpref.loadUserID())
                                    .collection("planovi").document(document1.get("idPlana").toString().trim())
                                    .collection("grupeNaPlanu").whereEqualTo("p1Svojstvo", "prihod");
                            stavkePlana.get().addOnCompleteListener(task13 -> {
                                if (task13.isSuccessful()) {
                                    for (QueryDocumentSnapshot document2 : task13.getResult()) {
                                        getDatesPlanStavke(document1, document2, ostvareneStavke, startDate, endDate, generatorID);

                                    }
                                }
                            });

                        }
                    }
                }

            });
        }
        if (generatorID.equals("trosak")) {
            Query queryPlan = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("planovi");
            queryPlan.get().addOnCompleteListener(task12 -> {
                if (task12.isSuccessful()) {
                    for (QueryDocumentSnapshot document1 : task12.getResult()) {
                        if (startDate.equals(document1.get("pocetniDatum")) && endDate.equals(document1.get("zavrsniDatum"))) {
                            Query stavkePlana = fStore.collection("korisnici").document(sharedpref.loadUserID())
                                    .collection("planovi").document(document1.get("idPlana").toString().trim())
                                    .collection("grupeNaPlanu").whereEqualTo("p1Svojstvo", "trosak");
                            stavkePlana.get().addOnCompleteListener(task13 -> {
                                if (task13.isSuccessful()) {
                                    for (QueryDocumentSnapshot document2 : task13.getResult()) {
                                        getDatesPlanStavke(document1, document2, ostvareneStavke, startDate, endDate, generatorID);

                                    }
                                }
                            });

                        }
                    }
                }

            });
        }
        if (generatorID.equals("comp")) {
            planiraneStavke.clear();
            Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("stavke").whereEqualTo("svojstvoStavke", "Prihod");
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int y = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String iznosStavke, nazivStavke;
                        String targetDate = String.valueOf(document.getString("datumStavke"));
                        try {
                            if (validateDatOd(startDate, endDate, targetDate)) {
                                nazivStavke = String.valueOf(document.get("idGrupe"));
                                iznosStavke = String.valueOf(document.get("iznosStavke"));
                                planiraneStavke.add(new ArrayList<String>());
                                planiraneStavke.get(y).add(nazivStavke);
                                planiraneStavke.get(y).add(iznosStavke);
                                planiraneStavke.get(y).add(targetDate);
                                y++;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            setChartEntries(ostvareneStavke, planiraneStavke, startDate, endDate, generatorID, isYearly);
        }, 1000);

    }

    public void getDatesPlanStavke(QueryDocumentSnapshot document1, QueryDocumentSnapshot document2, List<List<String>> ostvareneStavke, String startDate, String endDate, String generatorID) {

        Query stavkeGrupe = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi").document(document1.get("idPlana").toString().trim())
                .collection("grupeNaPlanu").document(document2.getId()).collection("stavke");
        stavkeGrupe.get().addOnCompleteListener(task -> {
            List<List<String>> planiraneStavkeTemp = new ArrayList<List<String>>();
            int y = 0;
            for (QueryDocumentSnapshot document3 : task.getResult()) {
                String iznosStavke = String.valueOf(document3.get("iznosStavke"));
                String nazivStavke = String.valueOf(document3.get("idGrupe"));
                String targetDate = String.valueOf(document3.getString("datumStavke"));
                planiraneStavkeTemp.add(new ArrayList<String>());
                planiraneStavkeTemp.get(y).add(nazivStavke);
                planiraneStavkeTemp.get(y).add(iznosStavke);
                planiraneStavkeTemp.get(y).add(targetDate);
                y++;
            }
            planiraneStavke.addAll(planiraneStavkeTemp);
        });

    }

    public void setChartEntries(List<List<String>> ostvareneStavke, List<List<String>> planiraneStavke, String startDate, String endDate, String generatorID, Boolean isYearly) {

        ArrayList<Entry> ostvareniEntries = new ArrayList<>();
        ArrayList<Entry> planiraniEntries = new ArrayList<>();
        ArrayList<String> datumiNastanka = new ArrayList<>();
        String entryDatum, entryDatum1;
        float entryIznos;
        int brojac = 0;
        for (int i = 0; i < ostvareneStavke.size(); i++) {
            entryDatum = ostvareneStavke.get(i).get(2);
            if (!(datumiNastanka.contains(entryDatum))) {
                float total = 0;
                for (int x = 0; x < ostvareneStavke.size(); x++) {
                    entryIznos = Float.valueOf(ostvareneStavke.get(x).get(1));
                    entryDatum1 = ostvareneStavke.get(x).get(2);
                    if (entryDatum.equals(entryDatum1)) {
                        total = total + entryIznos;
                    }

                }
                planiraniEntries.add(new Entry(brojac, addToList1(entryDatum, planiraneStavke)));
                datumiNastanka.add(entryDatum);
                ostvareniEntries.add(new Entry(brojac, total));
                brojac++;
            }
        }
        for (int i = 0; i < planiraneStavke.size(); i++) {
            entryDatum = planiraneStavke.get(i).get(2);
            if (!(datumiNastanka.contains(entryDatum))) {
                float total = 0;
                for (int x = 0; x < planiraneStavke.size(); x++) {
                    entryIznos = Float.valueOf(planiraneStavke.get(x).get(1));
                    entryDatum1 = planiraneStavke.get(x).get(2);
                    if (entryDatum.equals(entryDatum1)) {
                        total = total + entryIznos;
                    }

                }
                ostvareniEntries.add(new Entry(brojac, addToList1(entryDatum, ostvareneStavke)));
                datumiNastanka.add(entryDatum);
                planiraniEntries.add(new Entry(brojac, total));
                brojac++;
            }
        }


        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                sortLists(datumiNastanka, ostvareniEntries, planiraniEntries, startDate, endDate, generatorID, isYearly);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }, 1);


    }

    public float addToList1(String datumStavke, List<List<String>> grupePoStavkama) {
        float totalGrupe = 0;
        float iznosStavke;
        if (!(grupePoStavkama.isEmpty())) {
            for (int x = 0; x < grupePoStavkama.size(); x++) {
                if (datumStavke.equals(grupePoStavkama.get(x).get(2))) {
                    iznosStavke = Float.valueOf(grupePoStavkama.get(x).get(1));
                    totalGrupe = totalGrupe + iznosStavke;
                }
            }
        }
        if (totalGrupe != 0)
            return totalGrupe;
        else
            return 0f;
    }

    public void sortLists(ArrayList<String> datumiNastanka, ArrayList<Entry> planiraniEntries, ArrayList<Entry> ostvareniEntries, String startDate, String endDate, String generatorID, Boolean isYearly) throws ParseException {

        List<List<String>> sortList = new ArrayList<>();
        ArrayList<Entry> razlikaEntries = new ArrayList<>();
        ArrayList<BarEntry> planiraniBar = new ArrayList<>();
        ArrayList<BarEntry> ostvareniBar = new ArrayList<>();
        for (int i = 0; i < datumiNastanka.size(); i++) {
            sortList.add(new ArrayList<String>());
            sortList.get(i).add(datumiNastanka.get(i));
            sortList.get(i).add(String.valueOf(planiraniEntries.get(i).getY()));
            sortList.get(i).add(String.valueOf(ostvareniEntries.get(i).getY()));
        }
        sortList.size();
        datumiNastanka.clear();
        planiraniEntries.clear();
        ostvareniEntries.clear();

        if (isYearly) {
            for (int i = 1; i <= 12; i++) {
                String targetMonth = "";
                float mjesecniTotalP = 0;
                float mjesecniTotlO = 0;
                if (i < 10)
                    targetMonth = "15.0" + i + ".2021";
                else
                    targetMonth = "15." + i + ".2021";

                for (int x = 0; x < sortList.size(); x++) {
                    if (isInMonth(targetMonth, sortList.get(x).get(0))) {
                        mjesecniTotalP = mjesecniTotalP + Float.parseFloat(sortList.get(x).get(1));
                        mjesecniTotlO = mjesecniTotlO + Float.parseFloat(sortList.get(x).get(2));
                        sortList.get(x).clear();
                        sortList.get(x).add("31.12.2100");
                    }
                }

                datumiNastanka.add(getMonth(i));
                razlikaEntries.add(new Entry(i, mjesecniTotlO - mjesecniTotalP));
                planiraniEntries.add(new Entry(i, mjesecniTotlO));
                ostvareniEntries.add(new Entry(i, mjesecniTotalP));
                planiraniBar.add(new BarEntry(i, mjesecniTotlO));
                ostvareniBar.add(new BarEntry(i, mjesecniTotalP));
            }
        } else {

            for (int brojac = 0; brojac < sortList.size(); brojac++) {
                String sNajmanjiDatum = "01.01.2100";
                String sTargetDatum;
                float ostvareniIznos = 0;
                float planiraniIznos = 0;
                int index = 0;
                for (int i = 0; i < sortList.size(); i++) {

                    sTargetDatum = sortList.get(i).get(0);

                    if (getNajmanji(sNajmanjiDatum, sTargetDatum)) {
                        sNajmanjiDatum = sTargetDatum;
                        planiraniIznos = Float.parseFloat(sortList.get(i).get(1));
                        ostvareniIznos = Float.parseFloat(sortList.get(i).get(2));
                        index = i;
                    }
                }
                sortList.get(index).clear();
                sortList.get(index).add("31.12.2100");
                datumiNastanka.add(sNajmanjiDatum);
                razlikaEntries.add(new Entry(brojac, ostvareniIznos - planiraniIznos));
                planiraniEntries.add(new Entry(brojac, ostvareniIznos));
                ostvareniEntries.add(new Entry(brojac, planiraniIznos));
                planiraniBar.add(new BarEntry(brojac, ostvareniIznos));
                ostvareniBar.add(new BarEntry(brojac, planiraniIznos));
            }
        }

        if (datumiNastanka.isEmpty()) {
            Toast.makeText(getActivity(), "Ne postoje uneseni podatci za odabrano razdoblje", Toast.LENGTH_SHORT).show();
        } else {
            generateLineChart(datumiNastanka, razlikaEntries, startDate, endDate, generatorID, isYearly);
            generateGroupedBarChart(datumiNastanka, planiraniBar, ostvareniBar, razlikaEntries, startDate, endDate, generatorID);
        }

    }

    public Boolean isInMonth(String targetMonth, String targetDate) throws ParseException {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        Date targetDateD = sdf.parse(targetDate);
        Date targetMonthD = sdf.parse(targetMonth);


        cal1.setTime(targetDateD);
        cal2.setTime(targetMonthD);

        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
            if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    public boolean getNajmanji(String sNajmanjiDatum, String sTargetDatum) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        Date dNajmanji = sdf.parse(sNajmanjiDatum);
        Date tDate = sdf.parse(sTargetDatum);
        if (tDate.before(dNajmanji)) {
            return true;
        } else {
            return false;
        }

    }

    public void generateGroupedBarChart(ArrayList<String> datumiNastanka, ArrayList<BarEntry> planiraniEntries, ArrayList<BarEntry> ostvareniEntries, ArrayList<Entry> razlikaEntries, String startDate, String endDate, String generatorID) {


        if (groupedBarChart.isEmpty()) {
            if (sharedpref.loadNightModeState()) {
                if (generatorID.equals("comp")) {
                    BarDataSet barDataSet = new BarDataSet(planiraniEntries, "Prihodi");
                    barDataSet.setColor(Color.GREEN);
                    barDataSet.setValueTextColor(Color.WHITE);
                    barDataSet.setValueTextSize(10f);

                    BarDataSet barDataSet1 = new BarDataSet(ostvareniEntries, "Troškovi");
                    barDataSet1.setColor(Color.RED);
                    barDataSet1.setValueTextColor(Color.WHITE);
                    barDataSet1.setValueTextSize(10f);

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(barDataSet);
                    dataSets.add(barDataSet1);


                    BarData barData = new BarData(barDataSet, barDataSet1);
                    groupedBarChart.setData(barData);

                    Legend legend = groupedBarChart.getLegend();
                    legend.setTextColor(Color.WHITE);
                    legend.setTextSize(10f);

                    Description des = groupedBarChart.getDescription();
                    des.setEnabled(false);

                    YAxis leftAxis = groupedBarChart.getAxisLeft();
                    leftAxis.setTextColor(Color.WHITE);
                    leftAxis.setTextSize(10f);

                    YAxis rightAxis = groupedBarChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    XAxis xAxis = groupedBarChart.getXAxis();
                    xAxis.setTextColor(Color.WHITE);
                    xAxis.setTextSize(10f);
                    xAxis.setYOffset(0f);
                    xAxis.setXOffset(0f);
                    xAxis.setLabelCount(datumiNastanka.size() + 1, true);
                    xAxis.setLabelRotationAngle(-90);
                    xAxis.setGranularity(1f);


                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            if ((int) value < datumiNastanka.size()) {
                                return datumiNastanka.get((int) value);
                            } else {
                                return " ";
                            }
                        }
                    });


                    setBarWidth(barData, datumiNastanka, dataSets);
                    groupedBarChart.setExtraTopOffset(40f);
                    groupedBarChart.invalidate();
                } else {
                    BarDataSet barDataSet = new BarDataSet(planiraniEntries, "Planirani");
                    barDataSet.setColor(Color.RED);
                    barDataSet.setValueTextColor(Color.WHITE);
                    barDataSet.setValueTextSize(10f);

                    BarDataSet barDataSet1 = new BarDataSet(ostvareniEntries, "Ostvareni");
                    barDataSet1.setColor(Color.GREEN);
                    barDataSet1.setValueTextColor(Color.WHITE);
                    barDataSet1.setValueTextSize(10f);

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(barDataSet);
                    dataSets.add(barDataSet1);


                    BarData barData = new BarData(barDataSet, barDataSet1);
                    groupedBarChart.setData(barData);

                    Legend legend = groupedBarChart.getLegend();
                    legend.setTextColor(Color.WHITE);
                    legend.setTextSize(10f);

                    Description des = groupedBarChart.getDescription();
                    des.setEnabled(false);

                    YAxis leftAxis = groupedBarChart.getAxisLeft();
                    leftAxis.setTextColor(Color.WHITE);
                    leftAxis.setTextSize(10f);

                    YAxis rightAxis = groupedBarChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    XAxis xAxis = groupedBarChart.getXAxis();
                    xAxis.setTextColor(Color.WHITE);
                    xAxis.setTextSize(10f);
                    xAxis.setYOffset(0f);
                    xAxis.setXOffset(0f);
                    xAxis.setLabelCount(datumiNastanka.size() + 1, true);
                    xAxis.setLabelRotationAngle(-90);
                    xAxis.setGranularity(1f);


                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            if ((int) value < datumiNastanka.size()) {
                                return datumiNastanka.get((int) value);
                            } else {
                                return " ";
                            }
                        }
                    });


                    setBarWidth(barData, datumiNastanka, dataSets);
                    groupedBarChart.setExtraTopOffset(40f);
                    groupedBarChart.invalidate();
                }
            } else {
                if (generatorID.equals("comp")) {
                    BarDataSet barDataSet = new BarDataSet(planiraniEntries, "Prihodi");
                    barDataSet.setColor(Color.GREEN);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(10f);

                    BarDataSet barDataSet1 = new BarDataSet(ostvareniEntries, "Troškovi");
                    barDataSet1.setColor(Color.RED);
                    barDataSet1.setValueTextColor(Color.BLACK);
                    barDataSet1.setValueTextSize(10f);

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(barDataSet);
                    dataSets.add(barDataSet1);


                    BarData barData = new BarData(barDataSet, barDataSet1);
                    groupedBarChart.setData(barData);

                    Legend legend = groupedBarChart.getLegend();
                    legend.setTextColor(Color.BLACK);
                    legend.setTextSize(10f);

                    Description des = groupedBarChart.getDescription();
                    des.setEnabled(false);

                    YAxis leftAxis = groupedBarChart.getAxisLeft();
                    leftAxis.setTextColor(Color.BLACK);
                    leftAxis.setTextSize(10f);

                    YAxis rightAxis = groupedBarChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    XAxis xAxis = groupedBarChart.getXAxis();
                    xAxis.setTextColor(Color.BLACK);
                    xAxis.setTextSize(10f);
                    xAxis.setYOffset(0f);
                    xAxis.setXOffset(0f);
                    xAxis.setLabelCount(datumiNastanka.size() + 1, true);
                    xAxis.setLabelRotationAngle(-90);
                    xAxis.setGranularity(1f);


                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            if ((int) value < datumiNastanka.size()) {
                                return datumiNastanka.get((int) value);
                            } else {
                                return " ";
                            }
                        }
                    });


                    setBarWidth(barData, datumiNastanka, dataSets);
                    groupedBarChart.setExtraTopOffset(40f);
                    groupedBarChart.invalidate();
                } else {
                    BarDataSet barDataSet = new BarDataSet(planiraniEntries, "Planirani");
                    barDataSet.setColor(Color.RED);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(10f);

                    BarDataSet barDataSet1 = new BarDataSet(ostvareniEntries, "Ostvareni");
                    barDataSet1.setColor(Color.GREEN);
                    barDataSet1.setValueTextColor(Color.BLACK);
                    barDataSet1.setValueTextSize(10f);

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(barDataSet);
                    dataSets.add(barDataSet1);


                    BarData barData = new BarData(barDataSet, barDataSet1);
                    groupedBarChart.setData(barData);

                    Legend legend = groupedBarChart.getLegend();
                    legend.setTextColor(Color.BLACK);
                    legend.setTextSize(10f);

                    Description des = groupedBarChart.getDescription();
                    des.setEnabled(false);

                    YAxis leftAxis = groupedBarChart.getAxisLeft();
                    leftAxis.setTextColor(Color.BLACK);
                    leftAxis.setTextSize(10f);

                    YAxis rightAxis = groupedBarChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    XAxis xAxis = groupedBarChart.getXAxis();
                    xAxis.setTextColor(Color.BLACK);
                    xAxis.setTextSize(10f);
                    xAxis.setYOffset(0f);
                    xAxis.setXOffset(0f);
                    xAxis.setLabelCount(datumiNastanka.size() + 1, true);
                    xAxis.setLabelRotationAngle(-90);
                    xAxis.setGranularity(1f);


                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            if ((int) value < datumiNastanka.size()) {
                                return datumiNastanka.get((int) value);
                            } else {
                                return " ";
                            }
                        }
                    });


                    setBarWidth(barData, datumiNastanka, dataSets);
                    groupedBarChart.setExtraTopOffset(40f);
                    groupedBarChart.invalidate();
                }
            }

        }


    }

    private void setBarWidth(BarData barData, ArrayList<String> datumiNastanka, ArrayList<IBarDataSet> dataSets) {
        float defaultBarWidth = -1;
        if (dataSets.size() > 1) {
            float barSpace = 0.02f;
            float groupSpace = 0.3f;
            defaultBarWidth = (1 - groupSpace) / dataSets.size() - barSpace;
            if (defaultBarWidth >= 0) {
                barData.setBarWidth(defaultBarWidth);
            } else {
                Toast.makeText(getActivity(), "Default Barwdith " + defaultBarWidth, Toast.LENGTH_SHORT).show();
            }
            int groupCount = datumiNastanka.size();
            if (groupCount != -1) {
                groupedBarChart.getXAxis().setAxisMinimum(0);
                groupedBarChart.getXAxis().setAxisMaximum(0 + groupedBarChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                groupedBarChart.getXAxis().setCenterAxisLabels(true);
            } else {
                Toast.makeText(getActivity(), "no of bar groups is " + groupCount, Toast.LENGTH_SHORT).show();
            }

            groupedBarChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
            groupedBarChart.invalidate();
        }
    }

    public void generateLineChart(ArrayList<String> datumiNastanka, ArrayList<Entry> razlikaEntries, String startDate, String endDate, String generatorID, Boolean isYearly) {


        if (lineChart.isEmpty()) {
            if (generatorID.equals("comp")) {
                if (sharedpref.loadNightModeState()) {
                    LineDataSet razlikaDataSet = new LineDataSet(razlikaEntries, "Razlika prihodi - troškovi");
                    razlikaDataSet.setCircleColor(Color.CYAN);
                    razlikaDataSet.setColor(Color.CYAN);
                    razlikaDataSet.setValueTextColor(Color.WHITE);
                    razlikaDataSet.setValueTextSize(10f);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(razlikaDataSet);

                    LineData lineData = new LineData(dataSets);

                    lineChart.setData(lineData);
                    Legend legend = lineChart.getLegend();
                    legend.setTextColor(Color.WHITE);
                    legend.setTextSize(10f);

                    Description des = lineChart.getDescription();
                    des.setEnabled(false);

                    YAxis leftAxis = lineChart.getAxisLeft();
                    leftAxis.setTextColor(Color.WHITE);
                    leftAxis.setTextSize(10f);

                    YAxis rightAxis = lineChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setTextColor(Color.WHITE);
                    xAxis.setTextSize(10f);
                    xAxis.setYOffset(0f);
                    xAxis.setXOffset(0f);
                    xAxis.setGranularity(1f);
                    xAxis.setLabelCount(datumiNastanka.size(), true);
                    xAxis.setLabelRotationAngle(-90);

                    if (isYearly) {
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                if (!(datumiNastanka.isEmpty())) {
                                    if ((int) value < datumiNastanka.size() + 1) {
                                        //Toast.makeText(getActivity(), value+"", Toast.LENGTH_SHORT).show();
                                        return datumiNastanka.get((int) value - 1);
                                    } else {
                                        return " ";
                                    }
                                } else {
                                    return " ";
                                }
                            }
                        });
                    } else {
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                if (!(datumiNastanka.isEmpty()) && value != -1) {
                                    if ((int) value < datumiNastanka.size()) {
                                        //Toast.makeText(getActivity(), value+"", Toast.LENGTH_SHORT).show();
                                        return datumiNastanka.get((int) value);
                                    } else {
                                        return " ";
                                    }
                                } else {
                                    return " ";
                                }
                            }
                        });
                    }


                    lineChart.setExtraTopOffset(40f);
                    lineChart.invalidate();
                } else {
                    LineDataSet razlikaDataSet = new LineDataSet(razlikaEntries, "Razlika prihodi - troškovi");
                    razlikaDataSet.setCircleColor(Color.CYAN);
                    razlikaDataSet.setColor(Color.CYAN);
                    razlikaDataSet.setValueTextColor(Color.BLACK);
                    razlikaDataSet.setValueTextSize(10f);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(razlikaDataSet);


                    LineData lineData = new LineData(dataSets);

                    lineChart.setData(lineData);
                    Legend legend = lineChart.getLegend();
                    legend.setTextColor(Color.BLACK);
                    legend.setTextSize(10f);

                    Description des = lineChart.getDescription();
                    des.setEnabled(false);

                    YAxis leftAxis = lineChart.getAxisLeft();
                    leftAxis.setTextColor(Color.BLACK);
                    leftAxis.setTextSize(10f);

                    YAxis rightAxis = lineChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setTextColor(Color.BLACK);
                    xAxis.setTextSize(10f);
                    xAxis.setYOffset(0f);
                    xAxis.setXOffset(0f);
                    xAxis.setGranularity(1f);
                    xAxis.setLabelCount(datumiNastanka.size(), true);
                    xAxis.setLabelRotationAngle(-90);


                    if (isYearly) {
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                if (!(datumiNastanka.isEmpty())) {
                                    if ((int) value < datumiNastanka.size() + 1) {
                                        return datumiNastanka.get((int) value - 1);
                                    } else {
                                        return " ";
                                    }
                                } else {
                                    return " ";
                                }
                            }
                        });
                    } else {
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                if (!(datumiNastanka.isEmpty()) && value != -1) {
                                    if ((int) value < datumiNastanka.size()) {
                                        return datumiNastanka.get((int) value);
                                    } else {
                                        return " ";
                                    }
                                } else {
                                    return " ";
                                }
                            }
                        });
                    }
                    ;

                    lineChart.setExtraTopOffset(40f);
                    lineChart.invalidate();
                }
            } else {
                if (sharedpref.loadNightModeState()) {
                    LineDataSet razlikaDataSet = new LineDataSet(razlikaEntries, "Razlika planirani - ostvareni");
                    razlikaDataSet.setCircleColor(Color.CYAN);
                    razlikaDataSet.setColor(Color.CYAN);
                    razlikaDataSet.setValueTextColor(Color.WHITE);
                    razlikaDataSet.setValueTextSize(10f);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(razlikaDataSet);


                    LineData lineData = new LineData(dataSets);

                    lineChart.setData(lineData);
                    Legend legend = lineChart.getLegend();
                    legend.setTextColor(Color.WHITE);
                    legend.setTextSize(10f);

                    Description des = lineChart.getDescription();
                    des.setEnabled(false);

                    YAxis leftAxis = lineChart.getAxisLeft();
                    leftAxis.setTextColor(Color.WHITE);
                    leftAxis.setTextSize(10f);

                    YAxis rightAxis = lineChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setTextColor(Color.WHITE);
                    xAxis.setTextSize(10f);
                    xAxis.setYOffset(0f);
                    xAxis.setXOffset(0f);
                    xAxis.setGranularity(1f);
                    xAxis.setLabelCount(datumiNastanka.size(), true);
                    xAxis.setLabelRotationAngle(-90);

                    if (isYearly) {
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                if (!(datumiNastanka.isEmpty())) {
                                    if ((int) value < datumiNastanka.size() + 1) {
                                        return datumiNastanka.get((int) value - 1);
                                    } else {
                                        return " ";
                                    }
                                } else {
                                    return " ";
                                }
                            }
                        });
                    } else {
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                if (!(datumiNastanka.isEmpty()) && value != -1) {
                                    if ((int) value < datumiNastanka.size()) {
                                        return datumiNastanka.get((int) value);
                                    } else {
                                        return " ";
                                    }
                                } else {
                                    return " ";
                                }
                            }
                        });
                    }


                    lineChart.setExtraTopOffset(40f);
                    lineChart.invalidate();
                } else {
                    LineDataSet razlikaDataSet = new LineDataSet(razlikaEntries, "Razlika planirani - ostvareni");
                    razlikaDataSet.setCircleColor(Color.CYAN);
                    razlikaDataSet.setColor(Color.CYAN);
                    razlikaDataSet.setValueTextColor(Color.BLACK);
                    razlikaDataSet.setValueTextSize(10f);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(razlikaDataSet);


                    LineData lineData = new LineData(dataSets);

                    lineChart.setData(lineData);
                    Legend legend = lineChart.getLegend();
                    legend.setTextColor(Color.BLACK);
                    legend.setTextSize(10f);

                    Description des = lineChart.getDescription();
                    des.setEnabled(false);

                    YAxis leftAxis = lineChart.getAxisLeft();
                    leftAxis.setTextColor(Color.BLACK);
                    leftAxis.setTextSize(10f);

                    YAxis rightAxis = lineChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setTextColor(Color.BLACK);
                    xAxis.setTextSize(10f);
                    xAxis.setYOffset(0f);
                    xAxis.setXOffset(0f);
                    xAxis.setGranularity(1f);
                    xAxis.setLabelCount(datumiNastanka.size(), true);
                    xAxis.setLabelRotationAngle(-90);

                    if (isYearly) {
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                if (!(datumiNastanka.isEmpty())) {
                                    if ((int) value < datumiNastanka.size() + 1) {
                                        return datumiNastanka.get((int) value - 1);
                                    } else {
                                        return " ";
                                    }
                                } else {
                                    return " ";
                                }
                            }
                        });
                    } else {
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                if (!(datumiNastanka.isEmpty()) && value != -1) {
                                    if ((int) value < datumiNastanka.size()) {

                                        return datumiNastanka.get((int) value);
                                    } else {
                                        return " ";
                                    }
                                } else {
                                    return " ";
                                }
                            }
                        });
                    }


                    lineChart.setExtraTopOffset(40f);
                    lineChart.invalidate();
                }
            }
        }

    }

    public void getData(View v, String generatorID, int idGrafa, String startDate, String endDate) {

        oPieChart = v.findViewById(R.id.oPieChart);
        oBarChart = v.findViewById(R.id.oBarChart);
        oRadarChart = v.findViewById(R.id.oRadarChart);

        if (idGrafa == 1) {
            oPieChart.setVisibility(View.VISIBLE);
            oBarChart.setVisibility(View.GONE);
            oRadarChart.setVisibility(View.GONE);

            if (generatorID.equals("prihod")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Prihod");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float pTotal = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            float iznosStavke;
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    iznosStavke = Float.valueOf(String.valueOf(document.get("iznosStavke")));
                                    pTotal = pTotal + iznosStavke;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        getStavkePlana(pTotal, startDate, endDate, generatorID);

                    }
                });
            }
            if (generatorID.equals("trosak")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Trošak");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float pTotal = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            float iznosStavke;
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    iznosStavke = Float.valueOf(String.valueOf(document.get("iznosStavke")));
                                    pTotal = pTotal + iznosStavke;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        getStavkePlana(pTotal, startDate, endDate, generatorID);

                    }
                });

            }
            if (generatorID.equals("comp")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Trošak");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float pTotal = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            float iznosStavke;
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    iznosStavke = Float.valueOf(String.valueOf(document.get("iznosStavke")));
                                    pTotal = pTotal + iznosStavke;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        getStavkePlana(pTotal, startDate, endDate, generatorID);

                    }
                });

            }

            oPieChart.setVisibility(View.GONE);
            oPieChart.setVisibility(View.VISIBLE);

        }
        if (idGrafa == 2) {

            oPieChart.setVisibility(View.GONE);
            oBarChart.setVisibility(View.VISIBLE);
            oRadarChart.setVisibility(View.GONE);

            if (generatorID.equals("prihod")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Prihod");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float pTotal = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            float iznosStavke;
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    iznosStavke = Float.valueOf(String.valueOf(document.get("iznosStavke")));
                                    pTotal = pTotal + iznosStavke;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        getStavkePlana(pTotal, startDate, endDate, generatorID);

                    }
                });
            }
            if (generatorID.equals("trosak")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Trošak");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float pTotal = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            float iznosStavke;
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    iznosStavke = Float.valueOf(String.valueOf(document.get("iznosStavke")));
                                    pTotal = pTotal + iznosStavke;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        getStavkePlana(pTotal, startDate, endDate, generatorID);

                    }
                });

            }
            if (generatorID.equals("comp")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Trošak");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float pTotal = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            float iznosStavke;
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    iznosStavke = Float.valueOf(String.valueOf(document.get("iznosStavke")));
                                    pTotal = pTotal + iznosStavke;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        getStavkePlana(pTotal, startDate, endDate, generatorID);

                    }
                });

            }


        }
        if (idGrafa == 3) {

            oPieChart.setVisibility(View.GONE);
            oBarChart.setVisibility(View.GONE);
            oRadarChart.setVisibility(View.VISIBLE);


            if (generatorID.equals("prihod")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Prihod");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float pTotal = 0;
                        int i = 0;
                        String iznosStavke;
                        String grupaStavke = "";

                        List<List<String>> grupePoStavkama = new ArrayList<List<String>>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    iznosStavke = String.valueOf(document.get("iznosStavke")).trim();
                                    grupaStavke = String.valueOf(document.getString("idGrupe")).trim();
                                    grupePoStavkama.add(new ArrayList<String>());
                                    grupePoStavkama.get(i).add(grupaStavke);
                                    grupePoStavkama.get(i).add(iznosStavke);
                                    i++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        getStavkePlanaRadar(grupePoStavkama, startDate, endDate, generatorID);


                    }
                });
            }
            if (generatorID.equals("trosak")) {
                Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("stavke").whereEqualTo("svojstvoStavke", "Trošak");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float pTotal = 0;
                        int i = 0;
                        String iznosStavke;
                        String grupaStavke = "";

                        List<List<String>> grupePoStavkama = new ArrayList<List<String>>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String targetDate = String.valueOf(document.getString("datumStavke"));
                            try {
                                if (validateDatOd(startDate, endDate, targetDate)) {
                                    iznosStavke = String.valueOf(document.get("iznosStavke")).trim();
                                    grupaStavke = String.valueOf(document.getString("idGrupe")).trim();
                                    grupePoStavkama.add(new ArrayList<String>());
                                    grupePoStavkama.get(i).add(grupaStavke);
                                    grupePoStavkama.get(i).add(iznosStavke);
                                    i++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        getStavkePlanaRadar(grupePoStavkama, startDate, endDate, generatorID);

                    }
                });

            }

        }

    }

    public void getStavkePlana(final float fTotal, String startDate, String endDate, String generatorID) {
        if (generatorID.equals("prihod")) {
            Query queryPlan = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("planovi");
            queryPlan.get().addOnCompleteListener(task12 -> {
                if (task12.isSuccessful()) {
                    for (QueryDocumentSnapshot document1 : task12.getResult()) {
                        if (startDate.equals(document1.get("pocetniDatum")) && endDate.equals(document1.get("zavrsniDatum"))) {
                            Query stavkePlana = fStore.collection("korisnici").document(sharedpref.loadUserID())
                                    .collection("planovi").document(document1.get("idPlana").toString().trim())
                                    .collection("grupeNaPlanu").whereEqualTo("p1Svojstvo", "prihod");
                            stavkePlana.get().addOnCompleteListener(task13 -> {
                                if (task13.isSuccessful()) {
                                    float tTotal = 0;
                                    float iznosStavke1;
                                    for (QueryDocumentSnapshot document2 : task13.getResult()) {
                                        iznosStavke1 = Float.valueOf(String.valueOf(document2.get("p1Iznos")));
                                        tTotal = tTotal + iznosStavke1;
                                    }
                                    ArrayList<PieEntry> ukupno = new ArrayList<>();
                                    ArrayList<BarEntry> ukupnoBar = new ArrayList<>();
                                    ukupno.add(new PieEntry(fTotal, "Ostvareni prihod"));
                                    ukupno.add(new PieEntry(tTotal, "Planirani prihod"));
                                    ukupnoBar.add(new BarEntry(0, fTotal));
                                    ukupnoBar.add(new BarEntry(1, tTotal));
                                    generatePieTotal(generatorID, ukupno);
                                    generateBarTotal(generatorID, ukupnoBar);
                                }
                            });

                        }
                    }
                }

            });
        }
        if (generatorID.equals("trosak")) {
            Query queryPlan = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("planovi");
            queryPlan.get().addOnCompleteListener(task12 -> {
                if (task12.isSuccessful()) {
                    for (QueryDocumentSnapshot document1 : task12.getResult()) {
                        if (startDate.equals(document1.get("pocetniDatum")) && endDate.equals(document1.get("zavrsniDatum"))) {

                            Query stavkePlana = fStore.collection("korisnici").document(sharedpref.loadUserID())
                                    .collection("planovi").document(document1.get("idPlana").toString().trim())
                                    .collection("grupeNaPlanu").whereEqualTo("p1Svojstvo", "trosak");
                            stavkePlana.get().addOnCompleteListener(task13 -> {
                                if (task13.isSuccessful()) {
                                    float tTotal = 0;
                                    float iznosStavke1;
                                    for (QueryDocumentSnapshot document2 : task13.getResult()) {
                                        iznosStavke1 = Float.valueOf(String.valueOf(document2.get("p1Iznos")));
                                        tTotal = tTotal + iznosStavke1;
                                    }
                                    ArrayList<PieEntry> ukupno = new ArrayList<>();
                                    ArrayList<BarEntry> ukupnoBar = new ArrayList<>();
                                    ukupno.add(new PieEntry(fTotal, "Nastali troškovi"));
                                    ukupno.add(new PieEntry(tTotal, "Planirani troškovi"));
                                    ukupnoBar.add(new BarEntry(0, fTotal));
                                    ukupnoBar.add(new BarEntry(1, tTotal));
                                    generatePieTotal(generatorID, ukupno);
                                    generateBarTotal(generatorID, ukupnoBar);
                                }
                            });

                        }
                    }
                }

            });
        }
        if (generatorID.equals("comp")) {
            Query query = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("stavke").whereEqualTo("svojstvoStavke", "Prihod");
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    float tTotal = 0;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        float iznosStavke;
                        String targetDate = String.valueOf(document.getString("datumStavke"));
                        try {
                            if (validateDatOd(startDate, endDate, targetDate)) {
                                iznosStavke = Float.valueOf(String.valueOf(document.get("iznosStavke")));
                                tTotal = tTotal + iznosStavke;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayList<PieEntry> ukupno = new ArrayList<>();
                    ArrayList<BarEntry> ukupnoBar = new ArrayList<>();
                    ukupno.add(new PieEntry(fTotal, "Troškovi"));
                    ukupno.add(new PieEntry(tTotal, "Prihodi"));
                    ukupnoBar.add(new BarEntry(0, fTotal));
                    ukupnoBar.add(new BarEntry(1, tTotal));
                    generatePieTotal(generatorID, ukupno);
                    generateBarTotal(generatorID, ukupnoBar);

                }
            });
        }
    }

    public void getStavkePlanaRadar(List<List<String>> grupePoStavkama, String startDate, String endDate, String generatorID) {
        if (generatorID.equals("prihod")) {
            Query queryPlan = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("planovi");
            queryPlan.get().addOnCompleteListener(task12 -> {
                if (task12.isSuccessful()) {
                    for (QueryDocumentSnapshot document1 : task12.getResult()) {
                        if (startDate.equals(document1.get("pocetniDatum")) && endDate.equals(document1.get("zavrsniDatum"))) {
                            Query stavkePlana = fStore.collection("korisnici").document(sharedpref.loadUserID())
                                    .collection("planovi").document(document1.get("idPlana").toString().trim())
                                    .collection("grupeNaPlanu").whereEqualTo("p1Svojstvo", "prihod");

                            stavkePlana.get().addOnCompleteListener(task13 -> {
                                if (task13.isSuccessful()) {
                                    String iznosGrupe;
                                    String nazivGrupe;
                                    int y = 0;
                                    List<List<String>> grupePoPlanu = new ArrayList<List<String>>();
                                    for (QueryDocumentSnapshot document2 : task13.getResult()) {
                                        iznosGrupe = String.valueOf(document2.get("p1Iznos")).trim();
                                        nazivGrupe = String.valueOf(document2.get("p1Naziv")).trim();
                                        grupePoPlanu.add(new ArrayList<String>());
                                        grupePoPlanu.get(y).add(nazivGrupe);
                                        grupePoPlanu.get(y).add(iznosGrupe);
                                        y++;

                                    }
                                    generateRadarTotalLists(grupePoStavkama, grupePoPlanu, generatorID);
                                }
                            });

                        }
                    }
                }

            });
        }
        if (generatorID.equals("trosak")) {
            Query queryPlan = fStore.collection("korisnici").document(sharedpref.loadUserID())
                    .collection("planovi");
            queryPlan.get().addOnCompleteListener(task12 -> {
                if (task12.isSuccessful()) {
                    for (QueryDocumentSnapshot document1 : task12.getResult()) {
                        if (startDate.equals(document1.get("pocetniDatum")) && endDate.equals(document1.get("zavrsniDatum"))) {
                            Query stavkePlana = fStore.collection("korisnici").document(sharedpref.loadUserID())
                                    .collection("planovi").document(document1.get("idPlana").toString().trim())
                                    .collection("grupeNaPlanu").whereEqualTo("p1Svojstvo", "trosak");

                            stavkePlana.get().addOnCompleteListener(task13 -> {
                                if (task13.isSuccessful()) {
                                    String iznosGrupe;
                                    String nazivGrupe;
                                    int y = 0;
                                    List<List<String>> grupePoPlanu = new ArrayList<List<String>>();
                                    for (QueryDocumentSnapshot document2 : task13.getResult()) {
                                        iznosGrupe = String.valueOf(document2.get("p1Iznos")).trim();
                                        nazivGrupe = String.valueOf(document2.get("p1Naziv")).trim();
                                        grupePoPlanu.add(new ArrayList<String>());
                                        grupePoPlanu.get(y).add(nazivGrupe);
                                        grupePoPlanu.get(y).add(iznosGrupe);
                                        y++;

                                    }
                                    generateRadarTotalLists(grupePoStavkama, grupePoPlanu, generatorID);
                                }
                            });

                        }
                    }
                }

            });
        }
    }

    public void generatePieTotal(String generatorID, ArrayList<PieEntry> ukupno) {
        PieDataSet pieDataSet = new PieDataSet(ukupno, "");
        pieDataSet.setValueTextSize(15f);

        if (generatorID.equals("prihod")) {
            if (sharedpref.loadNightModeState() == true) {
                pieDataSet.setColors(dColors);
                pieDataSet.setValueTextColor(Color.WHITE);
                PieData pieData = new PieData(pieDataSet);
                oPieChart.setData(pieData);
                oPieChart.getDescription().setEnabled(false);
                oPieChart.setCenterText("Plairani i ostvareni prihod");
                oPieChart.setCenterTextColor(Color.WHITE);
                oPieChart.setEntryLabelColor(Color.WHITE);
                oPieChart.setEntryLabelTextSize(0);
                Legend legend = oPieChart.getLegend();
                legend.setTextColor(Color.WHITE);
                oPieChart.setHoleColor(Color.TRANSPARENT);
                oPieChart.animate();
            } else {
                pieDataSet.setColors(lColors);
                pieDataSet.setValueTextColor(Color.BLACK);
                PieData pieData = new PieData(pieDataSet);
                oPieChart.setData(pieData);
                oPieChart.getDescription().setEnabled(false);
                oPieChart.setCenterText("Plairani i ostvareni prihod");
                oPieChart.setCenterTextColor(Color.BLACK);
                oPieChart.setEntryLabelColor(Color.BLACK);
                oPieChart.setEntryLabelTextSize(0);
                Legend legend = oPieChart.getLegend();
                legend.setTextColor(Color.BLACK);
                oPieChart.setHoleColor(Color.TRANSPARENT);
                oPieChart.animate();
            }
        }
        if (generatorID.equals("trosak")) {
            if (sharedpref.loadNightModeState() == true) {
                pieDataSet.setColors(dColors);
                pieDataSet.setValueTextColor(Color.WHITE);
                PieData pieData = new PieData(pieDataSet);
                oPieChart.setData(pieData);
                oPieChart.getDescription().setEnabled(false);
                oPieChart.setCenterText("Plairani i nastali troškovi");
                oPieChart.setCenterTextColor(Color.WHITE);
                oPieChart.setEntryLabelColor(Color.WHITE);
                oPieChart.setEntryLabelTextSize(0);
                Legend legend = oPieChart.getLegend();
                legend.setTextColor(Color.WHITE);
                oPieChart.setHoleColor(Color.TRANSPARENT);
                oPieChart.animate();
            } else {
                pieDataSet.setColors(lColors);
                pieDataSet.setValueTextColor(Color.BLACK);
                PieData pieData = new PieData(pieDataSet);
                oPieChart.setData(pieData);
                oPieChart.getDescription().setEnabled(false);
                oPieChart.setCenterText("Plairani i nastali troškovi");
                oPieChart.setCenterTextColor(Color.BLACK);
                oPieChart.setEntryLabelColor(Color.BLACK);
                oPieChart.setEntryLabelTextSize(0);
                Legend legend = oPieChart.getLegend();
                legend.setTextColor(Color.BLACK);
                oPieChart.setHoleColor(Color.TRANSPARENT);
                oPieChart.animate();
            }
        }
        if (generatorID.equals("comp")) {
            if (sharedpref.loadNightModeState() == true) {
                pieDataSet.setColors(dColors);
                pieDataSet.setValueTextColor(Color.WHITE);
                PieData pieData = new PieData(pieDataSet);
                oPieChart.setData(pieData);
                oPieChart.getDescription().setEnabled(false);
                oPieChart.setCenterText("Ostvareni troškovi i prihodi");
                oPieChart.setCenterTextColor(Color.WHITE);
                oPieChart.setEntryLabelColor(Color.WHITE);
                oPieChart.setEntryLabelTextSize(0);
                Legend legend = oPieChart.getLegend();
                legend.setTextColor(Color.WHITE);
                oPieChart.setHoleColor(Color.TRANSPARENT);
                oPieChart.animate();
            } else {
                pieDataSet.setColors(lColors);
                pieDataSet.setValueTextColor(Color.BLACK);
                PieData pieData = new PieData(pieDataSet);
                oPieChart.setData(pieData);
                oPieChart.getDescription().setEnabled(false);
                oPieChart.setCenterText("Ostvareni troškovi i prihodi");
                oPieChart.setCenterTextColor(Color.BLACK);
                oPieChart.setEntryLabelColor(Color.BLACK);
                oPieChart.setEntryLabelTextSize(0);
                Legend legend = oPieChart.getLegend();
                legend.setTextColor(Color.BLACK);
                oPieChart.setHoleColor(Color.TRANSPARENT);
                oPieChart.animate();
            }
        }

    }

    public void generateBarTotal(String generatorID, ArrayList<BarEntry> ukupno) {
        BarDataSet barDataSet = new BarDataSet(ukupno, "");
        barDataSet.setValueTextSize(15f);
        if (generatorID.equals("prihod")) {
            if (sharedpref.loadNightModeState() == true) {
                barDataSet.setColors(dColors);
                barDataSet.setValueTextColor(Color.WHITE);
                BarData barData = new BarData(barDataSet);
                oBarChart.setData(barData);
                oBarChart.getDescription().setEnabled(false);
                Legend legend = oBarChart.getLegend();
                legend.setTextColor(Color.WHITE);
                LegendEntry l1 = new LegendEntry("Ostvareni prihod", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(0, 255, 255));
                LegendEntry l2 = new LegendEntry("Planirani prihod", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(0, 0, 0));
                legend.setCustom(new LegendEntry[]{l1, l2});
                legend.setWordWrapEnabled(true);
                XAxis xAxis = oBarChart.getXAxis();
                xAxis.setTextColor(Color.TRANSPARENT);
                YAxis leftAxis = oBarChart.getAxisLeft();
                YAxis rightAxis = oBarChart.getAxisRight();
                rightAxis.setEnabled(false);
                leftAxis.setTextColor(Color.WHITE);
                oBarChart.invalidate();
                oBarChart.animate();
            } else {
                barDataSet.setColors(lColors);
                barDataSet.setValueTextColor(Color.BLACK);
                BarData barData = new BarData(barDataSet);
                oBarChart.setData(barData);
                oBarChart.getDescription().setEnabled(false);
                Legend legend = oBarChart.getLegend();
                legend.setTextColor(Color.BLACK);
                LegendEntry l1 = new LegendEntry("Ostvareni prihod", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(0, 255, 255));
                LegendEntry l2 = new LegendEntry("Planirani prihod", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(192, 192, 192));
                legend.setCustom(new LegendEntry[]{l1, l2});
                legend.setWordWrapEnabled(true);
                XAxis xAxis = oBarChart.getXAxis();
                xAxis.setTextColor(Color.TRANSPARENT);
                YAxis leftAxis = oBarChart.getAxisLeft();
                YAxis rightAxis = oBarChart.getAxisRight();
                rightAxis.setEnabled(false);
                leftAxis.setTextColor(Color.BLACK);
                oBarChart.invalidate();
                oBarChart.animate();
            }
        }
        if (generatorID.equals("trosak")) {
            if (sharedpref.loadNightModeState() == true) {
                barDataSet.setColors(dColors);
                barDataSet.setValueTextColor(Color.WHITE);
                BarData barData = new BarData(barDataSet);
                oBarChart.setData(barData);
                oBarChart.getDescription().setEnabled(false);
                Legend legend = oBarChart.getLegend();
                legend.setTextColor(Color.WHITE);
                LegendEntry l1 = new LegendEntry("Nastali troškovi", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(0, 255, 255));
                LegendEntry l2 = new LegendEntry("Planirani troškovi", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(0, 0, 0));
                legend.setCustom(new LegendEntry[]{l1, l2});
                legend.setWordWrapEnabled(true);
                XAxis xAxis = oBarChart.getXAxis();
                xAxis.setTextColor(Color.TRANSPARENT);
                YAxis leftAxis = oBarChart.getAxisLeft();
                YAxis rightAxis = oBarChart.getAxisRight();
                rightAxis.setEnabled(false);
                leftAxis.setTextColor(Color.WHITE);
                oBarChart.animate();
            } else {
                barDataSet.setColors(lColors);
                barDataSet.setValueTextColor(Color.BLACK);
                BarData barData = new BarData(barDataSet);
                oBarChart.setData(barData);
                oBarChart.getDescription().setEnabled(false);
                Legend legend = oBarChart.getLegend();
                legend.setTextColor(Color.BLACK);
                LegendEntry l1 = new LegendEntry("Nastali troškovi", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(0, 255, 255));
                LegendEntry l2 = new LegendEntry("Planirani troškovi", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(192, 192, 192));
                legend.setCustom(new LegendEntry[]{l1, l2});
                legend.setWordWrapEnabled(true);
                XAxis xAxis = oBarChart.getXAxis();
                xAxis.setTextColor(Color.TRANSPARENT);
                YAxis leftAxis = oBarChart.getAxisLeft();
                YAxis rightAxis = oBarChart.getAxisRight();
                rightAxis.setEnabled(false);
                leftAxis.setTextColor(Color.BLACK);
                oBarChart.animate();
            }
        }
        if (generatorID.equals("comp")) {
            if (sharedpref.loadNightModeState() == true) {
                barDataSet.setColors(dColors);
                barDataSet.setValueTextColor(Color.WHITE);
                BarData barData = new BarData(barDataSet);
                oBarChart.setData(barData);
                oBarChart.getDescription().setEnabled(false);
                Legend legend = oBarChart.getLegend();
                legend.setTextColor(Color.WHITE);
                LegendEntry l1 = new LegendEntry("Troškovi", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(0, 255, 255));
                LegendEntry l2 = new LegendEntry("Prihodi", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(0, 0, 0));
                legend.setCustom(new LegendEntry[]{l1, l2});
                legend.setWordWrapEnabled(true);
                XAxis xAxis = oBarChart.getXAxis();
                xAxis.setTextColor(Color.TRANSPARENT);
                YAxis leftAxis = oBarChart.getAxisLeft();
                YAxis rightAxis = oBarChart.getAxisRight();
                rightAxis.setEnabled(false);
                leftAxis.setTextColor(Color.WHITE);
                oBarChart.animate();
            } else {
                barDataSet.setColors(lColors);
                barDataSet.setValueTextColor(Color.BLACK);
                BarData barData = new BarData(barDataSet);
                oBarChart.setData(barData);
                oBarChart.getDescription().setEnabled(false);
                Legend legend = oBarChart.getLegend();
                legend.setTextColor(Color.BLACK);
                LegendEntry l1 = new LegendEntry("Troškovi", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(0, 255, 255));
                LegendEntry l2 = new LegendEntry("Prihodi", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(192, 192, 192));
                legend.setCustom(new LegendEntry[]{l1, l2});
                legend.setWordWrapEnabled(true);
                XAxis xAxis = oBarChart.getXAxis();
                xAxis.setTextColor(Color.TRANSPARENT);
                YAxis leftAxis = oBarChart.getAxisLeft();
                YAxis rightAxis = oBarChart.getAxisRight();
                rightAxis.setEnabled(false);
                leftAxis.setTextColor(Color.BLACK);
                oBarChart.animate();
            }
        }
    }

    private boolean validateStart(View v) {
        mStart = v.findViewById(R.id.intPickStart);
        String pickeddat = mStart.getEditText().getText().toString().trim();
        if (pickeddat.equals("Početni datum")) {
            mStart.setError("Odaberite datum");
            return false;
        } else {
            mStart.setError(null);
            return true;
        }
    }

    private boolean validateEnd(View v) {
        mEnd = v.findViewById(R.id.intPickEnd);
        String pickeddat = mEnd.getEditText().getText().toString().trim();
        if (pickeddat.equals("Završni datum")) {
            mEnd.setError("Odaberite datum");
            return false;
        } else {
            mEnd.setError(null);
            return true;
        }
    }

    public float addToList(String nazivGrupe, List<List<String>> grupePoStavkama) {
        float totalGrupe = 0;
        float iznosStavke;
        if (!(grupePoStavkama.isEmpty())) {
            for (int x = 0; x < grupePoStavkama.size(); x++) {
                if (nazivGrupe.equals(grupePoStavkama.get(x).get(0))) {
                    iznosStavke = Float.valueOf(grupePoStavkama.get(x).get(1));
                    totalGrupe = totalGrupe + iznosStavke;
                }
            }
        }
        if (totalGrupe != 0)
            return totalGrupe;
        else
            return 0f;
    }

    public void generateRadarTotalLists(List<List<String>> grupePoStavkama, List<List<String>> grupePoPlanu, String generatotID) {

        ostvareni.clear();
        planirani.clear();
        grupe.clear();

        String nazivGrupe1;
        float totalGrupe1 = 0;

        for (int x = 0; x < grupePoPlanu.size(); x++) {
            nazivGrupe1 = grupePoPlanu.get(x).get(0);
            totalGrupe1 = Float.valueOf(grupePoPlanu.get(x).get(1));
            planirani.add(new RadarEntry(totalGrupe1));
            ostvareni.add(new RadarEntry(addToList(nazivGrupe1, grupePoStavkama)));
            grupe.add(nazivGrupe1);
        }

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            String nazivGrupe = "";
            String zadnjaGrupa = "";
            float totalGrupe = 0;
            float iznosStavke;
            if (!(grupePoStavkama.isEmpty())) {
                for (int i = 0; i < grupePoStavkama.size(); i++) {
                    zadnjaGrupa = grupePoStavkama.get(i).get(0);

                    if (!(grupe.contains(zadnjaGrupa))) {
                        for (int x = 0; x < grupePoStavkama.size(); x++) {
                            nazivGrupe = grupePoStavkama.get(x).get(0);
                            iznosStavke = Float.valueOf(grupePoStavkama.get(x).get(1));
                            if (nazivGrupe.equals(zadnjaGrupa)) {
                                totalGrupe = totalGrupe + iznosStavke;
                                planirani.add(new RadarEntry(0));
                                ostvareni.add(new RadarEntry(totalGrupe));
                                grupe.add(nazivGrupe);
                            }
                        }
                    }

                }
            }
        }, 1);
        generateRadarTotal(ostvareni, planirani, generatotID);

    }

    public void generateRadarTotal(ArrayList<RadarEntry> ostvareni, ArrayList<RadarEntry> planirani, String generatorID) {
        if (sharedpref.loadNightModeState()) {
            if (generatorID.equals("prihod")) {
                YAxis yAxis = oRadarChart.getYAxis();
                yAxis.setTextColor(Color.WHITE);
                yAxis.setTextSize(10f);
                yAxis.setXOffset(0);
                yAxis.setYOffset(0);

                Legend legend = oRadarChart.getLegend();
                legend.setTextColor(Color.WHITE);
                legend.setTextSize(10f);


                RadarDataSet planiraniSet = new RadarDataSet(planirani, "Planirani prihodi");
                planiraniSet.setColor(dColors[0]);
                planiraniSet.setFillColor(dColors[0]);
                planiraniSet.setDrawFilled(true);
                planiraniSet.setFillAlpha(100);
                planiraniSet.setLineWidth(2f);
                planiraniSet.setDrawHighlightIndicators(false);
                planiraniSet.setDrawHighlightCircleEnabled(true);

                RadarDataSet ostvareniSet = new RadarDataSet(ostvareni, "Ostvareni prihodi");
                ostvareniSet.setColor(dColors[1]);
                ostvareniSet.setFillColor(dColors[1]);
                ostvareniSet.setDrawFilled(true);
                ostvareniSet.setFillAlpha(100);
                ostvareniSet.setLineWidth(2f);
                ostvareniSet.setDrawHighlightIndicators(false);
                ostvareniSet.setDrawHighlightCircleEnabled(true);

                ArrayList<IRadarDataSet> sets = new ArrayList<>();
                sets.add(planiraniSet);
                sets.add(ostvareniSet);

                RadarData data = new RadarData(sets);
                data.setValueTextColor(Color.WHITE);
                data.setValueTextSize(9f);
                data.setDrawValues(false);


                XAxis xAxis = oRadarChart.getXAxis();
                xAxis.setTextColor(Color.WHITE);
                xAxis.setTextSize(10f);
                xAxis.setXOffset(0);
                xAxis.setYOffset(0);
                xAxis.setCenterAxisLabels(false);

                /*
                ValueFormatter xAxisFormatter = new DayAxisValueFormatter(oRadarChart);
                xAxis.setValueFormatter(xAxisFormatter);
                 */
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        if ((int) value < grupe.size()) {
                            return grupe.get((int) value);
                        } else {
                            return " ";
                        }
                    }
                });

                Description des = oRadarChart.getDescription();
                des.setEnabled(false);

                oRadarChart.setWebColor(Color.WHITE);
                oRadarChart.setWebColorInner(Color.WHITE);
                oRadarChart.setData(data);
                oRadarChart.animate();
                oRadarChart.invalidate();
            }
            if (generatorID.equals("trosak")) {
                YAxis yAxis = oRadarChart.getYAxis();
                yAxis.setTextColor(Color.WHITE);
                yAxis.setTextSize(10f);
                yAxis.setXOffset(0);
                yAxis.setYOffset(0);

                Legend legend = oRadarChart.getLegend();
                legend.setTextColor(Color.WHITE);
                legend.setTextSize(10f);


                RadarDataSet planiraniSet = new RadarDataSet(planirani, "Planirani troškovi");
                planiraniSet.setColor(dColors[0]);
                planiraniSet.setFillColor(dColors[0]);
                planiraniSet.setDrawFilled(true);
                planiraniSet.setFillAlpha(100);
                planiraniSet.setLineWidth(2f);
                planiraniSet.setDrawHighlightIndicators(false);
                planiraniSet.setDrawHighlightCircleEnabled(true);

                RadarDataSet ostvareniSet = new RadarDataSet(ostvareni, "Nastali troškovi");
                ostvareniSet.setColor(dColors[1]);
                ostvareniSet.setFillColor(dColors[1]);
                ostvareniSet.setDrawFilled(true);
                ostvareniSet.setFillAlpha(100);
                ostvareniSet.setLineWidth(2f);
                ostvareniSet.setDrawHighlightIndicators(false);
                ostvareniSet.setDrawHighlightCircleEnabled(true);

                ArrayList<IRadarDataSet> sets = new ArrayList<>();
                sets.add(planiraniSet);
                sets.add(ostvareniSet);

                RadarData data = new RadarData(sets);
                data.setValueTextColor(Color.WHITE);
                data.setValueTextSize(9f);
                data.setDrawValues(false);


                XAxis xAxis = oRadarChart.getXAxis();
                xAxis.setTextColor(Color.WHITE);
                xAxis.setTextSize(10f);
                xAxis.setXOffset(0);
                xAxis.setYOffset(0);
                xAxis.setCenterAxisLabels(false);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        if ((int) value < grupe.size()) {
                            return grupe.get((int) value);
                        } else {
                            return " ";
                        }
                    }
                });

                Description des = oRadarChart.getDescription();
                des.setEnabled(false);
                oRadarChart.setWebColor(Color.WHITE);
                oRadarChart.setWebColorInner(Color.WHITE);
                oRadarChart.setData(data);
                oRadarChart.animate();
                oRadarChart.invalidate();

            }
        } else {
            if (generatorID.equals("prihod")) {
                YAxis yAxis = oRadarChart.getYAxis();
                yAxis.setTextColor(Color.BLACK);
                yAxis.setTextSize(10f);
                yAxis.setXOffset(0);
                yAxis.setYOffset(0);

                Legend legend = oRadarChart.getLegend();
                legend.setTextColor(Color.BLACK);
                legend.setTextSize(10f);


                RadarDataSet planiraniSet = new RadarDataSet(planirani, "Planirani prihodi");
                planiraniSet.setColor(lColors[0]);
                planiraniSet.setFillColor(lColors[0]);
                planiraniSet.setDrawFilled(true);
                planiraniSet.setFillAlpha(100);
                planiraniSet.setLineWidth(2f);
                planiraniSet.setDrawHighlightIndicators(false);
                planiraniSet.setDrawHighlightCircleEnabled(true);

                RadarDataSet ostvareniSet = new RadarDataSet(ostvareni, "Ostvareni prihodi");
                ostvareniSet.setColor(lColors[1]);
                ostvareniSet.setFillColor(lColors[1]);
                ostvareniSet.setDrawFilled(true);
                ostvareniSet.setFillAlpha(100);
                ostvareniSet.setLineWidth(2f);
                ostvareniSet.setDrawHighlightIndicators(false);
                ostvareniSet.setDrawHighlightCircleEnabled(true);

                ArrayList<IRadarDataSet> sets = new ArrayList<>();
                sets.add(planiraniSet);
                sets.add(ostvareniSet);

                RadarData data = new RadarData(sets);
                data.setValueTextColor(Color.BLACK);
                data.setValueTextSize(9f);
                data.setDrawValues(false);


                XAxis xAxis = oRadarChart.getXAxis();
                xAxis.setTextColor(Color.BLACK);
                xAxis.setTextSize(10f);
                xAxis.setXOffset(0);
                xAxis.setYOffset(0);
                xAxis.setCenterAxisLabels(false);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        if ((int) value < grupe.size()) {
                            return grupe.get((int) value);
                        } else {
                            return " ";
                        }
                    }
                });

                Description des = oRadarChart.getDescription();
                des.setEnabled(false);

                oRadarChart.setWebColor(Color.BLACK);
                oRadarChart.setWebColorInner(Color.BLACK);
                oRadarChart.setData(data);
                oRadarChart.animate();
                oRadarChart.invalidate();
            }
            if (generatorID.equals("trosak")) {
                YAxis yAxis = oRadarChart.getYAxis();
                yAxis.setTextColor(Color.BLACK);
                yAxis.setTextSize(10f);
                yAxis.setXOffset(0);
                yAxis.setYOffset(0);

                Legend legend = oRadarChart.getLegend();
                legend.setTextColor(Color.BLACK);
                legend.setTextSize(10f);


                RadarDataSet planiraniSet = new RadarDataSet(planirani, "Planirani troškovi");
                planiraniSet.setColor(Color.RED);
                planiraniSet.setFillColor(Color.RED);
                planiraniSet.setDrawFilled(true);
                planiraniSet.setFillAlpha(100);
                planiraniSet.setLineWidth(2f);
                planiraniSet.setDrawHighlightIndicators(false);
                planiraniSet.setDrawHighlightCircleEnabled(true);

                RadarDataSet ostvareniSet = new RadarDataSet(ostvareni, "Nastali troškovi");
                ostvareniSet.setColor(Color.GREEN);
                ostvareniSet.setFillColor(Color.GREEN);
                ostvareniSet.setDrawFilled(true);
                ostvareniSet.setFillAlpha(100);
                ostvareniSet.setLineWidth(2f);
                ostvareniSet.setDrawHighlightIndicators(false);
                ostvareniSet.setDrawHighlightCircleEnabled(true);

                ArrayList<IRadarDataSet> sets = new ArrayList<>();
                sets.add(planiraniSet);
                sets.add(ostvareniSet);

                RadarData data = new RadarData(sets);
                data.setValueTextColor(Color.BLACK);
                data.setValueTextSize(9f);
                data.setDrawValues(false);


                XAxis xAxis = oRadarChart.getXAxis();
                xAxis.setTextColor(Color.BLACK);
                xAxis.setTextSize(10f);
                xAxis.setXOffset(0);
                xAxis.setYOffset(0);
                xAxis.setCenterAxisLabels(false);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        if ((int) value < grupe.size()) {
                            return grupe.get((int) value);
                        } else {
                            return " ";
                        }
                    }
                });
                ;

                Description des = oRadarChart.getDescription();
                des.setEnabled(false);
                oRadarChart.setWebColor(Color.BLACK);
                oRadarChart.setWebColorInner(Color.BLACK);
                oRadarChart.setData(data);
                oRadarChart.animate();
                oRadarChart.invalidate();

            }
        }

    }

    public Boolean getDifference(String startDate, String endDate) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        Date sDate = sdf.parse(startDate);
        Date eDate = sdf.parse(endDate);


        long different = sDate.getTime() - eDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        if ((elapsedDays * -1) > 31) {
            return true;
        } else {
            return false;
        }
    }

    private void refreshFragment() {
        sharedpref.setPlanRepID("");
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, IzvjestajiFragment.newInstance());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}