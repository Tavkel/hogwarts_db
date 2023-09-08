package ru.hogwarts.school.services.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.models.domain.Faculty;
import ru.hogwarts.school.models.domain.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query(value = "SELECT s " +
            "FROM Student s " +
            "WHERE LOWER(s.name) LIKE CONCAT('%',LOWER(?1),'%') " +
            "AND s.deleted = false")
    List<Student> searchStudentByNamePart(String searchString);

    @Query(value = "SELECT f.id, f.colour, f.name, f.deleted " +
            "FROM Student s " +
            "JOIN Faculty f ON s.faculty.id = f.id " +
            "WHERE s.id = ?1 AND s.deleted = false")
    Optional<Faculty> getStudentsFaculty(long id);

    @Query(value = "SELECT s " +
            "FROM Student s " +
            "WHERE s.faculty.id = ?1 AND s.deleted = false")
    List<Student> getStudentsFromFaculty(long id);

    Optional<Student> findFirstByNameIgnoreCase(String name);
    @Query(value = "SELECT s " +
            "FROM Student s " +
            "WHERE s.age = ?1 " +
            "AND s.deleted = false")
    List<Student> findByAge(int age);
    @Query(value = "SELECT s " +
            "FROM Student s " +
            "WHERE s.age BETWEEN ?1 AND ?2 " +
            "AND s.deleted = false")
    List<Student> findByAgeBetween(int floor, int ceiling);
}
