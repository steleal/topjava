package ru.javawebinar.topjava.dao;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class MealDao implements CrudDao<Meal> {
    private static final Logger log = getLogger(MealDao.class);

    private final Map<Integer, Meal> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger();

    @Override
    public Meal add(Meal m) {
        int id = idSequence.incrementAndGet();
        Meal meal = new Meal(id, m.getDateTime(), m.getDescription(), m.getCalories());
        log.debug("Save {}", meal);
        storage.put(id, meal);
        return meal;
    }

    @Override
    public Meal getById(int id) {
        log.debug("getById with id {}", id);
        return storage.get(id);
    }

    @Override
    public Meal update(Meal item) {
        log.debug("Update {}", item);
        storage.replace(item.getId(), item);
        return item;
    }

    @Override
    public void delete(int id) {
        log.debug("Delete {}", id);
        storage.remove(id);
    }

    @Override
    public List<Meal> getAll() {
        log.debug("getAll");
        return new ArrayList<>(storage.values());
    }
}
