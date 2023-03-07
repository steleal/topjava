package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;

import static ru.javawebinar.topjava.util.DateTimeUtil.toLocalDateTime;

@Repository
@Profile(Profiles.HSQL_DB)
public class JdbcHsqlDbMealRepository extends JdbcMealRepository {

    private static final RowMapper<Meal> ROW_MAPPER = (rs, rowNum) ->
            new Meal(rs.getInt("ID"),
                    toLocalDateTime(rs.getTimestamp("DATE_TIME")),
                    rs.getString("DESCRIPTION"),
                    rs.getInt("CALORIES"));

    public JdbcHsqlDbMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    protected RowMapper<Meal> getRowMapper() {
        return ROW_MAPPER;
    }
}
