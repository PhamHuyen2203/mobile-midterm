package com.example.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.models.User;

public class SessionManager {

    private static final String PREF_NAME =
            "MCommerceSession";

    private static final String KEY_IS_LOGGED_IN =
            "isLoggedIn";

    private static final String KEY_USER_ID =
            "userID";

    private static final String KEY_USERNAME =
            "username";

    private static final String KEY_FULL_NAME =
            "fullName";

    private static final String KEY_EMAIL =
            "email";

    private static final String KEY_TYPE =
            "type";

    private static final String KEY_ROLE =
            "role";

    public static final String ROLE_CUSTOMER =
            "Customer";

    public static final String ROLE_EMPLOYEE =
            "Employee";

    public static final String ROLE_ADMIN =
            "Admin";

    private static final String ADMIN_USERNAME =
            "admin.sys";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREF_NAME,
                                Context.MODE_PRIVATE
                        );
    }

    /**
     * Lưu thông tin đăng nhập.
     */
    public void saveLoginSession(User user) {
        String role = resolveRole(user);

        preferences.edit()
                .putBoolean(
                        KEY_IS_LOGGED_IN,
                        true
                )
                .putInt(
                        KEY_USER_ID,
                        user.getUserID()
                )
                .putString(
                        KEY_USERNAME,
                        user.getUsername()
                )
                .putString(
                        KEY_FULL_NAME,
                        user.getFullName()
                )
                .putString(
                        KEY_EMAIL,
                        user.getEmail()
                )
                .putString(
                        KEY_TYPE,
                        user.getType()
                )
                .putString(
                        KEY_ROLE,
                        role
                )
                .apply();
    }

    /**
     * Xác định quyền dựa trên Type.
     *
     * cust -> Customer
     * emp + admin.sys -> Admin
     * emp còn lại -> Employee
     */
    private String resolveRole(User user) {
        String type = user.getType();
        String username = user.getUsername();

        if ("cust".equalsIgnoreCase(type)) {
            return ROLE_CUSTOMER;
        }

        if ("emp".equalsIgnoreCase(type)
                && ADMIN_USERNAME.equalsIgnoreCase(
                username
        )) {
            return ROLE_ADMIN;
        }

        if ("emp".equalsIgnoreCase(type)) {
            return ROLE_EMPLOYEE;
        }

        /*
         * Dự phòng nếu database sau này có Type = admin.
         */
        if ("admin".equalsIgnoreCase(type)) {
            return ROLE_ADMIN;
        }

        return "";
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(
                KEY_IS_LOGGED_IN,
                false
        );
    }

    public int getUserID() {
        return preferences.getInt(
                KEY_USER_ID,
                -1
        );
    }

    public String getUsername() {
        return preferences.getString(
                KEY_USERNAME,
                ""
        );
    }

    public String getFullName() {
        return preferences.getString(
                KEY_FULL_NAME,
                ""
        );
    }

    public String getEmail() {
        return preferences.getString(
                KEY_EMAIL,
                ""
        );
    }

    public String getType() {
        return preferences.getString(
                KEY_TYPE,
                ""
        );
    }

    public String getRole() {
        return preferences.getString(
                KEY_ROLE,
                ""
        );
    }

    public boolean isCustomer() {
        return ROLE_CUSTOMER.equals(
                getRole()
        );
    }

    public boolean isEmployee() {
        return ROLE_EMPLOYEE.equals(
                getRole()
        );
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(
                getRole()
        );
    }

    /**
     * Xóa toàn bộ Session khi đăng xuất.
     */
    public void logout() {
        preferences.edit()
                .clear()
                .apply();
    }
}