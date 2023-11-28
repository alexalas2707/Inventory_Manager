package com.example.inventorymanager;

import android.app.Activity;
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

    private ImageView imageViewProduct;
    private EditText editTextProductName, editTextBarcodeNumber, editTextProductBrand, editTextLocation, editTextTags;
    private BarcodeScannerHelper barcodeScannerHelper;

    public AddInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_inventory, container, false);
        initializeViews(view);
        setListeners(view);
        return view;
    }


    private void initializeViews(View view) {
        barcodeScannerHelper = new BarcodeScannerHelper(this);
        imageViewProduct = view.findViewById(R.id.imageViewProduct);
        editTextProductName = view.findViewById(R.id.editTextProductName);
        editTextBarcodeNumber = view.findViewById(R.id.editTextBarcodeNumber);
        editTextProductBrand = view.findViewById(R.id.editTextProductBrand);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        editTextTags = view.findViewById(R.id.editTextTags);
    }

    private void setListeners(View view) {
        Button buttonSaveProduct = view.findViewById(R.id.buttonSaveProduct);
        buttonSaveProduct.setOnClickListener(v -> saveProduct());

        Button addImageButton = view.findViewById(R.id.buttonAddImage);
        addImageButton.setOnClickListener(v -> checkPermissions());

        editTextBarcodeNumber.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP &&
                    event.getRawX() >= (editTextBarcodeNumber.getRight() - editTextBarcodeNumber.getCompoundDrawables()[2].getBounds().width())) {
                barcodeScannerHelper.startBarcodeScan();
                return true;
            }
            return false;
        });
    }


    private void saveProduct() {
        String name = editTextProductName.getText().toString().trim();
        String barcode = editTextBarcodeNumber.getText().toString().trim();
        String brand = editTextProductBrand.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String tags = editTextTags.getText().toString().trim();
        Bitmap image = getBitmapFromImageView(imageViewProduct);

        if (!name.isEmpty() && !barcode.isEmpty()) {
            DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
            Product existingProduct = databaseHelper.getProductByBarcode(barcode);

            if (existingProduct != null) {
                // Product exists, increment quantity
                int newQuantity = existingProduct.getQuantityInStock() + 1; // Increment by 1 or desired amount
                existingProduct.setQuantityInStock(newQuantity);
                databaseHelper.updateProduct(existingProduct);
                Toast.makeText(getContext(), "Product quantity updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Product does not exist, add new product with initial quantity
                Product newProduct = new Product(barcode, name, brand, 1, location, tags, image); // Start with a quantity of 1
                databaseHelper.addProduct(newProduct);
                Toast.makeText(getContext(), "New product added successfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Product name and barcode are required", Toast.LENGTH_SHORT).show();
        }
    }


    private Bitmap getBitmapFromImageView(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        return imageView.getDrawingCache();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
        } else {
            showImagePickDialog();
        }
    }

    private void showImagePickDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Choose Image Source")
                .setItems(new String[]{"Camera", "Gallery"}, (dialogInterface, i) -> {
                    Intent intent = i == 0 ? new Intent(MediaStore.ACTION_IMAGE_CAPTURE) :
                            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, i == 0 ? IMAGE_CAPTURE_REQUEST : IMAGE_PICK_REQUEST);
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showImagePickDialog();
        } else {
            Toast.makeText(getActivity(), "Permissions are required to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == IMAGE_PICK_REQUEST && data.getData() != null) {
                imageViewProduct.setImageURI(data.getData());
            } else if (requestCode == IMAGE_CAPTURE_REQUEST) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageViewProduct.setImageBitmap(imageBitmap);
            }
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            editTextBarcodeNumber.setText(result.getContents());
        }
    }
}



