package com.project.expensetrackerapi.repositories;

import com.project.expensetrackerapi.domain.User;
import com.project.expensetrackerapi.exceptions.EtAuthException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    // Uses RETURNING to reliably get the generated ID in PostgreSQL
    private static final String SQL_CREATE =
        "INSERT INTO ET_USERS(USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) " +
        "VALUES(NEXTVAL('et_users_seq'), ?, ?, ?, ?) RETURNING USER_ID";

    private static final String SQL_COUNT_BY_EMAIL =
        "SELECT COUNT(*) FROM ET_USERS WHERE EMAIL = ?";

    private static final String SQL_FIND_BY_ID =
        "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD FROM ET_USERS WHERE USER_ID = ?";

    private static final String SQL_FIND_BY_EMAIL =
        "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD FROM ET_USERS WHERE EMAIL = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer create(String firstName, String lastName, String email, String password)
            throws EtAuthException {
        try {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
            // queryForObject with RETURNING is the most reliable way to get a generated ID in PostgreSQL
            Integer userId = jdbcTemplate.queryForObject(
                SQL_CREATE, Integer.class,
                firstName, lastName, email.toLowerCase(), hashedPassword
            );
            if (userId == null) {
                throw new EtAuthException("Failed to retrieve generated user ID after insert");
            }
            return userId;
        } catch (EtAuthException e) {
            throw e;
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("unique") ||
                e.getMessage() != null && e.getMessage().contains("duplicate")) {
                throw new EtAuthException("Email already in use");
            }
            throw new EtAuthException("Registration failed: " + e.getMessage());
        }
    }

    @Override
    public User findByEmailAndPassword(String email, String password) throws EtAuthException {
        try {
            User user = jdbcTemplate.queryForObject(SQL_FIND_BY_EMAIL, new Object[]{email}, userRowMapper);
            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                throw new EtAuthException("Invalid email/password");
            }
            return user;
        } catch (EtAuthException e) {
            throw e;
        } catch (EmptyResultDataAccessException e) {
            throw new EtAuthException("Invalid email/password");
        }
    }

    @Override
    public Integer getCountByEmail(String email) {
        return jdbcTemplate.queryForObject(SQL_COUNT_BY_EMAIL, new Object[]{email}, Integer.class);
    }

    @Override
    public User findById(Integer userId) {
        return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{userId}, userRowMapper);
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) ->
        new User(
            rs.getInt("USER_ID"),
            rs.getString("FIRST_NAME"),
            rs.getString("LAST_NAME"),
            rs.getString("EMAIL"),
            rs.getString("PASSWORD")
        );
}
