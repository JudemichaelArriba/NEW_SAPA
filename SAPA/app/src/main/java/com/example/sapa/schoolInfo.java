package com.example.sapa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sapa.databinding.ActivitySchoolInfoBinding;

public class schoolInfo extends AppCompatActivity {

    private ActivitySchoolInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivitySchoolInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String schoolName = extras.getString("school_name", "N/A");
            String schoolAddress = extras.getString("school_address", "N/A");
            String schoolStatus = extras.getString("school_status", "N/A");
            String schoolEmail = extras.getString("school_email", "N/A"); // if you passed email; else remove
            String schoolImageBase64 = extras.getString("school_image_base64", "");
            String schoolMobile = extras.getString("school_mobile", "N/A");
           int studentCount = extras.getInt("StudentCount",0);

            binding.schoolName1.setText(schoolName);
            binding.schoolAddressTv.setText(schoolAddress);
            binding.statusTv.setText(schoolStatus);
            binding.schoolEmailTv.setText(schoolEmail);
            binding.mobileTv2.setText(schoolMobile);
            binding.studentsCountTv.setText(String.valueOf(studentCount));

            if (schoolImageBase64 != null && !schoolImageBase64.isEmpty()) {
                try {
                    if (schoolImageBase64.startsWith("data:image")) {
                        schoolImageBase64 = schoolImageBase64.substring(schoolImageBase64.indexOf(",") + 1);
                    }
                    byte[] decodedBytes = Base64.decode(schoolImageBase64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    if (bitmap != null) {
                        binding.profileImage.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }


        binding.backButton.setOnClickListener(v -> {

            finish();

        });


    }
}
