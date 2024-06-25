package com.app.qrteachernavigation.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.qrteachernavigation.R;
import com.app.qrteachernavigation.models.Student;

import java.util.List;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsListAdapter extends RecyclerView.Adapter<StudentsListAdapter.ViewHolder> {

    private List<Student> studentList;

    public StudentsListAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView photoImgView;
        private TextView textViewName;
        private TextView textViewId;
        private TextView textViewGroup;
        private TextView textViewSpecialization;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImgView = itemView.findViewById(R.id.photoImgView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewId = itemView.findViewById(R.id.textViewId);
            textViewGroup = itemView.findViewById(R.id.textViewGroup);
            textViewSpecialization = itemView.findViewById(R.id.textViewSpecialization);
        }

        public void bind(Student student) {
            textViewName.setText(student.getFirstName() + " " + student.getLastName());
            textViewId.setText("ID: " + student.getId());
            textViewGroup.setText("Group: " + student.getGroup());
            textViewSpecialization.setText("Major: " + student.getSpecialization());

            if (student.getImageUrl() != null && !student.getImageUrl().isEmpty()) {
                Picasso.get().load(student.getImageUrl()).into(photoImgView);
            }
        }
    }
}
