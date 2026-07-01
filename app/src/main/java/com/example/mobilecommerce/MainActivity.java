package com.example.mobilecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String fullName = sessionManager.getFullName();
        String role = sessionManager.getRole();
        
        Toast.makeText(this, "Welcome " + fullName + " (" + role + ")", Toast.LENGTH_SHORT).show();
    }
}