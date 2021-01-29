package com.example.zavrsniapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zavrsniapp.Adapteri.PlanoviViewAdapter;
import com.example.zavrsniapp.Adapteri.ValutaViewAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfilFragment extends Fragment {

    private ImageView imgIIP;
    private ImageView imgMail;
    private ShapeableImageView profilePicture;
    private EditText txtIme, txtPrezime, txtMail;
    private RelativeLayout editPicLayout;
    private RelativeLayout editLozinkaLayout;
    private ProgressBar progresBar;
    private SharedPref sharedpref;
    private MaterialButton btnSave;
    private TextView sendPass;
    private boolean changeID = false;
    private RecyclerView plansRecycler;
    private ImageButton imgPlansPicker;
    private ArrayList<String> mNazivi = new ArrayList<>();
    private ArrayList<String> mRaspon = new ArrayList<>();

    private StorageReference storageReference;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    public static ProfilFragment newInstance() {

        return new ProfilFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedpref = new SharedPref(Objects.requireNonNull(getActivity()));
        if (sharedpref.loadNightModeState()) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }

        View v = inflater.inflate(R.layout.fragment_profil, container, false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progresBar = v.findViewById(R.id.progressBar);
        editPicLayout = v.findViewById(R.id.editable);
        editLozinkaLayout = v.findViewById(R.id.lozinkaEdit);
        imgIIP = v.findViewById(R.id.icedit1);
        imgMail = v.findViewById(R.id.icedit2);
        txtIme = v.findViewById(R.id.ime);
        txtPrezime = v.findViewById(R.id.prezime);
        txtMail = v.findViewById(R.id.email);
        setData(v);
        SwitchMaterial editData = v.findViewById(R.id.switch_uredi);
        editData.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editPicLayout.setVisibility(View.VISIBLE);
                editLozinkaLayout.setVisibility(View.VISIBLE);
                imgIIP.setVisibility(View.VISIBLE);
                imgMail.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                sendPass = v.findViewById(R.id.linkPromijeni);
                sendPass.setOnClickListener(v13 ->
                        fAuth.sendPasswordResetEmail(Objects.requireNonNull(Objects.requireNonNull(fAuth.getCurrentUser()).getEmail())).addOnSuccessListener(aVoid -> {
                            Snackbar.make((View) btnSave.getParent(), "Poruka sa linkom za promjenu lozinke poslana je na: "
                                    + fAuth.getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
                            signOut();

                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.container, PrijavaFragment.newInstance());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                        }));
                txtIme.setEnabled(true);
                txtPrezime.setEnabled(true);
                txtMail.setEnabled(true);
                txtMail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        changeID = true;
                    }
                });
                editPicLayout.setOnClickListener(v16 -> changePhoto());
            } else {
                editPicLayout.setVisibility(View.GONE);
                editLozinkaLayout.setVisibility(View.GONE);
                imgIIP.setVisibility(View.GONE);
                imgMail.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);

                txtIme.setEnabled(false);

                txtPrezime.setEnabled(false);

                txtMail.setEnabled(false);
            }
        });
        btnSave = v.findViewById(R.id.btnPohrani);
        btnSave.setOnClickListener(v14 -> {
            updateIme();
            updatePrezime();
            if (changeID) {
                updateEmail();
            }

        });

        RelativeLayout odjavaLayout = v.findViewById(R.id.odjava);
        odjavaLayout.setOnClickListener(v15 -> signOut());

        ImageView imgNazad = v.findViewById(R.id.natrag);
        imgNazad.setOnClickListener(v1 -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, PocetnaFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        imgPlansPicker = v.findViewById(R.id.plansPicker);
        initPlans();
        initRecycler(v);
        RelativeLayout showPlans = v.findViewById(R.id.planovi);
        showPlans.setOnClickListener(v12 -> {
            float deg = (imgPlansPicker.getRotation() == 90F) ? 0F : 90F;
            imgPlansPicker.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
            if (plansRecycler.getVisibility() == View.GONE) {
                plansRecycler.setVisibility(View.VISIBLE);
            } else {
                plansRecycler.setVisibility(View.GONE);
            }

        });

        plansRecycler.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                refresh();
                super.onChanged();
            }
        });

        return v;
    }

    private void refresh() {

        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, KreiranjeFragment.newInstance());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void initPlans() {

        Query colRef = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("planovi");
        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    mNazivi.add(document.get("nazivPlana").toString());
                    mRaspon.add(document.get("idPlana").toString());

                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "Greška", Toast.LENGTH_SHORT).show());
    }


    private void initRecycler(View v) {
        plansRecycler = v.findViewById(R.id.plansRecycler);
        PlanoviViewAdapter adapter = new PlanoviViewAdapter(getActivity(), mNazivi, mRaspon);
        plansRecycler.setAdapter(adapter);
        plansRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    private void setData(View v) {

        DocumentReference docRef = fStore.collection("korisnici").document(sharedpref.loadUserID());
        docRef.get().addOnCompleteListener(task -> {
            txtIme = v.findViewById(R.id.ime);
            txtPrezime = v.findViewById(R.id.prezime);
            txtMail = v.findViewById(R.id.email);
            txtIme.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get("ime")).toString());
            txtPrezime.setText(Objects.requireNonNull(task.getResult().get("prezime")).toString());
            txtMail.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getEmail());
            profilePicture = v.findViewById(R.id.pPic);
            final StorageReference profileRef = storageReference.child("korisnici/" + fAuth.getCurrentUser().getUid() + "profile.jpeg");
            profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profilePicture));
        });
    }

    private void updateIme() {

        DocumentReference docRef = fStore.collection("korisnici").document(sharedpref.loadUserID());
        docRef.update("ime", txtIme.getText().toString());
    }

    private void updatePrezime() {

        DocumentReference docRef = fStore.collection("korisnici").document(sharedpref.loadUserID());
        docRef.update("prezime", txtPrezime.getText().toString());
    }

    private void updateEmail() {
        DocumentReference docRef = fStore.collection("korisnici").document(sharedpref.loadUserID());
        docRef.update("email", txtMail.getText().toString());
        Objects.requireNonNull(fAuth.getCurrentUser()).updateEmail(txtMail.getText().toString());
        fAuth.signOut();

        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, PrijavaFragment.newInstance());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void changePhoto() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, 1000);

    }

    private void uploadPhoto(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("korisnici/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid() + "profile.jpeg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(profilePicture);
                    progresBar.setVisibility(View.GONE);
                })).addOnFailureListener(e ->
                Snackbar.make((View) btnSave.getParent(), "Neuspješan prijenos slike." + e.getMessage(), Snackbar.LENGTH_SHORT).show()
        );


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            Uri imageUri;
            if (data != null) {
                imageUri = data.getData();

                progresBar.setVisibility(View.VISIBLE);
                uploadPhoto(imageUri);
            }

        }
    }

    private void signOut() {
        Map<String, Object> savedState = new HashMap<>();
        savedState.put("darkmode", sharedpref.loadNightModeState());
        savedState.put("notifications", sharedpref.loadNotifications());
        savedState.put("planID", String.valueOf(sharedpref.loadPlanID()));
        savedState.put("currency", sharedpref.loadCurrency());
        savedState.put("currencyPosition", sharedpref.loadCurrencyPosition());

        DocumentReference dr = fStore.collection("korisnici").document(sharedpref.loadUserID())
                .collection("savedState").document("savedState");
        dr.set(savedState).addOnSuccessListener(aVoid -> {
            sharedpref = new SharedPref(Objects.requireNonNull(getActivity()));
            sharedpref.setNotifications(false);
            sharedpref.setNightModeState(false);
            sharedpref.setPlanID("prazno");
            sharedpref.setCurrency("kn");
            sharedpref.setCurrencyPosition(1);
            sharedpref.setUserID("guest");
            fAuth.signOut();
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, PrijavaFragment.newInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        })
                .addOnFailureListener(e -> {
                    String error = e.getMessage();
                    Snackbar.make((View) btnSave.getParent(), "Greška: " + error, Snackbar.LENGTH_SHORT).show();
                });
    }

}
