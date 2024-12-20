package com.example.testnavigation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user(EMAIL_ADDRESS TEXT PRIMARY KEY, FIRST_NAME TEXT, LAST_NAME TEXT, PASSWORD TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + // add foreign key of user ********************
                "task_title TEXT, " +
                "task_description TEXT, " +
                "due_date TEXT NOT NULL, " + // Store date as String in ISO format (e.g., "2024-11-01 10:00")
                "priority INTEGER, " + // "0 -> High", "1 -> Medium", "2 -> Low"
                "can_edit INTEGER, " + // 0 for false, 1 for true
                "can_delete INTEGER, " + // 0 for false, 1 for true
                "set_reminder INTEGER, " + // 0 for false, 1 for true
                "completion_status INTEGER, " +// 0 for incomplete, 1 for complete
                "user_email TEXT, " +
                "FOREIGN KEY (user_email) REFERENCES user(EMAIL_ADDRESS) ON DELETE CASCADE ON UPDATE CASCADE" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop the old tables if they exist
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS user");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(sqLiteDatabase);
    }

    // ------------------------- User Methods -------------------------

    public void insertUser(User user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL_ADDRESS", user.getEmailAddress());
        contentValues.put("FIRST_NAME", user.getFirstName());
        contentValues.put("LAST_NAME", user.getLastName());
        contentValues.put("PASSWORD", user.getPassword());
        sqLiteDatabase.insert("user", null, contentValues);

//         Delete
//        sqLiteDatabase.execSQL("DELETE FROM user");
    }

    public Cursor getAllUsers() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM user", null);
    }

    public Cursor getUser(String emailAddress) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM user WHERE EMAIL_ADDRESS = ?", new String[]{emailAddress});
    }

    public boolean checkUserExists(String emailAddress) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM user WHERE EMAIL_ADDRESS = ?", new String[]{emailAddress});
        return cursor.getCount() > 0;
    }

    public void updateUser(String id, User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL_ADDRESS", user.getEmailAddress());
        contentValues.put("PASSWORD", user.getPassword());
        db.update("user", contentValues, "EMAIL_ADDRESS = ?", new String[]{id});
        db.close();
    }

    // ------------------------- Task Methods -------------------------

    public void deleteTable(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        db.close();
    }

    // Insert task
    public void insertTask(String task_title, String task_description, String due_date, int priority,
                           boolean can_edit, boolean can_delete,
                           boolean set_reminder, boolean completion_status, String user_email) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task_title", task_title);
        contentValues.put("task_description", task_description);
        contentValues.put("due_date", due_date);
        contentValues.put("priority", priority);
        contentValues.put("can_edit", can_edit ? 1 : 0);
        contentValues.put("can_delete", can_delete ? 1 : 0);
        contentValues.put("set_reminder", set_reminder ? 1 : 0);
        contentValues.put("completion_status", completion_status ? 1 : 0);
        contentValues.put("user_email", user_email);
        long id = db.insert("tasks", null, contentValues);

        if (id != -1) {
            // Task successfully inserted
            new Task(id, task_title, task_description, due_date, priority, can_edit, can_delete, set_reminder, completion_status, user_email);
        } else {
            // Handle insertion failure (e.g., foreign key constraint violation)
            Log.e("DB_ERROR", "Failed to insert task. Check foreign key constraint.");
        }
//        db.close();
//        db.execSQL("DELETE FROM tasks");
    }

    public Cursor getTaskByIdForUser(long taskId, String userEmail) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM tasks WHERE id = ? AND user_email = ?",
                new String[]{String.valueOf(taskId), userEmail});
    }


    // Get tasks for today
    public Cursor getTodayTasks(String userEmail) { // get tasks for specific user
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM tasks WHERE DATE(due_date) = DATE('now', 'localtime') and user_email = ?", new String[]{userEmail});
    }

    // Get all tasks
    public Cursor getAllTasks(String userEmail) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM tasks WHERE user_email = ? ORDER BY DATE(due_date) ASC;", new String[]{userEmail});
    }

    // Get all completed tasks
    public Cursor getCompletedTasks(String userEmail) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM tasks WHERE completion_status = 1 and user_email = ? ORDER BY DATE(due_date) ASC;", new String[]{userEmail});
    }

    public Cursor getTasksWithinDateRange(String startDate, String endDate, String userEmail) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM tasks WHERE DATE(due_date) BETWEEN ? AND ? and user_email = ? ORDER BY DATE(due_date) ASC";
        return db.rawQuery(query, new String[]{startDate, endDate, userEmail});
    }


    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task_title", task.getTitle());
        contentValues.put("task_description", task.getDescription());
        contentValues.put("due_date", task.getDueDate());
        contentValues.put("priority", task.getPriority());
        contentValues.put("can_edit", task.isCanEdit() ? 1 : 0);
        contentValues.put("can_delete", task.isCanDelete() ? 1 : 0);
        contentValues.put("set_reminder", task.isSetReminder() ? 1 : 0);
        contentValues.put("completion_status", task.isCompleted() ? 1 : 0);

        // Update the task with the given ID
        db.update("tasks", contentValues, "id = ?", new String[]{String.valueOf(task.getId())});
    }

//    // Get completed tasks
//    public Cursor getTasksByPriority(String priority) {
//        SQLiteDatabase db = getReadableDatabase();
//        return db.rawQuery("SELECT * FROM tasks WHERE priority = ?", new String[]{priority});
//    }


    // Get tasks by completion status
    public Cursor getTasksByCompletionStatus(boolean completed) {
        SQLiteDatabase db = getReadableDatabase();
        int status = completed ? 1 : 0;
        return db.rawQuery("SELECT * FROM tasks WHERE completion_status = ?", new String[]{String.valueOf(status)});
    }

    // Update task completion status
    public void updateTaskCompletionStatus(long taskId, boolean completed) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("completion_status", completed ? 1 : 0);
        db.update("tasks", contentValues, "id = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }


    // Delete task
    public void deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tasks", "id = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }
}
