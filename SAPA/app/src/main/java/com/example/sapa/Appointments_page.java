package com.example.sapa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.UpcomingAppointmentsAdapter;
import com.example.sapa.databinding.FragmentAppointmentsPageBinding;
import com.example.sapa.models.UpcomingAppointment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Appointments_page extends Fragment {

    private FragmentAppointmentsPageBinding binding;
    private List<UpcomingAppointment> appointmentList;
    private UpcomingAppointmentsAdapter adapter;

    public Appointments_page() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppointmentsPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appointmentList = new ArrayList<>();
        adapter = new UpcomingAppointmentsAdapter(requireContext(), appointmentList, appointment -> {

        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        fetchAppointments();

        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), select_school_for_appointment.class);
            startActivity(intent);
        });
    }

    private void fetchAppointments() {
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("coordinator_id", "");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            Log.e("Appointments_page", "User ID is null or empty");
            return;
        }
        Log.d("Appointments_page", "User ID: " + userId);

        ApiInterface api = ApiClient.getClient(requireContext()).create(ApiInterface.class);

        api.getUpcomingAppointments(userId).enqueue(new Callback<List<UpcomingAppointment>>() {
            @Override
            public void onResponse(Call<List<UpcomingAppointment>> call, Response<List<UpcomingAppointment>> response) {
                Log.d("Appointments_page", "HTTP Response code: " + response.code());
                Log.d("Appointments_page", "Response raw: " + response.raw().toString());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        appointmentList.clear();
                        appointmentList.addAll(removeDuplicateAppointments(response.body())); // ✅ remove duplicates by slotId
                        adapter.notifyDataSetChanged();
                        Log.d("Appointments_page", "Fetched appointments: " + appointmentList.toString());
                        Toast.makeText(requireContext(), "Fetched " + appointmentList.size() + " unique appointments", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("Appointments_page", "Response body is null");
                        Toast.makeText(requireContext(), "No appointments found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("Appointments_page", "Response not successful. Code: " + response.code() + ", Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("Appointments_page", "Error reading errorBody", e);
                    }
                    Toast.makeText(requireContext(), "Failed to fetch appointments: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UpcomingAppointment>> call, Throwable t) {
                Log.e("Appointments_page", "API call failed: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private List<UpcomingAppointment> removeDuplicateAppointments(List<UpcomingAppointment> appointments) {
        List<UpcomingAppointment> uniqueList = new ArrayList<>();
        HashSet<Integer> seenSlotIds = new HashSet<>();

        for (UpcomingAppointment appt : appointments) {
            if (seenSlotIds.add(appt.getSlotId())) {
                uniqueList.add(appt);
            }
        }
        return uniqueList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
