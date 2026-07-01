package com.example.mobilecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dals.UserDAO;
import com.example.models.User;
import com.example.utils.DataImporter;
import com.example.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView tvLoginMessage;

    private UserDAO userDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Tự động chuyển dữ liệu từ JSON sang SQLite khi khởi động ứng dụng
        DataImporter.importJsonToSqlite(this);

        initializeViews();
        initializeObjects();
        initializeEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*
         * Nếu đã đăng nhập trước đó thì không bắt
         * người dùng đăng nhập lại.
         */
        if (sessionManager != null
                && sessionManager.isLoggedIn()) {
            navigateByRole(
                    sessionManager.getRole()
            );
        }
    }

    private void initializeViews() {
        edtUsername =
                findViewById(
                        R.id.edtUsername
                );

        edtPassword =
                findViewById(
                        R.id.edtPassword
                );

        btnLogin =
                findViewById(
                        R.id.btnLogin
                );

        tvLoginMessage =
                findViewById(
                        R.id.tvLoginMessage
                );
    }

    private void initializeObjects() {
        userDAO =
                new UserDAO(this);

        sessionManager =
                new SessionManager(this);
    }

    private void initializeEvents() {
        btnLogin.setOnClickListener(
                view -> performLogin()
        );

        edtPassword.setOnEditorActionListener(
                (textView, actionId, event) -> {
                    if (actionId
                            == EditorInfo.IME_ACTION_DONE) {
                        performLogin();
                        return true;
                    }

                    return false;
                }
        );
    }

    private void performLogin() {
        hideMessage();

        String username =
                edtUsername.getText()
                        .toString()
                        .trim();

        String password =
                edtPassword.getText()
                        .toString();

        if (TextUtils.isEmpty(username)) {
            edtUsername.setError(
                    getString(R.string.error_empty_username)
            );

            edtUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError(
                    getString(R.string.error_empty_password)
            );

            edtPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText(R.string.action_checking);

        try {
            User user =
                    userDAO.login(
                            username,
                            password
                    );

            if (user == null) {
                showMessage(
                        getString(R.string.error_invalid_login)
                );

                return;
            }

            /*
             * Lưu UserID và thông tin User vào Session.
             */
            sessionManager.saveLoginSession(
                    user
            );

            Toast.makeText(
                    this,
                    R.string.login_success,
                    Toast.LENGTH_SHORT
            ).show();

            navigateByRole(
                    sessionManager.getRole()
            );

        } catch (Exception exception) {
            showMessage(
                    getString(R.string.error_invalid_login)
                            + ": "
                            + exception.getMessage()
            );

        } finally {
            btnLogin.setEnabled(true);
            btnLogin.setText(R.string.action_login);
        }
    }

    private void navigateByRole(String role) {
        Intent intent;

        if (SessionManager.ROLE_ADMIN.equals(role)) {
            intent = new Intent(
                    this,
                    AdminDashboardActivity.class
            );

        } else if (
                SessionManager.ROLE_EMPLOYEE.equals(
                        role
                )
        ) {
            intent = new Intent(
                    this,
                    EmployeeHomeActivity.class
            );

        } else if (
                SessionManager.ROLE_CUSTOMER.equals(
                        role
                )
        ) {
            intent = new Intent(
                    this,
                    CustomerHomeActivity.class
            );

        } else {
            sessionManager.logout();

            showMessage(
                    getString(R.string.error_invalid_type)
            );

            return;
        }

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    private void showMessage(String message) {
        tvLoginMessage.setText(message);
        tvLoginMessage.setVisibility(
                View.VISIBLE
        );
    }

    private void hideMessage() {
        tvLoginMessage.setText("");
        tvLoginMessage.setVisibility(
                View.GONE
        );
    }
}