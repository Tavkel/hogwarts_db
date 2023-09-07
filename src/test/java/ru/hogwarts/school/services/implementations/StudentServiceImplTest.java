package ru.hogwarts.school.services.implementations;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.exceptions.EntryAlreadyExistsException;
import ru.hogwarts.school.helpers.mapper.StudentMapper;
import ru.hogwarts.school.models.domain.Faculty;
import ru.hogwarts.school.models.domain.Student;
import ru.hogwarts.school.models.dto.FacultyDto;
import ru.hogwarts.school.models.dto.StudentDto;
import ru.hogwarts.school.services.repositories.StudentRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static ru.hogwarts.school.services.implementations.StudentServiceImplTest.StudentServiceTestData.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;
    @InjectMocks
    private StudentServiceImpl sut;


    @Test
    void addStudent_shouldPassStudentToRepoAndReturnSavedStudent() {
        when(studentRepository.findFirstByNameIgnoreCase(anyString())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f ->
                                        StringUtils.equalsAnyIgnoreCase(f.getName(), (CharSequence) i.getArgument(0)))
                                .findFirst()
        );
        when(studentRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var guineaPig = new StudentDto(0L, "Ron", 12, 1, "Gryffindoor");

        var actual = sut.addStudent(guineaPig);

        assertEquals(guineaPig, actual);
        verify(studentRepository, times(1)).findFirstByNameIgnoreCase(guineaPig.getName());
        verify(studentRepository, times(1)).saveAndFlush(StudentMapper.MAPPER.toStudent(guineaPig));
    }

    @Test
    void addStudent_deletedStudent_shouldPassStudentToRepoAndReturnSavedStudent() {
        when(studentRepository.findFirstByNameIgnoreCase(anyString())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f ->
                                        StringUtils.equalsIgnoreCase(f.getName(), i.getArgument(0)))
                                .findFirst()
        );
        when(studentRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var guineaPig = new StudentDto(0L, "Sedrik", 16, 4, "Hufflepuff");

        var restoredSedrik = new Student(false, 4L, "Sedrik", 16, 4);

        var actual = sut.addStudent(guineaPig);

        assertEquals(guineaPig, actual);
        assertEquals(restoredSedrik.getId(), actual.getId());
        verify(studentRepository, times(1)).findFirstByNameIgnoreCase(guineaPig.getName());
        verify(studentRepository, times(1)).saveAndFlush(restoredSedrik);
    }

    @Test
    void addStudent_shouldThrowEntryAlreadyExistsExceptionIfStudentExistsAndNotDeleted() {
        when(studentRepository.findFirstByNameIgnoreCase(anyString())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f ->
                                        StringUtils.equalsIgnoreCase(f.getName(), i.getArgument(0)))
                                .findFirst()
        );
        var guineaPig = new StudentDto(2L, "Drako", 17, 2, "Slytherin");

        var ex = assertThrows(EntryAlreadyExistsException.class, () -> sut.addStudent(guineaPig));
        assertEquals("This student already exists.", ex.getMessage());
        verify(studentRepository, times(1)).findFirstByNameIgnoreCase(guineaPig.getName());
        verify(studentRepository, never()).saveAndFlush(any());
    }

    @Test
    void getStudentById_shouldReturnStudentWithSpecifiedId() {
        when(studentRepository.findById(anyLong())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        long id = 2L;

        assertEquals(DRAKO_DTO, sut.getStudentById(id));
        verify(studentRepository, times(1)).findById(id);
    }

    @Test
    void getStudentById_shouldThrowNoSuchElementExceptionIfStudentNotFoundOrDeleted() {
        when(studentRepository.findById(anyLong())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        long id = 4L;

        var ex = assertThrows(NoSuchElementException.class, () -> sut.getStudentById(id));
        assertEquals("Student not found.", ex.getMessage());
        verify(studentRepository, times(1)).findById(id);
    }

    @Test
    void getStudentsFaculty_shouldReturnStudentsFaculty() {
        when(studentRepository.findById(anyLong())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        long id = 2L;
        var expectedFaculty = new FacultyDto(2L, null, null);
        assertEquals(expectedFaculty, sut.getStudentsFaculty(id));
        verify(studentRepository, times(1)).findById(id);
    }

    @Test
    void getStudentsFaculty_shouldThrowNoSuchElementExceptionIfStudentNotFoundOrDeleted() {
        when(studentRepository.findById(anyLong())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        long id = 4L;

        var ex = assertThrows(NoSuchElementException.class, () -> sut.getStudentsFaculty(id));
        assertEquals("Student not found.", ex.getMessage());
        verify(studentRepository, times(1)).findById(id);
    }

    @Test
    void updateStudent_shouldPassStudentToRepoAndReturnSavedStudent() {
        when(studentRepository.findById(anyLong())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        when(studentRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var guineaPig = new StudentDto(1L, "Haarryy", 14, 1, "Gryffindoor");

        var actual = sut.updateStudent(guineaPig);

        assertEquals(guineaPig, actual);
        verify(studentRepository, times(1)).findById(guineaPig.getId());
        verify(studentRepository, times(1)).saveAndFlush(StudentMapper.MAPPER.toStudent(guineaPig));
    }

    @Test
    void updateStudent_shouldThrowNoSuchElementExceptionIfStudentWithProvidedIdWasNotFound() {
        when(studentRepository.findById(anyLong())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        var guineaPig = new StudentDto(4L, "Haarryy", 14, 1, "Gryffindoor");

        var ex = assertThrows(NoSuchElementException.class, () -> sut.updateStudent(guineaPig));
        assertEquals("Student not found.", ex.getMessage());
        verify(studentRepository, times(1)).findById(guineaPig.getId());
        verify(studentRepository, times(0)).saveAndFlush(any());
    }

    @Test
    void removeStudent_shouldPassStudentWithDeletedFlagToRepoAndReturnDeletedStudent() {
        when(studentRepository.findById(anyLong())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        when(studentRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        long id = 3L;
        var guineaPig = new Student(true, 3L, "Chang", 17, 3);

        var actual = sut.removeStudent(id);
        assertEquals(CHANG_DTO, actual);
        verify(studentRepository, times(1)).findById(id);
        verify(studentRepository, times(1)).saveAndFlush(guineaPig);
    }

    @Test
    void removeStudent_shouldThrowNoSuchElementExceptionIfStudentWasNotFoundOrDeleted() {
        when(studentRepository.findById(anyLong())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getId() == i.getArgument(0))
                                .findFirst()

        );
        long id = 51L;

        var ex = assertThrows(NoSuchElementException.class, () -> sut.removeStudent(id));
        assertEquals("Student not found.", ex.getMessage());
        verify(studentRepository, times(1)).findById(id);
        verify(studentRepository, times(0)).saveAndFlush(any());
    }

    @Test
    void getStudentsByAge_shouldReturnListOfStudentsWithProvidedAge() {
        when(studentRepository.findByAge(anyInt())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getAge() == i.getArgument(0))
                                .collect(Collectors.toList())

        );

        var age = 17;
        var actual = sut.getStudentsByAge(age);
        assertEquals(List.of(DRAKO_DTO, CHANG_DTO), actual);
        verify(studentRepository, times(1)).findByAge(age);
    }

    @Test
    void getStudentsByAge_shouldReturnEmptyListIfNothingFound() {
        when(studentRepository.findByAge(anyInt())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getAge() == i.getArgument(0))
                                .collect(Collectors.toList())

        );

        var age = 45;
        var actual = sut.getStudentsByAge(age);
        assertEquals(List.of(), actual);
        verify(studentRepository, times(1)).findByAge(age);
    }

    @Test
    void getStudentsByAgeInRange_shouldReturnListOfStudentsWithAgeWithinBounds() {
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getAge().compareTo(i.getArgument(0)) >= 0)
                                .filter(f -> f.getAge().compareTo(i.getArgument(1)) <= 0)
                                .collect(Collectors.toList())

        );
        int floor = 13;
        int ceiling = 20;
        var actual = sut.getStudentsByAgeInRange(floor, ceiling);

        assertEquals(List.of(DRAKO_DTO, CHANG_DTO), actual);
    }

    @Test
    void getStudentsByAgeInRange_shouldReturnEmptyListIfNothingFoundOrArgumentsInvalid() {
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> f.getAge().compareTo(i.getArgument(0)) >= 0)
                                .filter(f -> f.getAge().compareTo(i.getArgument(1)) <= 0)
                                .collect(Collectors.toList())

        );
        int floor = 20;
        int ceiling = 13;
        var actual = sut.getStudentsByAgeInRange(floor, ceiling);

        assertEquals(List.of(), actual);
    }

    @Test
    void searchStudentsByName_shouldReturnListOfStudentsNamesContainingSearchString() {
        when(studentRepository.searchStudentByNamePart(anyString())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> StringUtils.containsIgnoreCase(f.getName(), i.getArgument(0)))
                                .collect(Collectors.toList())
        );
        String searchString = "R";
        var actual = sut.searchStudentsByName(searchString);

        assertEquals(List.of(HARRY_DTO, DRAKO_DTO), actual);
    }

    @Test
    void searchStudentsByName_shouldReturnEmptyListIfNothingFoundOrArgumentsInvalid() {
        when(studentRepository.searchStudentByNamePart(anyString())).thenAnswer(
                i ->
                        students.stream()
                                .filter(f -> !f.getDeleted())
                                .filter(f -> StringUtils.containsIgnoreCase(f.getName(), i.getArgument(0)))
                                .collect(Collectors.toList())
        );
        String searchString = "asdasd";
        var actual = sut.searchStudentsByName(searchString);

        assertEquals(List.of(), actual);
    }

    static class StudentServiceTestData {
        public static StudentDto HARRY_DTO = new StudentDto(1L, "Harry", 12, 1, "Gryffindoor");
        public static StudentDto DRAKO_DTO = new StudentDto(2L, "Drako", 17, 2, "Slytherin");
        public static StudentDto CHANG_DTO = new StudentDto(3L, "Chang", 17, 3, "Ravenclaw");
        public static StudentDto SEDRIK_DTO = new StudentDto(4L, "Sedrik", 16, 4, "Hufflepuff");
        public static List<StudentDto> studentDtos = List.of(HARRY_DTO, DRAKO_DTO, CHANG_DTO, SEDRIK_DTO);
        public static Student HARRY = new Student(false, 1L, "Harry", 12, 1);
        public static Student DRAKO = new Student(false, 2L, "Drako", 17, 2);
        public static Student CHANG = new Student(false, 3L, "Chang", 17, 3);
        public static Student SEDRIK = new Student(true, 4L, "Sedrik", 16, 4);
        public static List<Student> students = List.of(HARRY, DRAKO, CHANG, SEDRIK);
    }
}