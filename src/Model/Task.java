package model;

import java.time.LocalDate;

/**
 * Task Model Class
 * Represents a single task with all its properties
 * This is a POJO (Plain Old Java Object) with proper encapsulation
 */
public class Task {

    // Instance variables - private for encapsulation
    private int taskId;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private Status status;

    // Enums for Priority and Status to restrict values (Type Safety)
    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    public enum Status {
        PENDING, COMPLETED
    }

    /**
     * Parameterized Constructor
     * Used when creating a new task
     */
    public Task(int taskId, String title, String description,
                Priority priority, LocalDate dueDate, Status status) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
    }

    /**
     * Default Constructor
     * Required for certain operations
     */
    public Task() {
        this.status = Status.PENDING; // Default status
    }

    // Getter and Setter methods for all fields

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * toString() method for easy printing
     * Overriding Object class method
     * @return formatted string representation of task
     */
    @Override
    public String toString() {
        return String.format(
                "┌─────────────────────────────────────────────────┐\n" +
                        "│ Task ID      : %-32d │\n" +
                        "│ Title        : %-32s │\n" +
                        "│ Description  : %-32s │\n" +
                        "│ Priority     : %-32s │\n" +
                        "│ Due Date     : %-32s │\n" +
                        "│ Status       : %-32s │\n" +
                        "└─────────────────────────────────────────────────┘",
                taskId, title, description, priority, dueDate, status
        );
    }

    /**
     * Method to convert task object to CSV format for file storage
     * Format: taskId,title,description,priority,dueDate,status
     * @return CSV string representation
     */
    public String toCSV() {
        return taskId + "," + title + "," + description + "," +
                priority + "," + dueDate + "," + status;
    }

    /**
     * Static method to create Task object from CSV string
     * Used when reading from file
     * @param csvLine - CSV formatted string
     * @return Task object
     */
    public static Task fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");

        int id = Integer.parseInt(parts[0]);
        String title = parts[1];
        String desc = parts[2];
        Priority priority = Priority.valueOf(parts[3]);
        LocalDate dueDate = LocalDate.parse(parts[4]);
        Status status = Status.valueOf(parts[5]);

        return new Task(id, title, desc, priority, dueDate, status);
    }
}