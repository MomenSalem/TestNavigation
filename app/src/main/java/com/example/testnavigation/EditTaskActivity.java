package com.example.testnavigation;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.testnavigation.databinding.ActivityEditTaskBinding;

public class EditTaskActivity extends AppCompatActivity {
    private ActivityEditTaskBinding binding;
    SharedPrefManager sharedPrefManager;
    DataBaseHelper dataBaseHelper;
    private EditText titleEditText, descriptionEditText;
    private TextView dueDateTime;
    private RadioGroup priorityRadioGroup;
    private CheckBox reminderCheckBox, completionCheckBox, canEditCheckBox, canDeleteCheckBox;
    private Button dateButton, timeButton, saveButton, cancelButton;
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
        dateButton = binding.dateBtn;
        timeButton = binding.timeBtn;
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
            switch(priority){
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

            // Set reminder and completion status
            reminderCheckBox.setChecked(setReminder);
            completionCheckBox.setChecked(completionStatus);
            canEditCheckBox.setChecked(canEdit);
            canDeleteCheckBox.setChecked(canDelete);


            // Set up listeners
            saveButton.setOnClickListener(v -> saveTask());
            cancelButton.setOnClickListener(v -> finish());
        }
        else{
            // if there is an error close the activity
            finish();
        }
    }
    private void saveTask() {
        // Extract the text from the EditText fields
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String dueDT = dueDateTime.getText().toString();

        // Check if any fields are empty
        if (title.isEmpty() || description.isEmpty()) {
            // Create a toast message for the empty field(s)
            String message = "Please fill in all the fields:";
            if (title.isEmpty()) message += "\n- Title";
            if (description.isEmpty()) message += "\n- Description";

            // Show the toast with a red background
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.getView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
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
        Log.d("Momen", String.valueOf(task.getPriority()));
        task.setCanEdit(canEditCheckBox.isChecked());
        task.setCanDelete(canDeleteCheckBox.isChecked());
        task.setSetReminder(reminderCheckBox.isChecked());
        task.setCompleted(completionCheckBox.isChecked());

        // Open the database, update the task, and close the database
        try {
            dataBaseHelper = new DataBaseHelper(this, "final_project_db", null, 1);
            dataBaseHelper.updateTask(task);
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
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

}