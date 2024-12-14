package com.example.testnavigation.ui.new_task;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
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

import com.example.testnavigation.DataBaseHelper;
import com.example.testnavigation.MainActivity;
import com.example.testnavigation.Task;
import com.example.testnavigation.databinding.FragmentNewTaskBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class NewTaskFragment extends Fragment {

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


    private FragmentNewTaskBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity(), "final_project_db", null, 1);

        dueDateTimeTextView = binding.dueDateTimeTextView;
        selectDateTimeButton = binding.selectDateTimeButton;
        taskTitleEditText = binding.taskTitleEditText;
        taskDescriptionEditText = binding.taskDescriptionEditText;
        priorityLevelRadioGroup = binding.priorityLevelRadioGroup;


        // Get the due date and time
        selectDateTimeButton.setOnClickListener(view -> showDateTimePicker());

        // Set the click listener for the "Add Task" button
        binding.addButton.setOnClickListener(view -> {

            // Get the data from the TextInputEditText
            String taskTitle = getData(taskTitleEditText);
            String taskDescription = getData(taskDescriptionEditText);
            getPriorityLevel();
            boolean completionStatus = isChecked(binding.completionStatusCheckBox);
            boolean reminderStatus = isChecked(binding.remindMeCheckBox);
            boolean deletableTask = isChecked(binding.deletableTaskCheckBox);
            boolean editableTask = isChecked(binding.editableTaskCheckBox);

            // Check if any of the fields are empty
            if (!areEmptyFields(taskTitle, taskDescription)) {
                // Add the task to the database
                dataBaseHelper.insertTask(taskTitle, taskDescription, dueDateAndTime, priorityLevel, editableTask, deletableTask, reminderStatus, completionStatus);
                // Make a toast message
                showToastMessage("Task added successfully!");

            }else {
                // Show error message
                showAlertDialog("Please fill in all the fields.");
            }

        });


        return root;
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
//        // show the priority level in a Toast message
//        showToastMessage("Priority Level: " + priorityLevel);
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
                                this.dueDateAndTime = dueDateTime;
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

    private void showToastMessage(String s) {
        Toast toast = Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}