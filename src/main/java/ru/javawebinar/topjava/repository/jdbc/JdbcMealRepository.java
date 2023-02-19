package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepository implements MealRepository {
    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    private final NamedParameterJdbcTemplate jdbc;

    @Autowired
    public JdbcMealRepository(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbc = namedJdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("user_id", userId)
                .addValue("date_time", meal.getDateTime())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories());

        if (meal.isNew()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(
                    "INSERT INTO meals (user_id, date_time, description, calories)\n" +
                            "VALUES (:user_id, :date_time, :description, :calories);",
                    map, keyHolder, new String[]{"id"});
            Number key = keyHolder.getKey();
            if (key == null) {
                return null;
            }
            meal.setId(key.intValue());
        } else if (jdbc.update(
                "UPDATE meals\n" +
                        "SET date_time = :date_time,\n" +
                        "    description = :description,\n" +
                        "    calories = :calories\n" +
                        "WHERE id = :id\n" +
                        "  AND user_id = :user_id",
                map) == 0) {
            return null;
        }
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("user_id", userId);
        return jdbc.update("DELETE FROM meals WHERE id = :id AND user_id = :user_id", map) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("user_id", userId);
        List<Meal> meals = jdbc.query("SELECT * FROM meals WHERE id = :id AND user_id = :user_id", map, ROW_MAPPER);
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("user_id", userId);
        return jdbc.query("SELECT * FROM meals WHERE user_id = :user_id ORDER BY date_time DESC", map, ROW_MAPPER);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("start_dt", startDateTime)
                .addValue("end_dt", endDateTime)
                .addValue("user_id", userId);
        return jdbc.query(
                "SELECT *\n" +
                        "FROM meals\n" +
                        "WHERE user_id = :user_id\n" +
                        "  AND date_time >= :start_dt\n" +
                        "  AND date_time < :end_dt\n" +
                        "ORDER BY date_time DESC", map, ROW_MAPPER);
    }
}
