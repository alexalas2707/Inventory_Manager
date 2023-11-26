package com.example.inventorymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class DashboardActivity  extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        databaseHelper = new DatabaseHelper(this);
        ImageView menuIcon = findViewById(R.id.menuIcon);

        SharedPreferences prefs = getSharedPreferences("YourAppPrefs", MODE_PRIVATE);
        String loggedInUsername = prefs.getString("username", null);

        if (loggedInUsername != null) {
            User currentUser = databaseHelper.getUserByUsername(loggedInUsername);
            if (currentUser != null) {
                NavigationView navigationView = findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView headerUserName = headerView.findViewById(R.id.header_user_name);
                headerUserName.setText(currentUser.getName()); // Assuming you want to display just the first name
            }
        }

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
