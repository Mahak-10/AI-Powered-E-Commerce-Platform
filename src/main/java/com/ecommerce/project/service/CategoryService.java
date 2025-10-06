package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CategoryService {


    String deleteCategory(@PathVariable Long categoryId);

    List<Category> getAllCategories();
    void addCategory(@RequestBody Category category);

    Category updateCategory(Category category,Long categoryId);
}
