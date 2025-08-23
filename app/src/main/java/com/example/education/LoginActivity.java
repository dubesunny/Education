package com.example.education;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
EditText editEmail,editPassword;
Button loginBtn;
TextView registerRedirect;
ImageView togglePassword;
boolean isPasswordVisible = false;
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.login_email);
        editPassword = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_button);
        registerRedirect = findViewById(R.id.signUpRedirectText);
        togglePassword = findViewById(R.id.togglePassword);

        mAuth = FirebaseAuth.getInstance();

        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

    }

    private void loginUser(){
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            editEmail.setError("Enter email");
            return;
        }

        if(TextUtils.isEmpty(password)){
            editPassword.setError("Enter password");
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,task -> {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user != null && user.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,"Please verify your email first.",Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"Login Failed:"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
        });
    }
}