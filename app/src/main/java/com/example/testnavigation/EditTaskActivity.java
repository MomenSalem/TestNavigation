package com.example.testnavigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testnavigation.databinding.ActivityEditTaskBinding;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {
    private ActivityEditTaskBinding binding;
    SharedPrefManager sharedPrefManager;
    DataBaseHelper dataBaseHelper;
    private EditText titleEditText, descriptionEditText;
    private TextView dueDateTime;
    private RadioGroup priorityRadioGroup;
    private CheckBox reminderCheckBox, completionCheckBox, canEditCheckBox, canDeleteCheckBox;
    private Button dateTimeButton, saveButton, cancelButton;
    Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPrefManager = SharedPrefManager.getInstance(this);
        String userEmail = sharedPrefManager.readString("user_primary_key", "no way");


        // Initialize views
        titleEditText = binding.editTextTitle;
        descriptionEditText = binding.editTextDescription;
        dueDateTime = binding.TextDueDateTime;
        priorityRadioGroup = binding.priorityLevelRadioGroup;
        reminderCheckBox = binding.checkBoxRemider;
        completionCheckBox = binding.checkBoxCompleted;
        canEditCheckBox = binding.checkBoxCanEdit;
        canDeleteCheckBox = binding.checkBoxCanDelete;
        dateTimeButton = binding.dateTimeBtn;
        saveButton = binding.buttonSave;
        cancelButton = binding.buttonCancel;

        try {
            dataBaseHelper = new DataBaseHelper(this, "final_project_db", null, 1);
            // Use the database helper
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataBaseHelper != null) {
                // Assuming DataBaseHelper has a method to close the database or cleanup
                dataBaseHelper.close();
            }
        }

        // Get the task ID from the intent
        long taskId = getIntent().getLongExtra("task_id", -1);
        if (taskId == -1) {
            // Handle the case where the task ID is not provided
            finish();
            return;
        }

        // Load the task from the database
        Cursor cursor = dataBaseHelper.getTaskByIdForUser(taskId, userEmail);

        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            String taskTitle = cursor.getString(1);
            String taskDescription = cursor.getString(2);
            String dueDate = cursor.getString(3);
            int priority = cursor.getInt(4);
            boolean canEdit = cursor.getInt(5) == 1;
            boolean canDelete = cursor.getInt(6) == 1;
            boolean setReminder = cursor.getInt(7) == 1;
            boolean completionStatus = cursor.getInt(8) == 1;

            // Create a new Task object
            task = new Task(id, taskTitle, taskDescription, dueDate, priority, canEdit, canDelete, setReminder, completionStatus);

            // Populate the fields with task data
            titleEditText.setText(taskTitle);
            descriptionEditText.setText(taskDescription);
            dueDateTime.setText(dueDate);

            // Set priority level
            switch (priority) {
                case 0:
                    priorityRadioGroup.check(R.id.radioHigh);
                    break;
                case 1:
                    priorityRadioGroup.check(R.id.radioMedium);
                    break;
                case 2:
                    priorityRadioGroup.check(R.id.radioLow);
                    break;
            }
            dateTimeButton.setOnClickListener(v -> showDateTimePicker());

            // Set reminder and completion status
            reminderCheckBox.setChecked(setReminder);
            completionCheckBox.setChecked(completionStatus);
            canEditCheckBox.setChecked(canEdit);
            canDeleteCheckBox.setChecked(canDelete);

            // Set up listeners
            saveButton.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Edit Task (" + task.getTitle() + ")")
                            .setMessage("Are you sure you want to edit this task?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                saveTask();
                            })
                            .setNegativeButton("No", null)
                            .show());

            cancelButton.setOnClickListener(v -> finish());
        } else {
            // if there is an error close the activity
            finish();
        }
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();


        // DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    // After date is selected, show TimePickerDialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                // Combine date and time
                                String dueDateTimeNew = String.format("%04d-%02d-%02d %02d:%02d", year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);
                                dueDateTime.setText(dueDateTimeNew);
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

    private void saveTask() {
        // Extract the text from the EditText fields
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String dueDT = dueDateTime.getText().toString();

        if (title.isEmpty()) {
            titleEditText.setBackgroundColor(Color.RED);
        }
        if (description.isEmpty()) {
            descriptionEditText.setBackgroundColor(Color.RED);
        }
        // Check if any fields are empty
        if (title.isEmpty() || description.isEmpty()) {
            // Create a toast message for the empty field(s)
            String toastMessage = "Please fill in all the fields:";
            if (title.isEmpty()) toastMessage += "\n- Title";
            if (description.isEmpty()) toastMessage += "\n- Description";

            // Show the toast with a red background
            Toast toast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
            toast.show();
            return;  // Do not continue with the save
        }

        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDT);
        int selectedId = priorityRadioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.radioHigh) {
            task.setPriority(0);
        } else if (selectedId == R.id.radioMedium) {
            task.setPriority(1);
        } else if (selectedId == R.id.radioLow) {
            task.setPriority(2);
        }
        task.setCanEdit(canEditCheckBox.isChecked());
        task.setCanDelete(canDeleteCheckBox.isChecked());
        task.setSetReminder(reminderCheckBox.isChecked());
        task.setCompleted(completionCheckBox.isChecked());

        // Open the database, update the task, and close the database
        try {
            dataBaseHelper = new DataBaseHelper(this, "final_project_db", null, 1);
            dataBaseHelper.updateTask(task);
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            intent.putExtra("task_id", task.getId());
            setResult(Activity.RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
        } finally {
            if (dataBaseHelper != null) {
                dataBaseHelper.close();
            }
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // suppress lint warning
        // noinspection SuperCallOnBackPressed
        // Do nothing.
        // not return to the previous activity using the back button in phone
    }

}