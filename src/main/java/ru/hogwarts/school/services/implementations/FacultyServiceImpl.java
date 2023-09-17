package ru.hogwarts.school.services.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(FacultyServiceImpl.class);
    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public FacultyDto addFaculty(FacultyDto facultyDto) {
        logger.debug("Attempting to create a faculty");
        var faculty = FacultyMapper.MAPPER.toFaculty(facultyDto);
        var check = facultyRepository.findFirstByNameIgnoreCase(faculty.getName());
        if (check.isPresent()) {
            logger.warn("Found faculty with the same name");
            if (check.get().getDeleted()) {
                logger.warn("Restoring previously deleted faculty and applying changes to it");
                check.get().setDeleted(false);
                check.get().setName(faculty.getName());
                check.get().setColour(faculty.getColour());
                return FacultyMapper.MAPPER.fromFaculty(facultyRepository.saveAndFlush(check.get()));
            }
            throw new EntryAlreadyExistsException("This faculty already exists.");
        }
        logger.debug("Faculty saved");
        return FacultyMapper.MAPPER.fromFaculty(facultyRepository.saveAndFlush(faculty));
    }

    @Override
    public FacultyDto getFacultyById(long id) {
        logger.debug(String.format("Fetching faculty %d", id));
        var result = facultyRepository.findById(id);
        if (result.isPresent() && !result.get().getDeleted()) {
            return FacultyMapper.MAPPER.fromFaculty(result.get());
        } else {
            throw new NoSuchElementException("Faculty not found.");
        }
    }

    @Override
    public FacultyDto updateFaculty(FacultyDto facultyDto) {
        logger.debug(String.format("Attempting to update faculty %d", facultyDto.getId()));
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
        logger.debug(String.format("Attempting to delete faculty %d", id));
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
        logger.debug(String.format("Searching for faculties with colour %s", colour));
        var dbResponse = facultyRepository.findByColourIgnoreCase(colour);
        return dbResponse.stream().filter(f -> !f.getDeleted()).map(FacultyMapper.MAPPER::fromFaculty).collect(Collectors.toList());
    }

    @Override
    public List<FacultyDto> searchFacultyByColourOrName(String searchString) {
        logger.debug(String.format("Searching for faculties with name or colour containing \"%s\"", searchString));
        var dbResponse = facultyRepository.searchFacultyByColourOrName(searchString);
        return dbResponse.stream().map(FacultyMapper.MAPPER::fromFaculty).collect(Collectors.toList());
    }
}
