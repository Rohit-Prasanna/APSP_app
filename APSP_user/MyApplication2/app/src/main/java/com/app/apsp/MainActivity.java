package com.app.apsp;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.app.apsp.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
        Button chennai = findViewById(R.id.chennai);
        chennai.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
            startActivity(intent);
        });
    }
}
