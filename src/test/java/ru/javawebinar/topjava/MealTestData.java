package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int BASE_ID = START_SEQ + 3;
    public static final LocalDate JAN_30 = LocalDate.of(2020, 1, 30);
    public static final LocalDate JAN_31 = LocalDate.of(2020, 1, 31);

    public static final Meal userMeal0 = new Meal(BASE_ID, JAN_30.atTime(10, 0), "Завтрак", 500);
    public static final Meal userMeal1 = new Meal(BASE_ID + 1, JAN_30.atTime(13, 0), "Обед", 1000);
    public static final Meal userMeal2 = new Meal(BASE_ID + 2, JAN_30.atTime(20, 0), "Ужин", 500);
    public static final Meal userMeal3 = new Meal(BASE_ID + 3, JAN_31.atTime(0, 0), "Еда на граничное значение", 100);
    public static final Meal userMeal4 = new Meal(BASE_ID + 4, JAN_31.atTime(10, 0), "Завтрак", 500);
    public static final Meal userMeal5 = new Meal(BASE_ID + 5, JAN_31.atTime(13, 0), "Обед", 1000);
    public static final Meal userMeal6 = new Meal(BASE_ID + 6, JAN_31.atTime(20, 0), "Ужин", 410);
    public static final Meal adminMeal0 = new Meal(BASE_ID + 7, JAN_31.atTime(10, 0), "Завтрак Админа", 512);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2023, 1, 1, 13, 0), "Обед Админа", 1024);
    }

    public static Meal getUpdatedUserMeal0() {
        Meal meal = new Meal(userMeal0);
        meal.setDateTime(LocalDateTime.of(2020, 1, 30, 10, 1));
        meal.setDescription("Завтрак upd");
        meal.setCalories(501);
        return meal;
    }

    public static List<Meal> getAllUserMeals() {
        return Arrays.asList(userMeal6, userMeal5, userMeal4, userMeal3, userMeal2, userMeal1, userMeal0);
    }

    public static List<Meal> getJan30UserMeals() {
        return Arrays.asList(userMeal2, userMeal1, userMeal0);
    }

    public static List<Meal> getAdminMeals() {
        return Collections.singletonList(adminMeal0);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
