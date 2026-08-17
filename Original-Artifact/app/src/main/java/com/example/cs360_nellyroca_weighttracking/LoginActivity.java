/**
 * Updated to match DatabaseHelper
 */
package com.example.cs360_nellyroca_weighttracking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editUsername;
    private TextInputEditText editPassword;
    private Button buttonLogin;
    private Button buttonCreateAccount;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Link UI elements
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        // Login button behavior
        buttonLogin.setOnClickListener(v -> loginUser());

        // Create account button behavior
        buttonCreateAccount.setOnClickListener(v -> createAccount());
    }

    /**
     * Validates existing user credentials and navigates to dashboard.
     */
    private void loginUser() {
        String username = getInput(editUsername);
        String password = getInput(editPassword);

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter a username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isValid = databaseHelper.validateUser(username, password);

        if (isValid) {
            int userId = databaseHelper.getUserId(username);

            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("USERNAME", username);
            startActivity(intent);

            Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a new account if the username does not already exist.
     */
    private void createAccount() {
        String username = getInput(editUsername);
        String password = getInput(editPassword);

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter a username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.userExists(username)) {
            Toast.makeText(this, "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = databaseHelper.registerUser(username, password);

        if (success) {
            Toast.makeText(this, "Account created successfully. You can now log in.", Toast.LENGTH_SHORT).show();
            editUsername.setText("");
            editPassword.setText("");
        } else {
            Toast.makeText(this, "Account creation failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Safely returns trimmed text from an input field.
     */
    private String getInput(TextInputEditText inputField) {
        if (inputField.getText() == null) {
            return "";
        }
        return inputField.getText().toString().trim();
    }
}