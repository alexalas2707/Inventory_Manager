package com.example.inventorymanager;

import android.graphics.Bitmap;

public class Product {
        private String barcode;
        private String name;
        private String brand;
        private int quantityInStock;
        private String warehouseLocation;
        private String tags;
        private Bitmap image;

        public Product(String barcode, String name, String brand, int quantityInStock, String warehouseLocation, String tags, Bitmap image) {
            this.barcode = barcode;
            this.name = name;
            this.brand = brand;
            this.quantityInStock = quantityInStock;
            this.warehouseLocation = warehouseLocation;
            this.tags = tags;
            this.image = image;
        }

        // Getters and Setters

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public int getQuantityInStock() {
            return quantityInStock;
        }

        public void setQuantityInStock(int quantityInStock) {
            this.quantityInStock = quantityInStock;
        }

        public String getWarehouseLocation() {
            return warehouseLocation;
        }

        public void setWarehouseLocation(String warehouseLocation) {
            this.warehouseLocation = warehouseLocation;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }
    }

