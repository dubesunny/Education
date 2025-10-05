package com.example.education;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Add_User extends BaseActivity {
    Spinner spinnerStatus, spinnerRole;
    EditText textName, textEmail, textPhone, textSchool, textPassword, textCPassword;
    Button addBtn;
    ImageView togglePassword, toggleCPassword;
    boolean isPasswordVisible = false;
    boolean isCPasswordVisible = false;
    String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";

    FirebaseFirestore db;
    String userId = null; // for edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageContent(R.layout.activity_add_user);

        db = FirebaseFirestore.getInstance();

        spinnerStatus = findViewById(R.id.userstatus);
        spinnerRole = findViewById(R.id.userrole);
        textName = findViewById(R.id.username);
        textEmail = findViewById(R.id.useremail);
        textPhone = findViewById(R.id.userphone);
        textSchool = findViewById(R.id.userschool);
        textPassword = findViewById(R.id.userpassword);
        textCPassword = findViewById(R.id.usercpassword);
        togglePassword = findViewById(R.id.togglePassword);
        toggleCPassword = findViewById(R.id.toggleCPassword);
        addBtn = findViewById(R.id.addBtn);

        // Password toggle
        togglePassword.setOnClickListener(v -> togglePasswordVisibility(textPassword, togglePassword, true));
        toggleCPassword.setOnClickListener(v -> togglePasswordVisibility(textCPassword, toggleCPassword, false));

        // Status Spinner
        String[] statusOptions = {"Active", "Inactive"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item, statusOptions);
        statusAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Role Spinner
        String[] roleOptions = {"Student", "Teacher", "Admin"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item, roleOptions);
        roleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        // Get userId from Intent (if editing)
        userId = getIntent().getStringExtra("USER_ID");
        if (userId != null) {
            addBtn.setText("Update User");
            loadUserData(userId);
        }

        addBtn.setOnClickListener(view -> {
            if (userId != null) {
                updateUser();
            } else {
                addUser();
            }
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon, boolean isMain) {
        if ((isMain && isPasswordVisible) || (!isMain && isCPasswordVisible)) {
            editText.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
            toggleIcon.setImageResource(R.drawable.baseline_visibility_off_24);
        } else {
            editText.setTransformationMethod(null);
            toggleIcon.setImageResource(R.drawable.baseline_visibility_24);
        }
        editText.setSelection(editText.getText().length());
        if (isMain) isPasswordVisible = !isPasswordVisible;
        else isCPasswordVisible = !isCPasswordVisible;
    }

    // ✅ Load Firestore Data (Edit Mode)
    private void loadUserData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        textName.setText(documentSnapshot.getString("name"));
                        textEmail.setText(documentSnapshot.getString("email"));
                        textPhone.setText(documentSnapshot.getString("phone"));
                        textSchool.setText(documentSnapshot.getString("school"));
                        textPassword.setText(documentSnapshot.getString("password"));
                        textCPassword.setText(documentSnapshot.getString("password"));

                        String role = documentSnapshot.getString("role");
                        String status = documentSnapshot.getString("status");

                        if (role != null) {
                            int pos = ((ArrayAdapter<String>) spinnerRole.getAdapter()).getPosition(role);
                            spinnerRole.setSelection(pos);
                        }

                        if (status != null) {
                            int pos = ((ArrayAdapter<String>) spinnerStatus.getAdapter()).getPosition(status);
                            spinnerStatus.setSelection(pos);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load user", Toast.LENGTH_SHORT).show());
    }

    private void addUser() {
        String name = textName.getText().toString().trim();
        String email = textEmail.getText().toString().trim();
        String phone = textPhone.getText().toString().trim();
        String school = textSchool.getText().toString().trim();
        String password = textPassword.getText().toString().trim();
        String confirmPassword = textCPassword.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();
        String role = spinnerRole.getSelectedItem().toString();

        if (!validateInputs(name, email, phone, school, password, confirmPassword)) return;

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("phone", phone);
                        user.put("school", school);
                        user.put("password", password);
                        user.put("status", status);
                        user.put("role", role);
                        user.put("created_at", System.currentTimeMillis());

                        db.collection("users").add(user)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show();
                                    clearFields();
                                    startActivity(new Intent(this, User.class)); // Change to your list activity
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
    }

    // ✅ Update Existing User
    private void updateUser() {
        String name = textName.getText().toString().trim();
        String email = textEmail.getText().toString().trim();
        String phone = textPhone.getText().toString().trim();
        String school = textSchool.getText().toString().trim();
        String password = textPassword.getText().toString().trim();
        String confirmPassword = textCPassword.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();
        String role = spinnerRole.getSelectedItem().toString();

        if (!validateInputs(name, email, phone, school, password, confirmPassword)) return;

        Map<String, Object> update = new HashMap<>();
        update.put("name", name);
        update.put("email", email);
        update.put("phone", phone);
        update.put("school", school);
        update.put("password", password);
        update.put("status", status);
        update.put("role", role);

        db.collection("users").document(userId)
                .update(update)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, User.class)); // Change this to your user list activity
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean validateInputs(String name, String email, String phone, String school, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            textName.setError("Enter name");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            textEmail.setError("Enter email");
            return false;
        }
        if (phone.length() != 10) {
            textPhone.setError("Enter valid 10-digit phone");
            return false;
        }
        if (TextUtils.isEmpty(school)) {
            textSchool.setError("Enter school name");
            return false;
        }
        if (!password.matches(passwordPattern)) {
            textPassword.setError("Min 8 chars with upper, lower, digit & special char");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            textCPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void clearFields() {
        textName.setText("");
        textEmail.setText("");
        textPhone.setText("");
        textSchool.setText("");
        textPassword.setText("");
        textCPassword.setText("");
        spinnerRole.setSelection(0);
        spinnerStatus.setSelection(0);
    }
}
