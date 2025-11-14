package org.example.taskmanager.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Task implements Serializable {
    private String title;
    private String description;
    private Priority priority;
    private Category category;
    private Status status;
    private boolean completed;
    private LocalDateTime creationDate;
    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Category { WORK, PERSONAL, STUDY, OTHER }
    public enum Status { TODO, IN_PROGRESS, DONE }

    public Task(String title, String description, Status status, Priority priority, Category category) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.completed = false;
        this.creationDate = LocalDateTime.now();
    }

    public Task() {
        if (this.creationDate == null) {
            this.creationDate = LocalDateTime.now();
        }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public LocalDateTime getCreationDate() { return creationDate; }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return title + " [" + priority + ", " + category + "]";
    }
}