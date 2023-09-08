package ru.hogwarts.school.services.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.models.domain.Faculty;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findFirstByNameIgnoreCase(String name);
    List<Faculty> findByColourIgnoreCase(String colour);

    @Query(value = "SELECT f FROM Faculty f " +
            "WHERE LOWER(f.name) LIKE CONCAT('%',LOWER(?1),'%') " +
            "OR LOWER(f.colour) LIKE CONCAT('%',LOWER(?1),'%') " +
            "AND f.deleted = false")
    List<Faculty> searchFacultyByColourOrName(String searchString);
    List<Faculty> findByNameContainingIgnoreCaseOrColourContainingIgnoreCase(String searchString, String searchString2);
}
