package com.example.inventorymanager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;


public class SignUpActivity extends Activity {
    // Declare your input fields
    private EditText nameEditText;
    private EditText lastNameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpButton;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        databaseHelper = new DatabaseHelper(this);

        // Initializing Views
        nameEditText = findViewById(R.id.editTextName);
        lastNameEditText = findViewById(R.id.editTextLastname);
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextPasswordConfirm);
        signUpButton = findViewById(R.id.buttonSignUp);

        // Set up the button click listener
        signUpButton.setOnClickListener(view -> {
            String name = nameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (!password.equals(confirmPassword)) {
                //show a toast message if passwords do not match
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                    User newUser = new User(name, lastName, username, ""); // Set password to empty initially
                    newUser.setPassword(password); // This will hash the password

                    databaseHelper.addUser(newUser);

                    Intent dashboardIntent = new Intent(SignUpActivity.this, DashboardActivity.class);
                    startActivity(dashboardIntent);
                    finish();
            }
        });

    }
}
