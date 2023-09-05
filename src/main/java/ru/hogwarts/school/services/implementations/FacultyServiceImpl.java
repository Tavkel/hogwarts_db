package ru.hogwarts.school.services.implementations;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.EntryAlreadyExistsException;
import ru.hogwarts.school.helpers.mapper.FacultyMapper;
import ru.hogwarts.school.models.domain.Faculty;
import ru.hogwarts.school.models.dto.FacultyDto;
import ru.hogwarts.school.services.interfaces.FacultyService;
import ru.hogwarts.school.services.repositories.FacultyRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
        var check = facultyRepository.findFirstByNameIgnoreCase(faculty.getName());
        if (check.isPresent()) {
            if (check.get().getDeleted()) {
                check.get().setDeleted(false);
                check.get().setName(faculty.getName());
                check.get().setColour(faculty.getColour());
                return FacultyMapper.MAPPER.fromFaculty(facultyRepository.saveAndFlush(check.get()));
            }
            throw new EntryAlreadyExistsException("This faculty already exists.");
        }
        return FacultyMapper.MAPPER.fromFaculty(facultyRepository.saveAndFlush(faculty));
    }

    @Override
    public FacultyDto getFacultyById(long id) {
        var result = facultyRepository.findById(id);
        if (result.isPresent() && !result.get().getDeleted()) {
            return FacultyMapper.MAPPER.fromFaculty(result.get());
        } else {
            throw new NoSuchElementException("Faculty not found.");
        }
    }

    @Override
    public FacultyDto updateFaculty(FacultyDto facultyDto) {
        var faculty = FacultyMapper.MAPPER.toFaculty(facultyDto);
        var result = facultyRepository.findById(faculty.getId());
        if (result.isPresent()) {
            return FacultyMapper.MAPPER.fromFaculty(facultyRepository.saveAndFlush(faculty));
        } else {
            throw new NoSuchElementException("Faculty not found.");
        }
    }

    @Override
    public FacultyDto removeFaculty(long id) {
        var result = facultyRepository.findById(id);
        if (result.isPresent()) {
            result.get().setDeleted(true);
            return FacultyMapper.MAPPER.fromFaculty(facultyRepository.saveAndFlush(result.get()));
        } else {
            throw new NoSuchElementException("Faculty not found.");
        }
    }

    @Override
    public List<FacultyDto> getFacultiesByColour(String colour) {
        var dbResponse = facultyRepository.findByColourIgnoreCase(colour);
        return dbResponse.stream().filter(f -> !f.getDeleted()).map(FacultyMapper.MAPPER::fromFaculty).collect(Collectors.toList());
    }
}
