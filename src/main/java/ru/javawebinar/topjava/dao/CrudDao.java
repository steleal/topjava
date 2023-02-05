package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Identifiable;

import java.util.List;

public interface CrudDao<T extends Identifiable> {

    void add(T item);

    T getById(Integer id);

    void update(T item);

    void delete(Integer id);

    List<T> getAll();

    void clear();

    int size();
}
