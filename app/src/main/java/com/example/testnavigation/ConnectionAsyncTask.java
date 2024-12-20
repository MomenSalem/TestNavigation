package com.example.testnavigation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import java.util.List;

public class ConnectionAsyncTask extends AsyncTask<String, String, String> {
    private Context context;

    public ConnectionAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Show a Toast in the fragment's context
        if (context != null) {
            Toast.makeText(context, "Preparing to fetch data...", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected String doInBackground(String... params) {
        String data = HttpManager.getData(params[0]);
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        List<Task> tasks = TaskJsonParser.getObjectFromJson(s);

        // show all the tasks in alert dialog
        StringBuilder ds = new StringBuilder();
        for (Task task : tasks) {
            ds.append(task.getTitle()).append(" ");
        }

        // Show a Toast in the fragment's context
        if (context != null) {
            Toast.makeText(context, "Tasks fetched successfully!", Toast.LENGTH_SHORT).show();
        }

        // Save the tasks to the database
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, "final_project_db", null, 1);

        for (Task task : tasks) {
            Log.d("Task", task.toString());
            dataBaseHelper.insertTask(task.getTitle(), task.getDescription(), task.getDueDate(), task.getPriority(), task.isCanEdit(), task.isCanDelete(), task.isCompleted(), task.getUserEmail());
        }
    }
}