package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.HospitalAdapter;
import com.example.sapa.databinding.ActivitySelectHospitalBinding;
import com.example.sapa.models.Hospitals;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class select_hospital extends AppCompatActivity {

    private HospitalAdapter hospitalAdapter;
    private List<Hospitals> hospitalList = new ArrayList<>();
    private ActivitySelectHospitalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySelectHospitalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(v -> finish());

        String schoolId = getIntent().getStringExtra("school_id");
        Log.d("SelectHospital", "Received school_id: " + schoolId);

        hospitalAdapter = new HospitalAdapter(this, hospitalList, hospital -> {
            String hospitalId = hospital.getHospital_id();
            Log.d("SelectHospital", "Clicked hospital_id: " + hospitalId);


            Intent intent = new Intent(select_hospital.this, select_sections.class);
            intent.putExtra("school_id", schoolId);
            intent.putExtra("hospital_id", hospitalId);
            startActivity(intent);

            Toast.makeText(this, "School: " + schoolId + " | Hospital: " + hospitalId, Toast.LENGTH_SHORT).show();
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(hospitalAdapter);
        fetchHospitals();
    }

    private void fetchHospitals() {
        Log.d("FetchHospitals", "Starting fetchHospitals()...");

        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        Call<List<Hospitals>> call = apiInterface.getHospitals();

        call.enqueue(new Callback<List<Hospitals>>() {
            @Override
            public void onResponse(Call<List<Hospitals>> call, Response<List<Hospitals>> response) {
                Log.d("FetchHospitals", "onResponse called");

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("FetchHospitals", "Success: received " + response.body().size() + " hospitals");
                    hospitalList.clear();
                    hospitalList.addAll(response.body());
                    hospitalAdapter.notifyDataSetChanged();
                } else {
                    Log.e("FetchHospitals", "Response failed. Code: " + response.code());
                    Toast.makeText(select_hospital.this, "Failed to load hospitals", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Hospitals>> call, Throwable t) {
                Log.e("FetchHospitals", "onFailure: " + t.getMessage(), t);
                Toast.makeText(select_hospital.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
