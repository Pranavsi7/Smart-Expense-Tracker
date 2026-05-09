package com.project.expensetrackerapi.repositories;

import java.util.List;

import com.project.expensetrackerapi.domain.Category;
import com.project.expensetrackerapi.exceptions.EtBadRequestException;
import com.project.expensetrackerapi.exceptions.EtResourceNotFoundException;

public interface CategoryRepository {
	  List<Category> findAll(Integer userId) throws EtResourceNotFoundException;

	    Category findById(Integer userId, Integer categoryId) throws EtResourceNotFoundException;

	    Integer create(Integer userId, String title, String description) throws EtBadRequestException;

	    void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException;

	    void removeById(Integer userId, Integer categoryId);

}
