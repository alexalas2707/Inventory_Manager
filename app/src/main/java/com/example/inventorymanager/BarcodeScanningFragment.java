package com.example.inventorymanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarcodeScanningFragment extends Fragment {

    public interface OnBarcodeScannedListener {
        void onBarcodeScanned(String barcode);
    }

    private OnBarcodeScannedListener listener;

    private TextView scanResultTextView;
    private Button buttonAddNewItem, buttonTakeOutItem;
    private DatabaseHelper databaseHelper;
    private String scannedBarcode;

    private ImageView scanImageView;

    public BarcodeScanningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barcode_scanning, container, false);

        scanResultTextView = view.findViewById(R.id.scan_result_text_view);
        buttonAddNewItem = view.findViewById(R.id.button_add_new_item);
        buttonTakeOutItem = view.findViewById(R.id.button_take_out_item);
        scanImageView = view.findViewById(R.id.scanImageView);

        databaseHelper = new DatabaseHelper(getContext());

        // Hide buttons initially
        buttonAddNewItem.setVisibility(View.GONE);
        buttonTakeOutItem.setVisibility(View.GONE);

        // Automatically start scanning
        BarcodeScannerHelper barcodeScannerHelper = new BarcodeScannerHelper(this);
        barcodeScannerHelper.startBarcodeScan();

        setupButtons();

        return view;
    }

    private void setupButtons() {
        buttonAddNewItem.setOnClickListener(v -> navigateToAddInventory());
        buttonTakeOutItem.setOnClickListener(v -> takeOutItemFromWarehouse());
    }

    private void navigateToAddInventory() {
        if (listener != null) {
            listener.onBarcodeScanned(scannedBarcode);
        }
    }

    private void takeOutItemFromWarehouse() {
        // Logic to remove item from warehouse
        // Update the database as needed
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            scannedBarcode = result.getContents();
            handleScanResult(scannedBarcode);
        }
    }

    private void handleScanResult(String barcode) {
        Product product = databaseHelper.getProductByBarcode(barcode);
        if (product != null) {
            scanResultTextView.setText("Item in warehouse: " + product.getName() + "\nLocation: " + product.getWarehouseLocation());
            buttonTakeOutItem.setVisibility(View.VISIBLE);
            buttonAddNewItem.setVisibility(View.GONE);

            // Set the product image if it exists
            if (product.getImage() != null) {
                scanImageView.setImageBitmap(product.getImage());
            } else {
                // You might want to set a default image or make the ImageView invisible
                scanImageView.setImageResource(R.drawable.logo); // Replace with your default image resource
            }

        } else {
            scanResultTextView.setText("Item not in warehouse.");
            buttonAddNewItem.setVisibility(View.VISIBLE);
            buttonTakeOutItem.setVisibility(View.GONE);
            scanImageView.setImageResource(R.drawable.logo); // Set default or hide
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBarcodeScannedListener) {
            listener = (OnBarcodeScannedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBarcodeScannedListener");
        }
    }


}
