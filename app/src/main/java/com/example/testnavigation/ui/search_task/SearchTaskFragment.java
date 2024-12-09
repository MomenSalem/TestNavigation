package com.example.testnavigation.ui.search_task;

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
import java.util.Date;

public class SearchTaskFragment extends Fragment {

    private FragmentSearchTaskBinding binding;
    DataBaseHelper dataBaseHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context context = getContext();

        try {
            dataBaseHelper = new DataBaseHelper(this.getContext(), "final_project_db", null, 1);
            // Use the database helper
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button search = binding.searchButton;

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView recyclerView = binding.recyclerView;
                String startDateStr = binding.startDateEditText.getText().toString().trim();
                String endDateStr = binding.endDateEditText.getText().toString().trim();
                // Validate input
                if (TextUtils.isEmpty(startDateStr) || TextUtils.isEmpty(endDateStr)) {
                    Toast.makeText(getContext(), "Please enter both start and end dates", Toast.LENGTH_SHORT).show();
                    return ;
                }

                try {
                    ArrayList<Task> taskList = new ArrayList<>();
                    // Parse the dates
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = sdf.parse(startDateStr);
                    Date endDate = sdf.parse(endDateStr);

                    // Convert dates to string in the format of the database (ISO 8601)
                    String startDateFormatted = sdf.format(startDate);
                    String endDateFormatted = sdf.format(endDate);

                    // Query the database for tasks within the date range
                    Cursor cursor = dataBaseHelper.getTasksWithinDateRange(startDateFormatted, endDateFormatted);

                    // Clear previous data
//            taskList.clear();

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
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    // Notify adapter of data change
//            adapter.notifyDataSetChanged();

                } catch (ParseException er) {
                    Toast.makeText(getContext(), "Invalid date format. Use yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}