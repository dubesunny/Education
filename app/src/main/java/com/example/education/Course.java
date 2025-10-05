package com.example.education;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course extends BaseActivity {
    EditText editCourse;
    Spinner spinnerStatus;
    Button btnAddUpdate;
    RecyclerView courseRecyclerView;

    FirebaseFirestore db;
    List<CourseModel> courseList;
    CourseAdapter adapter;
    String selectedCourseId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageContent(R.layout.activity_course);

        editCourse = findViewById(R.id.coursename);
        spinnerStatus = findViewById(R.id.spinner_status);
        btnAddUpdate = findViewById(R.id.btnAddUpdate);
        courseRecyclerView = findViewById(R.id.courseRecyclerView);

        db = FirebaseFirestore.getInstance();
        courseList = new ArrayList<>();

        // Spinner setup
        String[] statusOptions = {"Active", "Inactive"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item, statusOptions);
        statusAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // RecyclerView setup
        adapter = new CourseAdapter(this, courseList, course -> {
            // When edit clicked
            selectedCourseId = course.getId();
            editCourse.setText(course.getName());
            spinnerStatus.setSelection(course.getStatus().equals("Active") ? 0 : 1);
            btnAddUpdate.setText("UPDATE");
        });
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseRecyclerView.setAdapter(adapter);

        loadCourses();

        btnAddUpdate.setOnClickListener(v -> {
            String name = editCourse.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();

            if (TextUtils.isEmpty(name)) {
                editCourse.setError("Enter course name");
                return;
            }

            if (selectedCourseId == null) {
                addCourse(name, status);
            } else {
                updateCourse(selectedCourseId, name, status);
            }
        });
    }

    private void addCourse(String name, String status) {
        Map<String, Object> course = new HashMap<>();
        course.put("name", name);
        course.put("status", status);
        course.put("created_at", System.currentTimeMillis());

        db.collection("courses")
                .add(course)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Course added", Toast.LENGTH_SHORT).show();
                    editCourse.setText("");
                    spinnerStatus.setSelection(0);
                    loadCourses();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateCourse(String id, String name, String status) {
        db.collection("courses").document(id)
                .update("name", name, "status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Course updated", Toast.LENGTH_SHORT).show();
                    selectedCourseId = null;
                    btnAddUpdate.setText("ADD");
                    editCourse.setText("");
                    spinnerStatus.setSelection(0);
                    loadCourses();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadCourses() {
        db.collection("courses")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    courseList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        CourseModel course = new CourseModel(
                                doc.getId(),
                                doc.getString("name"),
                                doc.getString("status")
                        );
                        courseList.add(course);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading courses: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
