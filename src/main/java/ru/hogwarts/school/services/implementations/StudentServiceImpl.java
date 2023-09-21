package ru.hogwarts.school.services.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.EntryAlreadyExistsException;
import ru.hogwarts.school.helpers.mapper.FacultyMapper;
import ru.hogwarts.school.helpers.mapper.StudentMapper;
import ru.hogwarts.school.models.dto.FacultyDto;
import ru.hogwarts.school.models.dto.StudentDto;
import ru.hogwarts.school.services.interfaces.StudentService;
import ru.hogwarts.school.services.repositories.CustomStudentRepository;
import ru.hogwarts.school.services.repositories.StudentRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final CustomStudentRepository customStudentRepository;
    private final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    public StudentServiceImpl(StudentRepository studentRepository, CustomStudentRepository customStudentRepository) {
        this.studentRepository = studentRepository;
        this.customStudentRepository = customStudentRepository;
    }

    @Override
    public StudentDto addStudent(StudentDto studentDto) {
        logger.debug("Attempting to add student");
        var student = StudentMapper.MAPPER.toStudent(studentDto);
        var check = studentRepository.findFirstByNameIgnoreCase(student.getName());
        if (check.isPresent()) {
            logger.warn("Student with the same name discovered");
            if (check.get().getDeleted()) {
                logger.warn(String.format("Restoring Student \"%s\", and applying changes", student.getName()));
                check.get().setDeleted(false);
                check.get().setName(student.getName());
                check.get().setAge(student.getAge());
                check.get().setFaculty(student.getFaculty());
                return StudentMapper.MAPPER.fromStudent(studentRepository.saveAndFlush(check.get()));
            }
            throw new EntryAlreadyExistsException("This student already exists.");
        }
        return StudentMapper.MAPPER.fromStudent(studentRepository.saveAndFlush(student));
    }

    @Override
    public StudentDto getStudentById(long id) {
        logger.debug(String.format("Attempting to fetch student %d", id));
        var result = studentRepository.findById(id);
        if (result.isPresent() && !result.get().getDeleted()) {
            return StudentMapper.MAPPER.fromStudent(result.get());
        } else {
            throw new NoSuchElementException("Student not found.");
        }
    }

    @Override
    public FacultyDto getStudentsFaculty(long id) {
        logger.debug(String.format("Attempting to fetch faculty of student %d", id));
        var dbResponse = studentRepository.findById(id);
        if (dbResponse.isPresent() && !dbResponse.get().getDeleted()) {
            return FacultyMapper.MAPPER.fromFaculty(dbResponse.get().getFaculty());
        } else {
            throw new NoSuchElementException("Student not found.");
        }
    }

    @Override
    public List<StudentDto> getStudentsFromFaculty(long id) {
        logger.debug(String.format("Attempting to fetch students of faculty %d", id));
        var dbResponse = studentRepository.getStudentsFromFaculty(id);
        return dbResponse.stream().map(StudentMapper.MAPPER::fromStudent).collect(Collectors.toList());
    }

    @Override
    public StudentDto updateStudent(StudentDto studentDto) {
        logger.debug(String.format("Attempting to update properties of student %d", studentDto.getId()));
        var student = StudentMapper.MAPPER.toStudent(studentDto);
        var result = studentRepository.findById(studentDto.getId());
        if (result.isPresent()) {
            return StudentMapper.MAPPER.fromStudent(studentRepository.saveAndFlush(student));
        } else {
            throw new NoSuchElementException("Student not found.");
        }
    }

    @Override
    public StudentDto removeStudent(long id) {
        logger.debug(String.format("Attempting to delete student %d", id));
        var result = studentRepository.findById(id);
        if (result.isPresent() && !result.get().getDeleted()) {
            result.get().setDeleted(true);
            return StudentMapper.MAPPER.fromStudent(studentRepository.saveAndFlush(result.get()));
        } else {
            throw new NoSuchElementException("Student not found.");
        }
    }

    @Override
    public List<StudentDto> getStudentsByAge(int age) {
        logger.debug("Attempting to fetch students with age " + age);
        var dbResponse = studentRepository.findByAge(age);

        return dbResponse.stream().map(StudentMapper.MAPPER::fromStudent).collect(Collectors.toList());
    }

    @Override
    public List<StudentDto> getStudentsByAgeInRange(int floor, int ceiling) {
        logger.debug("Attempting to fetch students with age from " + floor + " to " + ceiling);

        var dbResponse = studentRepository.findByAgeBetween(floor, ceiling);

        return dbResponse.stream().map(StudentMapper.MAPPER::fromStudent).collect(Collectors.toList());
    }

    @Override
    public List<StudentDto> searchStudentsByName(String searchString) {
        logger.debug(String.format("Attempting to fetch students with names containing \"%s\"", searchString));
        var dbResponse = studentRepository.searchStudentByNamePart(searchString);
        return dbResponse.stream().map(StudentMapper.MAPPER::fromStudent).collect(Collectors.toList());
    }

    @Override
    public Integer getStudentCount() {
        logger.debug("Counting students");
        return studentRepository.countNotDeleted();
    }

    @Override
    public Integer getAverageAge() {
        logger.debug("Counting students average age");
        return studentRepository.avgAge();
    }

    @Override
    public List<StudentDto> getLastStudents(int amount) {
        logger.debug(String.format("Attempting to fetch %d last students", amount));
        return customStudentRepository.findOrderedByIdLimitedTo(amount).stream().map(StudentMapper.MAPPER::fromStudent).collect(Collectors.toList());
    }

    @Override
    public void playWithThreads() {
        var students = customStudentRepository.findOrderedByIdLimitedTo(6).stream().map(StudentMapper.MAPPER::fromStudent).toList();
        print(students.get(0));
        print(students.get(1));

        var firstThread = new Thread(() -> {
            print(students.get(2));
            print(students.get(3));
        });
        var secondThread = new Thread(() -> {
            print(students.get(4));
            print(students.get(5));
        });
        firstThread.setName("first");
        secondThread.setName("second");

        firstThread.start();
        secondThread.start();
    }

    @Override
    public void playWithThreads2() {
        var students = customStudentRepository.findOrderedByIdLimitedTo(6).stream().map(StudentMapper.MAPPER::fromStudent).toList();
        print2(students.get(0));
        print2(students.get(1));

        var firstThread = new Thread(() -> {
            print2(students.get(2));
            print2(students.get(3));
        });
        var secondThread = new Thread(() -> {
            print2(students.get(4));
            print2(students.get(5));
        });
        firstThread.setName("first");
        secondThread.setName("second");

        firstThread.start();
        secondThread.start();
    }

    private void print(StudentDto student) {
        logger.info(student.getName());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void print2(StudentDto student) {
        logger.info(student.getName());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
