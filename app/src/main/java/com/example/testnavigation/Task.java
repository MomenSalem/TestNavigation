package com.example.testnavigation;

public class Task {
    private long id;
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private boolean canEdit;
    private boolean canDelete;
    private boolean setReminder;
    private boolean isCompleted;

    public Task(long id, String title, String description, String dueDate, String priority, boolean canEdit, boolean canDelete, boolean setReminder, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.setReminder = setReminder;
        this.isCompleted = isCompleted;
    }

    // Getters and setters for the task properties

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isSetReminder() {
        return setReminder;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public void setSetReminder(boolean setReminder) {
        this.setReminder = setReminder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
