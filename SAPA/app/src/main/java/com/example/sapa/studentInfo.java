package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sapa.databinding.ActivityStudentInfoBinding;

public class studentInfo extends AppCompatActivity {

    private ActivityStudentInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStudentInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        String id = getIntent().getStringExtra("id");
        String fullname = getIntent().getStringExtra("fullname");
        String email = getIntent().getStringExtra("email");
        String birthdate = getIntent().getStringExtra("birthdate");
        String gender = getIntent().getStringExtra("gender");
        String schoolName = getIntent().getStringExtra("schoolName");


        binding.fullnameTv.setText(fullname != null ? fullname : "N/A");
        binding.emailTv.setText(email != null ? email : "N/A");
        binding.birthdateTv.setText(birthdate != null ? birthdate : "N/A");
        binding.genderTv.setText(gender != null ? gender : "N/A");
        binding.schoolNameTv.setText(schoolName != null ? schoolName : "N/A");


        binding.backButton.setOnClickListener(v -> finish());


        binding.editStudent.setOnClickListener(v -> {

        });

        binding.editStudent.setOnClickListener(v -> {
            Intent intent = new Intent(studentInfo.this, edit_student.class);

            intent.putExtra("id", id);
            intent.putExtra("fullname", fullname);
            intent.putExtra("email", email);
            intent.putExtra("birthdate", birthdate);
            intent.putExtra("gender", gender);
            intent.putExtra("schoolName", schoolName);

            startActivity(intent);
        });
    }
}
