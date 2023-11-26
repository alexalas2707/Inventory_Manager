package com.example.inventorymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class DashboardActivity  extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private DatabaseHelper databaseHelper;


    //Handling Fragments Transactions

    private void setupDashboardButtons() {

        //----------------BarcodeScan---------------------------
        FrameLayout barcodeScanButton = findViewById(R.id.frameLayoutScan);
        barcodeScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new BarcodeScanningFragment())
                        .commit();
            }
        });

        //---------------AddInventory--------------------------
        FrameLayout addInventoryButton = findViewById(R.id.frameLayoutAdd);
        addInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddInventoryFragment())
                        .commit();
            }
        });

        //----------------InventoryList-----------------------------
        FrameLayout inventoryListButton = findViewById(R.id.frameLayoutProductList);
        inventoryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new InventoryListFragment())
                        .commit();
            }
        });

        //----------------Alerts-------------------------------------
        FrameLayout alertsButton = findViewById(R.id.frameLayoutAlerts);
        alertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AlertsFragment())
                        .commit();
            }
        });

        //---------------------ProductCategories-------------------------
        FrameLayout productCategoriesButton = findViewById(R.id.frameLayoutProductCategories);
        productCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProductCategoriesFragment())
                        .commit();
            }
        });

        //----------------------Export------------------------------------
        FrameLayout exportButton = findViewById(R.id.frameLayoutExport);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ExportInventoryFragment())
                        .commit();
            }
        });

        //--------------------------Settings------------------------------
        FrameLayout settingsButton = findViewById(R.id.frameLayoutSettings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .commit();
            }
        });

        //-----------------------------Help----------------------------------
        FrameLayout helpButton = findViewById(R.id.frameLayoutHelp);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HelpFragment())
                        .commit();
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        databaseHelper = new DatabaseHelper(this);
        ImageView menuIcon = findViewById(R.id.menuIcon);

        // Setup the initial default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new InventoryListFragment())
                .commit();

        setupDashboardButtons();


        //------------Getting user name from SharedPreferences---------------------------
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
