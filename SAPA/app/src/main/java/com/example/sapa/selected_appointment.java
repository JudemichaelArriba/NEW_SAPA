package com.example.sapa;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.sapa.databinding.ActivitySelectedAppointmentBinding;

public class selected_appointment extends AppCompatActivity {

    private ActivitySelectedAppointmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelectedAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String slotName = getIntent().getStringExtra("slotName");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        String hospitalName = getIntent().getStringExtra("hospitalName");
        String sectionName = getIntent().getStringExtra("sectionName");
        String status = getIntent().getStringExtra("status");
        int totalStudents = getIntent().getIntExtra("totalStudents", 0);

        binding.slotNameValue.setText(slotName != null ? slotName : "N/A");
        binding.startTimeValue.setText(startTime != null ? startTime : "N/A");
        binding.endTimeValue.setText(endTime != null ? endTime : "N/A");
        binding.hospitalNameValue.setText(hospitalName != null ? hospitalName : "N/A");
        binding.sectionNameValue.setText(sectionName != null ? sectionName : "N/A");
        binding.statusValue.setText(status != null ? status : "N/A");
        binding.maxCapacityValue.setText(String.valueOf(totalStudents));


        binding.cancelAppointmentBtn.setOnClickListener(v -> {

        });
    }
}
