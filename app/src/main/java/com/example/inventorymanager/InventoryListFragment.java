package com.example.inventorymanager;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class InventoryListFragment extends Fragment{

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<Product> productList;
    private DatabaseHelper databaseHelper;

    public InventoryListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_inventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseHelper = new DatabaseHelper(getContext());

        // Initialize productList
        productList = new ArrayList<>();
        adapter = new InventoryAdapter(productList);
        recyclerView.setAdapter(adapter);

        // Fetch the product names and quantities from the database
        fetchInventoryData();

        return view;
    }

    private void fetchInventoryData() {
        new Thread(() -> {
            // Fetch distinct product names
            List<String> productNames = databaseHelper.getDistinctProductNames();
            List<Product> tempProductList = new ArrayList<>();

            // For each product name, fetch a product and its quantity
            for (String name : productNames) {
                Product product = databaseHelper.getProductByName(name);
                if (product != null) {
                    int quantity = databaseHelper.getProductQuantity(name);
                    product.setQuantityInStock(quantity);
                    tempProductList.add(product);
                }
            }

            // Make sure to run UI updates on the main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    productList.clear();
                    productList.addAll(tempProductList);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }


}