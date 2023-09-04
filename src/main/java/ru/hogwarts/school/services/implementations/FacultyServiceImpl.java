package ru.hogwarts.school.services.implementations;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.EntryAlreadyExistsException;
import ru.hogwarts.school.models.domain.Faculty;
import ru.hogwarts.school.services.interfaces.FacultyService;
import ru.hogwarts.school.services.repositories.FacultyRepository;

import java.util.NoSuchElementException;

@Service
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    //TODO: find out safeDeleted prop and equals interaction
    @Override
    public Faculty addFaculty(Faculty faculty) {
        var check = facultyRepository.findByName(faculty.getName());
        if (check.isPresent() && !check.get().getDeleted()) {
            throw new EntryAlreadyExistsException();
        }
        return facultyRepository.saveAndFlush(faculty);
    }

    @Override
    public Faculty getFacultyById(long id) {
        var result = facultyRepository.findById(id);
        if (result.isPresent() && !result.get().getDeleted()) {
            return result.get();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Faculty updateFaculty(Faculty faculty) {
        var result = facultyRepository.findById(faculty.getId());
        if (result.isPresent()) {
            return facultyRepository.saveAndFlush(faculty);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Faculty removeFaculty(long id) {
        var result = facultyRepository.findById(id);
        if (result.isPresent()) {
            result.get().setDeleted(true);
            return facultyRepository.saveAndFlush(result.get());
        } else {
            throw new NoSuchElementException();
        }
    }
}
