package com.example.education;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends BaseActivity {
    Button addUser;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageContent(R.layout.activity_user);

        addUser = findViewById(R.id.add_user);
        addUser.setOnClickListener(view -> {
            Intent intent = new Intent(UserActivity.this, Add_User.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();

        // Listen for changes in Firestore collection
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("Firestore", "Listen failed.", error);
                    return;
                }

                userList.clear();
                if (value != null) {
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            user.setId(doc.getId()); // keep document id for delete/edit
                            userList.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
