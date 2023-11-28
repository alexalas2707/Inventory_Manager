package com.example.inventorymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsFragment extends Fragment{

    private EditText thresholdNumberEditText;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        thresholdNumberEditText = view.findViewById(R.id.thresholdNumber);
        Button saveButton = view.findViewById(R.id.saveThresholdButton);

        saveButton.setOnClickListener(v -> saveThresholdValue());
        return view;
    }

    private void saveThresholdValue() {
        int threshold = Integer.parseInt(thresholdNumberEditText.getText().toString());
        SharedPreferences prefs = getActivity().getSharedPreferences("AppSettingsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("ThresholdValue", threshold);
        editor.apply();
        Toast.makeText(getActivity(), "Threshold saved successfully", Toast.LENGTH_SHORT).show();
    }

}
