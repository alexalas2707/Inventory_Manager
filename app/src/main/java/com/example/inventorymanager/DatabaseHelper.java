package com.example.inventorymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    // Database Info
    private static final String DATABASE_NAME = "inventoryManagerDatabase";
    private static final int DATABASE_VERSION = 7;

    // Table Names
    private static final String TABLE_USERS = "user_data";
    private static final String TABLE_PRODUCTS = "products";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_LASTNAME = "lastname";
    private static final String KEY_USER_USERNAME = "username";
    private static final String KEY_USER_PASSWORD = "password";

    // Product Table Columns
    private static final String KEY_PRODUCT_BARCODE = "barcode";
    private static final String KEY_PRODUCT_NAME = "name";
    private static final String KEY_PRODUCT_BRAND = "brand";
    private static final String KEY_PRODUCT_QUANTITY = "quantity_in_stock";
    private static final String KEY_PRODUCT_LOCATION = "warehouse_location";
    private static final String KEY_PRODUCT_TAGS = "tags";
    private static final String KEY_PRODUCT_IMAGE = "image"; // For storing image as BLOB



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
                KEY_USER_USERNAME + " TEXT UNIQUE," + // Ensure the username is unique
                KEY_USER_PASSWORD + " TEXT" + // Store a hashed password, not plain text
                ")";

        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS +
                "(" +
                KEY_PRODUCT_BARCODE + " TEXT PRIMARY KEY," + // Barcode as the primary key
                KEY_PRODUCT_NAME + " TEXT," +
                KEY_PRODUCT_BRAND + " TEXT," +
                KEY_PRODUCT_QUANTITY + " INTEGER," +
                KEY_PRODUCT_LOCATION + " TEXT," +
                KEY_PRODUCT_TAGS + " TEXT," +
                KEY_PRODUCT_IMAGE + " BLOB" + // Image stored as binary data
                ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
            onCreate(db);
        }
    }


    // Insert a user into the database
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_LASTNAME, user.getLastname());
        values.put(KEY_USER_USERNAME, user.getUsername());
        values.put(KEY_USER_PASSWORD, user.getPassword());

        long id = db.insert(TABLE_USERS, null, values);
        if (id == -1) {
            Log.d("DatabaseHelper", "Error while trying to add user to database");
        }
    }


    //--------------------------------------user related operations-------------------------------
    // Check if a user exists
    public boolean checkUserExist(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID},
                KEY_USER_USERNAME + " = ?", new String[]{username},
                null, null, null);
        int numberOfRows = cursor.getCount();
        cursor.close();
        return numberOfRows > 0;
    }

    // Check user credentials
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_PASSWORD},
                KEY_USER_USERNAME + " = ?", new String[]{username},
                null, null, null);

        boolean passwordMatch = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int passwordColumnIndex = cursor.getColumnIndex(KEY_USER_PASSWORD);
                if (passwordColumnIndex != -1) {
                    String storedPassword = cursor.getString(passwordColumnIndex);
                    passwordMatch = BCrypt.checkpw(password, storedPassword);
                }
            }
            cursor.close();
        }
        return passwordMatch;
    }


    // Update a user's password in the database
    public int updateUserPassword(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_PASSWORD, hashPassword(user.getPassword())); // Hash the password

        // Updating password for user with that username
        return db.update(TABLE_USERS, values, KEY_USER_USERNAME + " = ?",
                new String[]{user.getUsername()});
    }

    // Get user by username
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


    // This method will be used in main activity to check if users exist
    // This method will be used to check if any users exist
    public boolean checkUserExist() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        boolean hasUsers = cursor.moveToFirst();
        cursor.close();
        return hasUsers;
    }


    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_PRODUCT_BARCODE, product.getBarcode());
        values.put(KEY_PRODUCT_NAME, product.getName());
        values.put(KEY_PRODUCT_BRAND, product.getBrand());
        values.put(KEY_PRODUCT_QUANTITY, product.getQuantityInStock());
        values.put(KEY_PRODUCT_LOCATION, product.getWarehouseLocation());
        values.put(KEY_PRODUCT_TAGS, product.getTags());
        // Assuming you have a method to convert Bitmap to byte array
        values.put(KEY_PRODUCT_IMAGE, getBitmapAsByteArray(product.getImage()));

        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }


    public Product getProductByBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Product product = null;

        Cursor cursor = db.query(
                TABLE_PRODUCTS, // The table to query
                new String[]{
                        KEY_PRODUCT_BARCODE,
                        KEY_PRODUCT_NAME,
                        KEY_PRODUCT_BRAND,
                        KEY_PRODUCT_QUANTITY,
                        KEY_PRODUCT_LOCATION,
                        KEY_PRODUCT_TAGS,
                        KEY_PRODUCT_IMAGE
                },
                KEY_PRODUCT_BARCODE + "=?", // The columns for the WHERE clause
                new String[]{barcode},      // The values for the WHERE clause
                null,                       // Don't group the rows
                null,                       // Don't filter by row groups
                null                        // The sort order
        );

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_PRODUCT_NAME);
            int brandIndex = cursor.getColumnIndex(KEY_PRODUCT_BRAND);
            int quantityIndex = cursor.getColumnIndex(KEY_PRODUCT_QUANTITY);
            int locationIndex = cursor.getColumnIndex(KEY_PRODUCT_LOCATION);
            int tagsIndex = cursor.getColumnIndex(KEY_PRODUCT_TAGS);
            int imageIndex = cursor.getColumnIndex(KEY_PRODUCT_IMAGE);

            if (nameIndex != -1 && brandIndex != -1 && quantityIndex != -1 &&
                    locationIndex != -1 && tagsIndex != -1 && imageIndex != -1) {
                String name = cursor.getString(nameIndex);
                String brand = cursor.getString(brandIndex);
                int quantity = cursor.getInt(quantityIndex);
                String location = cursor.getString(locationIndex);
                String tags = cursor.getString(tagsIndex);
                byte[] imageBytes = cursor.getBlob(imageIndex);
                Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                product = new Product(barcode, name, brand, quantity, location, tags, image);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return product;
    }


    // Update product details in the database
    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCT_NAME, product.getName());
        values.put(KEY_PRODUCT_BRAND, product.getBrand());
        values.put(KEY_PRODUCT_QUANTITY, product.getQuantityInStock());
        values.put(KEY_PRODUCT_LOCATION, product.getWarehouseLocation());
        values.put(KEY_PRODUCT_TAGS, product.getTags());
        if (product.getImage() != null) {
            values.put(KEY_PRODUCT_IMAGE, getBitmapAsByteArray(product.getImage()));
        }

        // Updating product details for given barcode
        return db.update(TABLE_PRODUCTS, values, KEY_PRODUCT_BARCODE + " = ?",
                new String[]{String.valueOf(product.getBarcode())});
    }

    // Delete a product from the database
    public void deleteProduct(String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, KEY_PRODUCT_BARCODE + " = ?", new String[]{barcode});
    }

    // List all products in the database
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            int barcodeIndex = cursor.getColumnIndex(KEY_PRODUCT_BARCODE);
            int nameIndex = cursor.getColumnIndex(KEY_PRODUCT_NAME);
            int brandIndex = cursor.getColumnIndex(KEY_PRODUCT_BRAND);
            int quantityIndex = cursor.getColumnIndex(KEY_PRODUCT_QUANTITY);
            int locationIndex = cursor.getColumnIndex(KEY_PRODUCT_LOCATION);
            int tagsIndex = cursor.getColumnIndex(KEY_PRODUCT_TAGS);
            int imageIndex = cursor.getColumnIndex(KEY_PRODUCT_IMAGE);

            if (barcodeIndex != -1 && nameIndex != -1 && brandIndex != -1 &&
                    quantityIndex != -1 && locationIndex != -1 && tagsIndex != -1 && imageIndex != -1) {
                do {
                    String barcode = cursor.getString(barcodeIndex);
                    String name = cursor.getString(nameIndex);
                    String brand = cursor.getString(brandIndex);
                    int quantity = cursor.getInt(quantityIndex);
                    String location = cursor.getString(locationIndex);
                    String tags = cursor.getString(tagsIndex);
                    byte[] imageBytes = cursor.getBlob(imageIndex);
                    Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    Product product = new Product(barcode, name, brand, quantity, location, tags, image);
                    productList.add(product);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return productList;
    }

    // Method to get the quantity of a product by its name
    public int getProductQuantity(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int quantity = 0;

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS +
                " WHERE " + KEY_PRODUCT_NAME + "=?", new String[]{productName});

        if (cursor != null && cursor.moveToFirst()) {
            quantity = cursor.getInt(0); // The count is in the first column
            cursor.close();
        }

        return quantity;
    }

    // Method to get distinct product names from the database
    public List<String> getDistinctProductNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> productNames = new ArrayList<>();

        String query = "SELECT DISTINCT " + KEY_PRODUCT_NAME + " FROM " + TABLE_PRODUCTS;
        Cursor cursor = db.rawQuery(query, null);

        // Get the index of the column first
        int nameIndex = cursor.getColumnIndex(KEY_PRODUCT_NAME);

        // Check if the index is valid
        if (nameIndex != -1 && cursor.moveToFirst()) {
            do {
                productNames.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productNames;
    }



    // Method to get a product by its name
    public Product getProductByName(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Product product = null;

        String[] projection = {
                KEY_PRODUCT_BARCODE,
                KEY_PRODUCT_NAME,
                KEY_PRODUCT_BRAND,
                // ... other columns to be needed
                KEY_PRODUCT_IMAGE
        };

        Cursor cursor = db.query(
                TABLE_PRODUCTS,
                projection,
                KEY_PRODUCT_NAME + "=?",
                new String[]{productName},
                null,
                null,
                null,
                "1" // Limit 1 since we only want one product with this name
        );

        if (cursor.moveToFirst()) {
            int barcodeIndex = cursor.getColumnIndex(KEY_PRODUCT_BARCODE);
            int nameIndex = cursor.getColumnIndex(KEY_PRODUCT_NAME);
            int brandIndex = cursor.getColumnIndex(KEY_PRODUCT_BRAND);
            int imageIndex = cursor.getColumnIndex(KEY_PRODUCT_IMAGE);

            if (barcodeIndex != -1 && nameIndex != -1 && brandIndex != -1 && imageIndex != -1) {
                String barcode = cursor.getString(barcodeIndex);
                String name = cursor.getString(nameIndex);
                String brand = cursor.getString(brandIndex);
                byte[] imageBytes = cursor.getBlob(imageIndex);
                Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                // Assuming Product constructor takes these parameters, and quantity will be set later
                product = new Product(barcode, name, brand, 0, null, null, image);
            }
        }

        cursor.close();
        return product;
    }




    //--------------------- Utility methods-----------------------------------------------

    // utility method to hash password
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Utility method to convert bitmap to byte array for BLOB storage
    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }


}