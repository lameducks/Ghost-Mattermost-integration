package app.repository.db;

import app.entity.db.UserPreferences;
import app.util.JsonMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Repository
public class UsersRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JsonMapper jsonMapper;

    public Optional<UserPreferences.Value> getPreferences(final String userId) {
        String jsonPreferences = null;
        try {
            jsonPreferences = jdbcTemplate.queryForObject("SELECT value FROM users WHERE id=?", new Object[] {userId},
                    String.class);
        } catch (EmptyResultDataAccessException ignore) {
            // fall through
        }
        return Optional.ofNullable(
                jsonPreferences != null ? jsonMapper.readValue(jsonPreferences, UserPreferences.Value.class) : null);
    }

    public List<String> getUserIds(final Predicate<UserPreferences.Value> filter) {
        return jdbcTemplate.query("SELECT id, value FROM users", (ResultSet rs) -> {
            final var userIds = new ArrayList<String>();
            while (rs.next()) {
                final String jsonPreferences = rs.getString(2);
                final UserPreferences.Value preferences = jsonMapper.readValue(jsonPreferences,
                        UserPreferences.Value.class);
                if (filter.test(preferences)) {
                    final String userId = rs.getString(1);
                    userIds.add(userId);
                }
            }
            return userIds;
        });
    }

    public void upsertPreferences(final String userId, final UserPreferences.Value preferences) {
        final var jsonPreferences = jsonMapper.writeValueAsString(preferences);
        jdbcTemplate.update("INSERT INTO users VALUES (?, ?) ON DUPLICATE KEY UPDATE value=?", userId, jsonPreferences,
                jsonPreferences);
    }

}