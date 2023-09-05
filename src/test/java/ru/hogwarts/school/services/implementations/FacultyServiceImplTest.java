package ru.hogwarts.school.services.implementations;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.exceptions.EntryAlreadyExistsException;
import ru.hogwarts.school.helpers.mapper.FacultyMapper;
import ru.hogwarts.school.models.domain.Faculty;
import ru.hogwarts.school.models.dto.FacultyDto;
import ru.hogwarts.school.services.repositories.FacultyRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.hogwarts.school.services.implementations.FacultyServiceImplTest.FacultyServiceTestData.*;

@ExtendWith(MockitoExtension.class)
class FacultyServiceImplTest {
    @Mock
    private FacultyRepository facultyRepository;
    @InjectMocks
    private FacultyServiceImpl sut;


    @Test
    void addFaculty_shouldPassFacultyToRepoAndReturnSavedFaculty() {
        when(facultyRepository.findFirstByNameIgnoreCase(anyString())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f ->
                                        StringUtils.equalsAnyIgnoreCase(f.getName(), (CharSequence) i.getArgument(0)))
                                .findFirst()
        );
        when(facultyRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var guineaPig = new FacultyDto(0L, "Gryffindoo", "Red");

        var actual = sut.addFaculty(guineaPig);

        assertEquals(guineaPig, actual);
        verify(facultyRepository, times(1)).findFirstByNameIgnoreCase(guineaPig.getName());
        verify(facultyRepository, times(1)).saveAndFlush(FacultyMapper.MAPPER.toFaculty(guineaPig));
    }

    @Test
    void addFaculty_deletedFaculty_shouldPassFacultyToRepoAndReturnSavedFaculty() {
        when(facultyRepository.findFirstByNameIgnoreCase(anyString())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f ->
                                        StringUtils.equalsIgnoreCase(f.getName(), i.getArgument(0)))
                                .findFirst()
        );
        when(facultyRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var guineaPig = new FacultyDto(0L, "Ravenclaw", "Blue");
        var restoredRavenclaw = new Faculty(3L, "Ravenclaw", "Blue", false);

        var actual = sut.addFaculty(guineaPig);

        assertEquals(guineaPig, actual);
        assertEquals(restoredRavenclaw.getId(), actual.getId());
        verify(facultyRepository, times(1)).findFirstByNameIgnoreCase(guineaPig.getName());
        verify(facultyRepository, times(1)).saveAndFlush(restoredRavenclaw);
    }

    @Test
    void addFaculty_shouldThrowEntryAlreadyExistsExceptionIfFacultyExistsAndNotDeleted() {
        when(facultyRepository.findFirstByNameIgnoreCase(anyString())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f ->
                                        StringUtils.equalsIgnoreCase(f.getName(), i.getArgument(0)))
                                .findFirst()
        );
        var guineaPig = new FacultyDto(0L, "Gryffindoor", "Red");

