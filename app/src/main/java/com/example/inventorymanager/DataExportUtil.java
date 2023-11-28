package com.example.inventorymanager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataExportUtil {

    public static void exportDataToCSV(String fileName, DatabaseHelper databaseHelper) {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(fileName);

            // Writing the header
            fileWriter.append("Barcode,Name,Brand,Quantity,Location,Tags\n");

            // Fetching data from database
            List<Product> productList = databaseHelper.getAllProducts();
            for (Product product : productList) {
                fileWriter.append(product.getBarcode()).append(",")
                        .append(product.getName()).append(",")
                        .append(product.getBrand()).append(",")
                        .append(String.valueOf(product.getQuantityInStock())).append(",")
                        .append(product.getWarehouseLocation()).append(",")
                        .append(product.getTags()).append("\n");
            }

            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
