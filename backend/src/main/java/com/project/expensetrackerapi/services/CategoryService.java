package com.project.expensetrackerapi.services;

import java.util.List;

import com.project.expensetrackerapi.domain.Category;
import com.project.expensetrackerapi.exceptions.EtBadRequestException;
import com.project.expensetrackerapi.exceptions.EtResourceNotFoundException;

public interface CategoryService {
	 List<Category> fetchAllCategories(Integer userId);

	    Category fetchCategoryById(Integer userId, Integer categoryId) throws EtResourceNotFoundException;

	    Category addCategory(Integer userId, String title, String description) throws EtBadRequestException;

	    void updateCategory(Integer userId, Integer categoryId, Category category) throws EtBadRequestException;

	    void removeCategoryWithAllTransactions(Integer userId, Integer categoryId) throws EtResourceNotFoundException;


}
