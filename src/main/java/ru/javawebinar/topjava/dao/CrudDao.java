package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Identifiable;

import java.util.List;

public interface CrudDao<T extends Identifiable> {

    T add(T item);

    T getById(int id);

    T update(T item);

    void delete(int id);

    List<T> getAll();

}
