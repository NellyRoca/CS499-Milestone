/**
 *  Updated to read from the Database
 */
package com.example.cs360_nellyroca_weighttracking;

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

public class DashboardActivity extends AppCompatActivity {

    private TextView textGoalWeight;
    private RecyclerView recyclerViewWeights;
    private Button buttonAddEntry;
    private Button buttonSetGoal;
    private Button buttonLogout;
    private DatabaseHelper databaseHelper;
    private WeightAdapter adapter;
    private List<WeightEntry> weightList;

    private int userId = -1;
    private String username = "";

    private final ActivityResultLauncher<Intent> goalActivityLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            String goalWeight = result.getData().getStringExtra("GOAL_WEIGHT");
                            if (goalWeight != null && !goalWeight.isEmpty()) {
                                textGoalWeight.setText("Goal Weight: " + goalWeight + " lbs");
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseHelper = new DatabaseHelper(this);

        textGoalWeight = findViewById(R.id.textGoalWeight);
        recyclerViewWeights = findViewById(R.id.recyclerViewWeights);
        buttonAddEntry = findViewById(R.id.buttonAddEntry);
        buttonSetGoal = findViewById(R.id.buttonSetGoal);
        buttonLogout = findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getIntExtra("USER_ID", -1);
            username = intent.getStringExtra("USERNAME");
        }

        recyclerViewWeights.setLayoutManager(new LinearLayoutManager(this));
        weightList = new ArrayList<>();

        adapter = new WeightAdapter(
                weightList,
                weightId -> {
                    boolean deleted = databaseHelper.deleteWeightEntry(weightId);
                    if (deleted) {
                        Toast.makeText(this, "Entry deleted.", Toast.LENGTH_SHORT).show();
                        loadWeights();
                    } else {
                        Toast.makeText(this, "Delete failed.", Toast.LENGTH_SHORT).show();
                    }
                },
                weightEntry -> {
                    Intent editIntent = new Intent(DashboardActivity.this, AddWeightActivity.class);
                    editIntent.putExtra("USER_ID", userId);
                    editIntent.putExtra("WEIGHT_ID", weightEntry.getId());
                    editIntent.putExtra("ENTRY_DATE", weightEntry.getDate());
                    editIntent.putExtra("WEIGHT_VALUE", weightEntry.getWeight().replace(" lbs", ""));
                    startActivity(editIntent);
                }
        );

        recyclerViewWeights.setAdapter(adapter);

        loadGoalWeight();
        loadWeights();

        buttonAddEntry.setOnClickListener(v -> {
            Intent addIntent = new Intent(DashboardActivity.this, AddWeightActivity.class);
            addIntent.putExtra("USER_ID", userId);
            startActivity(addIntent);
        });

        buttonSetGoal.setOnClickListener(v -> {
            Intent goalIntent = new Intent(DashboardActivity.this, GoalSmsActivity.class);
            goalIntent.putExtra("USER_ID", userId);
            goalActivityLauncher.launch(goalIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWeights();
        loadGoalWeight();
    }

    private void loadWeights() {
        weightList.clear();

        Cursor cursor = databaseHelper.getAllWeights(userId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String date = cursor.getString(1);
                double weightValue = cursor.getDouble(2);

                weightList.add(new WeightEntry(id, date, weightValue + " lbs"));
            }
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }

    private void loadGoalWeight() {
        double goalWeight = databaseHelper.getGoalWeight(userId);
        if (goalWeight != -1) {
            textGoalWeight.setText("Goal Weight: " + goalWeight + " lbs");
        } else {
            textGoalWeight.setText("Goal Weight: -- lbs");
        }
    }
}