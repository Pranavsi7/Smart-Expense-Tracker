package com.project.expensetrackerapi;

import com.project.expensetrackerapi.domain.Transaction;
import com.project.expensetrackerapi.exceptions.EtBadRequestException;
import com.project.expensetrackerapi.exceptions.EtResourceNotFoundException;
import com.project.expensetrackerapi.repositories.TransactionRepository;
import com.project.expensetrackerapi.services.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Tests")
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        sampleTransaction = new Transaction(1, 1, 1, 250.0, "Groceries", 1700000000000L);
    }

    @Test
    @DisplayName("fetchAllTransactions - returns transactions for user and category")
    void testFetchAllTransactions_ReturnsList() {
        List<Transaction> expected = Arrays.asList(sampleTransaction);
        when(transactionRepository.findAll(1, 1)).thenReturn(expected);

        List<Transaction> result = transactionService.fetchAllTransactions(1, 1);

        assertEquals(1, result.size());
        assertEquals(250.0, result.get(0).getAmount());
        verify(transactionRepository, times(1)).findAll(1, 1);
    }

    @Test
    @DisplayName("fetchAllTransactions - returns empty list for unknown category")
    void testFetchAllTransactions_ReturnsEmpty() {
        when(transactionRepository.findAll(1, 99)).thenReturn(List.of());

        List<Transaction> result = transactionService.fetchAllTransactions(1, 99);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("fetchTransactionById - returns correct transaction")
    void testFetchTransactionById_ReturnsTransaction() {
        when(transactionRepository.findById(1, 1, 1)).thenReturn(sampleTransaction);

        Transaction result = transactionService.fetchTransactionById(1, 1, 1);

        assertNotNull(result);
        assertEquals(1, result.getTransactionId());
        assertEquals("Groceries", result.getNote());
    }

    @Test
    @DisplayName("fetchTransactionById - throws exception when not found")
    void testFetchTransactionById_ThrowsNotFoundException() {
        when(transactionRepository.findById(1, 1, 999))
            .thenThrow(new EtResourceNotFoundException("Transaction not found"));

        assertThrows(EtResourceNotFoundException.class,
            () -> transactionService.fetchTransactionById(1, 1, 999));
    }

    @Test
    @DisplayName("addTransaction - creates and returns new transaction")
    void testAddTransaction_CreatesTransaction() {
        when(transactionRepository.create(1, 1, 500.0, "Dinner", 1700000000000L)).thenReturn(2);
        Transaction newTx = new Transaction(2, 1, 1, 500.0, "Dinner", 1700000000000L);
        when(transactionRepository.findById(1, 1, 2)).thenReturn(newTx);

        Transaction result = transactionService.addTransaction(1, 1, 500.0, "Dinner", 1700000000000L);

        assertNotNull(result);
        assertEquals(500.0, result.getAmount());
        assertEquals("Dinner", result.getNote());
    }

    @Test
    @DisplayName("addTransaction - throws exception on invalid data")
    void testAddTransaction_ThrowsBadRequestException() {
        when(transactionRepository.create(1, 1, -100.0, null, null))
            .thenThrow(new EtBadRequestException("Invalid request"));

        assertThrows(EtBadRequestException.class,
            () -> transactionService.addTransaction(1, 1, -100.0, null, null));
    }

    @Test
    @DisplayName("updateTransaction - calls repository update once")
    void testUpdateTransaction_Success() {
        doNothing().when(transactionRepository).update(1, 1, 1, sampleTransaction);

        assertDoesNotThrow(() -> transactionService.updateTransaction(1, 1, 1, sampleTransaction));
        verify(transactionRepository, times(1)).update(1, 1, 1, sampleTransaction);
    }

    @Test
    @DisplayName("removeTransaction - calls repository removeById")
    void testRemoveTransaction_Success() {
        doNothing().when(transactionRepository).removeById(1, 1, 1);

        assertDoesNotThrow(() -> transactionService.removeTransaction(1, 1, 1));
        verify(transactionRepository, times(1)).removeById(1, 1, 1);
    }

    @Test
    @DisplayName("removeTransaction - throws exception when transaction not found")
    void testRemoveTransaction_ThrowsNotFound() {
        doThrow(new EtResourceNotFoundException("Transaction not found"))
            .when(transactionRepository).removeById(1, 1, 999);

        assertThrows(EtResourceNotFoundException.class,
            () -> transactionService.removeTransaction(1, 1, 999));
    }
}
