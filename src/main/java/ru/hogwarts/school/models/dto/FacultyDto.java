package ru.hogwarts.school.models.dto;

import ru.hogwarts.school.models.domain.Student;

import java.util.List;

public class FacultyDto {
    private Long id;
    private String name;
    private String colour;
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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}