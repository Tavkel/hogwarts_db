package ru.hogwarts.school.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import ru.hogwarts.school.models.dto.FacultyDto;
import ru.hogwarts.school.models.dto.StudentDto;
import ru.hogwarts.school.services.interfaces.FacultyService;
import ru.hogwarts.school.services.interfaces.StudentService;
import ru.hogwarts.school.services.repositories.FacultyRepository;
import ru.hogwarts.school.services.repositories.StudentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.hogwarts.school.controllers.StudentControllerTest.StudentControllerTestData.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StudentControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    FacultyService facultyService;

    @Autowired
    StudentService studentService;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    StudentRepository studentRepository;

    @LocalServerPort
    int port;

    private String url;

    @BeforeEach
    void initDb() {
        url = "http://localhost:" + port + "/student";
        facultyService.addFaculty(GRYFFINDOOR_DTO);
        facultyService.addFaculty(SLYTHERIN_DTO);
        facultyService.addFaculty(RAVENCLAW_DTO);
        facultyService.addFaculty(HUFFLEPUFF_DTO);

        studentService.addStudent(HARRY_DTO);
        studentService.addStudent(DRAKO_DTO);
        studentService.addStudent(CHANG_DTO);
        studentService.addStudent(SEDRIK_DTO);
    }

    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void getStudent_shouldReturnStudentAndStatusOk() {
        var response = restTemplate.getForEntity(
                url + "/" + HARRY_DTO.getId(),
                StudentDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HARRY_DTO, response.getBody());
    }

    @Test
    void getStudent_shouldReturnErrorMessageAndStatusNotFound() {
        var response = restTemplate.getForEntity(
                url + "/" + 5L,
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Student not found.", response.getBody());
    }

    @Test
    void getStudentsFaculty_shouldReturnFacultyAndStatusOk() {
        var response = restTemplate.getForEntity(url + "/" + HARRY_DTO.getId() + "/faculty",
                FacultyDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(GRYFFINDOOR_DTO, response.getBody());
    }

    @Test
    void addStudent_shouldReturnStudentAndStatusOk() {
        StudentDto student = new StudentDto(0L, "Mary", 10, 1, "Gryffindoor");

        var studentResponseEntity = restTemplate.postForEntity(url,
                student, StudentDto.class);
        assertEquals(HttpStatus.OK, studentResponseEntity.getStatusCode());
        assertEquals(student, studentResponseEntity.getBody());
    }

    @Test
    void updateStudent_shouldReturnStudentAndStatusOk() {
        StudentDto student = new StudentDto(1L, "Mary", 10, 1, "Gryffindoor");

        var response = restTemplate.exchange(url, HttpMethod.PUT,
                new HttpEntity<>(student), StudentDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(student, response.getBody());
    }

    @Test
    void deleteStudent_shouldReturnStudentAndStatusOK() {
        var response = restTemplate.exchange(
                url + "/" + HARRY_DTO.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(HARRY_DTO),
                StudentDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HARRY_DTO, response.getBody());
    }

    @Test
    void getStudentsByAge_shouldReturnListStudentsAndStatusOk() {
        var response = restTemplate.exchange(
                url + "/age/getBy?age=17",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDto>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(DRAKO_DTO, CHANG_DTO), response.getBody());
    }

    @Test
    void getStudentsByAgeInRange_shouldReturnListStudentsAndStatusOk() {
        var response = restTemplate.exchange(
                url + "/age/getInRange?floor=15&ceiling=18",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDto>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(DRAKO_DTO, CHANG_DTO, SEDRIK_DTO), response.getBody());
    }

    @Test
    void searchStudentByName_shouldReturnStudentAndStatusOk() {
        var response = restTemplate.exchange(
                url + "/search?searchString=Harry",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDto>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(HARRY_DTO), response.getBody());
    }

    @Test
    void searchStudentByName_shouldReturnEmptyListAndStatusOk() {
        var response = restTemplate.exchange(
                url + "/search?searchString=ASDASD",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDto>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    void getStudentCount_ShouldReturnAmountOfNotDeletedStudents() {
        studentService.removeStudent(2);
        var response = restTemplate.exchange(
                url + "/count",
                HttpMethod.GET,
                null,
                Integer.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody());
    }

    @Test
    void getAverageAge_shouldReturnAverageAgeOfNotDeletedStudents() {
        studentService.removeStudent(2);
        var response = restTemplate.exchange(
                url + "/age/avg",
                HttpMethod.GET,
                null,
                Integer.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(15, response.getBody());
    }

    @Test
    void getLastStudents_shouldReturnListOfLastXStudents() {
        var response = restTemplate.exchange(
                url + "/last?amount=2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDto>>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(SEDRIK_DTO, CHANG_DTO), response.getBody());
    }

    @Test
    void getNamesStartingWithA_shouldReturnListOfNamesStartingWithDAndStatusOk() {
        var response = restTemplate.exchange(
                url + "/starts-with-d",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(DRAKO_DTO.getName().toUpperCase()), response.getBody());
    }

    @Test
    void getAverageAgeStream_shouldReturnAverageAgeOfAllStudentsAndStatusOk() {
        var response = restTemplate.exchange(
                url + "/age/avg-stream",
                HttpMethod.GET,
                null,
                Double.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(15.5, response.getBody());
    }

    protected static class StudentControllerTestData {
        public static FacultyDto GRYFFINDOOR_DTO = new FacultyDto(1L, "Gryffindoor", "Red");
        public static FacultyDto SLYTHERIN_DTO = new FacultyDto(2L, "Slytherin", "Green");
        public static FacultyDto RAVENCLAW_DTO = new FacultyDto(3L, "Ravenclaw", "Blue");
        public static FacultyDto HUFFLEPUFF_DTO = new FacultyDto(4L, "Hufflepuff", "Yellow");
        public static StudentDto HARRY_DTO = new StudentDto(1L, "Harry", 12, 1, "Gryffindoor");
        public static StudentDto DRAKO_DTO = new StudentDto(2L, "Drako", 17, 2, "Slytherin");
        public static StudentDto CHANG_DTO = new StudentDto(3L, "Chang", 17, 3, "Ravenclaw");
        public static StudentDto SEDRIK_DTO = new StudentDto(4L, "Sedrik", 16, 4, "Hufflepuff");
    }
}