package com.example.testnavigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testnavigation.ui.today_task.TodayTaskFragment;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private Context context;
    private ArrayList<Task> taskList;

    public TaskAdapter(Context context, ArrayList<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
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
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskTitle;
        private final TextView taskDescription;
        private final TextView taskDueDate;
        private final TextView taskPriority;
        private final CheckBox completionStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            taskPriority = itemView.findViewById(R.id.taskPriority);
            completionStatus = itemView.findViewById(R.id.completionStatus);
        }
        public void bind(Task task) {
            taskTitle.setText(task.getTitle());
            taskDescription.setText(task.getDescription());
            taskDueDate.setText("Due: " + task.getDueDate());
            taskPriority.setText("Priority: " + task.getPriority());
            completionStatus.setChecked(task.isCompleted());
        }
    }
}
