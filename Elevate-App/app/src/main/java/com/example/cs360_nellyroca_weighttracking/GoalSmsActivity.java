package com.example.cs360_nellyroca_weighttracking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;

public class GoalSmsActivity extends AppCompatActivity {

    private TextInputEditText editGoalWeight;
    private TextView textPermissionStatus;
    private TextView textPermissionMessage;
    private Button buttonSaveGoal;
    private Button buttonEnableSms;

    private DatabaseHelper databaseHelper;
    private int userId = -1;

    private final ActivityResultLauncher<String> requestSmsPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    textPermissionStatus.setText(getString(R.string.permission_status_granted));
                    textPermissionMessage.setVisibility(View.GONE);
                    Toast.makeText(this, "SMS permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    textPermissionStatus.setText(getString(R.string.permission_status_denied));
                    textPermissionMessage.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_sms);

        databaseHelper = new DatabaseHelper(this);

        editGoalWeight = findViewById(R.id.editGoalWeight);
        textPermissionStatus = findViewById(R.id.textPermissionStatus);
        textPermissionMessage = findViewById(R.id.textPermissionMessage);
        buttonSaveGoal = findViewById(R.id.buttonSaveGoal);
        buttonEnableSms = findViewById(R.id.buttonEnableSms);

        if (getIntent() != null) {
            userId = getIntent().getIntExtra("USER_ID", -1);
        }

        loadCurrentGoal();

        buttonSaveGoal.setOnClickListener(v -> saveGoalWeight());
        buttonEnableSms.setOnClickListener(v -> checkSmsPermission());
    }

    private void loadCurrentGoal() {
        if (userId == -1) {
            return;
        }

        double currentGoal = databaseHelper.getGoalWeight(userId);
        if (currentGoal != -1) {
            editGoalWeight.setText(String.valueOf(currentGoal));
        }
    }

    private void saveGoalWeight() {
        String goalText = getInput(editGoalWeight);

        if (goalText.isEmpty()) {
            Toast.makeText(this, "Please enter a goal weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == -1) {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        double goalWeight;
        try {
            goalWeight = Double.parseDouble(goalText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = databaseHelper.saveGoalWeight(userId, goalWeight);

        if (success) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("GOAL_WEIGHT", goalText);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Goal weight saved.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save goal weight.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            textPermissionStatus.setText(getString(R.string.permission_status_granted));
            textPermissionMessage.setVisibility(View.GONE);
            Toast.makeText(this, "SMS permission already granted.", Toast.LENGTH_SHORT).show();
        } else {
            requestSmsPermission.launch(Manifest.permission.SEND_SMS);
        }
    }

    private void sendSmsAlert(String phoneNumber, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, "SMS alert sent.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getInput(TextInputEditText inputField) {
        if (inputField.getText() == null) {
            return "";
        }
        return inputField.getText().toString().trim();
    }
}