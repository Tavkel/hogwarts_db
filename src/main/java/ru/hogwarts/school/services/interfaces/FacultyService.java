package ru.hogwarts.school.services.interfaces;

import ru.hogwarts.school.models.domain.Faculty;

public interface FacultyService {
    Faculty addFaculty(Faculty faculty);
    Faculty getFacultyById(long id);
    Faculty updateFaculty(Faculty faculty);
    Faculty removeFaculty(long id);
}
