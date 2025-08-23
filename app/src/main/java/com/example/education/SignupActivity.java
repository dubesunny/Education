package com.example.education;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
TextView tvStudent, tvTeacher ,tvLogin;
EditText editName,editEmail,editPhone,editSchool,editPassword,editConfirmPassword;
Button btnRegister;
String selectedRole = "Teacher";
ImageView togglePassword, toggleCPassword;
boolean isPasswordVisible = false;
boolean isCPasswordVisible = false;
FirebaseAuth mAuth;
FirebaseFirestore db;
String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        tvStudent = findViewById(R.id.roleStudent);
        tvTeacher = findViewById(R.id.roleTeacher);
        tvLogin = findViewById(R.id.loginRedirectText);

        editName = findViewById(R.id.signup_name);
        editEmail = findViewById(R.id.signup_email);
        editPhone = findViewById(R.id.signup_phone);
        editSchool = findViewById(R.id.signup_schoolname);
        editPassword = findViewById(R.id.signup_password);
        editConfirmPassword = findViewById(R.id.signup_cpassword);
        togglePassword = findViewById(R.id.togglePassword);
        toggleCPassword = findViewById(R.id.toggleCPassword);

        btnRegister = findViewById(R.id.signup_button);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tvStudent.setOnClickListener(view -> {
            setRole(tvStudent);
        });

        tvTeacher.setOnClickListener(view -> {
            setRole(tvTeacher);
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editPassword.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
                togglePassword.setImageResource(R.drawable.baseline_visibility_off_24);
            } else {
                editPassword.setTransformationMethod(null);
                togglePassword.setImageResource(R.drawable.baseline_visibility_24);
            }
            editPassword.setSelection(editPassword.getText().length()); // move cursor to end
            isPasswordVisible = !isPasswordVisible;
        });

        toggleCPassword.setOnClickListener(v -> {
            if (isCPasswordVisible) {
                editConfirmPassword.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
                toggleCPassword.setImageResource(R.drawable.baseline_visibility_off_24);
            } else {
                editConfirmPassword.setTransformationMethod(null);
                toggleCPassword.setImageResource(R.drawable.baseline_visibility_24);
            }
            editConfirmPassword.setSelection(editConfirmPassword.getText().length());
            isCPasswordVisible = !isCPasswordVisible;
        });
        btnRegister.setOnClickListener(v -> {
            registerUser();
        });
    }

    private void setRole(TextView selected)
    {
        tvStudent.setBackgroundResource(R.drawable.bg_toggle_unselected);
        tvTeacher.setBackgroundResource(R.drawable.bg_toggle_unselected);

        tvStudent.setTextColor(Color.BLACK);
        tvTeacher.setTextColor(Color.BLACK);

        selected.setBackgroundResource(R.drawable.bg_toggle_selected);
        selected.setTextColor(Color.WHITE);

        selectedRole = selected.getText().toString();

    }

    private void registerUser(){
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String school = editSchool.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            editName.setError("Enter name");
            return;
        }

        if(TextUtils.isEmpty(email)){
            editEmail.setError("Enter email");
            return;
        }

        if(phone.length() != 10){
            editPhone.setError("Enter valid 10-digit phone");
            return;
        }

        if(TextUtils.isEmpty(school)){
            editSchool.setError("Enter school name");
            return;
        }

        if(!password.matches(passwordPattern)){
            editPassword.setError("Min 8 characters with upper, lower, digit, special character");
            return;
        }

        if(!password.equals(confirmPassword)){
            editConfirmPassword.setError("Passwords do not match");
            return;
        }

        if(TextUtils.isEmpty(selectedRole)){
            Toast.makeText(this,"Select a role",Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                this,task -> {
                    if(task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null){
                            String uid = firebaseUser.getUid();
                            Map<String,Object> user = new HashMap<>();
                            user.put("name",name);
                            user.put("email",email);
                            user.put("phone",phone);
                            user.put("school",school);
                            user.put("role",selectedRole);
                            db.collection("users").document(uid)
                                    .set(user)
                                    .addOnSuccessListener(aVoid ->{
                                        firebaseUser.sendEmailVerification()
                                                .addOnCompleteListener(verifyTask -> {
                                                    if (verifyTask.isSuccessful()){
                                                        Toast.makeText(SignupActivity.this,"Registeration successfull,please verify your email",Toast.LENGTH_SHORT).show();
                                                        mAuth.signOut();
                                                        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else {
                                                        Toast.makeText(SignupActivity.this,
                                                                "Failed to send verification: " + verifyTask.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    })
                                    .addOnFailureListener(e->{
                                        Toast.makeText(SignupActivity.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    });
                        }

                    }else {
                        Toast.makeText(SignupActivity.this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
