package com.example.mobilecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utils.SessionManager;

public class AdminDashboardActivity
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
         * Chỉ Admin mới được mở Dashboard.
         */
        if (!sessionManager.isLoggedIn()
                || !sessionManager.isAdmin()) {
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
                R.string.admin_dashboard_title
        );

        tvWelcome.setText(
                getString(R.string.admin_welcome_prefix)
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
                                    AdminDashboardActivity.this,
                                    ProductSearchActivity.class
                            );

                    startActivity(intent);
                }
        );

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