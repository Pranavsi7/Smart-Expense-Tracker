package com.project.expensetrackerapi;

import com.project.expensetrackerapi.domain.Category;
import com.project.expensetrackerapi.exceptions.EtBadRequestException;
import com.project.expensetrackerapi.exceptions.EtResourceNotFoundException;
import com.project.expensetrackerapi.repositories.CategoryRepository;
import com.project.expensetrackerapi.services.CategoryServiceImpl;
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
@DisplayName("CategoryService Unit Tests")
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category(1, 1, "Food", "Food expenses", 500.0);
    }

    @Test
    @DisplayName("fetchAllCategories - returns list of categories for user")
    void testFetchAllCategories_ReturnsCategories() {
        List<Category> expected = Arrays.asList(sampleCategory);
        when(categoryRepository.findAll(1)).thenReturn(expected);

        List<Category> result = categoryService.fetchAllCategories(1);

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getTitle());
        verify(categoryRepository, times(1)).findAll(1);
    }

    @Test
    @DisplayName("fetchAllCategories - returns empty list when no categories exist")
    void testFetchAllCategories_ReturnsEmptyList() {
        when(categoryRepository.findAll(99)).thenReturn(List.of());

        List<Category> result = categoryService.fetchAllCategories(99);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("fetchCategoryById - returns correct category")
    void testFetchCategoryById_ReturnsCategory() {
        when(categoryRepository.findById(1, 1)).thenReturn(sampleCategory);

        Category result = categoryService.fetchCategoryById(1, 1);

        assertNotNull(result);
        assertEquals(1, result.getCategoryId());
        assertEquals("Food", result.getTitle());
    }

    @Test
    @DisplayName("fetchCategoryById - throws exception when not found")
    void testFetchCategoryById_ThrowsNotFoundException() {
        when(categoryRepository.findById(1, 999)).thenThrow(new EtResourceNotFoundException("Category not found"));

        assertThrows(EtResourceNotFoundException.class,
            () -> categoryService.fetchCategoryById(1, 999));
    }

    @Test
    @DisplayName("addCategory - creates and returns new category")
    void testAddCategory_CreatesCategory() {
        when(categoryRepository.create(1, "Travel", "Travel expenses")).thenReturn(2);
        Category newCat = new Category(2, 1, "Travel", "Travel expenses", 0.0);
        when(categoryRepository.findById(1, 2)).thenReturn(newCat);

        Category result = categoryService.addCategory(1, "Travel", "Travel expenses");

        assertNotNull(result);
        assertEquals("Travel", result.getTitle());
        verify(categoryRepository).create(1, "Travel", "Travel expenses");
    }

    @Test
    @DisplayName("addCategory - throws exception on bad input")
    void testAddCategory_ThrowsBadRequestException() {
        when(categoryRepository.create(1, null, "desc")).thenThrow(new EtBadRequestException("Invalid request"));

        assertThrows(EtBadRequestException.class,
            () -> categoryService.addCategory(1, null, "desc"));
    }

    @Test
    @DisplayName("updateCategory - calls repository update")
    void testUpdateCategory_CallsRepositoryUpdate() {
        doNothing().when(categoryRepository).update(1, 1, sampleCategory);

        assertDoesNotThrow(() -> categoryService.updateCategory(1, 1, sampleCategory));
        verify(categoryRepository, times(1)).update(1, 1, sampleCategory);
    }

    @Test
    @DisplayName("removeCategoryWithAllTransactions - removes when category exists")
    void testRemoveCategoryWithAllTransactions_Success() {
        when(categoryRepository.findById(1, 1)).thenReturn(sampleCategory);
        doNothing().when(categoryRepository).removeById(1, 1);

        assertDoesNotThrow(() -> categoryService.removeCategoryWithAllTransactions(1, 1));
        verify(categoryRepository).removeById(1, 1);
    }

    @Test
    @DisplayName("removeCategoryWithAllTransactions - throws when category not found")
    void testRemoveCategoryWithAllTransactions_ThrowsNotFound() {
        when(categoryRepository.findById(1, 999)).thenThrow(new EtResourceNotFoundException("Category not found"));

        assertThrows(EtResourceNotFoundException.class,
            () -> categoryService.removeCategoryWithAllTransactions(1, 999));
        verify(categoryRepository, never()).removeById(anyInt(), anyInt());
    }
}
