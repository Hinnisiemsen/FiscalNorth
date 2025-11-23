package de.fiscalnorth.category.controller;

import de.fiscalnorth.category.dto.CreateCategoryRequest;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(CreateCategoryRequest createCategoryRequest) {
        Category category = categoryService.createCategory(createCategoryRequest);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable @Valid Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }
}
