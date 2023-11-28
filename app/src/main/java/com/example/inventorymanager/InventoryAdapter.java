package com.example.inventorymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<Product> productList;
    private DatabaseHelper databaseHelper;

    public InventoryAdapter(List<Product> productList) {
        this.productList = productList;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewProduct;
        private TextView textViewProductName;
        private TextView textViewBrand;
        private TextView textViewQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.image_product);
            textViewProductName = itemView.findViewById(R.id.text_product_name);
            textViewBrand = itemView.findViewById(R.id.text_product_brand);
            textViewQuantity = itemView.findViewById(R.id.text_product_quantity);
        }

        public void bind(Product product) {
            // Set the product image, name, brand, and quantity
            imageViewProduct.setImageBitmap(product.getImage()); // If you have an ImageView in the layout
            textViewProductName.setText(product.getName());
            textViewBrand.setText("Brand: "+product.getBrand());
            textViewQuantity.setText("Quantity: "+String.valueOf(product.getQuantityInStock()));
        }
    }
}

