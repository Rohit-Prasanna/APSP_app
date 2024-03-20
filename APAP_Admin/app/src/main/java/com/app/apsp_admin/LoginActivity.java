package com.app.apsp_admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public String username;
    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword, etUsername;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView linkTextView = findViewById(R.id.linkTextView);
        TextView newacnt = findViewById(R.id.new_act);
        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event, e.g., open a browser
                Uri uri = Uri.parse("https://rohit-prasanna.github.io/APSP_ADMIN_WEB/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        newacnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event, e.g., open a browser
                Uri uri = Uri.parse("https://rohit-prasanna.github.io/Createuser_APSP/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();
        etUsername = findViewById(R.id.idETUsername);
        etEmail = findViewById(R.id.idETEmail);
        etPassword = findViewById(R.id.idETPassword);
        Button btnLogin = findViewById(R.id.idBTNLogin);

        btnLogin.setOnClickListener(v -> {
            username = etUsername.getText().toString();
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();
            saveUsernameToSharedPreferences(username);


            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
            } else {
                signInWithEmailAndPassword(email, password);
            }
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        // Remove leading and trailing whitespace from email and password
        email = email.trim();
        password = password.trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail: success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "signInWithEmail: failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void saveUsernameToSharedPreferences(String username) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", username);
        editor.apply();
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this, "Authentication successful. Welcome, " + user.getEmail(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("userid", user.getEmail());
            intent.putExtra("name", username);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Authentication failure. Please check the credentials.", Toast.LENGTH_SHORT).show();
        }
    }
}
