package ru.hogwarts.school.services.interfaces;

import ru.hogwarts.school.models.dto.StudentDto;

public interface StudentService {
    StudentDto addStudent(StudentDto student);
    StudentDto getStudentById(long id);
    StudentDto updateStudent(StudentDto student);
    StudentDto removeStudent(long id);
}
