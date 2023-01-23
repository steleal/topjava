package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByStreamsOptional(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dayCaloriesSums = new HashMap<>();
        for (UserMeal meal : meals) {
            LocalDate date = meal.getDateTime().toLocalDate();
            dayCaloriesSums.compute(date,
                    (k, v) -> v == null
                            ? meal.getCalories()
                            : meal.getCalories() + v);
        }

        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            LocalTime time = meal.getDateTime().toLocalTime();
            if (TimeUtil.isBetweenHalfOpen(time, startTime, endTime)) {
                boolean excess = dayCaloriesSums.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
                result.add(convert(meal, excess));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dayCaloriesSums = meals.stream()
                .collect(Collectors.groupingBy(m -> m.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> convert(m, dayCaloriesSums.get(m.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreamsOptional(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(getUserMealWithExcessFilteredByTimeCollector(startTime, endTime, caloriesPerDay));
    }

    private static Collector<UserMeal, CollectionContainer, List<UserMealWithExcess>> getUserMealWithExcessFilteredByTimeCollector(
            LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return Collector.of(
                // supplier
                CollectionContainer::new,
                // accumulator
                (c, e) -> {
                    LocalTime time = e.getDateTime().toLocalTime();
                    if (TimeUtil.isBetweenHalfOpen(time, startTime, endTime)) {
                        c.filteredMeals.add(e);
                    }
                    LocalDate date = e.getDateTime().toLocalDate();
                    c.dayCaloriesSums.compute(date, (k, v) -> v == null ? e.getCalories() : e.getCalories() + v);
                },
                // combiner
                (c1, c2) -> {
                    c1.filteredMeals.addAll(c2.filteredMeals);
                    Map<LocalDate, Integer> mapSum1 = c1.dayCaloriesSums;
                    Map<LocalDate, Integer> mapSum2 = c2.dayCaloriesSums;
                    mapSum2.forEach((k2, v2) -> mapSum1.compute(k2, (k1, v1) -> (v1 == null) ? v2 : v2 + v1));
                    return c1;
                },
                // finisher
                c -> {
                    List<UserMealWithExcess> result = new ArrayList<>();
                    for (UserMeal meal : c.filteredMeals) {
                        boolean excess = c.dayCaloriesSums.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
                        result.add(convert(meal, excess));
                    }
                    return result;
                }
        );
    }

    private static UserMealWithExcess convert(UserMeal meal, boolean excess) {
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }

    private static class CollectionContainer {
        private final List<UserMeal> filteredMeals = new ArrayList<>();
        private final Map<LocalDate, Integer> dayCaloriesSums = new HashMap<>();
    }
}
