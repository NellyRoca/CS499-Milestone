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
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_login
        );

        initializeDatabase();
        initializeViews();
        setupButtonListeners();
    }

    /**
     * Initializes the SQLite database helper.
     */
    private void initializeDatabase() {

        databaseHelper =
                new DatabaseHelper(this);
    }

    /**
     * Connects Java variables to the login layout.
     */
    private void initializeViews() {

        editUsername =
                findViewById(
                        R.id.editUsername
                );

        editPassword =
                findViewById(
                        R.id.editPassword
                );

        buttonLogin =
                findViewById(
                        R.id.buttonLogin
                );

        buttonCreateAccount =
                findViewById(
                        R.id.buttonCreateAccount
                );
    }

    /**
     * Sets up login and account creation actions.
     */
    private void setupButtonListeners() {

        buttonLogin.setOnClickListener(
                v -> loginUser()
        );

        buttonCreateAccount.setOnClickListener(
                v -> createAccount()
        );
    }

    /**
     * Validates the user's credentials and opens
     * the dashboard when authentication succeeds.
     */
    private void loginUser() {

        String username =
                getInput(editUsername);

        String password =
                getInput(editPassword);

        if (username.isEmpty() ||
                password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter a username and password.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean isValid =
                databaseHelper.validateUser(
                        username,
                        password
                );

        if (!isValid) {

            Toast.makeText(
                    this,
                    "Invalid username or password.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int userId =
                databaseHelper.getUserId(
                        username
                );

        if (userId == -1) {

            Toast.makeText(
                    this,
                    "Unable to retrieve user information.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        /*
         * The user's profile is loaded by DashboardActivity.
         * Passing the user ID establishes the relationship
         * between authentication and the user's database records.
         */
        Intent intent =
                new Intent(
                        LoginActivity.this,
                        DashboardActivity.class
                );

        intent.putExtra(
                DashboardActivity.EXTRA_USER_ID,
                userId
        );

        intent.putExtra(
                DashboardActivity.EXTRA_USERNAME,
                username
        );

        startActivity(intent);

        Toast.makeText(
                this,
                "Login successful.",
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * Creates a new user account.
     *
     * DatabaseHelper.registerUser() creates both the
     * users record and the associated default profile.
     */
    private void createAccount() {

        String username =
                getInput(editUsername);

        String password =
                getInput(editPassword);

        if (username.isEmpty() ||
                password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter a username and password.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (databaseHelper.userExists(username)) {

            Toast.makeText(
                    this,
                    "Username already exists. Please choose another.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean success =
                databaseHelper.registerUser(
                        username,
                        password
                );

        if (success) {

            Toast.makeText(
                    this,
                    "Account created successfully. " +
                            "You can now log in.",
                    Toast.LENGTH_SHORT
            ).show();

            editUsername.setText("");
            editPassword.setText("");

        } else {

            Toast.makeText(
                    this,
                    "Account creation failed.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Safely retrieves and trims text from an input field.
     */
    private String getInput(
            TextInputEditText inputField) {

        if (inputField.getText() == null) {
            return "";
        }

        return inputField
                .getText()
                .toString()
                .trim();
    }
}
