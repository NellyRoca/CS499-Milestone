package com.example.cs360_nellyroca_weighttracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    /*
     * Elevate - Database Enhancement
     *
     * Milestone Three: Databases
     *
     * Enhancements:
     * - Added user_profiles table
     * - Added database migration support
     * - Added user profile persistence
     * - Improved weight history queries
     * - Added current and starting weight queries
     * - Added weight statistics queries
     * - Added user-specific data retrieval
     */

    private static final String DATABASE_NAME = "weight_tracker.db";
    private static final int DATABASE_VERSION = 2;

    // ============================================================
    // Users table
    // ============================================================

    public static final String TABLE_USERS = "users";

    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // ============================================================
    // User Profiles table
    // ============================================================

    public static final String TABLE_PROFILES = "user_profiles";

    public static final String COL_PROFILE_ID = "id";
    public static final String COL_PROFILE_USER_ID = "user_id";
    public static final String COL_DISPLAY_NAME = "display_name";
    public static final String COL_WEIGHT_UNIT = "weight_unit";

    // ============================================================
    // Weights table
    // ============================================================

    public static final String TABLE_WEIGHTS = "weights";

    public static final String COL_WEIGHT_ID = "id";
    public static final String COL_WEIGHT_USER_ID = "user_id";
    public static final String COL_ENTRY_DATE = "entry_date";
    public static final String COL_WEIGHT_VALUE = "weight";

    // ============================================================
    // Goals table
    // ============================================================

    public static final String TABLE_GOALS = "goals";

    public static final String COL_GOAL_ID = "id";
    public static final String COL_GOAL_USER_ID = "user_id";
    public static final String COL_GOAL_WEIGHT = "goal_weight";

    // ============================================================
    // Constructor
    // ============================================================

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ============================================================
    // Database creation
    // ============================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // --------------------------------------------------------
        // Users
        // --------------------------------------------------------

        String createUsersTable =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                        COL_PASSWORD + " TEXT NOT NULL)";

        // --------------------------------------------------------
        // User Profiles
        // --------------------------------------------------------

        String createProfilesTable =
                "CREATE TABLE " + TABLE_PROFILES + " (" +
                        COL_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_PROFILE_USER_ID + " INTEGER UNIQUE NOT NULL, " +
                        COL_DISPLAY_NAME + " TEXT, " +
                        COL_WEIGHT_UNIT + " TEXT DEFAULT 'lbs', " +
                        "FOREIGN KEY(" + COL_PROFILE_USER_ID + ") REFERENCES " +
                        TABLE_USERS + "(" + COL_USER_ID + "))";

        // --------------------------------------------------------
        // Weights
        // --------------------------------------------------------

        String createWeightsTable =
                "CREATE TABLE " + TABLE_WEIGHTS + " (" +
                        COL_WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_WEIGHT_USER_ID + " INTEGER NOT NULL, " +
                        COL_ENTRY_DATE + " TEXT NOT NULL, " +
                        COL_WEIGHT_VALUE + " REAL NOT NULL, " +
                        "FOREIGN KEY(" + COL_WEIGHT_USER_ID + ") REFERENCES " +
                        TABLE_USERS + "(" + COL_USER_ID + "))";

        // --------------------------------------------------------
        // Goals
        // --------------------------------------------------------

        String createGoalsTable =
                "CREATE TABLE " + TABLE_GOALS + " (" +
                        COL_GOAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_GOAL_USER_ID + " INTEGER UNIQUE NOT NULL, " +
                        COL_GOAL_WEIGHT + " REAL NOT NULL, " +
                        "FOREIGN KEY(" + COL_GOAL_USER_ID + ") REFERENCES " +
                        TABLE_USERS + "(" + COL_USER_ID + "))";

        db.execSQL(createUsersTable);
        db.execSQL(createProfilesTable);
        db.execSQL(createWeightsTable);
        db.execSQL(createGoalsTable);
    }

    // ============================================================
    // Database upgrade / migration
    // ============================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        /*
         * Version 1 -> Version 2
         *
         * Existing users, weights, and goals are preserved.
         * Only the new profile table is created.
         */

        if (oldVersion < 2) {

            String createProfilesTable =
                    "CREATE TABLE " + TABLE_PROFILES + " (" +
                            COL_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            COL_PROFILE_USER_ID + " INTEGER UNIQUE NOT NULL, " +
                            COL_DISPLAY_NAME + " TEXT, " +
                            COL_WEIGHT_UNIT + " TEXT DEFAULT 'lbs', " +
                            "FOREIGN KEY(" + COL_PROFILE_USER_ID + ") REFERENCES " +
                            TABLE_USERS + "(" + COL_USER_ID + "))";

            db.execSQL(createProfilesTable);

            /*
             * Create a default profile for existing users.
             * This allows users from the previous database version
             * to immediately use the new profile functionality.
             */
            db.execSQL(
                    "INSERT INTO " + TABLE_PROFILES +
                            " (" + COL_PROFILE_USER_ID + ", " +
                            COL_DISPLAY_NAME + ", " +
                            COL_WEIGHT_UNIT + ") " +
                            "SELECT " +
                            COL_USER_ID + ", " +
                            COL_USERNAME + ", 'lbs' " +
                            "FROM " + TABLE_USERS
            );
        }
    }

    // ============================================================
    // User registration
    // ============================================================

    public boolean registerUser(String username, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        try {

            ContentValues userValues = new ContentValues();
            userValues.put(COL_USERNAME, username);
            userValues.put(COL_PASSWORD, password);

            long userId =
                    db.insert(TABLE_USERS, null, userValues);

            if (userId == -1) {
                return false;
            }

            // Create a default profile for the new user.
            ContentValues profileValues = new ContentValues();

            profileValues.put(
                    COL_PROFILE_USER_ID,
                    userId
            );

            profileValues.put(
                    COL_DISPLAY_NAME,
                    username
            );

            profileValues.put(
                    COL_WEIGHT_UNIT,
                    "lbs"
            );

            long profileResult =
                    db.insert(
                            TABLE_PROFILES,
                            null,
                            profileValues
                    );

            if (profileResult == -1) {
                return false;
            }

            db.setTransactionSuccessful();

            return true;

        } finally {

            db.endTransaction();
        }
    }

    // ============================================================
    // User authentication
    // ============================================================

    public boolean validateUser(
            String username,
            String password) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        String[] columns = {
                COL_USER_ID
        };

        String selection =
                COL_USERNAME + "=? AND " +
                        COL_PASSWORD + "=?";

        String[] selectionArgs = {
                username,
                password
        };

        Cursor cursor = db.query(
                TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean isValid =
                cursor.moveToFirst();

        cursor.close();

        return isValid;
    }

    // ============================================================
    // User existence
    // ============================================================

    public boolean userExists(String username) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USERNAME + "=?",
                new String[]{username},
                null,
                null,
                null
        );

        boolean exists =
                cursor.moveToFirst();

        cursor.close();

        return exists;
    }

    // ============================================================
    // Get user ID
    // ============================================================

    public int getUserId(String username) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USERNAME + "=?",
                new String[]{username},
                null,
                null,
                null
        );

        int userId = -1;

        if (cursor.moveToFirst()) {

            userId =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    COL_USER_ID
                            )
                    );
        }

        cursor.close();

        return userId;
    }

    // ============================================================
    // Weight entry methods
    // ============================================================

    public boolean addWeightEntry(
            int userId,
            String date,
            double weight) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COL_WEIGHT_USER_ID,
                userId
        );

        values.put(
                COL_ENTRY_DATE,
                date
        );

        values.put(
                COL_WEIGHT_VALUE,
                weight
        );

        long result =
                db.insert(
                        TABLE_WEIGHTS,
                        null,
                        values
                );

        return result != -1;
    }

    /**
     * Returns all weight entries for a specific user.
     *
     * Newest database entry appears first.
     */
    public Cursor getAllWeights(int userId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        String[] columns = {
                COL_WEIGHT_ID,
                COL_ENTRY_DATE,
                COL_WEIGHT_VALUE
        };

        return db.query(
                TABLE_WEIGHTS,
                columns,
                COL_WEIGHT_USER_ID + "=?",
                new String[]{
                        String.valueOf(userId)
                },
                null,
                null,
                COL_WEIGHT_ID + " DESC"
        );
    }

    /**
     * Returns the most recent weight entry.
     */
    public Cursor getLatestWeight(int userId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        String[] columns = {
                COL_WEIGHT_ID,
                COL_ENTRY_DATE,
                COL_WEIGHT_VALUE
        };

        return db.query(
                TABLE_WEIGHTS,
                columns,
                COL_WEIGHT_USER_ID + "=?",
                new String[]{
                        String.valueOf(userId)
                },
                null,
                null,
                COL_WEIGHT_ID + " DESC",
                "1"
        );
    }

    /**
     * Returns the earliest recorded weight entry.
     */
    public Cursor getStartingWeight(int userId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        String[] columns = {
                COL_WEIGHT_ID,
                COL_ENTRY_DATE,
                COL_WEIGHT_VALUE
        };

        return db.query(
                TABLE_WEIGHTS,
                columns,
                COL_WEIGHT_USER_ID + "=?",
                new String[]{
                        String.valueOf(userId)
                },
                null,
                null,
                COL_WEIGHT_ID + " ASC",
                "1"
        );
    }

    /**
     * Returns the number of weight entries
     * belonging to a specific user.
     */
    public int getWeightEntryCount(int userId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " +
                        TABLE_WEIGHTS +
                        " WHERE " +
                        COL_WEIGHT_USER_ID +
                        "=?",
                new String[]{
                        String.valueOf(userId)
                }
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    /**
     * Returns the average recorded weight
     * for a specific user.
     */
    public double getAverageWeight(int userId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT AVG(" +
                        COL_WEIGHT_VALUE +
                        ") FROM " +
                        TABLE_WEIGHTS +
                        " WHERE " +
                        COL_WEIGHT_USER_ID +
                        "=?",
                new String[]{
                        String.valueOf(userId)
                }
        );

        double average = 0;

        if (cursor.moveToFirst() &&
                !cursor.isNull(0)) {

            average = cursor.getDouble(0);
        }

        cursor.close();

        return average;
    }

    // ============================================================
    // Weight modification
    // ============================================================

    public boolean deleteWeightEntry(int weightId) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        TABLE_WEIGHTS,
                        COL_WEIGHT_ID + "=?",
                        new String[]{
                                String.valueOf(weightId)
                        }
                );

        return result > 0;
    }

    public boolean updateWeightEntry(
            int weightId,
            String date,
            double weight) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COL_ENTRY_DATE,
                date
        );

        values.put(
                COL_WEIGHT_VALUE,
                weight
        );

        int result =
                db.update(
                        TABLE_WEIGHTS,
                        values,
                        COL_WEIGHT_ID + "=?",
                        new String[]{
                                String.valueOf(weightId)
                        }
                );

        return result > 0;
    }

    // ============================================================
    // Goal methods
    // ============================================================

    public boolean saveGoalWeight(
            int userId,
            double goalWeight) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COL_GOAL_USER_ID,
                userId
        );

        values.put(
                COL_GOAL_WEIGHT,
                goalWeight
        );

        long result =
                db.insertWithOnConflict(
                        TABLE_GOALS,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );

        return result != -1;
    }

    public double getGoalWeight(int userId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_GOALS,
                new String[]{
                        COL_GOAL_WEIGHT
                },
                COL_GOAL_USER_ID + "=?",
                new String[]{
                        String.valueOf(userId)
                },
                null,
                null,
                null
        );

        double goalWeight = -1;

        if (cursor.moveToFirst()) {

            goalWeight =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    COL_GOAL_WEIGHT
                            )
                    );
        }

        cursor.close();

        return goalWeight;
    }

    // ============================================================
    // User profile methods
    // ============================================================

    /**
     * Creates or updates a user's profile.
     */
    public boolean saveUserProfile(
            int userId,
            String displayName,
            String weightUnit) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COL_PROFILE_USER_ID,
                userId
        );

        values.put(
                COL_DISPLAY_NAME,
                displayName
        );

        values.put(
                COL_WEIGHT_UNIT,
                weightUnit
        );

        long result =
                db.insertWithOnConflict(
                        TABLE_PROFILES,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );

        return result != -1;
    }

    /**
     * Returns the user's display name.
     */
    public String getDisplayName(int userId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PROFILES,
                new String[]{
                        COL_DISPLAY_NAME
                },
                COL_PROFILE_USER_ID + "=?",
                new String[]{
                        String.valueOf(userId)
                },
                null,
                null,
                null
        );

        String displayName = "";

        if (cursor.moveToFirst()) {

            String value =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    COL_DISPLAY_NAME
                            )
                    );

            if (value != null &&
                    !value.trim().isEmpty()) {

                displayName = value;
            }
        }

        cursor.close();

        return displayName;
    }

    /**
     * Returns the user's preferred weight unit.
     */
    public String getWeightUnit(int userId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PROFILES,
                new String[]{
                        COL_WEIGHT_UNIT
                },
                COL_PROFILE_USER_ID + "=?",
                new String[]{
                        String.valueOf(userId)
                },
                null,
                null,
                null
        );

        String weightUnit = "lbs";

        if (cursor.moveToFirst()) {

            String storedUnit =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    COL_WEIGHT_UNIT
                            )
                    );

            if (storedUnit != null &&
                    !storedUnit.trim().isEmpty()) {

                weightUnit = storedUnit;
            }
        }

        cursor.close();

        return weightUnit;
    }

    /**
     * Returns both profile values in one query.
     *
     * Column 0 = display name
     * Column 1 = preferred weight unit
     */
    public Cursor getUserProfile(int userId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_PROFILES,
                new String[]{
                        COL_DISPLAY_NAME,
                        COL_WEIGHT_UNIT
                },
                COL_PROFILE_USER_ID + "=?",
                new String[]{
                        String.valueOf(userId)
                },
                null,
                null,
                null
        );
    }
}