package ru.hogwarts.school.services.interfaces;

import ru.hogwarts.school.models.dto.FacultyDto;

import java.util.List;

public interface FacultyService {
    FacultyDto addFaculty(FacultyDto faculty);
    FacultyDto getFacultyById(long id);
    FacultyDto updateFaculty(FacultyDto faculty);
    FacultyDto removeFaculty(long id);
    List<FacultyDto> getFacultiesByColour(String colour);
    List<FacultyDto> searchFacultyByColourOrName(String searchString);
}
