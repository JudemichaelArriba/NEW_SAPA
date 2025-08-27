package com.example.sapa;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.BillAdapter;
import com.example.sapa.databinding.FragmentBillsBinding;
import com.example.sapa.models.Bill;
import com.example.sapa.models.UserBillsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class bills extends Fragment {

    private FragmentBillsBinding binding;
    private BillAdapter billAdapter;
    private List<Bill> allBills = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBillsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        billAdapter = new BillAdapter(new ArrayList<>());
        binding.billsView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.billsView.setAdapter(billAdapter);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.status_bills,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.Billstatus.setAdapter(spinnerAdapter);

        binding.Billstatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString();
                filterBills(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                filterBills("All");
            }
        });


        fetchUserBills("USR-20250820-0001");

        binding.payAllBtn.setOnClickListener(v -> {

        });
    }

    private void filterBills(String status) {
        List<Bill> filteredList = new ArrayList<>();
        if (status.equalsIgnoreCase("All")) {
            filteredList.addAll(allBills);
        } else {
            for (Bill bill : allBills) {
                if (bill.getStatus().equalsIgnoreCase(status)) {
                    filteredList.add(bill);
                }
            }
        }
        billAdapter.updateList(filteredList);
    }

    private void fetchUserBills(String userId) {
        ApiInterface api = ApiClient.getClient(requireContext()).create(ApiInterface.class);
        Call<UserBillsResponse> call = api.getUserBills(userId);

        call.enqueue(new Callback<UserBillsResponse>() {
            @Override
            public void onResponse(Call<UserBillsResponse> call, Response<UserBillsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserBillsResponse userBills = response.body();

                    if (userBills.getBills() != null && !userBills.getBills().isEmpty()) {
                        allBills.clear();
                        allBills.addAll(userBills.getBills());

                        Log.d("BILLS", "Bills received:");
                        for (Bill bill : allBills) {
                            Log.d("BILLS", "Code: " + bill.getBillCode() +
                                    ", Amount: " + bill.getAmount() +
                                    ", IssuedAt: " + bill.getIssuedAt() +
                                    ", Status: " + bill.getStatus());
                        }

                        filterBills("All");

                        binding.totalBills.setText("₱" + userBills.getTotal_bills());
                        Log.d("BILLS", "Total Bills: " + userBills.getTotal_bills());
                    } else {
                        Log.d("BILLS", "No bills received from API!");
                    }

                } else {
                    Log.e("BILLS", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserBillsResponse> call, Throwable t) {
                Log.e("BILLS", "API call failed: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
