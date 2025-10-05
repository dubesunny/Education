package com.example.education;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected ImageView btnDrawerToggle;
    protected TextView authName, authEmail;
    protected FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the base drawer layout
        super.setContentView(R.layout.activity_base);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // Sidebar setup
        setupSidebar();
    }

    protected void setupSidebar() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnDrawerToggle = findViewById(R.id.drawerToggle);

        // Header
        View headerView = navigationView.getHeaderView(0);
        authName = headerView.findViewById(R.id.auth_username);
        authEmail = headerView.findViewById(R.id.auth_email);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Show Firebase user info
            authName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User");
            authEmail.setText(currentUser.getEmail());
        } else {
            // Redirect if not logged in
            redirectToLogin();
        }

        // Toggle open drawer
        btnDrawerToggle.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        // Logout
        MenuItem logoutBtn = navigationView.getMenu().findItem(R.id.logout);
        logoutBtn.setOnMenuItemClickListener(item -> {
            FirebaseAuth.getInstance().signOut();
            redirectToLogin();
            return true;
        });

        // Navigation click
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(navigationView);

            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_users) {
                startActivity(new Intent(this, UserActivity.class));
            } else if (id == R.id.nav_courses){
                startActivity(new Intent(this,Course.class));
            }
            return true;
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Used by child activities to inject their own layout
    protected void setPageContent(@LayoutRes int layoutRes) {
        getLayoutInflater().inflate(layoutRes, findViewById(R.id.container), true);
    }
}