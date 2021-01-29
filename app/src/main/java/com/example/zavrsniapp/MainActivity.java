package com.example.zavrsniapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    NavigationView navigationView;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        sharedPref = new SharedPref(this);

        if (sharedPref.loadUserID().equals("guest")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PrijavaFragment.newInstance())
                    .commitNow();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PocetnaFragment.newInstance())
                    .commitNow();
        }

    }

}
