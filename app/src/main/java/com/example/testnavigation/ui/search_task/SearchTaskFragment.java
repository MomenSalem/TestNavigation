package com.example.testnavigation.ui.search_task;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testnavigation.DataBaseHelper;
import com.example.testnavigation.Task;
import com.example.testnavigation.TaskAdapter;
import com.example.testnavigation.databinding.FragmentSearchTaskBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchTaskFragment extends Fragment {

    private FragmentSearchTaskBinding binding;
    DataBaseHelper dataBaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
                ArrayList<Task> taskList = new ArrayList<>();

                // Query the database for tasks within the date range
                Cursor cursor = dataBaseHelper.getTasksWithinDateRange(selectedStartDate[0], selectedEndDate[0]);

                // Process the cursor and add tasks to the list
                while (cursor.moveToNext()) {
                    String taskTitle = cursor.getString(1);
                    String taskDescription = cursor.getString(2);
                    String dueDate = cursor.getString(3);
                    String priority = cursor.getString(4);
                    boolean setReminder = cursor.getInt(5) == 1;
                    boolean completionStatus = cursor.getInt(6) == 1;

                    Task task = new Task(taskTitle, taskDescription, dueDate, priority, setReminder, completionStatus);
                    taskList.add(task);
                }

                // Set up the RecyclerView and adapter
                TaskAdapter adapter = new TaskAdapter(context, taskList);
                RecyclerView recyclerView = binding.recyclerView;
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));

            } catch (Exception e) {
                Toast.makeText(context, "Error while fetching tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return root;
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

    interface DatePickerCallback {
        void onDateSelected(String selectedDate);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}