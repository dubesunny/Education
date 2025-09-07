package com.example.education;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText("Name: " + user.getName());
        holder.tvEmail.setText("Email: " + user.getEmail());
        holder.tvPhone.setText("Phone: " + user.getPhone());
        holder.tvRole.setText("Role: " + user.getRole());
        holder.tvSchool.setText("School: " + user.getSchool());

        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            // TODO: Start Edit Activity with Intent extras
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (user.getId() != null) {
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.getId())
                        .delete();
                userList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone, tvRole, tvSchool;
        Button btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvSchool = itemView.findViewById(R.id.tvSchool);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
