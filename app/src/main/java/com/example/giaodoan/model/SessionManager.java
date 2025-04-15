package com.example.giaodoan.model;

import android.content.Context;
import android.content.SharedPreferences;


public class SessionManager {

    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Context context;

    private static SessionManager instance;

    private SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }


    public void saveLoginInfo(Integer user_id, String username, String password,String email,String phone, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID,  user_id);

        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_EMAIL,  email);
        editor.putString(KEY_PHONE,  phone);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }


    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }


    public UserInfo getLoggedInUser() {
        if (isLoggedIn()) {
            Integer user_id = sharedPreferences.getInt(KEY_USER_ID, -1);
            String username = sharedPreferences.getString(KEY_USERNAME, "_");
            String password = sharedPreferences.getString(KEY_PASSWORD, "_");
            String phone = sharedPreferences.getString(KEY_PHONE, "_");
            String email = sharedPreferences.getString(KEY_EMAIL, "_");
            String role = sharedPreferences.getString(KEY_ROLE, "User");
            return new UserInfo(user_id, username,password ,email, phone, role);
        }
        return null;
    }


    public Integer getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID,-1);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }
    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL,"_");
    }
    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE,"_");
    }


    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, "User");
    }


    public boolean isAdmin() {
        return "Admin".equals(getRole());
    }


    public boolean isStaff() {
        return "Staff".equals(getRole());
    }


    public void logout() {
        editor.clear();
        editor.apply();
    }
}


class UserInfo {
    private final Integer user_id;
    private final String username;
    private final String password;
    private final String email;
    private final String phone;
    private final String role;

    public UserInfo(Integer user_id, String username,String password,String email,String phone, String role) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public Integer getUserId() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + user_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}