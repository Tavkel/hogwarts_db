package ru.hogwarts.school.models.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STUDENTS")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Student extends SafeDeleted {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private Integer age;
    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    public Student() {
    }

    public Student(String name, Integer age, Faculty faculty) {
        this.name = name;
        this.age = age;
        this.faculty = faculty;
    }

    public Student(String name, Integer age, Long facultyId) {
        this.name = name;
        this.age = age;
        this.faculty = new Faculty(facultyId);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", faculty=" + faculty +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(age, student.age)
                && Objects.equals(id, student.id)
                && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }
}
