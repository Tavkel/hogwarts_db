package ru.hogwarts.school.models.dto;

import java.util.Objects;

public class StudentDto {
    private Long id;
    private String name;
    private Integer age;
    private int facultyId;
    private String facultyName;

    public StudentDto() {
    }

    public StudentDto(Long id, String name, Integer age, int facultyId, String facultyName) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentDto that = (StudentDto) o;
        return facultyId == that.facultyId && Objects.equals(name, that.name) && Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, facultyId);
    }

    @Override
    public String toString() {
        return "StudentDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", facultyId=" + facultyId +
                ", facultyName='" + facultyName + '\'' +
                '}';
    }
}
