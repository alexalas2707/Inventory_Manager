package com.example.inventorymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "inventoryManagerDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "user_data";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_LASTNAME = "lastname";
    private static final String KEY_USER_USERNAME = "username";
    private static final String KEY_USER_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_USER_NAME + " TEXT," +
                KEY_USER_LASTNAME + " TEXT," +
                KEY_USER_USERNAME + " TEXT," +
                KEY_USER_PASSWORD + " TEXT" + // Store a hashed password, not plain text
                ")";

        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simple database upgrade policy for the sake of the example
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    // Insert a post into the database
    public void addUser(User user) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, user.getName());
            values.put(KEY_USER_LASTNAME, user.getLastname());
            values.put(KEY_USER_USERNAME, user.getUsername());
            values.put(KEY_USER_PASSWORD, user.getPassword()); // Make sure to hash the password

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_USERS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DatabaseHelper", "Error while trying to add user to database", e);
        } finally {
            db.endTransaction();
        }
    }

    // Update a user's password in the database
    public int updateUserPassword(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_PASSWORD, user.getPassword()); // hash the password

        // Updating password for user with that username
        return db.update(TABLE_USERS, values, KEY_USER_USERNAME + " = ?",
                new String[]{String.valueOf(user.getUsername())});
    }

    // This method will be used in main activity to check if users exist
    public boolean checkUserExist() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        boolean hasObjects = cursor.moveToFirst();
        cursor.close();
        return hasObjects;
    }

    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + KEY_USER_PASSWORD + " FROM " + TABLE_USERS + " WHERE " + KEY_USER_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            int passwordColumnIndex = cursor.getColumnIndex(KEY_USER_PASSWORD);
            if (passwordColumnIndex != -1) {
                String storedPassword = cursor.getString(passwordColumnIndex);
                cursor.close();
                // Compare the stored hashed password with the hashed version of the input password
                return BCrypt.checkpw(password, storedPassword);
            }
        }


        if (cursor != null) {
            cursor.close();
        }
        return false;
    }

    // method to get username
    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] projection = {
                KEY_USER_ID,
                KEY_USER_NAME,
                KEY_USER_LASTNAME,
                KEY_USER_USERNAME
        };

        Cursor cursor = db.query(
                TABLE_USERS, // The table to query
                projection,  // The columns to return
                KEY_USER_USERNAME + "=?", // The columns for the WHERE clause
                new String[]{username},    // The values for the WHERE clause
                null,                      // Don't group the rows
                null,                      // Don't filter by row groups
                null                       // The sort order
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(KEY_USER_NAME);
                int lastNameIndex = cursor.getColumnIndex(KEY_USER_LASTNAME);
                int usernameIndex = cursor.getColumnIndex(KEY_USER_USERNAME);

                if (nameIndex != -1 && lastNameIndex != -1 && usernameIndex != -1) {
                    user = new User(
                            cursor.getString(nameIndex),
                            cursor.getString(lastNameIndex),
                            cursor.getString(usernameIndex),
                            ""); // Password is not needed here
                }
            }
            cursor.close();
        }

        return user;
    }





}

