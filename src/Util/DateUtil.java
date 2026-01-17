package Util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * DateUtil Class
 * Utility class for date-related operations
 * Uses Java 8+ Time API (LocalDate, DateTimeFormatter)
 * Provides date parsing, formatting, and calculation methods
 */
public class DateUtil {

    // Standard date format: yyyy-MM-dd (e.g., 2026-01-20)
    private static final DateTimeFormatter STANDARD_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // User-friendly date format: dd-MM-yyyy (e.g., 20-01-2026)
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Parse a date string in yyyy-MM-dd format
     * @param dateStr - date string to parse
     * @return LocalDate object
     * @throws IllegalArgumentException if format is invalid
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, STANDARD_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format! Please use yyyy-MM-dd (e.g., 2026-01-20)");
        }
    }

    /**
     * Parse a date string in dd-MM-yyyy format (user-friendly)
     * @param dateStr - date string to parse
     * @return LocalDate object
     * @throws IllegalArgumentException if format is invalid
     */
    public static LocalDate parseDateUserFormat(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DISPLAY_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format! Please use dd-MM-yyyy (e.g., 20-01-2026)");
        }
    }

    /**
     * Format a LocalDate to standard string format (yyyy-MM-dd)
     * @param date - LocalDate object
     * @return formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(STANDARD_FORMATTER);
    }

    /**
     * Format a LocalDate to user-friendly format (dd-MM-yyyy)
     * @param date - LocalDate object
     * @return formatted date string
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }

    /**
     * Calculate days between two dates
     * @param startDate - starting date
     * @param endDate - ending date
     * @return number of days (positive if endDate is after startDate)
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Calculate days until a future date from today
     * @param futureDate - target date
     * @return number of days remaining (negative if date is in past)
     */
    public static long daysUntil(LocalDate futureDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), futureDate);
    }

    /**
     * Check if a date is in the past
     * @param date - date to check
     * @return true if date is before today
     */
    public static boolean isInPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Check if a date is today
     * @param date - date to check
     * @return true if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    /**
     * Check if a date is in the future
     * @param date - date to check
     * @return true if date is after today
     */
    public static boolean isInFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }

    /**
     * Check if a date is overdue
     * Same as isInPast but semantically clearer for tasks
     * @param dueDate - due date to check
     * @return true if due date has passed
     */
    public static boolean isOverdue(LocalDate dueDate) {
        return dueDate.isBefore(LocalDate.now());
    }

    /**
     * Get a user-friendly description of when a date is
     * Examples: "Today", "Tomorrow", "In 3 days", "2 days ago"
     * @param date - date to describe
     * @return human-readable description
     */
    public static String getDateDescription(LocalDate date) {
        long days = daysUntil(date);

        if (days == 0) {
            return "Today";
        } else if (days == 1) {
            return "Tomorrow";
        } else if (days == -1) {
            return "Yesterday";
        } else if (days > 1) {
            return "In " + days + " days";
        } else {
            return Math.abs(days) + " days ago";
        }
    }

    /**
     * Get current date
     * @return today's date
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * Validate if a date string is in correct format
     * @param dateStr - date string to validate
     * @param userFormat - true for dd-MM-yyyy, false for yyyy-MM-dd
     * @return true if valid format, false otherwise
     */
    public static boolean isValidDateFormat(String dateStr, boolean userFormat) {
        try {
            if (userFormat) {
                LocalDate.parse(dateStr, DISPLAY_FORMATTER);
            } else {
                LocalDate.parse(dateStr, STANDARD_FORMATTER);
            }
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}