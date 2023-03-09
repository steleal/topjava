package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;

import java.time.LocalDateTime;
import java.util.Date;

import static ru.javawebinar.topjava.util.DateTimeUtil.toDateTime;

@Repository
@Profile(Profiles.HSQL_DB)
public class JdbcHsqlDbMealRepository extends JdbcMealRepository {
    public JdbcHsqlDbMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public Date toDbDateTime(LocalDateTime ldt) {
        return toDateTime(ldt);
    }
}
