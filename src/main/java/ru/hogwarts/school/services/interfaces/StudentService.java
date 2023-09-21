package ru.hogwarts.school.services.interfaces;

import ru.hogwarts.school.models.dto.FacultyDto;
import ru.hogwarts.school.models.dto.StudentDto;

import java.util.List;

public interface StudentService {
    StudentDto addStudent(StudentDto student);

    /**
     * Search for student in db.
     *
     * @param id to perform search query on.
     * @return {@code StudentDto} — if one found.
     * @throws java.util.NoSuchElementException if not found.
     */
    StudentDto getStudentById(long id);

    FacultyDto getStudentsFaculty(long id);

    List<StudentDto> getStudentsFromFaculty(long id);

    StudentDto updateStudent(StudentDto student);

    StudentDto removeStudent(long id);

    List<StudentDto> getStudentsByAge(int age);

    List<StudentDto> getStudentsByAgeInRange(int floor, int ceiling);

    List<StudentDto> searchStudentsByName(String searchString);

    Integer getStudentCount();

    Integer getAverageAge();

    List<StudentDto> getLastStudents(int amount);

    void playWithThreads();
    void playWithThreads2();
}
