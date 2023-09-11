package ru.hogwarts.school.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.models.domain.Faculty;
import ru.hogwarts.school.models.domain.Student;
import ru.hogwarts.school.models.dto.FacultyDto;
import ru.hogwarts.school.models.dto.StudentDto;
import ru.hogwarts.school.services.implementations.FacultyServiceImpl;
import ru.hogwarts.school.services.implementations.StudentServiceImpl;
import ru.hogwarts.school.services.repositories.FacultyRepository;
import ru.hogwarts.school.services.repositories.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.hogwarts.school.controllers.FacultyControllerTest.FacultyControllerTestData.*;

@WebMvcTest(controllers = {FacultyController.class})
class FacultyControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @SpyBean
    FacultyServiceImpl facultyService;

    @SpyBean
    StudentServiceImpl studentService;

    @Autowired
    FacultyController facultyController;

    @MockBean
    FacultyRepository facultyRepository;

    @MockBean
    StudentRepository studentRepository;

    private String url = "/faculty";

    @BeforeAll
    private static void init() {
        HARRY.setFaculty(GRYFFINDOOR);
        DRAKO.setFaculty(SLYTHERIN);
        CHANG.setFaculty(RAVENCLAW);
        SEDRIK.setFaculty(HUFFLEPUFF);
    }

    @Test
    void getFaculty_shouldReturnFacultyAndStatusOk() throws Exception {
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(GRYFFINDOOR));
        mockMvc.perform(get(url + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(GRYFFINDOOR_DTO.getName()))
                .andExpect(jsonPath("$.colour").value(GRYFFINDOOR_DTO.getColour()));
    }

    @Test
    void getStudentsFromFaculty_shouldReturnListStudentsAndStatusOk() throws Exception {
        when(studentRepository.getStudentsFromFaculty(anyLong())).thenAnswer(i ->
                students.stream()
                        .filter(s -> s.getFaculty().getId() == i.getArgument(0))
                        .collect(Collectors.toList()));
        mockMvc.perform(get(url + "/3/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value(CHANG_DTO.getName()))
                .andExpect(jsonPath("$.[0].facultyId").value(RAVENCLAW.getId()));
    }

    @Test
    void addFaculty_shouldReturnFacultyAndStatusOk() throws Exception {
        var faculty = new FacultyDto(0L, "Gryffindoor", "Red");
        when(facultyRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.colour").value(faculty.getColour()));
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFacultyAndStatusOk() throws Exception {
        long id = 1L;
        var faculty = new FacultyDto(id, "Gryffindoodoo", "Red");
        when(facultyRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        when(facultyRepository.findById(anyLong())).thenAnswer(i ->
                faculties.stream()
                        .filter(f -> f.getId() == i.getArgument(0))
                        .findFirst());
        mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.colour").value(faculty.getColour()));
    }

    @Test
    void deleteFaculty() {
        //Soon™
    }

    @Test
    void getFacultiesByColour() {
        //Soon™
    }

    @Test
    void searchFacultiesByNameOrColour() {
        //Soon™
    }

    static class FacultyControllerTestData {
        public static FacultyDto GRYFFINDOOR_DTO = new FacultyDto(1L, "Gryffindoor", "Red");
        public static FacultyDto SLYTHERIN_DTO = new FacultyDto(2L, "Slytherin", "Green");
        public static FacultyDto RAVENCLAW_DTO = new FacultyDto(3L, "Ravenclaw", "Blue");
        public static FacultyDto HUFFLEPUFF_DTO = new FacultyDto(4L, "Hufflepuff", "Yellow");
        public static List<FacultyDto> facultyDtos = List.of(GRYFFINDOOR_DTO, SLYTHERIN_DTO, RAVENCLAW_DTO, HUFFLEPUFF_DTO);

        public static Faculty GRYFFINDOOR = new Faculty(1L, "Gryffindoor", "Red", false);
        public static Faculty SLYTHERIN = new Faculty(2L, "Slytherin", "Green", false);
        public static Faculty RAVENCLAW = new Faculty(3L, "Ravenclaw", "Blue", false);
        public static Faculty HUFFLEPUFF = new Faculty(4L, "Hufflepuff", "Yellow", false);
        public static List<Faculty> faculties = List.of(GRYFFINDOOR, SLYTHERIN, RAVENCLAW, HUFFLEPUFF);

        public static StudentDto HARRY_DTO = new StudentDto(1L, "Harry", 12, 1, "Gryffindoor");
        public static StudentDto DRAKO_DTO = new StudentDto(2L, "Drako", 17, 2, "Slytherin");
        public static StudentDto CHANG_DTO = new StudentDto(3L, "Chang", 17, 3, "Ravenclaw");
        public static StudentDto SEDRIK_DTO = new StudentDto(4L, "Sedrik", 16, 4, "Hufflepuff");
        public static Student HARRY = new Student(false, 1L, "Harry", 12, 1);
        public static Student DRAKO = new Student(false, 2L, "Drako", 17, 2);
        public static Student CHANG = new Student(false, 3L, "Chang", 17, 3);
        public static Student SEDRIK = new Student(false, 4L, "Sedrik", 16, 4);
        public static List<Student> students = List.of(HARRY, DRAKO, CHANG, SEDRIK);
    }
}