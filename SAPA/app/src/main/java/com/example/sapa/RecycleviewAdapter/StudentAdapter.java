package com.example.sapa.RecycleviewAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapa.R;
import com.example.sapa.models.Students;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Students> studentList;
    private Context context;
    private Set<Integer> selectedPositions = new HashSet<>();
    private OnSelectionChangeListener selectionChangeListener;

    public StudentAdapter(List<Students> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Students student = studentList.get(position);
        holder.studentName.setText(student.getStudentFullname());
        holder.studentEmail.setText(student.getEmail());
        holder.studentMobile.setText(student.getContactNo());

        holder.itemView.setBackgroundColor(selectedPositions.contains(position) ?
                Color.parseColor("#D0E8FF") : Color.TRANSPARENT);

        holder.itemView.setOnClickListener(v -> {
            if (selectedPositions.contains(position)) {
                selectedPositions.remove(position);
            } else {
                selectedPositions.add(position);
            }
            notifyItemChanged(position);

            if (selectionChangeListener != null) {
                selectionChangeListener.onSelectionChanged(selectedPositions.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, studentEmail, studentMobile;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentEmail = itemView.findViewById(R.id.studentEmail);
            studentMobile = itemView.findViewById(R.id.studentMobile);
        }
    }

    public List<Students> getSelectedStudents() {
        List<Students> selectedStudents = new ArrayList<>();
        for (Integer pos : selectedPositions) {
            if (pos >= 0 && pos < studentList.size()) {
                selectedStudents.add(studentList.get(pos));
            }
        }
        return selectedStudents;
    }

    public void updateData(List<Students> newStudents) {
        studentList.clear();
        studentList.addAll(newStudents);
        selectedPositions.clear();
        notifyDataSetChanged();
    }

    public interface OnSelectionChangeListener {
        void onSelectionChanged(int selectedCount);
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }
}
