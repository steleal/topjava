package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Lock modify = new ReentrantLock();

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal == null || meal.getUserId() != userId) {
            return null;
        }
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }

        modify.lock();
        try {
            Meal om = get(meal.getId(), userId);
            if (om == null) {
                return null;
            }
            repository.put(meal.getId(), meal);
            return meal;
        } finally {
            modify.unlock();
        }
    }

    @Override
    public boolean delete(int id, int userId) {
        modify.lock();
        try {
            return get(id, userId) != null && repository.remove(id) != null;
        } finally {
            modify.unlock();
        }
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        return (meal == null || meal.getUserId() != userId) ? null : meal;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return getFilteredByPredicate(meal -> meal.getUserId() == userId);
    }

    @Override
    public Collection<Meal> getFilteredByDate(LocalDate start, LocalDate end, int userId) {
        LocalDateTime startDateTime = start.atTime(LocalTime.MIN);
        LocalDateTime endDateTime = end.plusDays(1).atTime(LocalTime.MIN);

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

