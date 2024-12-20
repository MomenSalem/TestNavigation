package com.example.testnavigation.ui.completed_task;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testnavigation.DataBaseHelper;
import com.example.testnavigation.DateSeparatorDecoration;
import com.example.testnavigation.SharedPrefManager;
import com.example.testnavigation.Task;
import com.example.testnavigation.TaskAdapter;
import com.example.testnavigation.databinding.FragmentCompletedBinding;

import java.util.ArrayList;

public class CompletedTaskFragment extends Fragment implements TaskAdapter.OnTaskInteractionListener{

    private FragmentCompletedBinding binding;
    SharedPrefManager sharedPrefManager;
    DataBaseHelper dataBaseHelper;
    ArrayList<Task> taskList;
    ArrayList<Task> filteredTaskList;
    TaskAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCompletedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPrefManager = SharedPrefManager.getInstance(this.getContext());
        String userEmail = sharedPrefManager.readString("user_primary_key", "no way");

        RecyclerView recyclerView = binding.recyclerView;
        SearchView searchView = binding.searchView;
        recyclerView.addItemDecoration(new DateSeparatorDecoration(this.getContext()));

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
        Cursor cursor = dataBaseHelper.getCompletedTasks(userEmail);
        taskList = new ArrayList<>();

        // Loop through the cursor and add tasks to the ArrayList
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String taskTitle = cursor.getString(1);
                String taskDescription = cursor.getString(2);
                String dueDate = cursor.getString(3);
                int priority = cursor.getInt(4);
                boolean canEdit = cursor.getInt(5) == 1;
                boolean canDelete = cursor.getInt(6) == 1;
                boolean setReminder = cursor.getInt(7) == 1;
                boolean completionStatus = cursor.getInt(8) == 1;

                // Create a Task object and add it to the list
//                Task task = new Task(id, taskTitle, taskDescription, dueDate, priority, canEdit, canDelete, setReminder, completionStatus);
//                taskList.add(task);
            } while (cursor.moveToNext());
        }
        filteredTaskList = new ArrayList<>(taskList);
        cursor.close();

        // Set up the RecyclerView and adapter
        adapter = new TaskAdapter(this.getContext(), filteredTaskList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        setupSearchView(searchView);
        return root;
    }

    private void setupSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTasks(newText);
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEditClicked(Task task) {
        Toast.makeText(this.getContext(), "Comp--Edit button clicked", Toast.LENGTH_SHORT).show();
    }

    private void filterTasks(String text) {
        filteredTaskList.clear();

        if (text.isEmpty()) {
            filteredTaskList.addAll(taskList);
        } else {
            text = text.toLowerCase();
            for (Task task : taskList) {
                if (task.getTitle().toLowerCase().contains(text) || task.getDescription().toLowerCase().contains(text)) {
                    filteredTaskList.add(task);
                }
            }
        }

        adapter.notifyDataSetChanged();

        if (filteredTaskList.isEmpty()) {
            Toast.makeText(getContext(), "No tasks found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClicked(Task task) {
        // Delete task from the database
        dataBaseHelper.deleteTask(task.getId());

        // Remove task from the RecyclerView's data source
        int position = taskList.indexOf(task);
        if (position != -1) {
            taskList.remove(position);
            filteredTaskList.remove(position); // Remove from the filtered list as well
            adapter.notifyItemRemoved(position); // Notify the adapter about the removal
        }

        // Show a confirmation Toast
        Toast.makeText(this.getContext(), "Task " + task.getTitle() + " deleted Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShareClicked(Task task) {

    }

    @Override
    public void onCheckBoxClicked(Task task) {
        dataBaseHelper.updateTaskCompletionStatus(task.getId(), task.isCompleted()); // Update in DB
        Toast.makeText(getContext(), task.isCompleted() ? "Task " + task.getTitle() + " marked as completed" : "Task " + task.getTitle() + " marked as incomplete", Toast.LENGTH_SHORT).show();
        int position = taskList.indexOf(task);
        if (position != -1) {
            taskList.remove(position);
            filteredTaskList.remove(position); // Remove from the filtered list as well
            adapter.notifyItemRemoved(position); // Notify the adapter about the removal
        }
    }
}