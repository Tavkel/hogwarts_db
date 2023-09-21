package ru.hogwarts.school.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.helpers.RequestValidator;
import ru.hogwarts.school.models.dto.FacultyDto;
import ru.hogwarts.school.models.dto.StudentDto;
import ru.hogwarts.school.services.interfaces.StudentService;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final Logger logger = LoggerFactory.getLogger(StudentController.class);

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable long id) {
        return new ResponseEntity<>(studentService.getStudentById(id), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/faculty")
    public ResponseEntity<FacultyDto> getStudentsFaculty(@PathVariable long id) {
        return new ResponseEntity<>(studentService.getStudentsFaculty(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<StudentDto> addStudent(@RequestBody StudentDto student) {
        RequestValidator.validateMustBeZero(student.getId(), "Id");
        return new ResponseEntity<>(studentService.addStudent(student), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<StudentDto> updateStudent(@RequestBody StudentDto student) {
        RequestValidator.validateMustBeGreaterThanZero(student.getId(), "Id");
        return new ResponseEntity<>(studentService.updateStudent(student), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<StudentDto> deleteStudent(@PathVariable long id) {
        RequestValidator.validateMustBeGreaterThanZero(id, "Id");
        return new ResponseEntity<>(studentService.removeStudent(id), HttpStatus.OK);
    }

    @GetMapping(path = "/age/getBy")
    public ResponseEntity<List<StudentDto>> getStudentsByAge(@RequestParam int age) {
        RequestValidator.validateMustBeGreaterThanZero(age, "Age");
        return new ResponseEntity<>(studentService.getStudentsByAge(age), HttpStatus.OK);
    }

    @GetMapping(path = "/age/getInRange")
    public ResponseEntity<List<StudentDto>> getStudentsByAgeInRange(@RequestParam int floor, @RequestParam int ceiling) {
        RequestValidator.validateRange(floor, ceiling);
        return new ResponseEntity<>(studentService.getStudentsByAgeInRange(floor, ceiling), HttpStatus.OK);
    }

    @GetMapping(path = "/age/avg")
    public ResponseEntity<Integer> getAverageAge() {
        return new ResponseEntity<>(studentService.getAverageAge(), HttpStatus.OK);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<StudentDto>> SearchStudentByName(@RequestParam String searchString) {
        return new ResponseEntity<>(studentService.searchStudentsByName(searchString), HttpStatus.OK);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Integer> getStudentCount() {
        return new ResponseEntity<>(studentService.getStudentCount(), HttpStatus.OK);
    }

    @GetMapping(path = "/last")
    public ResponseEntity<List<StudentDto>> getLastStudents(int amount) {
        return new ResponseEntity<>(studentService.getLastStudents(amount), HttpStatus.OK);
    }

    @GetMapping(path = "threads-playground")
    public void poke() {
        studentService.playWithThreads();
    }
    @GetMapping(path = "threads-playground2")
    public void poke2() {
        studentService.playWithThreads2();
    }
}
