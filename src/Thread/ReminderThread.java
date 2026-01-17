package Thread;

import model.Task;
import Service.TaskService;
import Util.DateUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * ReminderThread Class
 * Background thread that continuously monitors tasks
 * Prints reminder messages for overdue and due tasks
 * Demonstrates multithreading in Java
 */
public class ReminderThread extends Thread {

    // Reference to TaskService to access tasks
    private final TaskService taskService;

    // Flag to control thread execution
    private volatile boolean running;

    // Time interval between checks (in milliseconds)
    // 30000 ms = 30 seconds
    private static final long CHECK_INTERVAL = 30000;

    /**
     * Constructor
     * @param taskService - service to access task data
     */
    public ReminderThread(TaskService taskService) {
        this.taskService = taskService;
        this.running = true;

        // Set as daemon thread (won't prevent JVM shutdown)
        setDaemon(true);

        // Give thread a meaningful name
        setName("ReminderThread");
    }

    /**
     * Main thread execution method
     * Runs continuously until stopped
     */
    @Override
    public void run() {

        System.out.println("\n[REMINDER THREAD] Started monitoring tasks...\n");

        // Keep running until stopped
        while (running) {

            try {
                // Check for tasks needing reminders
                checkAndRemind();

                // Sleep for the specified interval
                Thread.sleep(CHECK_INTERVAL);

            } catch (InterruptedException e) {
                // Thread was interrupted - exit gracefully
                System.out.println("\n[REMINDER THREAD] Interrupted. Stopping...\n");
                running = false;

                // Restore interrupt status
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("[REMINDER THREAD] Stopped.\n");
    }

    /**
     * Check for tasks that need reminders and print alerts
     */
    private void checkAndRemind() {

        // Get all tasks that are due or overdue
        List<Task> tasksNeedingReminder = taskService.getTasksNeedingReminder();

        // If there are tasks to remind about
        if (!tasksNeedingReminder.isEmpty()) {

            printReminderHeader();

            for (Task task : tasksNeedingReminder) {
                printTaskReminder(task);
            }

            printReminderFooter();
        }
    }

    /**
     * Print reminder header
     */
    private void printReminderHeader() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              ⚠️  TASK REMINDER ALERT  ⚠️                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    /**
     * Print individual task reminder
     * @param task - task to remind about
     */
    private void printTaskReminder(Task task) {

        LocalDate dueDate = task.getDueDate();
        long daysOverdue = -DateUtil.daysUntil(dueDate);

        System.out.println("\n┌────────────────────────────────────────────────────────────┐");
        System.out.printf("│ Task ID    : %-45d │%n", task.getTaskId());
        System.out.printf("│ Title      : %-45s │%n", task.getTitle());
        System.out.printf("│ Priority   : %-45s │%n", task.getPriority());
        System.out.printf("│ Due Date   : %-45s │%n",
                DateUtil.formatDateForDisplay(dueDate));

        // Different message for overdue vs due today
        if (DateUtil.isToday(dueDate)) {
            System.out.printf("│ Status     : %-45s │%n", "⏰ DUE TODAY!");
        } else {
            System.out.printf("│ Status     : %-45s │%n",
                    "🔴 OVERDUE by " + daysOverdue + " day(s)");
        }

        System.out.println("└────────────────────────────────────────────────────────────┘");
    }

    /**
     * Print reminder footer
     */
    private void printReminderFooter() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  Please complete these tasks as soon as possible!          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }

    /**
     * Stop the reminder thread gracefully
     */
    public void stopReminder() {
        this.running = false;

        // Interrupt the thread if it's sleeping
        this.interrupt();
    }

    /**
     * Check if thread is currently running
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Get the check interval in seconds
     * @return interval in seconds
     */
    public static long getCheckIntervalSeconds() {
        return CHECK_INTERVAL / 1000;
    }
}
