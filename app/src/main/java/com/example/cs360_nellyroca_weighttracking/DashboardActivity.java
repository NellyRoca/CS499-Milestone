package com.example.cs360_nellyroca_weighttracking;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    // ============================================================
    // Intent extras
    // ============================================================

    public static final String EXTRA_USER_ID = "USER_ID";
    public static final String EXTRA_USERNAME = "USERNAME";
    public static final String EXTRA_WEIGHT_ID = "WEIGHT_ID";
    public static final String EXTRA_ENTRY_DATE = "ENTRY_DATE";
    public static final String EXTRA_WEIGHT_VALUE = "WEIGHT_VALUE";
    public static final String EXTRA_GOAL_WEIGHT = "GOAL_WEIGHT";

    // ============================================================
    // UI components
    // ============================================================

    private TextView textDashboardTitle;
    private TextView textDisplayName;
    private TextView textGoalWeight;
    private TextView textProgress;
    private TextView textEntryCount;
    private TextView textAverageWeight;

    private RecyclerView recyclerViewWeights;

    private Button buttonAddEntry;
    private Button buttonSetGoal;
    private Button buttonLogout;

    // ============================================================
    // Database and adapter
    // ============================================================

    private DatabaseHelper databaseHelper;
    private WeightAdapter adapter;

    // ============================================================
    // Weight data
    // ============================================================

    private final List<WeightEntry> weightList =
            new ArrayList<>();

    // ============================================================
    // Current user
    // ============================================================

    private int userId = -1;

    private String username = "";
    private String displayName = "";
    private String weightUnit = "lbs";

    // ============================================================
    // Goal activity result
    // ============================================================

    private final ActivityResultLauncher<Intent> goalActivityLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null) {

                            String goalWeight =
                                    result.getData().getStringExtra(
                                            EXTRA_GOAL_WEIGHT
                                    );

                            if (goalWeight != null &&
                                    !goalWeight.isEmpty()) {

                                loadGoalWeight();
                                updateProgress();
                            }
                        }
                    }
            );

    // ============================================================
    // Activity lifecycle
    // ============================================================

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_dashboard
        );

        initializeDatabase();
        initializeViews();
        loadUserData();
        loadUserProfile();
        setupRecyclerView();
        setupButtonListeners();

        loadDashboardData();
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadUserProfile();
        loadDashboardData();
    }

    // ============================================================
    // Initialization
    // ============================================================

    private void initializeDatabase() {

        databaseHelper =
                new DatabaseHelper(this);
    }

    private void initializeViews() {

        textDashboardTitle =
                findViewById(R.id.textDashboardTitle);

        textDisplayName =
                findViewById(R.id.textDisplayName);

        textGoalWeight =
                findViewById(R.id.textGoalWeight);

        textProgress =
                findViewById(R.id.textProgress);

        textEntryCount =
                findViewById(R.id.textEntryCount);

        textAverageWeight =
                findViewById(R.id.textAverageWeight);

        recyclerViewWeights =
                findViewById(R.id.recyclerViewWeights);

        buttonAddEntry =
                findViewById(R.id.buttonAddEntry);

        buttonSetGoal =
                findViewById(R.id.buttonSetGoal);

        buttonLogout =
                findViewById(R.id.buttonLogout);
    }

    // ============================================================
    // User information
    // ============================================================

    private void loadUserData() {

        Intent intent = getIntent();

        if (intent == null) {
            return;
        }

        userId =
                intent.getIntExtra(
                        EXTRA_USER_ID,
                        -1
                );

        username =
                intent.getStringExtra(
                        EXTRA_USERNAME
                );

        if (username == null) {
            username = "";
        }
    }

    /**
     * Retrieves the user's profile directly from SQLite.
     */
    private void loadUserProfile() {

        if (userId == -1) {
            return;
        }

        Cursor cursor =
                databaseHelper.getUserProfile(userId);

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                displayName =
                        cursor.getString(0);

                weightUnit =
                        cursor.getString(1);

                if (displayName == null ||
                        displayName.trim().isEmpty()) {

                    displayName = username;
                }

                if (weightUnit == null ||
                        weightUnit.trim().isEmpty()) {

                    weightUnit = "lbs";
                }
            }

            cursor.close();
        }

        textDisplayName.setText(
                "Welcome, " + displayName
        );

        if (adapter != null) {

            adapter.setWeightUnit(
                    weightUnit
            );
        }
    }

    // ============================================================
    // RecyclerView
    // ============================================================

    private void setupRecyclerView() {

        recyclerViewWeights.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter =
                new WeightAdapter(
                        weightList,
                        this::confirmDelete,
                        this::openEditEntry,
                        weightUnit
                );

        recyclerViewWeights.setAdapter(
                adapter
        );
    }

    // ============================================================
    // Button listeners
    // ============================================================

    private void setupButtonListeners() {

        buttonAddEntry.setOnClickListener(
                v -> openAddEntryActivity()
        );

        buttonSetGoal.setOnClickListener(
                v -> openGoalActivity()
        );

        buttonLogout.setOnClickListener(
                v -> logoutUser()
        );
    }

    // ============================================================
    // Dashboard loading
    // ============================================================

    private void loadDashboardData() {

        loadGoalWeight();
        loadWeights();
        loadStatistics();
        updateProgress();
    }

    // ============================================================
    // Navigation
    // ============================================================

    private void openAddEntryActivity() {

        Intent intent =
                new Intent(
                        this,
                        AddWeightActivity.class
                );

        intent.putExtra(
                EXTRA_USER_ID,
                userId
        );

        startActivity(intent);
    }

    private void openGoalActivity() {

        Intent intent =
                new Intent(
                        this,
                        GoalSmsActivity.class
                );

        intent.putExtra(
                EXTRA_USER_ID,
                userId
        );

        goalActivityLauncher.launch(intent);
    }

    private void logoutUser() {

        Intent intent =
                new Intent(
                        this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK
        );

        startActivity(intent);

        finish();
    }

    // ============================================================
    // Weight editing
    // ============================================================

    private void openEditEntry(
            WeightEntry entry) {

        Intent intent =
                new Intent(
                        this,
                        AddWeightActivity.class
                );

        intent.putExtra(
                EXTRA_USER_ID,
                userId
        );

        intent.putExtra(
                EXTRA_WEIGHT_ID,
                entry.getId()
        );

        intent.putExtra(
                EXTRA_ENTRY_DATE,
                entry.getDate()
        );

        intent.putExtra(
                EXTRA_WEIGHT_VALUE,
                entry.getWeight()
        );

        startActivity(intent);
    }

    // ============================================================
    // Weight deletion
    // ============================================================

    private void confirmDelete(
            int weightId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage(
                        "Are you sure you want to delete " +
                                "this weight entry?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) ->
                                deleteEntry(weightId)
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    private void deleteEntry(
            int weightId) {

        boolean deleted =
                databaseHelper.deleteWeightEntry(
                        weightId
                );

        if (deleted) {

            Toast.makeText(
                    this,
                    "Entry deleted successfully.",
                    Toast.LENGTH_SHORT
            ).show();

            loadWeights();
            loadStatistics();
            updateProgress();

        } else {

            Toast.makeText(
                    this,
                    "Unable to delete entry.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ============================================================
    // Weight history
    // ============================================================

    /**
     * Loads weight history from SQLite.
     *
     * SQLite handles the ordering, with the newest
     * entry returned first.
     */
    private void loadWeights() {

        weightList.clear();

        Cursor cursor =
                databaseHelper.getAllWeights(
                        userId
                );

        if (cursor != null) {

            while (cursor.moveToNext()) {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_WEIGHT_ID
                                )
                        );

                String date =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_ENTRY_DATE
                                )
                        );

                double weight =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_WEIGHT_VALUE
                                )
                        );

                weightList.add(
                        new WeightEntry(
                                id,
                                date,
                                weight
                        )
                );
            }

            cursor.close();
        }

        if (adapter != null) {

            adapter.setWeightUnit(
                    weightUnit
            );

            adapter.notifyDataSetChanged();
        }
    }

    // ============================================================
    // Database statistics
    // ============================================================

    /**
     * Retrieves statistics directly from SQLite.
     *
     * This demonstrates database-driven aggregation rather
     * than calculating statistics from the RecyclerView list.
     */
    private void loadStatistics() {

        if (userId == -1) {
            return;
        }

        int entryCount =
                databaseHelper.getWeightEntryCount(
                        userId
                );

        double averageWeight =
                databaseHelper.getAverageWeight(
                        userId
                );

        textEntryCount.setText(
                "Entries: " + entryCount
        );

        if (entryCount > 0) {

            textAverageWeight.setText(
                    String.format(
                            Locale.US,
                            "Average: %.1f %s",
                            averageWeight,
                            weightUnit
                    )
            );

        } else {

            textAverageWeight.setText(
                    "Average: --"
            );
        }
    }

    // ============================================================
    // Goal
    // ============================================================

    private void loadGoalWeight() {

        double goalWeight =
                databaseHelper.getGoalWeight(
                        userId
                );

        if (goalWeight >= 0) {

            textGoalWeight.setText(
                    String.format(
                            Locale.US,
                            "Goal Weight: %.1f %s",
                            goalWeight,
                            weightUnit
                    )
            );

        } else {

            textGoalWeight.setText(
                    "Goal Weight: -- " +
                            weightUnit
            );
        }
    }

    // ============================================================
    // Progress calculation
    // ============================================================

    /**
     * Calculates progress using the latest and earliest
     * weight records retrieved directly from SQLite.
     */
    private void updateProgress() {

        double goalWeight =
                databaseHelper.getGoalWeight(
                        userId
                );

        if (goalWeight < 0) {

            textProgress.setText(
                    "Progress: Set a goal to begin tracking."
            );

            return;
        }

        double currentWeight =
                getWeightFromCursor(
                        databaseHelper.getLatestWeight(
                                userId
                        )
                );

        if (currentWeight <= 0) {

            textProgress.setText(
                    "Progress: Add a weight entry to begin."
            );

            return;
        }

        if (currentWeight <= goalWeight) {

            textProgress.setText(
                    "Progress: Goal reached!"
            );

            return;
        }

        double startingWeight =
                getWeightFromCursor(
                        databaseHelper.getStartingWeight(
                                userId
                        )
                );

        if (startingWeight <= 0) {

            textProgress.setText(
                    "Progress: Unable to calculate."
            );

            return;
        }

        if (startingWeight <= goalWeight) {

            textProgress.setText(
                    "Progress: Goal tracking started."
            );

            return;
        }

        double totalWeightToLose =
                startingWeight - goalWeight;

        double weightLost =
                startingWeight - currentWeight;

        double progress =
                (weightLost /
                        totalWeightToLose) * 100;

        if (progress < 0) {
            progress = 0;
        }

        if (progress > 100) {
            progress = 100;
        }

        textProgress.setText(
                String.format(
                        Locale.US,
                        "Progress: %.1f%%",
                        progress
                )
        );
    }

    /**
     * Extracts a weight value from a database cursor.
     */
    private double getWeightFromCursor(
            Cursor cursor) {

        if (cursor == null) {
            return -1;
        }

        double weight = -1;

        if (cursor.moveToFirst()) {

            weight =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_WEIGHT_VALUE
                            )
                    );
        }

        cursor.close();

        return weight;
    }
}
