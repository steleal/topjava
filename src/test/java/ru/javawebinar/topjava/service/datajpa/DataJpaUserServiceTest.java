package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserServiceTest;

import java.util.Collection;
import java.util.Collections;

import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.UserTestData.GUEST_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest {

    @Test
    public void getWithMeal() {
        User expected = UserTestData.getUserWithMeal();
        User user = service.getWithMeal(USER_ID);
        USER_MATCHER.assertMatch(user, expected);
        MEAL_MATCHER.assertMatch(user.getMeals(), expected.getMeals());
    }

    @Test
    public void getWithMealForUserWithoutMeal(){
        User expected = UserTestData.guest;
        User user = service.getWithMeal(GUEST_ID);
        USER_MATCHER.assertMatch(user, expected);
        MEAL_MATCHER.assertMatch(user.getMeals(), Collections.emptyList());
    }
}
