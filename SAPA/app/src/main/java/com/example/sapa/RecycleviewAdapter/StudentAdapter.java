package com.example.sapa.RecycleviewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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

    private Set<String> selectedIds = new HashSet<>();
    private boolean selectionMode = false;
    private boolean enableSelection;
    private int maxSelection = Integer.MAX_VALUE; // 👈 default unlimited

    // 🔹 Callback for multi-select
    public interface OnSelectionChangeListener {
        void onSelectionChanged(int count);
    }

    private OnSelectionChangeListener selectionChangeListener;

    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    // 🔹 Callback for single-click (student info)
    public interface OnStudentClickListener {
        void onStudentClick(Students student);
    }

    private OnStudentClickListener studentClickListener;

    public void setOnStudentClickListener(OnStudentClickListener listener) {
        this.studentClickListener = listener;
    }

    public StudentAdapter(List<Students> studentList, Context context, boolean enableSelection) {
        this.studentList = studentList;
        this.context = context;
        this.enableSelection = enableSelection;
    }

    // 👇 Allow activity to set max capacity
    public void setMaxSelection(int max) {
        this.maxSelection = max;
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
        holder.studentMobile.setText(student.getSchoolName());

        holder.studentCheckBox.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
        holder.studentCheckBox.setChecked(selectedIds.contains(student.getId()));

        // 🔹 Click behavior changes depending on mode
        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                if (enableSelection && selectionMode) {
                    // Multi-select mode
                    toggleSelection(student);
                } else if (!enableSelection) {
                    // Normal click → open student info
                    if (studentClickListener != null) {
                        studentClickListener.onStudentClick(student);
                    }
                }
            }
        });

        // 🔹 Long click only used to start multi-select
        holder.itemView.setOnLongClickListener(v -> {
            if (enableSelection && !selectionMode) {
                selectionMode = true;
                toggleSelection(student);
                return true;
            }
            return false;
        });

        // 🔹 Checkbox click = same toggle
        holder.studentCheckBox.setOnClickListener(v -> toggleSelection(student));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    private void toggleSelection(Students student) {
        if (selectedIds.contains(student.getId())) {
            // 👇 remove if unselected
            selectedIds.remove(student.getId());
        } else {
            if (selectedIds.size() >= maxSelection) {
                Toast.makeText(context, "You can only select up to " + maxSelection + " students", Toast.LENGTH_SHORT).show();
                return; // 🚫 don't allow more than max
            }
            selectedIds.add(student.getId());
        }
        notifyDataSetChanged();

        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(selectedIds.size());
        }
    }

    // 👇 Allows unselecting from activity
    public void unselectStudent(Students student) {
        selectedIds.remove(student.getId());
        notifyDataSetChanged();

        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(selectedIds.size());
        }
    }

    // ✅ Return actual selected IDs
    public ArrayList<String> getSelectedIds() {
        return new ArrayList<>(selectedIds);
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, studentEmail, studentMobile;
        CheckBox studentCheckBox;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentEmail = itemView.findViewById(R.id.studentEmail);
            studentMobile = itemView.findViewById(R.id.studentMobile);
            studentCheckBox = itemView.findViewById(R.id.studentCheckBox);
        }
    }

    public List<Students> getSelectedStudents() {
        List<Students> selected = new ArrayList<>();
        for (Students s : studentList) {
            if (selectedIds.contains(s.getId())) {
                selected.add(s);
            }
        }
        return selected;
    }

    public void updateData(List<Students> newStudents) {
        this.studentList.clear();
        this.studentList.addAll(newStudents);
        selectedIds.clear();
        notifyDataSetChanged();

        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(0);
        }
    }

    public void clearSelection() {
        selectionMode = false;
        selectedIds.clear();
        notifyDataSetChanged();
        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(0);
        }
    }
}
