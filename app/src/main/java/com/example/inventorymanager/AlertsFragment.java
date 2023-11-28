package com.example.inventorymanager;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class AlertsFragment extends Fragment{

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<Product> lowStockProducts;
    private DatabaseHelper databaseHelper;

    public AlertsFragment (){
        //Constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_runningLow);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseHelper = new DatabaseHelper(getContext());

        lowStockProducts = new ArrayList<>();
        adapter = new InventoryAdapter(lowStockProducts);
        recyclerView.setAdapter(adapter);

        fetchLowStockProducts();
        return view;
    }

    private void fetchLowStockProducts() {
        SharedPreferences prefs = getActivity().getSharedPreferences("AppSettingsPrefs", Context.MODE_PRIVATE);
        int threshold = prefs.getInt("ThresholdValue", 5); // Default value is 5

        new Thread(() -> {
            List<String> productNames = databaseHelper.getDistinctProductNames();
            List<Product> tempLowStockProducts = new ArrayList<>();

            for (String name : productNames) {
                Product product = databaseHelper.getProductByName(name);
                if (product != null) {
                    int quantity = databaseHelper.getProductQuantity(name);
                    if (quantity <= threshold) {
                        product.setQuantityInStock(quantity);
                        tempLowStockProducts.add(product);
                    }
                }
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    lowStockProducts.clear();
                    lowStockProducts.addAll(tempLowStockProducts);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }


}
