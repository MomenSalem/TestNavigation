package com.example.testnavigation.ui.search_task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testnavigation.DataBaseHelper;
import com.example.testnavigation.EditTaskActivity;
import com.example.testnavigation.SharedPrefManager;
import com.example.testnavigation.Task;
import com.example.testnavigation.TaskAdapter;
import com.example.testnavigation.databinding.FragmentSearchTaskBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchTaskFragment extends Fragment implements TaskAdapter.OnTaskInteractionListener{

    private FragmentSearchTaskBinding binding;
    SharedPrefManager sharedPrefManager;
    DataBaseHelper dataBaseHelper;
    ArrayList<Task> taskList;
    ArrayList<Task> filteredTaskList;
    TaskAdapter adapter;
    String userEmail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPrefManager = SharedPrefManager.getInstance(this.getContext());
        userEmail = sharedPrefManager.readString("user_primary_key", "no way");

        Context context = getContext();

        // Initialize database helper
        try {
            dataBaseHelper = new DataBaseHelper(this.getContext(), "final_project_db", null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize variables to store dates
        final String[] selectedStartDate = {null};
        final String[] selectedEndDate = {null};

        RecyclerView recyclerView = binding.recyclerView;
        SearchView searchView = binding.searchView;

        // Configure Start Date Button
        binding.startDateButton.setOnClickListener(v -> {
            showDatePickerDialog((selectedDate) -> {
                selectedStartDate[0] = selectedDate;
                binding.startDateButton.setText("Start: " + selectedDate);
            });
        });

        // Configure End Date Button
        binding.endDateButton.setOnClickListener(v -> {
            showDatePickerDialog((selectedDate) -> {
                selectedEndDate[0] = selectedDate;
                binding.endDateButton.setText("End: " + selectedDate);
            });
        });

        // Search Button Logic
        binding.searchButton.setOnClickListener(view -> {
            // Check if both dates are selected
            if (selectedStartDate[0] == null || selectedEndDate[0] == null) {
                Toast.makeText(context, "Please select both start and end dates", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date startDate = sdf.parse(selectedStartDate[0]);
                Date endDate = sdf.parse(selectedEndDate[0]);

                if (endDate.before(startDate)) { // Check if endDate is earlier than startDate
                    Toast.makeText(context, "End date must be greater than or equal to the start date", Toast.LENGTH_SHORT).show();
                    return;
                }
                taskList = new ArrayList<>();

                // Query the database for tasks within the date range
                Cursor cursor = dataBaseHelper.getTasksWithinDateRange(selectedStartDate[0], selectedEndDate[0], userEmail);

                // Process the cursor and add tasks to the list
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(0);
                    String taskTitle = cursor.getString(1);
                    String taskDescription = cursor.getString(2);
                    String dueDate = cursor.getString(3);
                    int priority = cursor.getInt(4);
                    boolean canEdit = cursor.getInt(5) == 1;
                    boolean canDelete = cursor.getInt(6) == 1;
                    boolean setReminder = cursor.getInt(7) == 1;
                    boolean completionStatus = cursor.getInt(8) == 1;

//                    Task task = new Task(id, taskTitle, taskDescription, dueDate, priority, canEdit, canDelete, setReminder, completionStatus);
//                    taskList.add(task);
                }

                filteredTaskList = new ArrayList<>(taskList);

                // If no tasks are found, show a message
                if (taskList.isEmpty()) {
                    Toast.makeText(context, "No tasks found for the selected date period", Toast.LENGTH_SHORT).show();
                }
                searchView.setVisibility(View.VISIBLE);
                // Set up the RecyclerView and adapter
                adapter = new TaskAdapter(context, filteredTaskList, this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                setupSearchView(searchView);

            } catch (Exception e) {
                Toast.makeText(context, "Error while fetching tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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

    private void showDatePickerDialog(DatePickerCallback callback) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    // Format selected date
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    callback.onDateSelected(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @Override
    public void onEditClicked(Task task) {
        // Start EditTaskActivity and pass the task ID
        Intent intent = new Intent(getContext(), EditTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        editTaskLauncher.launch(intent);
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
        SharedPrefManager sharedPrefManager;
        sharedPrefManager = SharedPrefManager.getInstance(this.getContext());
        String userEmail = sharedPrefManager.readString("user_primary_key", "no way");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Todo Task Details");
        // Convert priority from integer to string label
        String priorityLabel = null;
        switch (task.getPriority()) {
            case 0:
                priorityLabel = "High";
                break;
            case 1:
                priorityLabel = "Medium";
                break;
            case 2:
                priorityLabel = "Low";
                break;
        }

        // Define additional task attributes
        String editable = task.isCanEdit() ? "Yes" : "No";
        String deletable = task.isCanDelete() ? "Yes" : "No";
        String reminderSet = task.isSetReminder() ? "Yes" : "No";
        String completedStatus = task.isCompleted() ? "Completed" : "Not Completed";

        // Prepare the text including all task details
        String taskDetails = "Task Title: " + task.getTitle() +
                "\nDescription: " + task.getDescription() +
                "\nDue Date and Time: " + task.getDueDate() +
                "\nPriority: " + priorityLabel +
                "\nEditable: " + editable +
                "\nDeletable: " + deletable +
                "\nReminder Set: " + reminderSet +
                "\nCompletion Status: " + completedStatus;

        // Put the extended text into the intent
        intent.putExtra(Intent.EXTRA_TEXT, taskDetails);

        try {
            this.getContext().startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this.getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckBoxClicked(Task task) {
        dataBaseHelper.updateTaskCompletionStatus(task.getId(), task.isCompleted()); // Update in DB
        Toast.makeText(getContext(), task.isCompleted() ? "Task " + task.getTitle() + " marked as completed" : "Task " + task.getTitle() + " marked as incomplete", Toast.LENGTH_SHORT).show();
    }

    interface DatePickerCallback {
        void onDateSelected(String selectedDate);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    ActivityResultLauncher<Intent> editTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // Retrieve the updated task details from the result intent
                    long taskId = result.getData().getLongExtra("task_id", -1);
                    if (taskId != -1) {
                        int position = findTaskPositionById(taskId);
                        if (position != -1) {
                            Task updatedTask = fetchTaskById(taskId); // Implement this method to fetch updated task from your database
                            taskList.set(position, updatedTask);
                            filteredTaskList.set(position, updatedTask);
                            adapter.notifyItemChanged(position);
                            Toast.makeText(getContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private int findTaskPositionById(long taskId) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getId() == taskId) {
                return i; // Return the position of the task in the list
            }
        }
        return -1; // Return -1 if the task is not found
    }

    private Task fetchTaskById(long taskId) {
        Cursor cursor = dataBaseHelper.getTaskByIdForUser(taskId, userEmail);
        Task updatedTask = null;
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            String taskTitle = cursor.getString(1);
            String taskDescription = cursor.getString(2);
            String dueDate = cursor.getString(3);
            int priority = cursor.getInt(4);
            boolean canEdit = cursor.getInt(5) == 1;
            boolean canDelete = cursor.getInt(6) == 1;
            boolean setReminder = cursor.getInt(7) == 1;
            boolean completionStatus = cursor.getInt(8) == 1;

            updatedTask = new Task(id, taskTitle, taskDescription, dueDate, priority, canEdit, canDelete, setReminder, completionStatus);
            cursor.close();
        }
        return updatedTask;
    }

}