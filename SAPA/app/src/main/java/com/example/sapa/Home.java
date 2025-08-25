package com.example.sapa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.UpcomingAppointmentsAdapter;
import com.example.sapa.databinding.FragmentHomeBinding;
import com.example.sapa.models.Hospitals;
import com.example.sapa.models.UpcomingAppointment;
import com.example.sapa.models.UserData;
import com.example.sapa.models.UserProfileResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment {

    private FragmentHomeBinding binding;


    private UpcomingAppointmentsAdapter appointmentAdapter;
    private List<UpcomingAppointment> appointmentList = new ArrayList<>();

    public Home() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupAppointmentsRecycler();

        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("user_session", requireActivity().MODE_PRIVATE);
        String userId = sharedPreferences.getString("coordinator_id", "");

        if (!userId.isEmpty()) {
            fetchUserProfile(userId);
            fetchUpcomingAppointments(userId);
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return binding.getRoot();
    }

    private void setupAppointmentsRecycler() {
        appointmentAdapter = new UpcomingAppointmentsAdapter(requireContext(), appointmentList, appt -> {

        });
        binding.upcomingAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.upcomingAppointmentsRecyclerView.setAdapter(appointmentAdapter);
    }

    private void fetchUserProfile(String userId) {
        ApiInterface apiInterface = ApiClient.getClient(requireContext()).create(ApiInterface.class);
        Call<UserProfileResponse> call = apiInterface.getUserProfile(userId);

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();
                    if (profile.getStatus().equals("success")) {
                        setTopCard(profile.getData());
                    } else {
                        Toast.makeText(getActivity(), profile.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("homeProfile", "Connection failed:  " + userId + profile.getMessage());
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTopCard(UserData data) {
        binding.userName.setText(data.getUsername());
        if (data.getStatus().equalsIgnoreCase("Pending")){
            binding.requestStatus.setText("Waiting for Approval");
        } else {
            binding.requestStatus.setText("Account Approved");
        }

        requireActivity().getSharedPreferences("user_session", requireActivity().MODE_PRIVATE)
                .edit()
                .putString("request_status", data.getStatus())
                .apply();

        if (data.getProfileImage() != null && !data.getProfileImage().isEmpty()) {
            Glide.with(this)
                    .load(data.getProfileImage())
                    .circleCrop()
                    .placeholder(R.drawable.circle_background)
                    .error(R.drawable.circle_background)
                    .into(binding.profileImage);
        }
    }

    private void fetchUpcomingAppointments(String userId) {
        ApiInterface api = ApiClient.getClient(requireContext()).create(ApiInterface.class);

        api.getUpcomingAppointments(userId).enqueue(new Callback<List<UpcomingAppointment>>() {
            @Override
            public void onResponse(Call<List<UpcomingAppointment>> call, Response<List<UpcomingAppointment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UpcomingAppointment> allAppointments = response.body();

                    List<UpcomingAppointment> approvedAppointments = new ArrayList<>();
                    for (UpcomingAppointment appt : allAppointments) {
                        if ("Approved".equalsIgnoreCase(appt.getAppointmentStatus())) {
                            approvedAppointments.add(appt);
                        }
                    }


                    Collections.sort(approvedAppointments, Comparator.comparing(UpcomingAppointment::getStartTime));


                    List<UpcomingAppointment> top3Appointments = approvedAppointments.size() > 3
                            ? approvedAppointments.subList(0, 3)
                            : approvedAppointments;

                    appointmentList.clear();
                    appointmentList.addAll(top3Appointments);
                    appointmentAdapter.notifyDataSetChanged();

                } else {
                    Log.e("HomeAppointments", "Failed to fetch appointments: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<UpcomingAppointment>> call, Throwable t) {
                Log.e("HomeAppointments", "API call failed: " + t.getMessage());
            }
        });
    }
}
