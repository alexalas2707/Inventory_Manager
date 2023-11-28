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
import android.widget.SeekBar;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    private EditText thresholdNumberEditText;
    private SeekBar seekBarThreshold;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        thresholdNumberEditText = view.findViewById(R.id.thresholdNumber);
        seekBarThreshold = view.findViewById(R.id.seekBar);
        Button saveButton = view.findViewById(R.id.saveThresholdButton);

        // Initialize SeekBar and EditText with current threshold value
        int currentThreshold = getCurrentThreshold();
        seekBarThreshold.setMax(50); // Set max value for SeekBar
        seekBarThreshold.setProgress(currentThreshold);
        thresholdNumberEditText.setText(String.valueOf(currentThreshold));

        // Update EditText when SeekBar value changes
        seekBarThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                thresholdNumberEditText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional method
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional method
            }
        });

        saveButton.setOnClickListener(v -> saveThresholdValue());
        return view;
    }

    private int getCurrentThreshold() {
        SharedPreferences prefs = getActivity().getSharedPreferences("AppSettingsPrefs", Context.MODE_PRIVATE);
        return prefs.getInt("ThresholdValue", 1); // Default to 1 if not set
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
