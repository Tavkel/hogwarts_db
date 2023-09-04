package ru.hogwarts.school.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.services.interfaces.StudentService;
import ru.hogwarts.school.models.domain.Student;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping()
    public ResponseEntity<Student> getStudent(@RequestParam Long id) {
        return new ResponseEntity<>(studentService.getStudentById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        if (student.getId() != 0) {
            throw new IllegalArgumentException();
        } else {
            return new ResponseEntity<>(studentService.addStudent(student), HttpStatus.OK);
        }
    }

    @PutMapping
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        if (student.getId() == 0) {
            throw new IllegalArgumentException();
        } else {
            return  new ResponseEntity<>(studentService.updateStudent(student), HttpStatus.OK);
        }
    }

    @DeleteMapping
    public ResponseEntity<Student> deleteStudent(@RequestParam long id) {
        return new ResponseEntity<>(studentService.removeStudent(id), HttpStatus.OK);
    }
}
