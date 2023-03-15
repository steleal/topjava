package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final ValidatorImpl validator;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate,
                              NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              ValidatorImpl validator) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.validator = validator;
    }

    @Override
    @Transactional
    public User save(User user) {
        validator.validate(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else {
            int updatedRowNumber = namedParameterJdbcTemplate.update("""
                       UPDATE users SET name=:name, email=:email, password=:password, 
                       registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                    """, parameterSource);
            if (updatedRowNumber == 0) {
                return null;
            }
            deleteRoles(user.getId());
        }
        insertRoles(user.getRoles(), user.getId());
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        users.forEach(user -> user.setRoles(getRoles(user.id())));
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        users.forEach(user -> user.setRoles(getRoles(user.id())));
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
        final Map<Integer, Set<Role>> userRoles = getAllRoles();
        users.forEach(u -> u.setRoles(userRoles.get(u.getId())));
        return users;
    }

    private void insertRoles(Set<Role> roles, Integer userId) {
        final List<MapSqlParameterSource> params = roles.stream()
                .map(role -> {
                    MapSqlParameterSource source = new MapSqlParameterSource();
                    source.addValue("role", role.name());
                    source.addValue("userId", userId);
                    return source;
                }).toList();
        namedParameterJdbcTemplate.batchUpdate("INSERT INTO user_role (user_id, role) VALUES (:userId, :role)",
                params.toArray(MapSqlParameterSource[]::new));
    }

    private void deleteRoles(int userId) {
        jdbcTemplate.update("DELETE FROM user_role WHERE user_id=?", userId);
    }

    private Map<Integer, Set<Role>> getAllRoles() {
        final Map<Integer, Set<Role>> userRoles = new HashMap<>();
        jdbcTemplate.query("SELECT role, user_id FROM user_role",
                rs -> {
                    userRoles.computeIfAbsent(rs.getInt("user_id"), k -> EnumSet.noneOf(Role.class))
                            .add(Role.valueOf(rs.getString("role")));
                });
        return userRoles;
    }

    private Set<Role> getRoles(int userId) {
        List<Role> roles = jdbcTemplate.query("SELECT role FROM user_role  WHERE user_id=?",
                (rs, rownum) -> Role.valueOf(rs.getString("role")), userId);
        return EnumSet.copyOf(roles);
    }
}
