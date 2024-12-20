package com.example.testnavigation.ui.new_task;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.testnavigation.ConnectionAsyncTask;
import com.example.testnavigation.DataBaseHelper;
import com.example.testnavigation.MainActivity;
import com.example.testnavigation.SharedPrefManager;
import com.example.testnavigation.Task;
import com.example.testnavigation.databinding.FragmentNewTaskBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class NewTaskFragment extends Fragment {

    SharedPrefManager sharedPrefManager;
    TextInputEditText taskTitleEditText;
    TextInputEditText taskDescriptionEditText;
    String dueDateAndTime;
    String priorityLevel;
    RadioGroup priorityLevelRadioGroup;
    boolean completionStatus;
    boolean reminderStatus;
    boolean deletableTask;
    boolean editableTask;
    TextView dueDateTimeTextView;
    Button selectDateTimeButton;

    Button getReadyTasksButton;


    private FragmentNewTaskBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPrefManager = SharedPrefManager.getInstance(this.getContext());
        String userEmail = sharedPrefManager.readString("user_primary_key", "no way");

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity(), "final_project_db", null, 1);

        dueDateTimeTextView = binding.dueDateTimeTextView;
        selectDateTimeButton = binding.selectDateTimeButton;
        taskTitleEditText = binding.taskTitleEditText;
        taskDescriptionEditText = binding.taskDescriptionEditText;
        priorityLevelRadioGroup = binding.priorityLevelRadioGroup;
        getReadyTasksButton = binding.getReadyTasksButton;

        // make the choice on the "medium" radio button selected by default
        priorityLevelRadioGroup.check(binding.priorityLevelRadioGroup.getChildAt(1).getId());


        // Get the due date and time
        selectDateTimeButton.setOnClickListener(view -> showDateTimePicker());

        // get ready tasks button
        getReadyTasksButton.setOnClickListener(view -> getReadyTasks());

        // Set the click listener for the "Add Task" button
        binding.addButton.setOnClickListener(view -> {

            // Get the data from the TextInputEditText
            String taskTitle = getData(taskTitleEditText);
            String taskDescription = getData(taskDescriptionEditText);
            getPriorityLevel();
            completionStatus = isChecked(binding.completionStatusCheckBox);
            reminderStatus = isChecked(binding.remindMeCheckBox);
            deletableTask = isChecked(binding.deletableTaskCheckBox);
            editableTask = isChecked(binding.editableTaskCheckBox);

            // Check if any of the fields are empty
            if (!areEmptyFields(taskTitle, taskDescription)) {
                int priority = 1;
                if (priorityLevel.equals("High")) {
                    priority = 0;
                } else if (priorityLevel.equals("Low")) {
                    priority = 2;
                }
                // Add the task to the database
                dataBaseHelper.insertTask(taskTitle, taskDescription, dueDateAndTime, priority, editableTask, deletableTask, completionStatus, userEmail);
                if (reminderStatus) {
                    // Define the formatter
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                    // Parse the string into a LocalDateTime object
                    LocalDateTime dateTime = LocalDateTime.parse(dueDateAndTime, formatter);

                    // Extract hours and minutes
                    int hours = dateTime.getHour();
                    int minutes = dateTime.getMinute();

                    Log.d("Basheer", "Hours: " + hours);
                    Log.d("Basheer", "Minutes: " + minutes);
                    // Create an intent to set an alarm
                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    intent.putExtra(AlarmClock.EXTRA_HOUR, hours);
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Todo Task (" + taskTitle + ")");
                    intent.putExtra(AlarmClock.EXTRA_SKIP_UI, false);  // Skip UI if you don't want the user to confirm the alarm settings

                    // Check if the device supports this action
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        Log.d("Basheer", "Intent is not null");
                        startActivity(intent);
                    } else {
                        Toast.makeText(this.getContext(), "No app can handle this action", Toast.LENGTH_SHORT).show();
                    }
                }

                // Make a toast message
                showToastMessage("Task added successfully!");

            }else {
                // Show error message
                showAlertDialog("Please fill in all the fields.");
            }
        });
        return root;
    }

    private void getReadyTasks() {
        ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(getActivity());
        connectionAsyncTask.execute("https://run.mocky.io/v3/7c31c6de-9cff-4923-b647-685998ba37cd");
    }

    private boolean areEmptyFields(String taskTitle, String taskDescription) {
        if (taskTitle.isEmpty() || taskDescription.isEmpty()) {
            showAlertDialog("Please fill in all the fields.");
            return true;
        }
        return false;
    }

    private boolean isChecked(CheckBox checkBox) {
        return checkBox.isChecked();
    }

    private void getPriorityLevel() {
        // Get the priority level from the radio group
        int selectedId = priorityLevelRadioGroup.getCheckedRadioButtonId();
        priorityLevel = "Medium";
        RadioButton radioButton = binding.priorityLevelRadioGroup.findViewById(selectedId);

        if (radioButton != null) {
            priorityLevel = radioButton.getText().toString();
        }
    }

    private String getData(EditText editText) {
        return editText.getText().toString();
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();


        // DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    // After date is selected, show TimePickerDialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            (timeView, hourOfDay, minute) -> {
                                // Combine date and time
                                String dueDateTime = String.format("%04d-%02d-%02d %02d:%02d", year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);
                                dueDateTimeTextView.setText(dueDateTime);
                                dueDateAndTime = dueDateTime;
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true); // Use 24-hour format
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showAlertDialog(String alertMessage) {
        new AlertDialog.Builder(getActivity()).setMessage(alertMessage)
                .setPositiveButton("OK", (dialog, which) -> { /* Handle yes */ })
                .show();
    }

    public void showToastMessage(String s) {
        Toast toast = Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}