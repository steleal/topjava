package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        int userId = getAuthUserId();
        log.info("getAll for userId = {}", userId);
        return MealsUtil.getTos(service.getAll(userId), getUserCaloriesPerDay());
    }

    public Meal get(int id) {
        int userId = getAuthUserId();
        log.info("get meal with id = {}, userId = {}", id, userId);
        return service.get(id, userId);
    }

    public Meal create(Meal meal) {
        int userId = getAuthUserId();
        log.info("create {}, userId = {}", meal, userId);
        checkNew(meal);
        return service.create(meal, userId);
    }

    public void update(Meal meal, int mealId) {
        int userId = getAuthUserId();
        log.info("update {}, userId = {}", meal, userId);
        assureIdConsistent(meal, mealId);
        service.update(meal, userId);
    }

    public void delete(int id) {
        int userId = getAuthUserId();
        log.info("delete meal with id = {}, userId = {}", id, userId);
        service.delete(id, userId);
    }

    public Collection<MealTo> getFiltered(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        int userId = getAuthUserId();
        log.info("getFiltered for userId = {}, startDate = {}, endDate = {}, startTime = {}, endTime = {}",
                userId, startDate, endDate, startTime, endDate);
        Collection<Meal> filteredMeals = service.getFilteredByDate(startDate == null ? LocalDate.MIN : startDate,
                endDate == null ? LocalDate.now() : endDate,
                userId);
        return MealsUtil.getFilteredTos(filteredMeals, getUserCaloriesPerDay(),
                startTime == null ? LocalTime.MIN : startTime,
                endTime == null ? LocalTime.MAX : endTime);
    }

    private int getAuthUserId() {
        return SecurityUtil.authUserId();
    }

    private int getUserCaloriesPerDay() {
        return SecurityUtil.authUserCaloriesPerDay();
    }

}