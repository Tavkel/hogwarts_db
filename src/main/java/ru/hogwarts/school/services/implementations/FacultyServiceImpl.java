package ru.hogwarts.school.services.implementations;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.EntryAlreadyExistsException;
import ru.hogwarts.school.helpers.mapper.FacultyMapper;
import ru.hogwarts.school.models.domain.Faculty;
import ru.hogwarts.school.models.dto.FacultyDto;
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
    public FacultyDto addFaculty(FacultyDto facultyDto) {
        var faculty = FacultyMapper.MAPPER.toFaculty(facultyDto);
        var check = facultyRepository.findByName(faculty.getName());
        if (check.isPresent() && !check.get().getDeleted()) {
            throw new EntryAlreadyExistsException();
        }
        return FacultyMapper.MAPPER.fromFaculty(facultyRepository.saveAndFlush(faculty));
    }

    @Override
    public FacultyDto getFacultyById(long id) {
        var result = facultyRepository.findById(id);
        if (result.isPresent() && !result.get().getDeleted()) {
            return FacultyMapper.MAPPER.fromFaculty(result.get());
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public FacultyDto updateFaculty(FacultyDto facultyDto) {
        var faculty = FacultyMapper.MAPPER.toFaculty(facultyDto);
        var result = facultyRepository.findById(faculty.getId());
        if (result.isPresent()) {
            return FacultyMapper.MAPPER.fromFaculty(facultyRepository.saveAndFlush(faculty));
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public FacultyDto removeFaculty(long id) {
        var result = facultyRepository.findById(id);
        if (result.isPresent()) {
            result.get().setDeleted(true);
            return FacultyMapper.MAPPER.fromFaculty(facultyRepository.saveAndFlush(result.get()));
        } else {
            throw new NoSuchElementException();
        }
    }
}
