package de.fiscalnorth.category.service;

import de.fiscalnorth.category.dto.CreateCategoryRequest;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository repository;

    public Category createCategory(CreateCategoryRequest createCategoryRequest) {
        Category category = new Category();
        category.setName(createCategoryRequest.name());
        category.setTransactionType(createCategoryRequest.transactionType());

        if (repository.existsByNameEqualsAndTransactionTypeEquals(category.getName(), category.getTransactionType())) {
            throw new IllegalStateException("This category already exists!");
        }
        return repository.save(category);
    }

    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    public Category getCategory(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Category doesn't exist in datasource!", "id", id));
    }
}
