package com.app.apsp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.app.apsp.R;

import java.util.Objects;

public class SemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sem);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Semester");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridView gridView = findViewById(R.id.gridView);
        String[] buttonLabels = {"Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8"};
        SemAdapter adapter = new SemAdapter(this, buttonLabels);
        gridView.setAdapter(adapter);

    }





}
