package com.example.inventorymanager;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ExportInventoryFragment extends Fragment{

    public interface OnExportActionListener {
        void onBackToDashboard();
    }

    private OnExportActionListener mListener;

    private DatabaseHelper databaseHelper;

    public ExportInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_export_inventory, container, false);

        databaseHelper = new DatabaseHelper(getContext());
        Button exportButton = view.findViewById(R.id.exportButton);

        exportButton.setOnClickListener(v -> exportDatabaseToCSV());

        return view;

    }

    private void exportDatabaseToCSV() {
        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ProductsExport.csv";
        DataExportUtil.exportDataToCSV(fileName, databaseHelper);

        // Optionally, show a message to the user
        Toast.makeText(getContext(), "Exported to " + fileName, Toast.LENGTH_SHORT).show();

        if (mListener != null) {
            mListener.onBackToDashboard();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnExportActionListener) {
            mListener = (OnExportActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExportActionListener");
        }
    }

}
