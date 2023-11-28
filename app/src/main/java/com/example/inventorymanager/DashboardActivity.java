package com.example.inventorymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity implements BarcodeScanningFragment.OnBarcodeScannedListener {

    private DrawerLayout drawerLayout;
    private DatabaseHelper databaseHelper;
    private GridLayout dashboardGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        databaseHelper = new DatabaseHelper(this);
        dashboardGrid = findViewById(R.id.dashboardGrid);

        setupDashboardButtons();
        setupNavigationDrawer();
        loadUserDetails();
        setupMenuIcon();
    }

    private void setupDashboardButtons() {
        findViewById(R.id.frameLayoutScan).setOnClickListener(v -> showFragment(new BarcodeScanningFragment()));
        findViewById(R.id.frameLayoutAdd).setOnClickListener(v -> showFragment(new AddInventoryFragment()));
        findViewById(R.id.frameLayoutProductList).setOnClickListener(v -> showFragment(new InventoryListFragment()));
        findViewById(R.id.frameLayoutAlerts).setOnClickListener(v -> showFragment(new AlertsFragment()));
        findViewById(R.id.frameLayoutProductCategories).setOnClickListener(v -> showFragment(new ProductCategoriesFragment()));
        findViewById(R.id.frameLayoutExport).setOnClickListener(v -> showFragment(new ExportInventoryFragment()));
        findViewById(R.id.frameLayoutSettings).setOnClickListener(v -> showFragment(new SettingsFragment()));
        findViewById(R.id.frameLayoutHelp).setOnClickListener(v -> showFragment(new HelpFragment()));
    }

    private void setupNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                showDashboard();
            } else if (itemId == R.id.nav_inventory_list) {
                showFragment(new InventoryListFragment());
            } else if (itemId == R.id.nav_add_inventory) {
                showFragment(new AddInventoryFragment());
            } else if (itemId == R.id.nav_product_categories) {
                showFragment(new ProductCategoriesFragment());
            } else if (itemId == R.id.nav_barcode_scanning) {
                showFragment(new BarcodeScanningFragment());
            } else if (itemId == R.id.nav_export_inventory) {
                showFragment(new ExportInventoryFragment());
            } else if (itemId == R.id.nav_settings) {
                showFragment(new SettingsFragment());
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    private void loadUserDetails() {
        SharedPreferences prefs = getSharedPreferences("YourAppPrefs", MODE_PRIVATE);
        String loggedInUsername = prefs.getString("username", null);

        if (loggedInUsername != null) {
            User currentUser = databaseHelper.getUserByUsername(loggedInUsername);
            if (currentUser != null) {
                NavigationView navigationView = findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView headerUserName = headerView.findViewById(R.id.header_user_name);
                headerUserName.setText(currentUser.getName());
            }
        }
    }

    private void setupMenuIcon() {
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void showFragment(Fragment fragment) {
        dashboardGrid.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showDashboard() {
        dashboardGrid.setVisibility(View.VISIBLE);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(currentFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (dashboardGrid.getVisibility() == View.GONE) {
                showDashboard();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onBarcodeScanned(String barcode) {
        //replace the current fragment with AddInventoryFragment
        AddInventoryFragment addInventoryFragment = new AddInventoryFragment();

        // Pass barcode data to AddInventoryFragment
        Bundle args = new Bundle();
        args.putString("barcode", barcode);
        addInventoryFragment.setArguments(args);

        // Replace the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addInventoryFragment)
                .addToBackStack(null)
                .commit();
    }

}
