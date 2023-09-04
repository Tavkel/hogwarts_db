package ru.hogwarts.school.services.implementations;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.EntryAlreadyExistsException;
import ru.hogwarts.school.helpers.mapper.StudentMapper;
import ru.hogwarts.school.models.dto.StudentDto;
import ru.hogwarts.school.services.interfaces.StudentService;
import ru.hogwarts.school.services.repositories.StudentRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    private StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentDto addStudent(StudentDto studentDto) {
        var student = StudentMapper.MAPPER.toStudent(studentDto);
        var check = studentRepository.findFirstByName(student.getName());
        if (check.isPresent()) {
            if (check.get().getDeleted()){
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
        var result = studentRepository.findById(id);
        if (result.isPresent() && !result.get().getDeleted()) {
            return StudentMapper.MAPPER.fromStudent(result.get());
        } else {
            throw new NoSuchElementException("Student not found.");
        }
    }

    @Override
    public StudentDto updateStudent(StudentDto studentDto) {
        var student = StudentMapper.MAPPER.toStudent(studentDto);
        var result = studentRepository.findById(student.getId());
        if (result.isPresent()) {
            return StudentMapper.MAPPER.fromStudent(studentRepository.saveAndFlush(student));
        } else {
            throw new NoSuchElementException("Student not found.");
        }
    }

    @Override
    public StudentDto removeStudent(long id) {
        var result = studentRepository.findById(id);
        if (result.isPresent() && !result.get().getDeleted()) {
            result.get().setDeleted(true);
            return StudentMapper.MAPPER.fromStudent(studentRepository.saveAndFlush(result.get()));
        } else {
            throw new NoSuchElementException("Student not found");
        }
    }

    @Override
    public List<StudentDto> getStudentsByAge(int age) {
        var dbResponse = studentRepository.findByAge(age);

        return dbResponse.stream().map(StudentMapper.MAPPER::fromStudent).collect(Collectors.toList());
    }
}
