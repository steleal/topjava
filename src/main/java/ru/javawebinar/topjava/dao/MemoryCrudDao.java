package ru.javawebinar.topjava.dao;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class MemoryCrudDao<T extends Identifiable> implements CrudDao<T>, Sequence {
    private static final Logger log = getLogger(MemoryCrudDao.class);

    private final Map<Integer, T> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger();

    @Override
    public void add(T item) {
        log.info("add");
        if (storage.containsKey(item.getId())) {
            throw new RuntimeException("Add error, the key exists " + item.getId());
        }
        storage.put(item.getId(), item);
    }

    @Override
    public T getById(Integer id) {
        log.info("getById");
        return storage.get(id);
    }

    @Override
    public void update(T item) {
        log.info("update");
        if (!storage.containsKey(item.getId())) {
            throw new RuntimeException("Update error, the key don't exists " + item.getId());
        }
        storage.put(item.getId(), item);
    }

    @Override
    public void delete(Integer id) {
        log.info("delete");
        storage.remove(id);
    }

    @Override
    public List<T> getAll() {
        log.info("getAll");
        return new ArrayList<>(storage.values());
    }

    @Override
    public void clear() {
        log.info("clear");
        storage.clear();
    }

    @Override
    public int size() {
        log.info("size");
        return storage.size();
    }

    @Override
    public synchronized int nextId() {
        log.info("next");
        int id = idSequence.addAndGet(1);
        while (storage.containsKey(id)) {
            id = idSequence.addAndGet(1);
        }
        return id;
    }
}
