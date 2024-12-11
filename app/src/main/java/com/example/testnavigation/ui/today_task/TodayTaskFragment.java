package com.example.testnavigation.ui.today_task;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testnavigation.DataBaseHelper;
import com.example.testnavigation.Task;
import com.example.testnavigation.TaskAdapter;
import com.example.testnavigation.databinding.FragmentTodayBinding;

import java.util.ArrayList;

public class TodayTaskFragment extends Fragment implements TaskAdapter.OnTaskInteractionListener{

    private FragmentTodayBinding binding;
    DataBaseHelper dataBaseHelper;
    ArrayList<Task> taskList;
    TaskAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTodayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;

        try {
            dataBaseHelper = new DataBaseHelper(this.getContext(), "final_project_db", null, 1);
            // Use the database helper
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataBaseHelper != null) {
                // Assuming DataBaseHelper has a method to close the database or cleanup
                dataBaseHelper.close();
            }
        }
        Cursor cursor = dataBaseHelper.getTodayTasks();
        taskList = new ArrayList<>();

        // Loop through the cursor and add tasks to the ArrayList
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String taskTitle = cursor.getString(1);
                String taskDescription = cursor.getString(2);
                String dueDate = cursor.getString(3);
                String priority = cursor.getString(4);
                boolean canEdit = cursor.getInt(5) == 1;
                boolean canDelete = cursor.getInt(6) == 1;
                boolean setReminder = cursor.getInt(7) == 1;
                boolean completionStatus = cursor.getInt(8) == 1;

                // Create a Task object and add it to the list
                Task task = new Task(id, taskTitle, taskDescription, dueDate, priority, canEdit, canDelete, setReminder, completionStatus);
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
//        // Add some sample tasks to the list
//        taskList.add(new Task("Task 1", "Description 1", "2024-11-1", "High", false, false));
//        taskList.add(new Task("Task 2", "Description 2", "2024-11-2", "Medium", false, true));
//        taskList.add(new Task("Task 3", "Description 3", "2024-11-3", "Low", false, false));
//        taskList.add(new Task("Task 4", "Description 4", "2024-11-4", "High", false, true));
//        taskList.add(new Task("Task 5", "Description 5", "2024-11-5", "Medium", false, false));


        // Set up the RecyclerView and adapter
        adapter = new TaskAdapter(this.getContext(), taskList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEditClicked(Task task) {
        Toast.makeText(this.getContext(), "Today--Edit button clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClicked(Task task) {
        // Delete task from the database
        dataBaseHelper.deleteTask(task.getId());

        // Remove task from the RecyclerView's data source
        int position = taskList.indexOf(task);
        if (position != -1) {
            taskList.remove(position);
            adapter.notifyItemRemoved(position); // Notify the adapter about the removal
        }

        // Show a confirmation Toast
        Toast.makeText(this.getContext(), "Task deleted Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShareClicked(Task task) {

    }
}