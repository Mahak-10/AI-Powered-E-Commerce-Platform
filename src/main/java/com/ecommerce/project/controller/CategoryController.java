package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {



    @Autowired
    CategoryService service;

    //CRUD

    //GET ALL CATEGORIES
    @GetMapping("/api/public/categories")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> allCategories = service.getAllCategories();
        return new ResponseEntity<>(allCategories,HttpStatus.OK);
    }

    //ADD A CATEGORY
    @PostMapping("/api/public/categories")
    public ResponseEntity<String> addCategory(@RequestBody Category category){
        service.addCategory(category);
        return new ResponseEntity<>("Category created successfully",HttpStatus.OK);
    }

    //DELETE A CATEGORY
    @DeleteMapping("/api/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){

        try {
            String status = service.deleteCategory(categoryId);
            return new ResponseEntity<>(status, HttpStatus.OK);
           }
        catch(ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        }
    }

    //UPDATE CATEGORY
    @PutMapping("/api/admin/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@RequestBody Category category,@PathVariable Long categoryId){


        try{
             Category savedCategory=service.updateCategory(category,categoryId);
            return new ResponseEntity<>("Created with category id:"+savedCategory,HttpStatus.OK);
        }
        catch(ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        }

    }




}
