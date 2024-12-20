package com.example.testnavigation;

public class Task {
    private long id;
    private String title;
    private String description;
    private String dueDate;
    private int priority;
    private boolean canEdit;
    private boolean canDelete;
    private boolean isCompleted;
    private String userEmail;

    public Task() {
    }

    public Task(long id, String title, String description, String dueDate, int priority, boolean canEdit, boolean canDelete, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.isCompleted = isCompleted;
    }

    public Task(long id, String title, String description, String dueDate, int priority, boolean canEdit, boolean canDelete, boolean isCompleted, String userEmail ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.isCompleted = isCompleted;
        this.userEmail = userEmail;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", priority=" + priority +
                ", canEdit=" + canEdit +
                ", canDelete=" + canDelete +
                ", isCompleted=" + isCompleted +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
