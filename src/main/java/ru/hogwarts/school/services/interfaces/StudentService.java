package ru.hogwarts.school.services.interfaces;

import ru.hogwarts.school.models.dto.StudentDto;

import java.util.List;

public interface StudentService {
    StudentDto addStudent(StudentDto student);
    StudentDto getStudentById(long id);
    StudentDto updateStudent(StudentDto student);
    StudentDto removeStudent(long id);
    List<StudentDto> getStudentsByAge(int age);
    List<StudentDto> getStudentsByAgeInRange(int floor, int ceiling);
    List<StudentDto> searchStudentsByName(String searchString);
}
