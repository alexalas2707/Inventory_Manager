package com.example.inventorymanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.Manifest;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AddInventoryFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST = 100;
    private static final int IMAGE_PICK_REQUEST = 101;
    private static final int IMAGE_CAPTURE_REQUEST = 102;


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
        } else {
            showImagePickDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showImagePickDialog();
        } else {
            Toast.makeText(getActivity(), "Permissions are required to continue", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image Source")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            // Camera option
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, IMAGE_CAPTURE_REQUEST);
                        } else {
                            // Gallery option
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, IMAGE_PICK_REQUEST);
                        }
                    }
                }).show();
    }


    public AddInventoryFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_inventory, container, false);


        // Initialize BarcodeScannerHelper with the current fragment
        BarcodeScannerHelper barcodeScannerHelper = new BarcodeScannerHelper(this);

        EditText editTextBarcodeNumber = view.findViewById(R.id.editTextBarcodeNumber);

        // Set up the Add Image button
        Button addImageButton = view.findViewById(R.id.buttonAddImage);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });

        editTextBarcodeNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextBarcodeNumber.getRight() - editTextBarcodeNumber.getCompoundDrawables()[2].getBounds().width())) {
                        // Start scanning
                        barcodeScannerHelper.startBarcodeScan(); // corrected method name
                        return true;
                    }
                }
                return false;
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ImageView imageViewProduct = getView().findViewById(R.id.imageViewProduct);

            if (requestCode == IMAGE_PICK_REQUEST && data != null && data.getData() != null) {
                Uri selectedImage = data.getData();
                imageViewProduct.setImageURI(selectedImage);
            } else if (requestCode == IMAGE_CAPTURE_REQUEST && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageViewProduct.setImageBitmap(imageBitmap);
            }
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Put the scan result into the EditText
                EditText editTextBarcodeNumber = getView().findViewById(R.id.editTextBarcodeNumber);
                editTextBarcodeNumber.setText(result.getContents());
            }
        }
    }


}

