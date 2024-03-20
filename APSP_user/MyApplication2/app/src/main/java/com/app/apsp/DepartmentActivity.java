package com.app.apsp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.app.apsp.R;

import java.util.Objects;

public class DepartmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department2);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Department");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String semname = intent.getStringExtra("SemName");

        GridView gridView = findViewById(R.id.gView);
        String[] buttonLabels = {"CSE", "CYS", "AIE", "ECE", "CCE", "MEE", "ARE"};
        DepartmentAdapter adapter = new DepartmentAdapter(this, buttonLabels,semname);
        gridView.setAdapter(adapter);
    }
}