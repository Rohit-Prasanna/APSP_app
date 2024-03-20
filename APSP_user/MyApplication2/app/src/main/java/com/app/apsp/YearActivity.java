package com.app.apsp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.GridView;

import com.app.apsp.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class YearActivity extends AppCompatActivity {

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Year");
        Intent intent = getIntent();
        String semname = intent.getStringExtra("semester");
        String departmentname = intent.getStringExtra("department");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        FirebaseApp.initializeApp(this);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference();
        StorageReference yearsRef = ref.child("chennai/" + semname + "/" + departmentname);
        ArrayList<String> files = new ArrayList<>();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) GridView gridView = findViewById(R.id.GRIDview);

        yearsRef.listAll().addOnSuccessListener(listResult -> {
                    for (StorageReference prefix : listResult.getPrefixes()) {
                        files.add(prefix.getName());
                        // All the prefixes under listRef.
                        // You may call listAll() recursively on them.

                    }
                    Collections.sort(files, Collections.reverseOrder());
                    YearAdapter adapter = new YearAdapter(YearActivity.this, files.toArray(new String[0]), semname, departmentname);
                    gridView.setAdapter(adapter);

                })
                .addOnFailureListener(e -> {
                    // Uh-oh, an error occurred!
                });
//        String[] buttonLabels = {"2023-2024", "2022-2023", "2021-2022", "2020-2021"};

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
}}
