package ru.hogwarts.school.services.repositories;

import ru.hogwarts.school.models.domain.Student;

import java.util.List;

public interface CustomStudentRepository {
    public List<Student> findOrderedByIdLimitedTo(int amount);
}
