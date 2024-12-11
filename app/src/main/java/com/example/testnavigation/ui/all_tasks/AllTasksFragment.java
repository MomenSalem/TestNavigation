package com.example.testnavigation.ui.all_tasks;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testnavigation.DataBaseHelper;
import com.example.testnavigation.SharedPrefManager;
import com.example.testnavigation.Task;
import com.example.testnavigation.TaskAdapter;
import com.example.testnavigation.databinding.FragmentAllTasksBinding;

import java.util.ArrayList;
import java.util.Objects;

public class AllTasksFragment extends Fragment implements TaskAdapter.OnTaskInteractionListener{

    private FragmentAllTasksBinding binding;
    DataBaseHelper dataBaseHelper;
    ArrayList<Task> taskList;
    TaskAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAllTasksBinding.inflate(inflater, container, false);
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
        Cursor cursor = dataBaseHelper.getAllTasks();
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

        // Set up the RecyclerView and adapter
        adapter = new TaskAdapter(this.getContext(), taskList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        // Set click listeners if buttons are visible

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEditClicked(Task task) {
        Toast.makeText(this.getContext(), "Edit button clicked", Toast.LENGTH_SHORT).show();
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
        SharedPrefManager sharedPrefManager;
        sharedPrefManager = SharedPrefManager.getInstance(this.getContext());
        String userEmail = sharedPrefManager.readString("user_primary_key", "no way");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Todo Task Details");
        intent.putExtra(Intent.EXTRA_TEXT, "Task Title: " + task.getTitle() + "\nDescription: "
                + task.getDescription() + "\nDue Date and Time: " + task.getDueDate());

        try {
            this.getContext().startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this.getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}