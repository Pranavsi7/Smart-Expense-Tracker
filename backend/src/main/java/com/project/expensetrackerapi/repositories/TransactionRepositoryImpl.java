package com.project.expensetrackerapi.repositories;

import com.project.expensetrackerapi.domain.Transaction;
import com.project.expensetrackerapi.exceptions.EtBadRequestException;
import com.project.expensetrackerapi.exceptions.EtResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final String SQL_FIND_ALL =
        "SELECT TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE " +
        "FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ?";

    private static final String SQL_FIND_BY_ID =
        "SELECT TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE " +
        "FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID = ?";

    // RETURNING makes ID retrieval reliable in PostgreSQL
    private static final String SQL_CREATE =
        "INSERT INTO ET_TRANSACTIONS(TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE) " +
        "VALUES(NEXTVAL('et_transactions_seq'), ?, ?, ?, ?, ?) RETURNING TRANSACTION_ID";

    private static final String SQL_UPDATE =
        "UPDATE ET_TRANSACTIONS SET AMOUNT = ?, NOTE = ?, TRANSACTION_DATE = ? " +
        "WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID = ?";

    private static final String SQL_DELETE =
        "DELETE FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Transaction> findAll(Integer userId, Integer categoryId) {
        return jdbcTemplate.query(SQL_FIND_ALL, new Object[]{userId, categoryId}, transactionRowMapper);
    }

    @Override
    public Transaction findById(Integer userId, Integer categoryId, Integer transactionId)
            throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.queryForObject(
                SQL_FIND_BY_ID, new Object[]{userId, categoryId, transactionId}, transactionRowMapper
            );
        } catch (Exception e) {
            throw new EtResourceNotFoundException("Transaction not found");
        }
    }

    @Override
    public Integer create(Integer userId, Integer categoryId, Double amount, String note, Long transactionDate)
            throws EtBadRequestException {
        try {
            Integer transactionId = jdbcTemplate.queryForObject(
                SQL_CREATE, Integer.class, categoryId, userId, amount, note, transactionDate
            );
            if (transactionId == null) throw new EtBadRequestException("Failed to create transaction");
            return transactionId;
        } catch (EtBadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request: " + e.getMessage());
        }
    }

    @Override
    public void update(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction)
            throws EtBadRequestException {
        try {
            jdbcTemplate.update(SQL_UPDATE,
                transaction.getAmount(), transaction.getNote(), transaction.getTransactionDate(),
                userId, categoryId, transactionId
            );
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void removeById(Integer userId, Integer categoryId, Integer transactionId)
            throws EtResourceNotFoundException {
        int count = jdbcTemplate.update(SQL_DELETE, userId, categoryId, transactionId);
        if (count == 0) throw new EtResourceNotFoundException("Transaction not found");
    }

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) ->
        new Transaction(
            rs.getInt("TRANSACTION_ID"),
            rs.getInt("CATEGORY_ID"),
            rs.getInt("USER_ID"),
            rs.getDouble("AMOUNT"),
            rs.getString("NOTE"),
            rs.getLong("TRANSACTION_DATE")
        );
}
