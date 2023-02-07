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
        log.debug("Save {}", item);
        if (storage.containsKey(item.getId())) {
            throw new RuntimeException("Add error, the key exists " + item.getId());
        }
        storage.put(item.getId(), item);
    }

    @Override
    public T getById(Integer id) {
        log.debug("getById with id {}", id);
        return storage.get(id);
    }

    @Override
    public void update(T item) {
        log.debug("Update {}", item);
        if (!storage.containsKey(item.getId())) {
            throw new RuntimeException("Update error, the key don't exists " + item.getId());
        }
        storage.put(item.getId(), item);
    }

    @Override
    public void delete(Integer id) {
        log.debug("Delete {}", id);
        storage.remove(id);
    }

    @Override
    public List<T> getAll() {
        log.debug("getAll");
        return new ArrayList<>(storage.values());
    }

    @Override
    public void clear() {
        log.debug("clear");
        storage.clear();
    }

    @Override
    public int size() {
        log.debug("size");
        return storage.size();
    }

    @Override
    public synchronized int nextId() {
        log.debug("nextId");
        int id = idSequence.addAndGet(1);
        while (storage.containsKey(id)) {
            id = idSequence.addAndGet(1);
        }
        log.debug("Return nextId {}", id);
        return id;
    }
}
