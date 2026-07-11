package de.fiscalnorth.user.service;

import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.service.HouseholdService;
import de.fiscalnorth.transaction.model.TransactionType;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserOnboardingService {

    private static final List<CategorySeed> DEFAULT_CATEGORIES = List.of(
            new CategorySeed("Groceries", TransactionType.Expense),
            new CategorySeed("Salary", TransactionType.Income),
            new CategorySeed("Entertainment", TransactionType.Expense),
            new CategorySeed("Rent", TransactionType.Expense),
            new CategorySeed("Transport", TransactionType.Expense),
            new CategorySeed("Dining", TransactionType.Expense),
            new CategorySeed("Health", TransactionType.Expense),
            new CategorySeed("Shopping", TransactionType.Expense),
            new CategorySeed("Utilities", TransactionType.Expense),
            new CategorySeed("Freelance", TransactionType.Income),
            new CategorySeed("Subscriptions", TransactionType.Expense),
            new CategorySeed("Travel", TransactionType.Expense)
    );

    private final CategoryRepository categoryRepository;
    private final HouseholdService householdService;

    @Transactional
    public void seedDefaultCategories(User owner) {
        Household household = householdService.createHouseholdForUser(owner, null);
        if (categoryRepository.existsByOwnerId(owner.getId())) {
            return;
        }
        for (CategorySeed seed : DEFAULT_CATEGORIES) {
            Category category = new Category();
            category.setName(seed.name());
            category.setTransactionType(seed.type());
            category.setOwner(owner);
            category.setHousehold(household);
            categoryRepository.save(category);
        }
    }

    private record CategorySeed(String name, TransactionType type) {
    }
}
