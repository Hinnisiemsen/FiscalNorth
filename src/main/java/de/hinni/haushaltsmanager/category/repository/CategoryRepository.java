package de.hinni.haushaltsmanager.category.repository;

import de.hinni.haushaltsmanager.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
