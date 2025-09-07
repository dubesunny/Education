package com.example.education;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Add_User extends BaseActivity {
    Spinner spinnerStatus, spinnerRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageContent(R.layout.activity_add_user);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerRole = findViewById(R.id.spinner_role);

        // Status options
        String[] statusOptions = {"Active", "Inactive"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_list_item,
                statusOptions
        );
        statusAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Role options
        String[] roleOptions = {"Student", "Teacher", "Admin"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_list_item,
                roleOptions
        );
        roleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

    }
}