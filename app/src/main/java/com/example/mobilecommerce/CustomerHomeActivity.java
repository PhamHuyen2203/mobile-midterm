package com.example.mobilecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utils.SessionManager;

public class CustomerHomeActivity
        extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        sessionManager =
                new SessionManager(this);

        /*
         * Bảo vệ màn hình:
         * chỉ Customer mới được mở.
         */
        if (!sessionManager.isLoggedIn()
                || !sessionManager.isCustomer()) {
            redirectToLogin();
            return;
        }

        setContentView(
                R.layout.activity_role_home
        );

        TextView tvRoleTitle =
                findViewById(
                        R.id.tvRoleTitle
                );

        TextView tvWelcome =
                findViewById(
                        R.id.tvWelcome
                );

        TextView tvSessionInfo =
                findViewById(
                        R.id.tvSessionInfo
                );

        Button btnLogout =
                findViewById(
                        R.id.btnLogout
                );

        tvRoleTitle.setText(
                R.string.customer_home_title
        );

        tvWelcome.setText(
                getString(R.string.welcome_prefix)
                        + sessionManager.getFullName()
        );

        tvSessionInfo.setText(
                getString(
                        R.string.session_info_format,
                        sessionManager.getUserID(),
                        sessionManager.getUsername(),
                        sessionManager.getType(),
                        sessionManager.getRole()
                )
        );

        Button btnBrowseProducts =
                findViewById(
                        R.id.btnBrowseProducts
                );

        btnBrowseProducts.setOnClickListener(
                view -> {
                    Intent intent =
                            new Intent(
                                    CustomerHomeActivity.this,
                                    ProductSearchActivity.class
                            );

                    startActivity(intent);
                }
        );

        Button btnViewCart = findViewById(R.id.btnViewCart);
        btnViewCart.setOnClickListener(view -> {
            Intent intent = new Intent(CustomerHomeActivity.this, CartActivity.class);
            startActivity(intent);
        });

        Button btnMyProfile = findViewById(R.id.btnMyProfile);
        btnMyProfile.setOnClickListener(view -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(
                view -> logout()
        );
    }

    private void logout() {
        sessionManager.logout();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(
                this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}