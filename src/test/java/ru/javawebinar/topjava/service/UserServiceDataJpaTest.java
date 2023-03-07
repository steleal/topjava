package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;

import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;

@ActiveProfiles("datajpa")
public class UserServiceDataJpaTest extends UserServiceTest {

    @Test
    public void getWithMeal() {
        User expected = UserTestData.getUserWithMeal();
        User user = service.getWithMeal(USER_ID);
        USER_MATCHER.assertMatch(user, expected);
        MEAL_MATCHER.assertMatch(user.getMeals(), expected.getMeals());
    }
}
