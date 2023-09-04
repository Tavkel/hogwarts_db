package ru.hogwarts.school.services.implementations;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.EntryAlreadyExistsException;
import ru.hogwarts.school.services.interfaces.StudentService;
import ru.hogwarts.school.services.repositories.StudentRepository;
import ru.hogwarts.school.models.domain.Student;

import java.util.NoSuchElementException;

@Service
public class StudentServiceImpl implements StudentService {
    private StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student addStudent(Student student) {
        var check = studentRepository.findByName(student.getName());
        if (check.isPresent() && !check.get().getDeleted()) {
            throw new EntryAlreadyExistsException();
        }
        return studentRepository.saveAndFlush(student);
    }

    @Override
    public Student getStudentById(long id) {
        var result = studentRepository.findById(id);
        if (result.isPresent() && !result.get().getDeleted()) {
            return result.get();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Student updateStudent(Student student) {
        var result = studentRepository.findById(student.getId());
        if (result.isPresent()) {
            return studentRepository.saveAndFlush(student);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Student removeStudent(long id) {
        var result = studentRepository.findById(id);
        if (result.isPresent()) {
            result.get().setDeleted(true);
            return studentRepository.saveAndFlush(result.get());
        } else {
            throw new NoSuchElementException();
        }
    }
}
