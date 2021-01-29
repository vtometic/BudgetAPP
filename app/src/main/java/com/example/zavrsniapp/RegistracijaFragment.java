package com.example.zavrsniapp;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.HashMap;
import java.util.Map;

public class RegistracijaFragment extends Fragment {
    TextInputLayout mIme, mPrezime, mEmail, mLozinka, mPonLozinka;
    Button btnRegSe, btnRegPri;
    private RelativeLayout registracijaLayout;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    SharedPref sharedpref;


    public static RegistracijaFragment newInstance() {

        return new RegistracijaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_registracija, container, false);

        mIme = v.findViewById(R.id.ime);
        mPrezime = v.findViewById(R.id.prezime);
        mEmail = v.findViewById(R.id.email);
        mLozinka = v.findViewById(R.id.lozinka);
        mPonLozinka = v.findViewById(R.id.ponlozinka);
        fAuth=FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        btnRegSe = v.findViewById(R.id.btn_reg_se);
        btnRegSe.setOnClickListener(v1 -> {
            logIn(v1);
            UIUtil.hideKeyboard(getActivity());

        });
        btnRegPri = v.findViewById(R.id.btn_reg_pri);
        btnRegPri.setOnClickListener(v12 -> {

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, PrijavaFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            UIUtil.hideKeyboard(getActivity());
        });

        return v;
    }
    private boolean validateIme(){
        String ime = mIme.getEditText().getText().toString().trim();
        if(ime.isEmpty()){
            mIme.setError("Unesite Ime");
            return false;
        }else{
            mIme.setError(null);
            return true;
        }
    }
    private boolean validatePrezime(){
        String prezime = mPrezime.getEditText().getText().toString().trim();
        if(prezime.isEmpty()){
            mPrezime.setError("Unesite email");
            return false;
        }else{
            mPrezime.setError(null);
            return true;
        }
    }
    private boolean validateEmail(){
        String email = mEmail.getEditText().getText().toString().trim();
        if(email.isEmpty()){
            mEmail.setError("Unesite email");
            return false;
        }else{
            mEmail.setError(null);
            return true;
        }
    }
    private boolean validateLozinka(){
        String lozinka = mLozinka.getEditText().getText().toString().trim();
        if(lozinka.isEmpty()){
            mLozinka.setError("Unesite lozinku!");
            return false;
        }
        if(lozinka.length()<8){
            mLozinka.setError("Lozinka mora biti duža od 8 znakova");
            return false;
        }
        else{
            mLozinka.setError(null);
            return true;
        }
    }
    private boolean validatePonLozinka(){
        String ponlozinka = mPonLozinka.getEditText().getText().toString().trim();
        String lozinka = mLozinka.getEditText().getText().toString().trim();
        if(ponlozinka.isEmpty()){
            mPonLozinka.setError("Potvrdite lozinku!");
            return false;
        }
        if(!ponlozinka.equals(lozinka)){
            mPonLozinka.setError("Ponovljena lozinka nije identična lozinki");
            return false;
        }
        else{
            mLozinka.setError(null);
            return true;
        }
    }
    public void logIn (View v){
        if(!validateIme()
                | !validatePrezime()
                | !validateEmail()
                | !validateLozinka()
                | !validatePonLozinka()){
            return;
        }

        final String ime = mIme.getEditText().getText().toString().trim();
        final String prezime = mPrezime.getEditText().getText().toString().trim();
        final String email = mEmail.getEditText().getText().toString().trim();
        final String lozinka = mLozinka.getEditText().getText().toString().trim();


        fAuth.createUserWithEmailAndPassword(email,lozinka).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Snackbar.make((View) mEmail.getParent(), "Korisnik "+ime+" "+prezime+" kreiran", Snackbar.LENGTH_LONG).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("korisnici").document(userID);
                    Map<String,Object> korisnik = new HashMap<>();
                    korisnik.put("id",userID);
                    korisnik.put("ime",ime);
                    korisnik.put("prezime",prezime);
                    korisnik.put("email",email);
                    documentReference.set(korisnik).addOnSuccessListener(aVoid -> {
                        sharedpref = new SharedPref(getActivity());
                        Map<String,Object> savedState = new HashMap<>();
                        savedState.put("darkmode",false);
                        savedState.put("notifications",false);
                        savedState.put("planID",  String.valueOf("prazno"));
                        savedState.put("currency","kn");
                        savedState.put("currencyPosition",1);
                        DocumentReference dr = fStore.collection("korisnici").document(userID)
                                .collection("savedState").document("savedState");
                        dr.set(savedState);
                    });


                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, PrijavaFragment.newInstance());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                } else {
                    Snackbar.make((View) mEmail.getParent(), "Greška!"+task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                }
            }
        });
    }
}