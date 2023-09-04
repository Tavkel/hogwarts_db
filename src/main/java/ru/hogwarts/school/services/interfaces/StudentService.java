package ru.hogwarts.school.services.interfaces;

import ru.hogwarts.school.models.domain.Student;

public interface StudentService {
    Student addStudent(Student student);
    Student getStudentById(long id);
    Student updateStudent(Student student);
    Student removeStudent(long id);
}
