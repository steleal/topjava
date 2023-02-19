package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Collections;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.JAN_30;
import static ru.javawebinar.topjava.MealTestData.JAN_31;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.getAdminMeals;
import static ru.javawebinar.topjava.MealTestData.getAllUserMeals;
import static ru.javawebinar.topjava.MealTestData.getJan30UserMeals;
import static ru.javawebinar.topjava.MealTestData.getNew;
import static ru.javawebinar.topjava.MealTestData.getUpdatedUserMeal0;
import static ru.javawebinar.topjava.MealTestData.userMeal0;
import static ru.javawebinar.topjava.MealTestData.userMeal1;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {
    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void createDuplicateDateTime() {
        Meal newMeal = new Meal(userMeal0);
        newMeal.setId(null);
        assertThrows(DataAccessException.class, () -> service.create(newMeal, USER_ID));
    }


    @Test
    public void get() {
        assertMatch(service.get(userMeal0.getId(), USER_ID), userMeal0);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(userMeal0.getId(), ADMIN_ID));
    }

    @Test
    public void update() {
        Meal updated = getUpdatedUserMeal0();
        service.update(updated, USER_ID);
        assertMatch(service.get(updated.getId(), USER_ID), getUpdatedUserMeal0());
    }

    @Test
    public void updateNotFound() {
        Meal updated = service.get(userMeal0.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void updateDuplicateDateTime() {
        Meal updated = service.get(userMeal0.getId(), USER_ID);
        updated.setDateTime(userMeal1.getDateTime());
        assertThrows(DataAccessException.class, () -> service.update(updated, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(userMeal0.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(userMeal0.getId(), USER_ID));
    }

    @Test
    public void deleteNotFound() {
        Meal existedUserMeal = service.get(userMeal1.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.delete(existedUserMeal.getId(), ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        assertMatch(service.getBetweenInclusive(JAN_30, JAN_30, USER_ID), getJan30UserMeals());
        assertMatch(service.getBetweenInclusive(JAN_30, JAN_30, ADMIN_ID), Collections.emptyList());
        assertMatch(service.getBetweenInclusive(JAN_31, JAN_31, ADMIN_ID), getAdminMeals());
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(USER_ID), getAllUserMeals());
        assertMatch(service.getAll(ADMIN_ID), getAdminMeals());
    }
}
