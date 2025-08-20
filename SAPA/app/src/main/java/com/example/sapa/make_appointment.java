package com.example.sapa;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sapa.databinding.ActivityMakeAppointmentBinding;
import com.example.sapa.models.Students;

import java.util.ArrayList;

public class make_appointment extends AppCompatActivity {

    private ActivityMakeAppointmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMakeAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.makeAppointment, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        String slotName = getIntent().getStringExtra("slot_name");
        String startTime = getIntent().getStringExtra("start_time");
        String endTime = getIntent().getStringExtra("end_time");
        int maxCapacity = getIntent().getIntExtra("max_capacity", 0);
        String hospitalName = getIntent().getStringExtra("hospital_name");
        String sectionName = getIntent().getStringExtra("section_name");
        int sectionId = getIntent().getIntExtra("section_id", 0);
        String hospitalId = getIntent().getStringExtra("hospital_id");
        String schoolId = getIntent().getStringExtra("school_id");
        int slotId = getIntent().getIntExtra("slot_id", 0);
        double billing = getIntent().getDoubleExtra("billing", 0.0);
        ArrayList<Students> selectedStudents = (ArrayList<Students>) getIntent().getSerializableExtra("selected_students");


        Log.d("MakeAppointment", "slotName: " + slotName);
        Log.d("MakeAppointment", "startTime: " + startTime);
        Log.d("MakeAppointment", "endTime: " + endTime);
        Log.d("MakeAppointment", "maxCapacity: " + maxCapacity);
        Log.d("MakeAppointment", "hospitalName: " + hospitalName);
        Log.d("MakeAppointment", "sectionName: " + sectionName);
        Log.d("MakeAppointment", "sectionId: " + sectionId);
        Log.d("MakeAppointment", "hospitalId: " + hospitalId);
        Log.d("MakeAppointment", "schoolId: " + schoolId);
        Log.d("MakeAppointment", "slotId: " + slotId);
        Log.d("MakeAppointment", "billing: " + billing);
        Log.d("MakeAppointment", "selectedStudents size: " + (selectedStudents != null ? selectedStudents.size() : 0));
        Log.d("MakeAppointment", "student id: " + (selectedStudents != null ? selectedStudents.size() : 0));

        binding.slotNameValue.setText(slotName != null ? slotName : "N/A");
        binding.startTimeValue.setText(startTime != null ? startTime : "N/A");
        binding.endTimeValue.setText(endTime != null ? endTime : "N/A");
        binding.maxCapacityValue.setText(String.valueOf(maxCapacity));
        binding.hospitalNameValue.setText(hospitalName != null ? hospitalName : "N/A");
        binding.sectionNameValue.setText(sectionName != null ? sectionName : "N/A");
        binding.paymentValue.setText(String.valueOf(billing));


        binding.actionButton.setOnClickListener(v -> {
            if (selectedStudents != null && selectedStudents.size() > maxCapacity) {
                Toast.makeText(this, "Selected students exceed maximum capacity", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Appointment confirmed for " +
                    (selectedStudents != null ? selectedStudents.size() : 0) + " students", Toast.LENGTH_SHORT).show();
        });


        binding.backButton.setOnClickListener(v -> finish());
    }
}
