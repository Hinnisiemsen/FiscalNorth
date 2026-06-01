package de.fiscalnorth.category.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.category.dto.CreateCategoryRequest;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.shared.LocalizedException;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository repository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = false)
    public Category createCategory(CreateCategoryRequest createCategoryRequest) {
        User owner = currentUserService.getCurrentUser();
        Category category = new Category();
        category.setName(createCategoryRequest.name());
        category.setTransactionType(createCategoryRequest.transactionType());
        category.setOwner(owner);

        if (repository.existsByOwnerIdAndNameEqualsAndTransactionTypeEquals(
                owner.getId(), category.getName(), category.getTransactionType())) {
            throw new LocalizedException("error.category.duplicate");
        }
        return repository.save(category);
    }

    public List<Category> getAllCategories() {
        return repository.findAllByOwnerId(currentUserService.getCurrentUserId());
    }

    public Category getCategory(@NonNull Long id) {
        return repository.findByIdAndOwnerId(id, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new RessourceNotFoundException("Category doesn't exist in datasource!", "id", id));
    }

    @Transactional(readOnly = false)
    public void deleteCategory(Long id) {
        Category category = repository.findByIdAndOwnerId(id, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new RessourceNotFoundException("Category", "id", id));
        repository.delete(category);
    }
}
