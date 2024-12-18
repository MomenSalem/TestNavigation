package com.example.testnavigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final Context context;
    private final ArrayList<Task> taskList;
    private final OnTaskInteractionListener listener;

    public TaskAdapter(Context context, ArrayList<Task> taskList, OnTaskInteractionListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskTitle;
        private final TextView taskDescription;
        private final TextView taskDueDate;
        private final TextView taskPriority;
        private final CheckBox completionStatus;
        private final ImageButton editButton;
        private final ImageButton deleteButton;
        private final ImageButton shareButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            editButton = itemView.findViewById(R.id.btnEdit);
            deleteButton = itemView.findViewById(R.id.btnDelete);
            shareButton = itemView.findViewById(R.id.btnShare);
            taskPriority = itemView.findViewById(R.id.taskPriority);
            completionStatus = itemView.findViewById(R.id.completionStatusBtn);
        }
        public void bind(Task task, final OnTaskInteractionListener listener) {
            taskTitle.setText(task.getTitle());
            taskDescription.setText(task.getDescription());
            taskDueDate.setText("Due: " + task.getDueDate());
            if (task.getPriority() == 2) {
                taskPriority.setText("Priority: Low");
                taskPriority.setTextColor(context.getResources().getColor(R.color.colorLowPriority));
            } else if (task.getPriority() == 1) {
                taskPriority.setText("Priority: Medium");
                taskPriority.setTextColor(context.getResources().getColor(R.color.colorMediumPriority));
            } else {
                taskPriority.setText("Priority: High");
                taskPriority.setTextColor(context.getResources().getColor(R.color.colorHighPriority));
            }
            completionStatus.setChecked(task.isCompleted());

            // Set visibility based on permissions set in task object
            editButton.setVisibility(task.isCanEdit() ? View.VISIBLE : View.GONE);
            deleteButton.setVisibility(task.isCanDelete() ? View.VISIBLE : View.GONE);

            editButton.setOnClickListener(v -> listener.onEditClicked(task));
            deleteButton.setOnClickListener(v -> {
                // Show confirmation dialog before deleting
                new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Call the listener to delete the task
                            listener.onDeleteClicked(task);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            shareButton.setOnClickListener(v -> {
                // Show confirmation dialog before deleting
                new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Share Task via Email")
                        .setMessage("Are you sure you want to share this task?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Call the listener to delete the task
                            listener.onShareClicked(task);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            completionStatus.setOnClickListener(v -> {
                task.setCompleted(completionStatus.isChecked());
                listener.onCheckBoxClicked(task);
            });

        }
    }
    public interface OnTaskInteractionListener {
        void onEditClicked(Task task);
        void onDeleteClicked(Task task);
        void onShareClicked(Task task);
        void onCheckBoxClicked(Task task);
    }

    public Task getTask(int position) {
        return taskList.get(position);
    }

}

