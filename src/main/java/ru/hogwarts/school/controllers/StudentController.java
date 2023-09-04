package ru.hogwarts.school.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.models.dto.StudentDto;
import ru.hogwarts.school.services.interfaces.StudentService;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable long id) {
        return new ResponseEntity<>(studentService.getStudentById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<StudentDto> addStudent(@RequestBody StudentDto student) {
        if (student.getId() != 0) {
            throw new IllegalArgumentException();
        } else {
            return new ResponseEntity<>(studentService.addStudent(student), HttpStatus.OK);
        }
    }

    @PutMapping
    public ResponseEntity<StudentDto> updateStudent(@RequestBody StudentDto student) {
        if (student.getId() == 0) {
            throw new IllegalArgumentException();
        } else {
            return new ResponseEntity<>(studentService.updateStudent(student), HttpStatus.OK);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<StudentDto> deleteStudent(@PathVariable long id) {
        return new ResponseEntity<>(studentService.removeStudent(id), HttpStatus.OK);
    }

    @GetMapping(path = "/byAge")
    public ResponseEntity<List<StudentDto>> getStudentsByAge(@RequestParam int age) {
        return new ResponseEntity<>(studentService.getStudentsByAge(age), HttpStatus.OK);
    }
}
