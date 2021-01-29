package com.example.zavrsniapp;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPref {

    SharedPreferences userID;


    SharedPreferences dmSharedPreferences;
    SharedPreferences notificationsSharedPreferences;
    SharedPreferences currencySharedPreference;
    SharedPreferences currencyPosition;
    SharedPreferences minDate;
    SharedPreferences dateInMilis;


    SharedPreferences planID;
    SharedPreferences groupID;
    SharedPreferences stavkaID;
    SharedPreferences spWeek;
    SharedPreferences spMonth;
    SharedPreferences spYear;
    SharedPreferences zgVisibility;
    SharedPreferences pocetniDatum;
    SharedPreferences zavrsniDatum;


    SharedPreferences tabID;
    SharedPreferences svojstvo;


    SharedPreferences repID;
    SharedPreferences planRepID;


    SharedPreferences planPosition;


    public SharedPref(Context context) {

        userID = context.getSharedPreferences("userID", Context.MODE_PRIVATE);


        dmSharedPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        notificationsSharedPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        currencySharedPreference = context.getSharedPreferences("currencyname", Context.MODE_PRIVATE);
        currencyPosition = context.getSharedPreferences("currencyposition", Context.MODE_PRIVATE);
        minDate = context.getSharedPreferences("minDate", Context.MODE_PRIVATE);
        dateInMilis = context.getSharedPreferences("dateInMilis", Context.MODE_PRIVATE);


        planID = context.getSharedPreferences("planID", Context.MODE_PRIVATE);
        groupID = context.getSharedPreferences("groupID", Context.MODE_PRIVATE);
        spWeek = context.getSharedPreferences("spWeek", Context.MODE_PRIVATE);
        spMonth = context.getSharedPreferences("spMonth", Context.MODE_PRIVATE);
        spYear = context.getSharedPreferences("spYear", Context.MODE_PRIVATE);
        zgVisibility = context.getSharedPreferences("zgVisibility", Context.MODE_PRIVATE);
        stavkaID = context.getSharedPreferences("stavkaID", Context.MODE_PRIVATE);
        pocetniDatum = context.getSharedPreferences("pD", Context.MODE_PRIVATE);
        zavrsniDatum = context.getSharedPreferences("zD", Context.MODE_PRIVATE);


        tabID = context.getSharedPreferences("tabID", Context.MODE_PRIVATE);
        svojstvo = context.getSharedPreferences("s", Context.MODE_PRIVATE);


        repID = context.getSharedPreferences("repID", Context.MODE_PRIVATE);
        planRepID = context.getSharedPreferences("planRepID", Context.MODE_PRIVATE);


        planPosition = context.getSharedPreferences("planPosition", Context.MODE_PRIVATE);
    }

    public void setUserID(String user_id) {
        SharedPreferences.Editor editor = userID.edit();
        editor.putString("userID", user_id);
        editor.commit();
    }

    public String loadUserID() {
        String user_id = userID.getString("userID", "guest");
        return user_id;
    }


    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = dmSharedPreferences.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }

    public Boolean loadNightModeState() {
        Boolean state = dmSharedPreferences.getBoolean("NightMode", false);
        return state;
    }


    public void setNotifications(Boolean state) {
        SharedPreferences.Editor editor = notificationsSharedPreferences.edit();
        editor.putBoolean("Notifications", state);
        editor.commit();
    }

    public Boolean loadNotifications() {
        Boolean state = notificationsSharedPreferences.getBoolean("Notifications", false);
        return state;
    }


    public void setCurrency(String oznaka) {
        SharedPreferences.Editor editor = currencySharedPreference.edit();
        editor.putString("currencyname", oznaka);
        editor.commit();
    }

    public String loadCurrency() {
        String oznaka = currencySharedPreference.getString("currencyname", "kn");
        return oznaka;
    }

    public void setCurrencyPosition(int position) {
        SharedPreferences.Editor editor = currencyPosition.edit();
        editor.putInt("currencyposition", position);
        editor.commit();
    }

    public int loadCurrencyPosition() {
        int position = currencyPosition.getInt("currencyposition", 1);
        return position;
    }

    public void setMinDate(String date) {
        SharedPreferences.Editor editor = minDate.edit();
        editor.putString("minDate", date);
        editor.commit();
    }

    public String loadMinDate() {
        String date = minDate.getString("minDate", "31.12.2100");
        return date;
    }


    public void setDateInMilis(long date) {
        SharedPreferences.Editor editor = dateInMilis.edit();
        editor.putLong("minDate", date);
        editor.commit();
    }

    public long loadDateInMilis() {
        long date = dateInMilis.getLong("minDate", 0);
        return date;
    }


    public void setPlanID(String id) {
        SharedPreferences.Editor editor = planID.edit();
        editor.putString("planID", id);
        editor.commit();
    }

    public String loadPlanID() {
        String id = planID.getString("planID", "prazno");
        return id;
    }

    public void setStavkaID(String id) {
        SharedPreferences.Editor editor = stavkaID.edit();
        editor.putString("stavkaID", id);
        editor.commit();
    }

    public String loadStavkaID() {
        String id = stavkaID.getString("stavkaID", "prazno");
        return id;
    }

    public void setZgVisibility(Boolean state) {
        SharedPreferences.Editor editor = zgVisibility.edit();
        editor.putBoolean("zgVisibility", state);
        editor.commit();
    }

    public Boolean loadZgVisibility() {
        Boolean state = zgVisibility.getBoolean("zgVisibility", true);
        return state;
    }

    public String loadGroupID() {
        String id = groupID.getString("groupID", "prazdgkjnadfiugbno");
        return id;
    }

    public void setGroupID(String id) {
        SharedPreferences.Editor editor = groupID.edit();
        editor.putString("groupID", id);
        editor.commit();
    }


    public void setSpWeek(int week) {
        SharedPreferences.Editor editor = spWeek.edit();
        editor.putInt("spWeek", week);
        editor.commit();
    }

    public int loadSpWeek() {
        int week = spWeek.getInt("spWeek", 1);
        return week;
    }

    public void setSpMonth(int month) {
        SharedPreferences.Editor editor = spMonth.edit();
        editor.putInt("spMonth", month);
        editor.commit();
    }

    public int loadSpMonth() {
        int month = spMonth.getInt("spMonth", 1);
        return month;
    }

    public void setSpYear(int year) {
        SharedPreferences.Editor editor = spMonth.edit();
        editor.putInt("spYear", year);
        editor.commit();
    }

    public int loadSpYear() {
        int year = spMonth.getInt("spYear", 1);
        return year;
    }

    public void setPocetniDatum(String pocetniDatum) {
        SharedPreferences.Editor editor = currencySharedPreference.edit();
        editor.putString("pD", pocetniDatum);
        editor.commit();
    }

    public String loadPocetniDatum() {
        String oznaka = currencySharedPreference.getString("pD", "");
        return oznaka;
    }

    public void setZavrsniDatum(String zavrsniDatum) {
        SharedPreferences.Editor editor = currencySharedPreference.edit();
        editor.putString("zD", zavrsniDatum);
        editor.commit();
    }

    public String loadZavrsniDatum() {
        String oznaka = currencySharedPreference.getString("zD", "");
        return oznaka;
    }

    public void setTabID(int position) {
        SharedPreferences.Editor editor = tabID.edit();
        editor.putInt("tabID", position);
        editor.commit();
    }

    public int loadTabID() {
        int position = tabID.getInt("tabID", 0);
        return position;
    }

    public void setSvojstvo(String m) {
        SharedPreferences.Editor editor = svojstvo.edit();
        editor.putString("s", m);
        editor.commit();
    }

    public String loadSvojstvo() {
        String m = svojstvo.getString("s", "s");
        return m;
    }

    public void setRepID(int position) {
        SharedPreferences.Editor editor = repID.edit();
        editor.putInt("repID", position);
        editor.commit();
    }

    public int loadRepID() {
        int position = repID.getInt("repID", 0);
        return position;
    }

    public void setPlanRepID(String m) {
        SharedPreferences.Editor editor = planRepID.edit();
        editor.putString("planRepID", m);
        editor.commit();
    }

    public String loadPlanRepID() {
        String m = planRepID.getString("planRepID", "");
        return m;
    }

    public void setPlanPosition(int position) {
        SharedPreferences.Editor editor = currencyPosition.edit();
        editor.putInt("planPosition", position);
        editor.commit();
    }

    public int loadPlanPosition() {
        int position = currencyPosition.getInt("planPosition", 1);
        return position;
    }
}
