package com.app.apsp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SelectionActivity extends AppCompatActivity {
    private Spinner spinnerDepartment, spinnerSemester;
    private Button btnUploadPdf;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        context = this;
        spinnerSemester = findViewById(R.id.idSpinnerSemester);
        spinnerDepartment = findViewById(R.id.idSpinnerDepartment);
        btnUploadPdf = findViewById(R.id.idBTNUpload);

        String[] semesterOptions = {"SELECT", "Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8"};
        String[] departmentOptions = {"SELECT", "CSE", "CYS", "AIE", "ECE", "CCE", "MEE", "ARE"};

        ArrayAdapter<String> semAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesterOptions);
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentOptions);
        semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semAdapter);
        spinnerDepartment.setAdapter(deptAdapter);

        btnUploadPdf.setOnClickListener(v -> {
            String semester = spinnerSemester.getSelectedItem().toString();
            String department = spinnerDepartment.getSelectedItem().toString();

            Intent intent = new Intent(context, YearActivity.class);
            intent.putExtra("semester", semester);
            intent.putExtra("department", department);
            startActivity(intent);
        });
    }
}
