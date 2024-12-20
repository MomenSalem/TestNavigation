package com.example.testnavigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TaskJsonParser {
    public static List<Task> getObjectFromJson(String json) {

        List<Task> tasks;
        try {
            JSONArray jsonArray = new JSONArray(json);
            tasks = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = new JSONObject();
                jsonObject = (JSONObject) jsonArray.get(i);
                Task task = new Task();

                task.setTitle(jsonObject.getString("task_title"));
                task.setDescription(jsonObject.getString("task_description"));
                task.setDueDate(jsonObject.getString("due_date_time"));
                String priority = jsonObject.getString("priority_level");

                switch (priority) {
                    case "High":
                        task.setPriority(0);
                        break;
                    case "Medium":
                        task.setPriority(1);
                        break;
                    case "Low":
                        task.setPriority(2);
                        break;
                }

                task.setCanEdit(jsonObject.getBoolean("completion_status"));
                task.setCanEdit(jsonObject.getBoolean("edit"));
                task.setCanDelete(jsonObject.getBoolean("delete"));
                task.setUserEmail(jsonObject.getString("email_address"));

                tasks.add(task);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return tasks;
    }
} 