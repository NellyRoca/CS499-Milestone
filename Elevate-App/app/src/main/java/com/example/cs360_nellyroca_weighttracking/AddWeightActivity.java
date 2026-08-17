/**
 * Also updated to interact with Database
 */
package com.example.cs360_nellyroca_weighttracking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;
import android.telephony.SmsManager;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddWeightActivity extends AppCompatActivity {

    private TextInputEditText editWeight;
    private Button buttonSaveEntry;
    private Button buttonCancel;

    private DatabaseHelper databaseHelper;

    private int userId = -1;
    private int weightId = -1;
    private String entryDate = null;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        databaseHelper = new DatabaseHelper(this);

        editWeight = findViewById(R.id.editWeight);
        buttonSaveEntry = findViewById(R.id.buttonSaveEntry);
        buttonCancel = findViewById(R.id.buttonCancel);

        if (getIntent() != null) {
            userId = getIntent().getIntExtra("USER_ID", -1);
            weightId = getIntent().getIntExtra("WEIGHT_ID", -1);
            entryDate = getIntent().getStringExtra("ENTRY_DATE");
            String weightValue = getIntent().getStringExtra("WEIGHT_VALUE");

            if (weightId != -1) {
                isEditMode = true;
                if (weightValue != null) {
                    editWeight.setText(weightValue);
                }
            }
        }

        buttonSaveEntry.setOnClickListener(v -> saveWeightEntry());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void saveWeightEntry() {
        String weightText = getInput(editWeight);

        if (weightText.isEmpty()) {
            Toast.makeText(this, "Please enter a weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == -1) {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        double weightValue;
        try {
            weightValue = Double.parseDouble(weightText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success;

        if (isEditMode) {
            success = databaseHelper.updateWeightEntry(weightId, entryDate, weightValue);
            if (success) {
                Toast.makeText(this, "Weight entry updated.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update entry.", Toast.LENGTH_SHORT).show();
            }
        } else {
            String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                    .format(new Date());

            success = databaseHelper.addWeightEntry(userId, currentDate, weightValue);
            if (success) {
                Toast.makeText(this, "Weight entry saved.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save entry.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void checkGoalReached(double currentWeight) {
        double goalWeight = databaseHelper.getGoalWeight(userId);

        if (goalWeight != -1 && currentWeight <= goalWeight) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                // For demonstration, use a test number or add your own value later
                String testPhoneNumber = "5551234567";
                sendSmsAlert(testPhoneNumber,
                        "Congratulations! You have reached your goal weight.");
            } else {
                Toast.makeText(this,
                        "Goal reached, but SMS permission is not granted.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendSmsAlert(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS alert sent.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
        }
    }
    private String getInput(TextInputEditText inputField) {
        if (inputField.getText() == null) {
            return "";
        }
        return inputField.getText().toString().trim();
    }
}