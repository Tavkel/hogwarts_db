package ru.hogwarts.school.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.models.dto.StudentDto;
import ru.hogwarts.school.services.interfaces.FacultyService;
import ru.hogwarts.school.models.dto.FacultyDto;
import ru.hogwarts.school.services.interfaces.StudentService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;
    private final StudentService studentService;

    public FacultyController(FacultyService facultyService, StudentService studentService) {
        this.facultyService = facultyService;
        this.studentService = studentService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<FacultyDto> getFaculty(@PathVariable long id) {
        return new ResponseEntity<>(facultyService.getFacultyById(id), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/students")
    public ResponseEntity<List<StudentDto>> getStudentsFromFaculty(@PathVariable long id) {
        return new ResponseEntity<>(studentService.getStudentsFromFaculty(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FacultyDto> addFaculty(@RequestBody FacultyDto faculty) {
        if (faculty.getId() != 0) {
            throw new IllegalArgumentException();
        } else {
            var result = facultyService.addFaculty(faculty);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @PutMapping
    public ResponseEntity<FacultyDto> updateFaculty(@RequestBody FacultyDto faculty) {
        if (faculty.getId() == 0) {
            throw new IllegalArgumentException();
        } else {
            return  new ResponseEntity<>(facultyService.updateFaculty(faculty), HttpStatus.OK);
        }
    }
    
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<FacultyDto> deleteFaculty(@PathVariable long id) {
        return new ResponseEntity<>(facultyService.removeFaculty(id), HttpStatus.OK);
    }

    @GetMapping(path = "/byColour")
    public ResponseEntity<List<FacultyDto>> getFacultiesByColour(@RequestParam String colour) {
        return new ResponseEntity<>(facultyService.getFacultiesByColour(colour), HttpStatus.OK);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<FacultyDto>> searchFacultiesByNameOrColour(@RequestParam String searchString) {
        return new ResponseEntity<>(facultyService.searchFacultyByColourOrName(searchString), HttpStatus.OK);
    }
}
