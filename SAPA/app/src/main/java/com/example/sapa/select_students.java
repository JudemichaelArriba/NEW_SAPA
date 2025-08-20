package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.StudentAdapter;
import com.example.sapa.databinding.ActivitySelectStudentsBinding;
import com.example.sapa.models.Students;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class select_students extends AppCompatActivity {

    private ActivitySelectStudentsBinding binding;
    private ApiInterface apiInterface;
    private StudentAdapter adapter;
    private List<Students> studentsList = new ArrayList<>();
    private boolean isFetching = false;

    private String schoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySelectStudentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String slotName = getIntent().getStringExtra("slot_name");
        Log.d("SelectStudents", "slotName: " + slotName);

        String startTime = getIntent().getStringExtra("start_time");
        Log.d("SelectStudents", "startTime: " + startTime);

        String endTime = getIntent().getStringExtra("end_time");
        Log.d("SelectStudents", "endTime: " + endTime);

        int maxCapacity = getIntent().getIntExtra("max_capacity", 0);
        Log.d("SelectStudents", "maxCapacity: " + maxCapacity);

        String hospitalName = getIntent().getStringExtra("hospital_name");
        Log.d("SelectStudents", "hospitalName: " + hospitalName);

        String sectionName = getIntent().getStringExtra("section_name");
        Log.d("SelectStudents", "sectionName: " + sectionName);

        int sectionId = getIntent().getIntExtra("section_id", 0);
        Log.d("SelectStudents", "sectionId: " + sectionId);

        String hospitalId = getIntent().getStringExtra("hospital_id");
        Log.d("SelectStudents", "hospitalId: " + hospitalId);

        schoolId = getIntent().getStringExtra("school_id");
        Log.d("SelectStudents", "schoolId: " + schoolId);

        int slotId = getIntent().getIntExtra("slot_id", 0);
        Log.d("SelectStudents", "slotId: " + slotId);

        double payments = getIntent().getDoubleExtra("billing", 0.0);
        Log.d("SelectStudents", "payments: " + payments);


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentsList, this);
        binding.recyclerView.setAdapter(adapter);


        binding.actionButton.setVisibility(View.GONE);
        adapter.setOnSelectionChangeListener(selectedCount ->
                binding.actionButton.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE)
        );


        apiInterface = ApiClient.getClient(this).create(ApiInterface.class);

        binding.backButton.setOnClickListener(v -> finish());

        binding.actionButton.setOnClickListener(v -> {
            List<Students> selectedStudents = adapter.getSelectedStudents();
            if (selectedStudents == null) selectedStudents = new ArrayList<>();

            if (selectedStudents.size() > maxCapacity) {
                Log.d("selectedStudents", "maximum: " + maxCapacity);
                Toast.makeText(this, "Selected students exceed maximum capacity", Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<String> selectedStudentIds = new ArrayList<>();
            for (Students s : selectedStudents) {
                selectedStudentIds.add(s.getId());
            }

            Intent intent = new Intent(this, make_appointment.class);

            intent.putExtra("slot_name", slotName);
            intent.putExtra("start_time", startTime);
            intent.putExtra("end_time", endTime);
            intent.putExtra("max_capacity", maxCapacity);
            intent.putExtra("hospital_name", hospitalName);
            intent.putExtra("section_name", sectionName);
            intent.putExtra("section_id", sectionId);
            intent.putExtra("hospital_id", hospitalId);
            intent.putExtra("school_id", schoolId);
            intent.putExtra("slot_id", slotId);
            intent.putExtra("billing", payments);
            intent.putStringArrayListExtra("selected_student_ids", selectedStudentIds);

            startActivity(intent);

        });


        fetchStudentsBySchool();
    }

    private void fetchStudentsBySchool() {
        if (isFetching) return;
        isFetching = true;

        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", null);

        if (coordinatorId == null || coordinatorId.isEmpty()) {
            Toast.makeText(this, "Coordinator ID is missing!", Toast.LENGTH_SHORT).show();
            isFetching = false;
            return;
        }

        Call<List<Students>> call = apiInterface.getStudentsByCoordinator(coordinatorId);
        call.enqueue(new Callback<List<Students>>() {
            @Override
            public void onResponse(Call<List<Students>> call, Response<List<Students>> response) {
                isFetching = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<Students> filtered = new ArrayList<>();
                    for (Students s : response.body()) {
                        Log.d("SelectStudents", "Student schoolId: " + s.getSchoolId() + " | Expected: " + schoolId);
                        if (schoolId != null && schoolId.equals(s.getSchoolId())) {
                            filtered.add(s);
                        }
                    }

                    if (filtered.isEmpty()) {
                        Toast.makeText(select_students.this, "No students found for this school", Toast.LENGTH_SHORT).show();
                    }

                    adapter.updateData(filtered);

                } else {
                    Toast.makeText(select_students.this, "Failed to fetch students", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Students>> call, Throwable t) {
                isFetching = false;
                Toast.makeText(select_students.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
