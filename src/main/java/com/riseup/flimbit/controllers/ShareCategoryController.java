package com.riseup.flimbit.controllers;


import com.riseup.flimbit.entity.ShareCategory;
import com.riseup.flimbit.service.ShareCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/share-category")
@CrossOrigin(origins = "*")
public class ShareCategoryController {

    @Autowired
    private ShareCategoryService shareCategoryService;

    @PostMapping
    public ResponseEntity<ShareCategory> createCategory(@RequestBody ShareCategory category) {
        ShareCategory created = shareCategoryService.createCategory(category);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ShareCategory>> getAllCategories() {
        return ResponseEntity.ok(shareCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShareCategory> getCategoryById(@PathVariable int id) {
        ShareCategory category = shareCategoryService.getCategoryById(id);
        return category != null ? ResponseEntity.ok(category) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        shareCategoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
