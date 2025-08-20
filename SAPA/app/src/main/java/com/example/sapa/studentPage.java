package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.StudentAdapter;
import com.example.sapa.databinding.ActivityStudentPageBinding;
import com.example.sapa.models.Students;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class studentPage extends AppCompatActivity {

    private ActivityStudentPageBinding binding;
    private ApiInterface apiInterface;
    private StudentAdapter adapter;
    private List<Students> studentsList = new ArrayList<>();
    private boolean isFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentsList, this);
        recyclerView.setAdapter(adapter);

        apiInterface = ApiClient.getClient(this).create(ApiInterface.class);

        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", null);

        Log.d("StudentPage", "Coordinator ID: " + coordinatorId);

        if (coordinatorId == null || coordinatorId.isEmpty()) {
            Toast.makeText(studentPage.this, "Coordinator ID is missing!", Toast.LENGTH_SHORT).show();
        }

        binding.backButton.setOnClickListener(v -> finish());
        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(studentPage.this, select_school.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchStudents() {
        if (isFetching) return;
        isFetching = true;

        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", null);

        if (coordinatorId == null || coordinatorId.isEmpty()) {
            Toast.makeText(studentPage.this, "Coordinator ID is missing!", Toast.LENGTH_SHORT).show();
            isFetching = false;
            return;
        }

        Call<List<Students>> call = apiInterface.getStudentsByCoordinator(coordinatorId);
        call.enqueue(new Callback<List<Students>>() {

            @Override
            public void onResponse(Call<List<Students>> call, Response<List<Students>> response) {
                isFetching = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<Students> responseList = response.body();
                    if (!responseList.isEmpty()) {
                        studentsList.clear();
                        studentsList.addAll(responseList);
                        adapter.notifyDataSetChanged();


                        for (Students s : responseList) {
                            Log.d("fetchStudent", "Student ID: " + s.getId() + ", Name: " + s.getStudentFullname());
                        }

                    } else {
                        Log.d("fetchStudent", "Empty list or null body");
                        Toast.makeText(studentPage.this, "No students found for this coordinator", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("fetchStudent", "Error Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("fetchStudent", "Error reading errorBody", e);
                    }
                    Toast.makeText(studentPage.this, "Server returned error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Students>> call, Throwable t) {
                isFetching = false;
                Log.e("fetchStudent", "API Failure: " + t.getMessage(), t);
                Toast.makeText(studentPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchStudents();
    }
}
