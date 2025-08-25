package com.example.sapa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.FragmentHomeBinding;
import com.example.sapa.models.UserData;
import com.example.sapa.models.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sapa.RecycleviewAdapter.HospitalAdapter;
import com.example.sapa.models.Hospitals;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {


    private HospitalAdapter hospitalAdapter;
    private List<Hospitals> hospitalList = new ArrayList<>();


    private FragmentHomeBinding binding;

    public Home() {
    }

    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);


        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("user_session", requireActivity().MODE_PRIVATE);
        String userId = sharedPreferences.getString("coordinator_id", "");

        if (!userId.isEmpty()) {
            fetchUserProfile(userId);
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }


        return binding.getRoot();


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
        Log.e("homeProfile", "Request status:  " +data.getStatus());
        if (data.getStatus().equalsIgnoreCase("Pending")){
            binding.requestStatus.setText("Waiting for Approval");
        }else{
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
}
