/**
 * Notes will be provided to explain the modules in this code specifically
 */
package com.example.cs360_nellyroca_weighttracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weight_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // Weights table
    public static final String TABLE_WEIGHTS = "weights";
    public static final String COL_WEIGHT_ID = "id";
    public static final String COL_WEIGHT_USER_ID = "user_id";
    public static final String COL_ENTRY_DATE = "entry_date";
    public static final String COL_WEIGHT_VALUE = "weight";

    // Goals table
    public static final String TABLE_GOALS = "goals";
    public static final String COL_GOAL_ID = "id";
    public static final String COL_GOAL_USER_ID = "user_id";
    public static final String COL_GOAL_WEIGHT = "goal_weight";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)";

        // Create weights table
        String createWeightsTable = "CREATE TABLE " + TABLE_WEIGHTS + " (" +
                COL_WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_WEIGHT_USER_ID + " INTEGER, " +
                COL_ENTRY_DATE + " TEXT, " +
                COL_WEIGHT_VALUE + " REAL, " +
                "FOREIGN KEY(" + COL_WEIGHT_USER_ID + ") REFERENCES " +
                TABLE_USERS + "(" + COL_USER_ID + "))";

        // Create goals table
        String createGoalsTable = "CREATE TABLE " + TABLE_GOALS + " (" +
                COL_GOAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_GOAL_USER_ID + " INTEGER UNIQUE, " +
                COL_GOAL_WEIGHT + " REAL, " +
                "FOREIGN KEY(" + COL_GOAL_USER_ID + ") REFERENCES " +
                TABLE_USERS + "(" + COL_USER_ID + "))";

        db.execSQL(createUsersTable);
        db.execSQL(createWeightsTable);
        db.execSQL(createGoalsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables if schema changes in future versions
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /**
     * Inserts a new user into the database. Returns true if successful, false otherwise.
     */
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    /**
     * Checks whether the username and password match an existing user.
     */
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " +
                        COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password}
        );

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    /**
     * Checks whether a username already exists.
     */
    public boolean userExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=?",
                new String[]{username}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * Returns the user ID for a given username.
     * Returns -1 if user is not found.
     */
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_ID + " FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=?",
                new String[]{username}
        );

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }

    /**
     * Adds a weight entry.
     */
    public boolean addWeightEntry(int userId, String date, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_WEIGHT_USER_ID, userId);
        values.put(COL_ENTRY_DATE, date);
        values.put(COL_WEIGHT_VALUE, weight);

        long result = db.insert(TABLE_WEIGHTS, null, values);
        return result != -1;
    }

    /**
     * Returns all weight entries.
     */
    public Cursor getAllWeights(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT " + COL_WEIGHT_ID + ", " + COL_ENTRY_DATE + ", " + COL_WEIGHT_VALUE +
                        " FROM " + TABLE_WEIGHTS +
                        " WHERE " + COL_WEIGHT_USER_ID + "=? ORDER BY " + COL_ENTRY_DATE + " DESC",
                new String[]{String.valueOf(userId)}
        );
    }

    /**
     * Deletes a weight entry by ID.
     */
    public boolean deleteWeightEntry(int weightId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_WEIGHTS, COL_WEIGHT_ID + "=?",
                new String[]{String.valueOf(weightId)});
        return result > 0;
    }

    /**
     * Updates a weight entry by ID.
     */
    public boolean updateWeightEntry(int weightId, String date, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ENTRY_DATE, date);
        values.put(COL_WEIGHT_VALUE, weight);

        int result = db.update(TABLE_WEIGHTS, values, COL_WEIGHT_ID + "=?",
                new String[]{String.valueOf(weightId)});
        return result > 0;
    }

    /**
     * Inserts or updates a user's goal weight.
     */
    public boolean saveGoalWeight(int userId, double goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_GOAL_USER_ID, userId);
        values.put(COL_GOAL_WEIGHT, goalWeight);

        long result = db.insertWithOnConflict(
                TABLE_GOALS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        return result != -1;
    }

    /**
     * Returns the user's goal weight, or -1 if none exists.
     */
    public double getGoalWeight(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_GOAL_WEIGHT + " FROM " + TABLE_GOALS +
                        " WHERE " + COL_GOAL_USER_ID + "=?",
                new String[]{String.valueOf(userId)}
        );

        double goalWeight = -1;
        if (cursor.moveToFirst()) {
            goalWeight = cursor.getDouble(0);
        }
        cursor.close();
        return goalWeight;
    }
}