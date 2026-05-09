package com.project.expensetrackerapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.expensetrackerapi.domain.Transaction;
import com.project.expensetrackerapi.exceptions.EtBadRequestException;
import com.project.expensetrackerapi.exceptions.EtResourceNotFoundException;
import com.project.expensetrackerapi.repositories.TransactionRepository;

import java.util.List;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    @Cacheable(value = "transactions", key = "#userId + '-' + #categoryId")
    public List<Transaction> fetchAllTransactions(Integer userId, Integer categoryId) {
        return transactionRepository.findAll(userId, categoryId);
    }

    @Override
    @Cacheable(value = "transaction", key = "#userId + '-' + #categoryId + '-' + #transactionId")
    public Transaction fetchTransactionById(Integer userId, Integer categoryId, Integer transactionId) throws EtResourceNotFoundException {
        return transactionRepository.findById(userId, categoryId, transactionId);
    }

    @Override
    @CacheEvict(value = {"transactions", "categories", "category"}, allEntries = true)
    public Transaction addTransaction(Integer userId, Integer categoryId, Double amount, String note, Long transactionDate) throws EtBadRequestException {
        int transactionId = transactionRepository.create(userId, categoryId, amount, note, transactionDate);
        return transactionRepository.findById(userId, categoryId, transactionId);
    }

    @Override
    @CacheEvict(value = {"transactions", "transaction", "categories", "category"}, allEntries = true)
    public void updateTransaction(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction) throws EtBadRequestException {
        transactionRepository.update(userId, categoryId, transactionId, transaction);
    }

    @Override
    @CacheEvict(value = {"transactions", "transaction", "categories", "category"}, allEntries = true)
    public void removeTransaction(Integer userId, Integer categoryId, Integer transactionId) throws EtResourceNotFoundException {
        transactionRepository.removeById(userId, categoryId, transactionId);
    }
}
