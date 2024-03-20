package com.app.apsp_admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PICK_PDF = 1;
    private EditText etYear, etFilename;
    private Button btnUploadPdf;
    private TextView tvOrigName;
    private ImageButton btnChoosePdf, btnX;
    private String userSpecifiedFilename;
    private StorageReference storageRef;
    private String filePath;
    private Uri selectedFileUri, pdfUri;
    private DatabaseReference mDatabase;
    private Spinner spinnerDepartment, spinnerSemester;
    private Dialog dateRangePickerDialog;
    private NumberPicker startYearPicker;
    private NumberPicker endYearPicker;
    private String userid;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FirebaseApp.initializeApp(this);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        storageRef = storage.getReference().child("chennai");

        etYear = findViewById(R.id.idETVYear);
        etFilename = findViewById(R.id.idETVFilename);
        btnChoosePdf = findViewById(R.id.idBTNPdfUpload);
        btnUploadPdf = findViewById(R.id.idBTNUpload);
        btnX = findViewById(R.id.idBTNClosePdf);
        tvOrigName = findViewById(R.id.idTVOrigName);
        Button btnDateRangePicker = findViewById(R.id.btnDateRangePicker);
        ArrayList<Bitmap> scannedDocument = getIntent().getParcelableArrayListExtra("scannedDocument");
        spinnerSemester = findViewById(R.id.idSpinnerSemester);
        spinnerDepartment = findViewById(R.id.idSpinnerDepartment);

        Intent i = getIntent();
        userid = i.getStringExtra("userid");
        String retrievedUsername = retrieveUsernameFromSharedPreferences();


        if (!TextUtils.isEmpty(retrievedUsername)) {
            getSupportActionBar().setTitle("Hi," + retrievedUsername);
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("application/pdf".equals(type)) {
                handleSharedPdf(intent);
            }
        } else if (Intent.ACTION_VIEW.equals(action) && type != null) {
            if ("application/pdf".equals(type)) {

            }
        }

        String[] semesterOptions = {"SELECT", "Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8"};
        String[] departmentOptions = {"SELECT", "CSE", "CYS", "AIE", "ECE", "CCE", "MEE", "ARE"};

        ArrayAdapter<String> semAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesterOptions);
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentOptions);
        semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semAdapter);
        spinnerDepartment.setAdapter(deptAdapter);

        // Request storage permission if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        btnDateRangePicker.setOnClickListener(view -> showDateRangePickerDialog());

        btnChoosePdf.setOnClickListener(view -> {
            Intent in = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            in.addCategory(Intent.CATEGORY_OPENABLE);
            in.setType("application/pdf");
            startActivityForResult(in, REQUEST_PICK_PDF);
        });

        btnX.setOnClickListener(v -> {
            selectedFileUri = null;
            tvOrigName.setText("");
            btnChoosePdf.setVisibility(View.VISIBLE);
//            btnCam.setVisibility(View.VISIBLE);
            btnX.setVisibility(View.GONE);
            tvOrigName.setVisibility(View.GONE);
            btnUploadPdf.setBackgroundColor(getResources().getColor(R.color.original));
        });


        btnUploadPdf.setOnClickListener(v -> {
            String semester = spinnerSemester.getSelectedItem().toString();
            String department = spinnerDepartment.getSelectedItem().toString();
            String year = etYear.getText().toString();
            userSpecifiedFilename = etFilename.getText().toString();

            if (spinnerSemester.getSelectedItemPosition() == 0 ||
                    spinnerDepartment.getSelectedItemPosition() == 0 ||
                    year.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all fields with valid data.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userSpecifiedFilename.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a filename.", Toast.LENGTH_SHORT).show();
                return;
            }

            filePath = semester + "/" + department + "/" + year + "/";

            if (selectedFileUri != null) {
                uploadFileToFirebaseStorage(selectedFileUri);
            } else if (pdfUri != null) {
                uploadFileToFirebaseStorage(pdfUri);
            } else {
                Toast.makeText(MainActivity.this, "Please choose a PDF file.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String retrieveUsernameFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("name", "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent loginIntent = new Intent(this,LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_PDF && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedFileUri = data.getData();

                btnChoosePdf.setVisibility(View.GONE);

                btnX.setVisibility(View.VISIBLE);
                tvOrigName.setVisibility(View.VISIBLE);
                tvOrigName.setText(getOriginalFileName(selectedFileUri));
                btnUploadPdf.setBackgroundColor(getResources().getColor(R.color.green));
                etFilename.setText("");
            }
        }
    }

    private String getOriginalFileName(Uri fileUri) {
        String originalFileName = null;
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
            cursor = getContentResolver().query(fileUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                if (index >= 0) {
                    originalFileName = cursor.getString(index);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return originalFileName;
    }

    private void showDateRangePickerDialog() {
        dateRangePickerDialog = new Dialog(this);
        dateRangePickerDialog.setContentView(R.layout.year_picker_dialog);

        startYearPicker = dateRangePickerDialog.findViewById(R.id.startYearPicker);
        endYearPicker = dateRangePickerDialog.findViewById(R.id.endYearPicker);

        int currentYear = getCurrentYear();

        // Set up start year picker
        startYearPicker.setMinValue(currentYear - 10);
        startYearPicker.setMaxValue(currentYear + 10);
        startYearPicker.setValue(currentYear);

        // Set up end year picker
        endYearPicker.setMinValue(currentYear - 10);
        endYearPicker.setMaxValue(currentYear + 10);
        endYearPicker.setValue(currentYear + 1);

        Button btnSetDateRange = dateRangePickerDialog.findViewById(R.id.btnSetDateRange);
        Button btnCancel = dateRangePickerDialog.findViewById(R.id.btnCancel);

        btnSetDateRange.setOnClickListener(view -> {
            int selectedStartYear = startYearPicker.getValue();
            int selectedEndYear = endYearPicker.getValue();
            String dateRange = selectedStartYear + "-" + selectedEndYear;
            etYear.setText(dateRange);
            dateRangePickerDialog.dismiss();
        });

        btnCancel.setOnClickListener(view -> dateRangePickerDialog.dismiss());

        dateRangePickerDialog.show();
    }

    private int getCurrentYear() {
        return java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    }

    private String getCurrentTimeAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void handleSharedPdf(Intent intent) {
        Uri sharedPdfUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (sharedPdfUri != null) {
            selectedFileUri = sharedPdfUri;

            btnChoosePdf.setVisibility(View.GONE);
            btnX.setVisibility(View.VISIBLE);
            tvOrigName.setVisibility(View.VISIBLE);
            tvOrigName.setText(getOriginalFileName(selectedFileUri));
            btnUploadPdf.setBackgroundColor(getResources().getColor(R.color.green));
            etFilename.setText("");
        }
    }

    private void uploadFileToFirebaseStorage(Uri fileUri) {
        StorageReference pdfRef = storageRef.child(filePath + userSpecifiedFilename);
        UploadTask uploadTask = pdfRef.putFile(fileUri);

        uploadTask.addOnFailureListener(exception -> Toast.makeText(MainActivity.this, "Upload failed: " + exception.getMessage(),
                Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> pdfRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
            String currentTime = getCurrentTimeAsString();

            PDF pdf = new PDF(userid, filePath, userSpecifiedFilename, downloadUri.toString(), currentTime);
            DatabaseReference pdfNode = mDatabase.child("Uploaded pdf").push();
            pdfNode.setValue(pdf);

            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

            spinnerDepartment.setSelection(0);
            spinnerSemester.setSelection(0);
            etYear.setText("");
            etFilename.setText("");
            selectedFileUri = null;
            btnChoosePdf.setVisibility(View.VISIBLE);
//            btnCam.setVisibility(View.VISIBLE);
            btnX.setVisibility(View.GONE);
            tvOrigName.setVisibility(View.GONE);
            btnUploadPdf.setBackgroundColor(getResources().getColor(R.color.original));
        }));
    }
}
