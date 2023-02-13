package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, meal.getUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUserId(userId);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }

        // update case
        if (get(meal.getId(), userId) == null) {
            return null;
        }
        return repository.computeIfPresent(meal.getId(), (k, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return get(id, userId) != null && repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        return (meal == null || meal.getUserId() != userId) ? null : meal;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getFilteredByPredicate(meal -> meal.getUserId() == userId);
    }

    @Override
    public List<Meal> getFilteredByDate(LocalDate start, LocalDate end, int userId) {
        LocalDateTime startDateTime = (start == null) ? LocalDateTime.MIN : start.atTime(LocalTime.MIN);
        LocalDateTime endDateTime = (end == null) ? LocalDateTime.MAX : end.plusDays(1).atTime(LocalTime.MIN);

        return getFilteredByPredicate(meal -> meal.getUserId() == userId
                && DateTimeUtil.isBetweenHalfOpen(meal.getDateTime(), startDateTime, endDateTime));
    }

    private List<Meal> getFilteredByPredicate(Predicate<Meal> predicate) {
        return repository.values().stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

