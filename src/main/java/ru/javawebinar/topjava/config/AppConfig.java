package ru.javawebinar.topjava.config;

import ru.javawebinar.topjava.dao.CrudDao;
import ru.javawebinar.topjava.dao.MemoryCrudDao;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class AppConfig {
    public static final int CALORIES_PER_DAY = 2000;
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private static final AppConfig INSTANCE = new AppConfig();

    private final DateTimeFormatter dateTimeFormatter;
    private final CrudDao<Meal> mealDao;

    private AppConfig() {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        this.mealDao = getInitedMealDao();
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    private static MemoryCrudDao<Meal> getInitedMealDao() {
        MemoryCrudDao<Meal> dao = new MemoryCrudDao<>();
        List<Meal> meals = Arrays.asList(
                new Meal(dao.nextId(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(dao.nextId(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(dao.nextId(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(dao.nextId(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(dao.nextId(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(dao.nextId(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(dao.nextId(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        meals.forEach(dao::add);
        return dao;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public CrudDao<Meal> getMealDao() {
        return mealDao;
    }
}
