package ru.hogwarts.school.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.services.interfaces.FacultyService;
import ru.hogwarts.school.models.dto.FacultyDto;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<FacultyDto> getFaculty(@PathVariable long id) {
        return new ResponseEntity<>(facultyService.getFacultyById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FacultyDto> addFaculty(@RequestBody FacultyDto faculty) {
        if (faculty.getId() != 0) {
            throw new IllegalArgumentException();
        } else {
            return new ResponseEntity<>(facultyService.addFaculty(faculty), HttpStatus.OK);
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
}
