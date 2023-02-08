package ru.javawebinar.topjava.dto;

import java.time.LocalDateTime;

public class MealEditTo {
    public static final MealEditTo EMPTY = new MealEditTo(null, null, null, 0);

    private final Integer id;

    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    public MealEditTo(Integer id, LocalDateTime dateTime, String description, int calories) {
        this.id = id;
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    @Override
    public String toString() {
        return "MealEditTo{" +
                "dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