        var ex = assertThrows(EntryAlreadyExistsException.class, () -> sut.addFaculty(guineaPig));
        assertEquals("This faculty already exists.", ex.getMessage());
        verify(facultyRepository, times(1)).findFirstByNameIgnoreCase(guineaPig.getName());
        verify(facultyRepository, never()).saveAndFlush(any());
    }

    @Test
    void getFacultyById_shouldReturnFacultyWithSpecifiedId() {
        when(facultyRepository.findById(anyLong())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        long id = 2L;

        assertEquals(SLYTHERIN_DTO, sut.getFacultyById(id));
    }

    @Test
    void getFacultyById_shouldThrowNoSuchElementExceptionIfFacultyNotFoundOrDeleted() {
        when(facultyRepository.findById(anyLong())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        long id = 3L;

        var ex = assertThrows(NoSuchElementException.class, () -> sut.getFacultyById(id));
        assertEquals("Faculty not found.", ex.getMessage());
        verify(facultyRepository, times(1)).findById(id);
    }

    @Test
    void updateFaculty_shouldPassFacultyToRepoAndReturnSavedFaculty() {
        when(facultyRepository.findById(anyLong())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        when(facultyRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var guineaPig = new FacultyDto(1L, "Gryffindoodoo", "Brown");

        var actual = sut.updateFaculty(guineaPig);

        assertEquals(guineaPig, actual);
        verify(facultyRepository, times(1)).findById(guineaPig.getId());
        verify(facultyRepository, times(1)).saveAndFlush(FacultyMapper.MAPPER.toFaculty(guineaPig));
    }

    @Test
    void updateFaculty_shouldThrowNoSuchElementExceptionIfFacultyWithProvidedIdWasNotFound() {
        when(facultyRepository.findById(anyLong())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        var guineaPig = new FacultyDto(3L, "Gryffindoodoo", "Brown");

        var ex = assertThrows(NoSuchElementException.class, () -> sut.updateFaculty(guineaPig));
        assertEquals("Faculty not found.", ex.getMessage());
        verify(facultyRepository, times(1)).findById(guineaPig.getId());
        verify(facultyRepository, times(0)).saveAndFlush(any());
    }

    @Test
    void removeFaculty_shouldPassFacultyWithDeletedFlagToRepoAndReturnDeletedFaculty() {
        when(facultyRepository.findById(anyLong())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        when(facultyRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        long id = 1L;
        var guineaPig = new Faculty(1L, "Gryffindoor", "Red", true);

        var actual = sut.removeFaculty(id);
        assertEquals(GRYFFINDOOR_DTO, actual);
        verify(facultyRepository, times(1)).findById(id);
        verify(facultyRepository, times(1)).saveAndFlush(guineaPig);
    }

    @Test
    void removeFaculty_shouldThrowNoSuchElementExceptionIfFacultyWasNotFoundOrDeleted() {
        when(facultyRepository.findById(anyLong())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        long id = 51L;

        var ex = assertThrows(NoSuchElementException.class, () -> sut.removeFaculty(id));
        assertEquals("Faculty not found.", ex.getMessage());
        verify(facultyRepository, times(1)).findById(id);
        verify(facultyRepository, times(0)).saveAndFlush(any());
    }

    @Test
    void getFacultiesByColour_shouldReturnListOfFacultiesWithProvidedColour() {
        when(facultyRepository.findByColourIgnoreCase(anyString())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> StringUtils.equalsIgnoreCase(f.getColour(), i.getArgument(0)))
                                .collect(Collectors.toList())

        );

        var colour = "red";
        var actual = sut.getFacultiesByColour(colour);
        assertEquals(List.of(GRYFFINDOOR_DTO), actual);
        verify(facultyRepository, times(1)).findByColourIgnoreCase(colour);
    }

    @Test
    void getFacultiesByColour_shouldReturnEmptyListIfNothingFound() {
        when(facultyRepository.findByColourIgnoreCase(anyString())).thenAnswer(
                i ->
                        faculties.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> StringUtils.equalsIgnoreCase(f.getColour(), i.getArgument(0)))
                                .collect(Collectors.toList())

        );

        var colour = "Not a colour";
        var actual = sut.getFacultiesByColour(colour);
        assertEquals(List.of(), actual);
        verify(facultyRepository, times(1)).findByColourIgnoreCase(colour);
    }

    static class FacultyServiceTestData {
        public static FacultyDto GRYFFINDOOR_DTO = new FacultyDto(1L, "Gryffindoor", "Red");
        public static FacultyDto SLYTHERIN_DTO = new FacultyDto(2L, "Slytherin", "Green");
        public static FacultyDto RAVENCLAW_DTO = new FacultyDto(3L, "Ravenclaw", "Blue");
        public static FacultyDto HUFFLEPUFF_DTO = new FacultyDto(4L, "Hufflepuff", "Yellow");
        public static List<FacultyDto> facultyDtos = List.of(GRYFFINDOOR_DTO, SLYTHERIN_DTO, RAVENCLAW_DTO, HUFFLEPUFF_DTO);

        public static Faculty GRYFFINDOOR = new Faculty(1L, "Gryffindoor", "Red", false);
        public static Faculty SLYTHERIN = new Faculty(2L, "Slytherin", "Green", false);
        public static Faculty RAVENCLAW = new Faculty(3L, "Ravenclaw", "Blue", true);
        public static Faculty HUFFLEPUFF = new Faculty(4L, "Hufflepuff", "Yellow", true);
        public static List<Faculty> faculties = List.of(GRYFFINDOOR, SLYTHERIN, RAVENCLAW, HUFFLEPUFF);
    }
}