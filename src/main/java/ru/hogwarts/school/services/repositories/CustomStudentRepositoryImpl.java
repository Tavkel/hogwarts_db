package ru.hogwarts.school.services.repositories;

import org.springframework.stereotype.Repository;
import ru.hogwarts.school.models.domain.Student;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CustomStudentRepositoryImpl implements CustomStudentRepository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Student> findOrderedByIdLimitedTo(int amount) {
        return entityManager
                .createQuery("SELECT s FROM Student s WHERE s.deleted = false ORDER BY s.id desc",
                        Student.class).setMaxResults(amount).getResultList();
    }
}