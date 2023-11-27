package com.example.inventorymanager;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;


public class BarcodeScannerHelper {

    private Fragment fragment;

    // Constructor that accepts a Fragment
    public BarcodeScannerHelper(Fragment fragment) {
        this.fragment = fragment;
    }

    // Method to start the barcode scan
    public void startBarcodeScan() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(fragment);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }
}