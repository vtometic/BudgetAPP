package com.example.zavrsniapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Objects;


public class PrijavaFragment extends Fragment {
    TextInputLayout mEmail, mLozinka;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    SharedPref sharedpref;
    TextView resetPassword;

    public static PrijavaFragment newInstance() {

        return new PrijavaFragment();
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
        View v = inflater.inflate(R.layout.fragment_prijava, container, false);

        mEmail = v.findViewById(R.id.email);
        mLozinka = v.findViewById(R.id.lozinka);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        resetPassword = v.findViewById(R.id.resetPassword);
        resetPassword.setOnClickListener(v13 -> {
            if (validateEmail()) {
                fAuth.sendPasswordResetEmail(Objects.requireNonNull(Objects.requireNonNull(mEmail.getEditText().getText().toString())))
                        .addOnSuccessListener(aVoid -> {
                            Snackbar.make((View) mEmail.getParent(), "Poruka sa linkom za promjenu lozinke poslana je na: "
                                    + mEmail.getEditText().getText().toString(), Snackbar.LENGTH_SHORT).show();
                        });
            }
        });


        Button btnPrijavise = v.findViewById(R.id.btn_pri_se);
        btnPrijavise.setOnClickListener(v1 -> {
            logIn(v1);
            UIUtil.hideKeyboard(getActivity());
        });

        Button btnPriReg = v.findViewById(R.id.btn_pri_reg);
        btnPriReg.setOnClickListener(v12 -> {

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, RegistracijaFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        return v;
    }

    private boolean validateEmail() {
        String email = mEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            mEmail.setError("Unesite email");
            return false;
        } else {
            mEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = mLozinka.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            mLozinka.setError("Unesite lozinku!");
            return false;
        } else {
            mLozinka.setError(null);
            return true;
        }
    }

    private void logIn(View v) {
        if (!validateEmail() | !validatePassword()) {
            return;
        }
        final String email = mEmail.getEditText().getText().toString().trim();
        String password = mLozinka.getEditText().getText().toString().trim();
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Snackbar.make((View) mEmail.getParent(), "Dobrodošli " + email + "!", Snackbar.LENGTH_LONG).show();
                sharedpref.setUserID(fAuth.getUid());
                DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                        .collection("savedState").document("savedState");
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        sharedpref = new SharedPref(getActivity());
                        sharedpref.setNotifications(Boolean.valueOf(task.getResult().get("notifications").toString()));
                        sharedpref.setNightModeState(Boolean.valueOf(task.getResult().get("darkmode").toString()));
                        sharedpref.setPlanID(String.valueOf(task.getResult().get("planID")));
                        sharedpref.setCurrency(String.valueOf(task.getResult().get("currency")));
                        sharedpref.setCurrencyPosition(Integer.valueOf(task.getResult().get("currencyPosition").toString()));

                    }
                });


                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            } else {
                Snackbar.make((View) mEmail.getParent(), "Greška! " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}